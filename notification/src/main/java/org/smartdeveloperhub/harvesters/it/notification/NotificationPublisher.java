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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.2.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;

/**
 * Utility class to enable publishing the notifications of a Collector instance
 */
public final class NotificationPublisher {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationPublisher.class);

	private final CollectorConfiguration configuration;

	private final CollectorController controller;

	private NotificationPublisher(final CollectorConfiguration configuration) {
		this.configuration = configuration;
		this.controller=CollectorController.createPublisher(configuration);
	}

	/**
	 * Start the notification publisher. Upon this point, the user will be able
	 * to push notifications.
	 *
	 * @throws IOException
	 *             if the notification publisher cannot connect to the broker
	 *             used by the specified Collector configuration
	 */
	public void start() throws IOException {
		LOGGER.info("Starting notification publisher for {}...",this.configuration.getInstance());
		try {
			this.controller.connect();
			LOGGER.info("Notification publisher for {} started",this.configuration.getInstance());
		} catch (final ControllerException e) {
			LOGGER.warn("Could not start publisher using {}. Full stacktrace follows",this.configuration,e);
			throw new IOException("Could not start publisher using "+this.configuration,e);
		}
	}

	public void publish(final Event event) throws IOException {
		try {
			this.controller.publishEvent(event);
		} catch (final ControllerException e) {
			throw new IOException("Could not publish event",e);
		}
	}

	/**
	 * Shutdown the notification publisher. Upon shutdown, the user will not be
	 * able to push notifications.
	 */
	public void shutdown() {
		LOGGER.info("Shutting down notification publisher for {}...",this.configuration.getInstance());
		this.controller.disconnect();
		LOGGER.info("Notification manager {} shutdown",this.configuration.getInstance());
	}

	/**
	 * Create a new instance that enable publishing notifications for the specified Collector instance.
	 *
	 * @param configuration
	 *            the Collector instance configuration details
	 * @return the created instance
	 * @throws NullPointerException
	 *             if the parameter is {@code null}
	 */
	public static NotificationPublisher newInstance(final CollectorConfiguration configuration) {
		checkNotNull(configuration,"Collector configuration cannot be null");
		return new NotificationPublisher(configuration);
	}

}
