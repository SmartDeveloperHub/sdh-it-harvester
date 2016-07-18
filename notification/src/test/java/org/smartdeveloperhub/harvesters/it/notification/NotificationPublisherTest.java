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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class NotificationPublisherTest  extends NotificationTestHelper {

	@Test
	public void controllerConfigurationCannotBeNull() throws Exception {
		try {
			NotificationPublisher.newInstance(null);
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Collector configuration cannot be null"));
		}
	}

	@Test
	public void cannotStartPublisherIfConnectorFailsToConnect() throws Exception {
		final CollectorConfiguration defaultCollector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final CollectorConfiguration aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(defaultCollector));
			}
			@Mock(invocations=1)
			void connect() throws ControllerException {
				throw new ControllerException("brokerHost", 12345, "virtualHost", "message", null);
			}
			@Mock(invocations=0)
			void disconnect() { }
		};
		final NotificationPublisher sut = NotificationPublisher.newInstance(defaultCollector);
		try {
			sut.start();
			fail("Should not be able to start if we are not able to connect");
		} catch (final IOException e) {
			assertThat(e.getMessage(),startsWith("Could not start publisher using "));
			assertThat(e.getCause(),instanceOf(ControllerException.class));
			final ControllerException c=(ControllerException) e.getCause();
			assertThat(c.getMessage(),equalTo("message"));
			assertThat(c.getBrokerHost(),equalTo("brokerHost"));
			assertThat(c.getBrokerPort(),equalTo(12345));
			assertThat(c.getVirtualHost(),equalTo("virtualHost"));
		}
	}

	@Test
	public void connectsControllerOnStart() throws Exception {
		final CollectorConfiguration defaultCollector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final CollectorConfiguration aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(defaultCollector));
			}
			@Mock(invocations=1)
			void connect() { }
		};
		final NotificationPublisher sut = NotificationPublisher.newInstance(defaultCollector);
		sut.start();
	}

	@Test
	public void disconnectsControllerOnShutdown() throws Exception {
		final CollectorConfiguration defaultCollector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final CollectorConfiguration aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(defaultCollector));
			}
			@Mock(invocations=1)
			void disconnect() { }
		};
		final NotificationPublisher sut = NotificationPublisher.newInstance(defaultCollector);
		sut.shutdown();
	}

	@Test
	public void relaysEventsOnPublish() throws Exception {
		final Event event=new ContributorCreatedEvent();
		final CollectorConfiguration defaultCollector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final CollectorConfiguration aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(defaultCollector));
			}
			@Mock(invocations=1)
			void publishEvent(final Event aEvent) {
				assertThat(aEvent,sameInstance(event));
			}
		};
		final NotificationPublisher sut = NotificationPublisher.newInstance(defaultCollector);
		sut.publish(event);
	}

	@Test
	public void publicationFailsIfRelayFails() throws Exception {
		final Event event=new ContributorCreatedEvent();
		final CollectorConfiguration defaultCollector = defaultCollector();
		new MockUp<CollectorController>() {
			@Mock(invocations=1)
			void $init(final Invocation invocation,final CollectorConfiguration aCollector, final String aName, final BlockingQueue<SuspendedNotification> aQueue) {
				assertThat(aCollector,sameInstance(defaultCollector));
			}
			@Mock(invocations=1)
			void publishEvent(final Event aEvent) throws ControllerException {
				assertThat(aEvent,sameInstance(event));
				throw new ControllerException("brokerHost", 12345, "virtualHost", "message", null);
			}
		};
		final NotificationPublisher sut = NotificationPublisher.newInstance(defaultCollector);
		try {
			sut.publish(event);
			fail("Should not be able to publish if we are not able to relay the message");
		} catch (final IOException e) {
			assertThat(e.getMessage(),startsWith("Could not publish event"));
			assertThat(e.getCause(),instanceOf(ControllerException.class));
			final ControllerException c=(ControllerException) e.getCause();
			assertThat(c.getMessage(),equalTo("message"));
			assertThat(c.getBrokerHost(),equalTo("brokerHost"));
			assertThat(c.getBrokerPort(),equalTo(12345));
			assertThat(c.getVirtualHost(),equalTo("virtualHost"));
		}

	}

}
