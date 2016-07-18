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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;

import com.google.common.base.MoreObjects;

@RunWith(JMockit.class)
public class NotificationPumpTest {

	private final class NaiveSuspendedNotification implements SuspendedNotification {
		boolean resumed=false;
		private final String id;
		private NaiveSuspendedNotification(final String id) {
			this.id = id;
		}
		@Override
		public void discard(final Throwable exception) {}
		@Override
		public void consume() {}
		@Override
		public void resume(final NotificationListener listener) {
			LOGGER.info("Resuming notification {}...",this.id);
			this.resumed=true;
			listener.onContributorCreation(this, null);
		}
		boolean isResumed() {
			return this.resumed;
		}
		@Override
		public String toString() {
			return
				MoreObjects.
					toStringHelper(getClass()).
						omitNullValues().
						add("id",this.id).
						add("resumed",this.resumed).
						toString();
		}
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationPumpTest.class);

	private BlockingQueue<SuspendedNotification> queue;
	private NaiveSuspendedNotification n1;
	private NaiveSuspendedNotification n2;

	@Before
	public void setUp() {
		this.queue = new LinkedBlockingQueue<>();
		this.n1 = new NaiveSuspendedNotification("n1");
		this.n2 = new NaiveSuspendedNotification("n2");
	}

	@Test
	public void testStartFailsIfAlreadyStarted(@Mocked final NotificationListener listener) throws Exception {
		final NotificationPump sut = new NotificationPump(this.queue, listener);
		sut.start();
		try {
			sut.start();
			fail("Should fail if already started");
		} catch(final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Pump already started"));
		} finally {
			sut.stop();
		}
	}

	@Test
	public void testStopFailsIfNotStarted(@Mocked final NotificationListener listener) throws Exception {
		final NotificationPump sut = new NotificationPump(this.queue, listener);
		try {
			sut.stop();
			fail("Should fail if not started");
		} catch(final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Pump not started"));
		}
	}

	@Test
	public void testPumpStopsWhenNoNotificationsAreAvailable() throws Exception {
		final NotificationPump sut = new NotificationPump(this.queue,new NullNotificationListener());
		sut.start();
		TimeUnit.SECONDS.sleep(5);
		sut.stop();
	}

	@Test
	public void testPumpStopsWhenTheListenerRespectsInterruptions() throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		final NotificationListener listener=
			new NullNotificationListener() {
				@Override
				public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
					LOGGER.info("Received notification {}...",notification);
					latch.countDown();
					try {
						LOGGER.info("Suspending listener...");
						TimeUnit.SECONDS.sleep(5);
					} catch (final InterruptedException e) {
						LOGGER.info("Listener interrupted");
						Thread.currentThread().interrupt();
					}
				}
			};
		queueNotifications();
		final NotificationPump sut = new NotificationPump(this.queue,listener);
		sut.start();
		LOGGER.info("Pump started. Awaiting consumption of first notification...");
		latch.await();
		LOGGER.info("First notification received. Stopping pump...");
		sut.stop();
		verifyQueueAndNotifications();
	}

	@Test
	public void testPumpStopsWhenTheListenerDoesNotRespectInterruptions() throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		final NotificationListener listener=
			new NullNotificationListener() {
				@Override
				public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
					LOGGER.info("Received notification {}...",notification);
					latch.countDown();
					try {
						LOGGER.info("Suspending listener...");
						TimeUnit.SECONDS.sleep(10);
					} catch (final InterruptedException e) {
						LOGGER.info("Listener interrupted. Suspending listener again...");
						try {
							TimeUnit.SECONDS.sleep(10);
						} catch (final InterruptedException e1) {
							LOGGER.info("Listener interrupted again");
						}
					}
				}
			};
		queueNotifications();
		final NotificationPump sut = new NotificationPump(this.queue,listener);
		sut.start();
		LOGGER.info("Pump started. Awaiting consumption of first notification...");
		latch.await();
		LOGGER.info("First notification received. Stopping pump...");
		sut.stop();
		verifyQueueAndNotifications();
	}

	@Test
	public void testPumpStopsWhenTheListenerDoesNotRespectInterruptionsAndPumpsThreadIsIterrupted() throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		final NotificationListener listener=
			new NullNotificationListener() {
				@Override
				public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
					LOGGER.info("Received notification {}...",notification);
					latch.countDown();
					try {
						LOGGER.info("Suspending listener...");
						TimeUnit.SECONDS.sleep(10);
					} catch (final InterruptedException e) {
						LOGGER.info("Listener interrupted. Suspending listener again...");
						try {
							TimeUnit.SECONDS.sleep(10);
						} catch (final InterruptedException e1) {
							LOGGER.info("Listener interrupted again");
						}
					}
				}
			};
		queueNotifications();
		final NotificationPump sut = new NotificationPump(this.queue,listener);
		sut.start();
		LOGGER.info("Pump started. Awaiting consumption of first notification...");
		latch.await();
		LOGGER.info("First notification received. Stopping pump...");
		final Thread thread = new Thread(
			new Runnable() {
				@Override
				public void run() {
					sut.stop();
				}
			},"PumpStopper");
		thread.start();
		thread.interrupt();
		thread.join();
		verifyQueueAndNotifications();
	}

	@Test
	public void testPumpStopsEvenIfListenerMadeWorkerFail() throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		final NotificationListener listener=
			new NullNotificationListener() {
				@Override
				public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
					LOGGER.info("Received notification {}...",notification);
					latch.countDown();
					LOGGER.info("Suspending listener...");
					throw new RuntimeException("Failure");
				}
			};
		queueNotifications();
		final NotificationPump sut = new NotificationPump(this.queue,listener);
		sut.start();
		LOGGER.info("Pump started. Awaiting consumption of first notification...");
		latch.await();
		LOGGER.info("First notification received...");
		TimeUnit.SECONDS.sleep(2);
		LOGGER.info("Stopping pump...");
		sut.stop();
		verifyQueueAndNotifications();
	}

	private void queueNotifications() {
		this.queue.offer(this.n1);
		this.queue.offer(this.n2);
	}

	private void verifyQueueAndNotifications() {
		LOGGER.info("Pump stopped. Checking notification and queue status...");
		assertThat(this.queue,hasSize(1));
		assertThat(this.n1.isResumed(),equalTo(true));
		assertThat(this.n2.isResumed(),equalTo(false));
	}

}
