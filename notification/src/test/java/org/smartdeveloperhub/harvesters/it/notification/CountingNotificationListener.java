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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

final class CountingNotificationListener implements NotificationListener {

	private static final Logger LOGGER=LoggerFactory.getLogger(CountingNotificationListener.class);

	private final ConcurrentMap<String,EventCounter> notifications;
	private final CountDownLatch expectedNotifications;
	private final Random random;

	CountingNotificationListener(final CountDownLatch expectedNotifications) {
		this.expectedNotifications = expectedNotifications;
		this.notifications=Maps.newConcurrentMap();
		this.random=new Random(System.nanoTime());
	}

	private void processEvent(final Notification notification, final Event event) {
		sleep();
		incrementCounter(event);
		notification.consume();
		this.expectedNotifications.countDown();
		LOGGER.info("Consumed event {{}}{{}}{}",event.getInstance(),new Date(event.getTimestamp()),event.getClass().getSimpleName());
	}

	private void incrementCounter(final Event event) {
		final String instance = event.getInstance();
		final EventCounter transientCounter = new EventCounter(instance);
		EventCounter cachedCounter = this.notifications.putIfAbsent(instance,transientCounter);
		if(cachedCounter==null) {
			cachedCounter=transientCounter;
		}
		cachedCounter.count(event);
	}

	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(this.random.nextInt(100));
		} catch (final InterruptedException e) {
		}
	}

	@Override
	public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onContributorDeletion(final Notification notification, final ContributorDeletedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onCommitCreation(final Notification notification, final CommitCreatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onCommitDeletion(final Notification notification, final CommitDeletedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onProjectCreation(final Notification notification, final ProjectCreatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onProjectDeletion(final Notification notification, final ProjectDeletedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public void onProjectUpdate(final Notification notification, final ProjectUpdatedEvent event) {
		processEvent(notification,event);
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					add("notifications",this.notifications).
					toString();
	}

	List<String> events(final String instance) {
		return this.notifications.get(instance).events();
	}

	int eventCount(final String instance, final String event) {
		return this.notifications.get(instance).count(event);
	}

}