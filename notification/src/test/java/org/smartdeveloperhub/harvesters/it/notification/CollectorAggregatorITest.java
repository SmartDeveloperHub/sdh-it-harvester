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

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

public class CollectorAggregatorITest extends NotificationTestHelper {

	private final class CustomNotificationListener implements NotificationListener {
		private final Random random = new Random(System.currentTimeMillis());

		private final boolean delay;

		private CustomNotificationListener(final boolean delay) {
			this.delay=delay;
		}

		@Override
		public void onProjectUpdate(final Notification notification, final ProjectUpdatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onProjectDeletion(final Notification notification, final ProjectDeletedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onProjectCreation(final Notification notification, final ProjectCreatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onContributorDeletion(final Notification notification, final ContributorDeletedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onCommitCreation(final Notification notification, final CommitCreatedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		@Override
		public void onCommitDeletion(final Notification notification, final CommitDeletedEvent event) {
			LOGGER.debug("Received {}",event);
			consume(notification);
		}

		private void consume(final Notification notification) {
			notification.consume();
			if(this.delay) {
				try {
					TimeUnit.MILLISECONDS.sleep(this.random.nextInt(10));
				} catch (final Exception e) {
				}
			}
		}
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(CollectorAggregatorITest.class);

	@Test
	public void testLifecycle() throws Exception {
		final CustomNotificationListener listener=new CustomNotificationListener(false);
		final CollectorAggregator sut = CollectorAggregator.newInstance("example", listener);
		final CollectorConfiguration collector=defaultCollector();
		sut.connect(Arrays.asList(collector));
		final CollectorController controller=sut.controller(collector.getInstance());
		try {
			for(long i=0;i<1000;i++) {
				final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
				event.setInstance(collector.getInstance());
				event.setTimestamp(i);
				controller.publishEvent(event);
			}
			TimeUnit.SECONDS.sleep(5);
		} finally {
			sut.disconnect();
		}
	}

}
