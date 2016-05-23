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
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.external.Collector;
import org.smartdeveloperhub.harvesters.it.notification.external.Enhancer;
import org.smartdeveloperhub.harvesters.it.notification.external.EnhancerController;

/**
 * Utility class to enable receiving notifications pushed by the Collectors used
 * by a given GitLab Enhancer instance
 */
public final class NotificationManager {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationManager.class);

	private final URI target;
	private final CollectorAggregator aggregator;

	private NotificationManager(final URI target, final NotificationListener listener) {
		this.target=target;
		this.aggregator=CollectorAggregator.newInstance(managerName(target), listener);
	}

	/**
	 * Start the notification manager. Upon this point, the manager will push to
	 * the listener any notification sent by the Collectors used by the
	 * specified GitLab Enhancer instance.
	 *
	 * @throws IOException
	 *             if the notification manager cannot connect to the specified
	 *             GitLab Enhancer instance, or to the brokers used by the
	 *             Collectors used by the GitLab Enhancer instance
	 */
	public void start() throws IOException {
		LOGGER.info("Starting notification manager for {}...",this.target);
		final List<Collector> usedCollectors = getEnhancerCollectorConfiguration();
		try {
			this.aggregator.connect(usedCollectors);
			LOGGER.info("Notification manager for {} started",this.target);
		} catch (final ControllerException e) {
			LOGGER.warn("Could not connect to collectors of {}. Full stacktrace follows",this.target,this.target);
			throw new IOException("Could not connect to collectors of "+this.target,e);
		}
	}

	/**
	 * Shutdown the notification manager. Upon shutdown, the listener will stop
	 * receiving notifications.
	 */
	public void shutdown() {
		LOGGER.info("Shutting down notification manager for {}...",this.target);
		this.aggregator.disconnect();
		LOGGER.info("Notification manager for {} shutdown",this.target);
	}

	private List<Collector> getEnhancerCollectorConfiguration() throws IOException {
		final EnhancerController controller=new EnhancerController(this.target.toString());
		final Enhancer enhancer = controller.getEnhancer();
		return enhancer.getCollectors();
	}

	private static String managerName(final URI target) {
		return String.format("manager%s.enhancer.hash%8X",UUID.randomUUID(),target.hashCode());
	}

	/**
	 * Create a new instance that will interact with the Collectors used by the
	 * specifed GitLab Enhancer instance and will push the notifications sent by
	 * these Collectors to the specified NotificationListener
	 *
	 * @param target
	 *            the URI of the GitLab Enhancer instance
	 * @param listener
	 *            the NotificationListener to which the notifications will be
	 *            pushed
	 * @return the created instance
	 * @throws NullPointerException
	 *             if any of the parameters is {@code null}
	 */
	public static NotificationManager newInstance(final URI target, final NotificationListener listener) {
		checkNotNull(target,"Target cannot be null");
		checkNotNull(listener,"Listener cannot be null");
		return new NotificationManager(target,listener);
	}

}