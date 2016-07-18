/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0
 *   Bundle      : it-harvester-notification-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

final class CollectorController {

	interface Cleaner {

		void clean(Channel channel) throws IOException;

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorController.class);

	private static final String EXCHANGE_TYPE="topic";

	private final String queueName;
	private final CollectorConfiguration collector;
	private final BlockingQueue<SuspendedNotification> notificationQueue;

	private final Lock write;

	private final Deque<Cleaner> cleaners;
	private final List<NotificationConsumer> callbacks;

	private String actualQueueName;

	private final ConnectionManager manager;

	private CollectorController(final CollectorConfiguration collector, final String queueName, final BlockingQueue<SuspendedNotification> notificationQueue) {
		checkNotNull(collector,"Collector cannot be null");
		this.collector=collector;
		this.manager=new ConnectionManager(collector.getInstance(),collector.getBrokerHost(),collector.getBrokerPort(),collector.getVirtualHost());
		this.queueName=queueName;
		this.notificationQueue=notificationQueue;
		final ReadWriteLock lock=new ReentrantReadWriteLock();
		this.write=lock.writeLock();
		this.cleaners=Lists.newLinkedList();
		this.callbacks=Lists.newArrayList();
	}

	CollectorConfiguration collector() {
		return this.collector;
	}

	String queueName() {
		return this.queueName;
	}

	String actualQueueName() {
		this.write.lock();
		try {
			checkState(this.manager.isConnected(),"Not connected");
			return this.actualQueueName;
		} finally {
			this.write.unlock();
		}
	}

	void connect() throws ControllerException {
		this.write.lock();
		try {
			checkState(!this.manager.isConnected(),"Already connected");
			this.manager.connect();
			declareExchange();
			prepareQueue();
		} catch(final ControllerException e) {
			disconnectGracefully();
			throw e;
		} finally {
			this.write.unlock();
		}
	}

	void disconnect() {
		this.write.lock();
		try {
			disconnectGracefully();
		} finally {
			this.write.unlock();
		}
	}

	void publishEvent(final Event event) throws ControllerException {
		try {
			publishEvent(EventUtil.marshall(event), event.getClass().getSimpleName());
		} catch (final IOException e) {
			throw new ControllerException(
				this.collector.getBrokerHost(),
				this.collector.getBrokerPort(),
				this.collector.getVirtualHost(),
				"Could not serialize event "+event, e);
		}
	}

	void publishEvent(final String event, final String eventType) throws ControllerException {
		this.write.lock();
		try {
			checkState(this.manager.isConnected(),"Not connected");
			final Channel aChannel = this.manager.currentChannel();
			aChannel.addReturnListener(new LoggingReturnListener());
			publish(
				aChannel,
				this.collector.getExchangeName(),
				Notifications.routingKey(eventType),
				event);
		} finally {
			this.write.unlock();
		}
	}

	private void publish(final Channel aChannel, final String exchangeName, final String routingKey, final String payload) throws ControllerException {
		try {
			LOGGER.trace("Publishing message to exchange '{}' and routing key '{}'. Payload: \n{}",exchangeName,routingKey,payload);
			final Map<String, Object> headers=Maps.newLinkedHashMap();
			headers.put(HttpHeaders.CONTENT_TYPE,Notifications.MIME);
			aChannel.
				basicPublish(
					exchangeName,
					routingKey,
					true,
					MessageProperties.
						MINIMAL_PERSISTENT_BASIC.
							builder().
								headers(headers).
								build(),
					payload.getBytes());
		} catch (final IOException e) {
			this.manager.discardChannel();
			final String errorMessage =
				String.format(
					"Could not publish message [%s] to exchange '%s' and routing key '%s' using broker %s:%s%s: %s",
					payload,exchangeName,routingKey,
					this.collector.getBrokerHost(),
					this.collector.getBrokerPort(),
					this.collector.getVirtualHost(),
					e.getMessage());
			LOGGER.error(errorMessage,e);
			throw new ControllerException(
				this.collector.getBrokerHost(),
				this.collector.getBrokerPort(),
				this.collector.getVirtualHost(),
				errorMessage,
				e);
		} catch (final Exception e) {
			this.manager.discardChannel();
			final String errorMessage =
				String.format(
					"Unexpected failure while publishing message [%s] to exchange '%s' and routing key '%s' using broker %s:%s%s: %s",
					payload,exchangeName,routingKey,
					this.collector.getBrokerHost(),
					this.collector.getBrokerPort(),
					this.collector.getVirtualHost(),
					e.getMessage());
			LOGGER.error(errorMessage,e);
			throw new ControllerException(
					this.collector.getBrokerHost(),
					this.collector.getBrokerPort(),
					this.collector.getVirtualHost(),
					errorMessage,
					e);
		}
	}

