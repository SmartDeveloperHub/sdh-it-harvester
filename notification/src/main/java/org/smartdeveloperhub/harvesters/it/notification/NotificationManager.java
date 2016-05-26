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

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Utility class to enable receiving notifications pushed by different Collector
 * instances
 */
public final class NotificationManager {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationManager.class);

	private final UUID id;
	private final ImmutableList<CollectorConfiguration> collectors;
	private final CollectorAggregator aggregator;

	/**
	 * TODO: Collector is not immutable, therefore the configuration could be
	 * externally changed...
	 */
	private NotificationManager(final List<CollectorConfiguration> collectors, final NotificationListener listener) {
		this.id=UUID.randomUUID();
		this.collectors=ImmutableList.copyOf(collectors);
		this.aggregator=CollectorAggregator.newInstance(managerName(this.id),listener);
	}

	/**
	 * Start the notification manager. Upon this point, the manager will push to
	 * the listener any notification sent by the specified Collector instances.
	 *
	 * @throws IOException
	 *             if the notification manager cannot connect to the specified
	 *             to the brokers used by the specified Collectors instances
	 */
	public void start() throws IOException {
		LOGGER.info("Starting notification manager {}...",this.id);
		try {
			this.aggregator.connect(this.collectors);
			LOGGER.info("Notification manager {} started",this.id);
		} catch (final ControllerException e) {
			LOGGER.warn("Could not connect to collectors of {}. Full stacktrace follows",this.id,e);
			throw new IOException("Could not connect to collectors of "+this.id,e);
		}
	}

	/**
	 * Shutdown the notification manager. Upon shutdown, the listener will stop
	 * receiving notifications.
	 */
	public void shutdown() {
		LOGGER.info("Shutting down notification manager {}...",this.id);
		this.aggregator.disconnect();
		LOGGER.info("Notification manager {} shutdown",this.id);
	}

	private static String managerName(final UUID id) {
		return String.format("manager%s",id);
	}

	/**
	 * Create a new instance that will interact with a collection of Collectors
	 * and will push the notifications sent by these Collectors to the specified
	 * NotificationListener
	 *
	 * @param collectors
	 *            the Collector instance configuration details
	 * @param listener
	 *            the NotificationListener to which the notifications will be
	 *            pushed
	 * @return the created instance
	 * @throws NullPointerException
	 *             if any of the parameters is {@code null}
	 */
	public static NotificationManager newInstance(final List<CollectorConfiguration> collectors, final NotificationListener listener) {
		checkNotNull(collectors,"Target cannot be null");
		checkNotNull(listener,"Listener cannot be null");
		return new NotificationManager(collectors,listener);
	}

}