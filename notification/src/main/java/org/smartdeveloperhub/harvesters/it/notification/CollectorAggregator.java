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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.external.Collector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

final class CollectorAggregator {

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorAggregator.class);

	private final String name;
	private final NotificationListener listener;

	private final Multimap<String,Collector> brokerCollectors;
	private final Multimap<String,String> brokerInstances;
	private final Map<String,CollectorController> brokerController;
	private final Map<String,String> instanceBroker;

	private final BlockingQueue<SuspendedNotification> notificationQueue;
	private final Deque<CollectorController> connectedControllers;

	private NotificationPump pump;

	private CollectorAggregator(final String name, final NotificationListener listener) {
		this.name=name;
		this.listener=listener;
		this.brokerCollectors=LinkedListMultimap.create();
		this.brokerInstances=LinkedListMultimap.create();
		this.instanceBroker=Maps.newLinkedHashMap();
		this.brokerController=Maps.newLinkedHashMap();
		this.notificationQueue=new LinkedBlockingQueue<>();
		this.connectedControllers=Lists.newLinkedList();
	}

	void connect(final List<Collector> collectors) throws ControllerException {
		LOGGER.info("Setting up collector aggregator for {}...",this.name);
		startNotificationPump();
		for(final Collector collector:collectors) {
			verifyCollectorIsNotConfigured(collector);
			addCollector(collector, queueName(collector));
		}
		LOGGER.info("Collector aggregator for {} connected",this.name);
	}

	List<String> instances() {
		return ImmutableList.copyOf(this.instanceBroker.keySet());
	}

	List<String> brokers() {
		return ImmutableList.copyOf(this.brokerController.keySet());
	}

	List<String> brokerInstances(final String brokerId) {
		return ImmutableList.copyOf(this.brokerInstances.get(brokerId));
	}

	CollectorController controller(final String instance) {
		checkNotNull(instance,"Instance cannot be null");
		final String queueName = this.instanceBroker.get(instance);
		checkArgument(queueName!=null,"Unknown instance '%s'",instance);
		return this.brokerController.get(queueName);
	}

	void disconnect() {
		LOGGER.info("Disconnecting {} collector aggregator...",this.name);
		shutdownGracefully();
		LOGGER.info("Collector aggregator for {} disconnected",this.name);
	}

	private void addCollector(final Collector collector, final String queueName) throws ControllerException {
		final boolean isNew=!this.brokerCollectors.containsKey(queueName);
		this.brokerInstances.put(queueName,collector.getInstance());
		this.instanceBroker.put(collector.getInstance(),queueName);
		this.brokerCollectors.put(queueName,collector);
		if(isNew) {
			final CollectorController controller=startController(collector,queueName);
			this.brokerController.put(queueName, controller);
			this.connectedControllers.add(controller);
		}
	}

	private void verifyCollectorIsNotConfigured(final Collector collector) {
		if(this.brokerInstances.containsValue(collector.getInstance())) {
			shutdownGracefully();
			throw new IllegalArgumentException("Multiple configurations found for collector "+collector.getInstance());
		}
	}

	private CollectorController startController(final Collector collector, final String queueName) throws ControllerException {
		final CollectorController controller = CollectorController.createNamedReceiver(collector,queueName,this.notificationQueue);
		LOGGER.info("Connecting controller for collector {}...",collector.getInstance());
		try {
			controller.connect();
			return controller;
		} catch (final ControllerException e) {
			LOGGER.warn("Could not connect controller for collector {}. Full stacktrace follows",collector.getInstance(),e);
			shutdownGracefully();
			throw e;
		}
	}

	private void startNotificationPump() {
		this.pump=new NotificationPump(this.notificationQueue,this.listener);
		this.pump.start();
	}

	private String queueName(final Collector collector) {
		final Integer hash =
			Objects.hash(
				collector.getBrokerHost(),
				collector.getBrokerPort(),
				collector.getVirtualHost(),
				collector.getExchangeName());
		return String.format("%s.collector.hash%8X",this.name,hash);
	}

	private void shutdownGracefully() {
		disconnectControllers();
		stopNotificationPump();
		this.brokerInstances.clear();
		this.brokerCollectors.clear();
		drainPendingNotifications();
	}

	private void stopNotificationPump() {
		if(this.pump!=null) {
			this.pump.stop();
			this.pump=null;
		}
	}
	private void drainPendingNotifications() {
		final List<SuspendedNotification> discarded=new ArrayList<>();
		if(this.notificationQueue.drainTo(discarded)>0) {
			LOGGER.warn("{} notifications were dropped",discarded.size());
		}
	}


	private void disconnectControllers() {
		final Iterator<CollectorController> iterator = this.connectedControllers.descendingIterator();
		while(iterator.hasNext()) {
			iterator.next().disconnect();
		}
		this.connectedControllers.clear();
		this.brokerController.clear();
	}

	static CollectorAggregator newInstance(final String name, final NotificationListener listener) {
		return new CollectorAggregator(name,listener);
	}

}