	private void prepareQueue() throws ControllerException {
		if(this.notificationQueue!=null) {
			this.actualQueueName = declareQueue();
			bindQueue(this.actualQueueName);
			try {
				final Channel currentChannel = this.manager.channel();
				final NotificationConsumer callback = new NotificationConsumer(currentChannel,this.notificationQueue);
				currentChannel.
					basicConsume(
						this.actualQueueName,
						false,
						callback
					);
				this.callbacks.add(callback);
			} catch (final IOException e) {
				throw new ControllerException(this.collector.getBrokerHost(),this.collector.getBrokerPort(),this.collector.getVirtualHost(),"Could not register consumer for queue '"+this.actualQueueName+"'",e);
			}
		}
	}

	/**
	 * The a durable topic exchange is always declared
	 */
	private void declareExchange() throws ControllerException {
		try {
			this.manager.channel().exchangeDeclare(this.collector.getExchangeName(),EXCHANGE_TYPE,true);
		} catch (final IOException e) {
			if(!FailureAnalyzer.isExchangeDeclarationRecoverable(e)) {
				throw new ControllerException(this.collector.getBrokerHost(),this.collector.getBrokerPort(),this.collector.getVirtualHost(),"Could not create exchange named '"+this.collector.getExchangeName()+"'",e);
			}
		}
	}

	/**
	 * The declared queues are durable, exclusive, and auto-delete, and expire if no
	 * client uses them after 1 second.
	 */
	private String declareQueue() throws ControllerException {
		final String targetQueueName=Optional.fromNullable(this.queueName).or("");
		try {
			final Map<String, Object> args=
				ImmutableMap.
					<String, Object>builder().
						put("x-expires",1000).
						build();
			final DeclareOk ok = this.manager.channel().queueDeclare(targetQueueName,true,true,true,args);
			final String declaredQueueName = ok.getQueue();
			this.cleaners.push(CleanerFactory.queueDelete(declaredQueueName));
			return declaredQueueName;
		} catch (final IOException e) {
			throw new ControllerException(this.collector.getBrokerHost(),this.collector.getBrokerPort(),this.collector.getVirtualHost(),"Could not create queue named '"+targetQueueName+"'",e);
		}
	}

	private void bindQueue(final String queueName) throws ControllerException {
		try {
			this.manager.channel().queueBind(queueName,this.collector.getExchangeName(),Notifications.ROUTING_KEY_PATTERN);
			this.cleaners.push(CleanerFactory.queueUnbind(this.collector.getExchangeName(),queueName,Notifications.ROUTING_KEY_PATTERN));
		} catch (final IOException e) {
			throw new ControllerException(this.collector.getBrokerHost(),this.collector.getBrokerPort(),this.collector.getVirtualHost(),"Could not bind queue '"+queueName+"' to exchange '"+this.collector.getExchangeName()+"' using routing key '"+Notifications.ROUTING_KEY_PATTERN+"'",e);
		}
	}

	private void disconnectGracefully() {
		if(this.manager.isConnected()) {
			cleanUp();
			this.callbacks.clear();
			this.actualQueueName=null;
			this.manager.disconnect();
		}
	}

	private void cleanUp() {
		LOGGER.debug("Cleaning up broker ({})...",this.cleaners.size());
		while(!this.cleaners.isEmpty()) {
			final Cleaner cleaner=this.cleaners.pop();
			try {
				cleaner.clean(this.manager.channel());
				LOGGER.trace("{} completed",cleaner);
			} catch (final Exception e) {
				LOGGER.warn("{} failed. Full stacktrace follows",cleaner,e);
			}
		}
		LOGGER.debug("Broker clean-up completed.",this.cleaners.size());
	}

	static CollectorController createPublisher(final CollectorConfiguration collector) {
		return new CollectorController(collector,null,null);
	}

	static CollectorController createAnonymousReceiver(final CollectorConfiguration collector, final BlockingQueue<SuspendedNotification> queue) {
		checkNotNull(queue,"Notification queue cannot be null");
		return new CollectorController(collector,null,queue);
	}

	static CollectorController createNamedReceiver(final CollectorConfiguration collector, final String queueName, final BlockingQueue<SuspendedNotification> queue) {
		Amqp.validateName(queueName,"Queue name");
		checkNotNull(queue,"Notification queue cannot be null");
		return new CollectorController(collector,queueName,queue);
	}

}