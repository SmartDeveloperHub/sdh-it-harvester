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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

final class ConnectionManager {

	private static final Logger LOGGER=LoggerFactory.getLogger(ConnectionManager.class);

	private final String name;
	private final String brokerHost;
	private final int brokerPort;
	private final String virtualHost;

	private final Map<Long,Channel> channels;

	private final Lock lock;
	private Connection connection;
	private Channel    channel;

	ConnectionManager(final String name, final String brokerHost, final int brokerPort, final String virtualHost) {
		this.name = name;
		this.brokerHost=brokerHost;
		this.brokerPort=brokerPort;
		this.virtualHost=virtualHost;
		this.lock=new ReentrantLock();
		this.channels=Maps.newLinkedHashMap();
	}

	void connect() throws ControllerException {
		this.lock.lock();
		try {
			if(connected()) {
				return;
			}
			final ConnectionFactory factory=new ConnectionFactory();
			factory.setHost(this.brokerHost);
			factory.setPort(this.brokerPort);
			factory.setVirtualHost(this.virtualHost);
			factory.setThreadFactory(brokerThreadFactory());
			factory.setExceptionHandler(new ConnectionManagerExceptionHandler(this));
			this.connection = factory.newConnection();
			createChannel();
		} catch(IOException | TimeoutException e) {
			final String message = String.format("Could not connect to broker at %s:%s using virtual host %s",this.brokerHost,this.brokerPort,this.virtualHost);
			throw new ControllerException(this.brokerHost,this.brokerPort,this.virtualHost,message,e);
		} finally {
			this.lock.unlock();
		}
	}

	void disconnect() {
		this.lock.lock();
		try {
			if(!connected()) {
				return;
			}
			closeChannelsQuietly();
			closeConnectionQuietly();
		} finally {
			this.lock.unlock();
		}
	}

	Channel channel() throws ControllerException {
		this.lock.lock();
		try {
			checkState(connected(),"Not connected");
			if(!this.channel.isOpen()) {
				createChannel();
			}
			return this.channel;
		} finally {
			this.lock.unlock();
		}
	}

	Channel currentChannel() throws ControllerException {
		final long threadId = Thread.currentThread().getId();
		Channel result;
		synchronized(this.channels) {
			result=this.channels.get(threadId);
			if(result==null) {
				result=createNewChannel();
				this.channels.put(threadId,result);
			}
		}
		return result;
	}

	void discardChannel() {
		final long threadId = Thread.currentThread().getId();
		synchronized(this.channels) {
			final Channel aChannel=this.channels.get(threadId);
			if(aChannel!=null) {
				closeQuietly(aChannel);
				this.channels.remove(threadId);
			}
		}
	}

	boolean isConnected() {
		this.lock.lock();
		try {
			return connected();
		} finally {
			this.lock.unlock();
		}
	}

	private boolean connected() {
		return this.connection!=null && this.connection.isOpen();
	}

	private ThreadFactory brokerThreadFactory() {
		return
			new ThreadFactoryBuilder().
				setNameFormat("{"+this.name+"}-connectionmanager-%d").
				setUncaughtExceptionHandler(new ConnectionManagerUncaughtExceptionHandler(this)).
				build();
	}

	private void createChannel() throws ControllerException {
		try {
			this.channel = createNewChannel();
		} catch (final ControllerException e) {
			closeConnectionQuietly();
			throw e;
		}
	}

	private Channel createNewChannel() throws ControllerException {
		this.lock.lock();
		try {
			checkState(connected(),"No connection available");
			final Channel result = this.connection.createChannel();
			checkNotNull(result,"No channel available");
			return result;
		} catch(final NullPointerException | IOException e) {
			final String message =
				String.format(
					"Could not create channel using connection %08X to broker at %s:%s using virtual host %s",
					this.connection.hashCode(),
					this.brokerHost,
					this.brokerPort,
					this.virtualHost);
			throw new ControllerException(this.brokerHost,this.brokerPort,this.virtualHost,message,e);
		} finally {
			this.lock.unlock();
		}
	}

	private void closeQuietly(final Channel channel) {
		if(channel.isOpen()) {
			try {
				channel.close();
			} catch (final Exception e) {
				LOGGER.trace("Could not close channel gracefully",e);
			}
		}
	}

	private void closeChannelsQuietly() {
		closeQuietly(this.channel);
		for(final Channel tmpChannel:this.channels.values()) {
			closeQuietly(tmpChannel);
		}
		this.channels.clear();
		this.channel=null;
	}

	private void closeConnectionQuietly() {
		try {
			this.connection.close();
		} catch (final Exception e) {
			LOGGER.trace("Could not close connection gracefully",e);
		}
		this.connection=null;
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					omitNullValues().
					add("name",this.name).
					add("brokerHost",this.brokerHost).
					add("brokerPort",this.brokerPort).
					add("virtualHost",this.virtualHost).
					add("connected",this.channel).
					add("channels{threadId}",this.channels.keySet()).
					toString();
	}
}