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

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;

final class EventPublisher implements Runnable {

	private static final Logger LOGGER=LoggerFactory.getLogger(EventPublisher.class);

	private final Event event;
	private final CollectorAggregator aggregator;

	EventPublisher(final Event event, final CollectorAggregator aggregator) {
		this.event = event;
		this.aggregator = aggregator;
	}

	@Override
	public void run() {
		final CollectorController controller = this.aggregator.controller(this.event.getInstance());
		try {
			controller.publishEvent(this.event);
			LOGGER.info("Published event {{}}{{}}{}",this.event.getInstance(),new Date(this.event.getTimestamp()),this.event.getClass().getSimpleName());
			final Random random = new Random(System.nanoTime());
			try {
				TimeUnit.MILLISECONDS.sleep(random.nextInt(250));
			} catch (final InterruptedException e) {
				LOGGER.error("Interrupted while sleeping after publishing event "+this.event);
			}
		} catch (final ControllerException e) {
			LOGGER.error("Could not publish event "+this.event,e);
		}
	}
}