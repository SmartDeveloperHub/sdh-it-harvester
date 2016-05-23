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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.notification.external.Collector;
import org.smartdeveloperhub.harvesters.it.notification.external.Enhancer;
import org.smartdeveloperhub.harvesters.it.notification.external.EnhancerController;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

/**
 * TODO: Update intialization logic
 */
@RunWith(JMockit.class)
public class NotificationManagerTest {

	private static final String ENDPOINT = "http://www.example.org:5000/api";
	@Mocked NotificationListener listener;

	/**
	 * TODO: Update intialization logic
	 */
	@Test
	public void testConnectionFailsIfCannotDiscoverTheEnhancerConfiguration() throws Exception {
		new MockUp<EnhancerController>() {
			@Mock(invocations=1)
			void $init(final String target) {
				assertThat(target,equalTo(ENDPOINT));
			}
			@Mock(invocations=1)
			Enhancer getEnhancer() throws IOException {
				throw new IOException("Failure");
			}
		};
		final NotificationManager sut = NotificationManager.newInstance(URI.create(ENDPOINT), this.listener);
		try {
			sut.start();
			fail("Should not be able to connect if we cannot discover the enhancer");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	/**
	 * TODO: Update intialization logic
	 */
	@Test
	public void testConnectionFailsIfAggregatorFails() throws Exception {
		new MockUp<EnhancerController>() {
			@Mock(invocations=1)
			void $init(final String target) {
				assertThat(target,equalTo(ENDPOINT));
			}
			@Mock(invocations=1)
			Enhancer getEnhancer() throws IOException {
				return new Enhancer();
			}
		};
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void connect(final List<Collector> collectors) throws ControllerException {
				throw new ControllerException("brokerHost", 12345, "virtualHost", "message", null);
			}
		};
		final NotificationManager sut = NotificationManager.newInstance(URI.create(ENDPOINT), this.listener);
		try {
			sut.start();
			fail("Should not be able to connect if we cannot discover the enhancer");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not connect to collectors of "+ENDPOINT));
			assertThat(e.getCause(),instanceOf(ControllerException.class));
			final ControllerException c=(ControllerException) e.getCause();
			assertThat(c.getMessage(),equalTo("message"));
			assertThat(c.getBrokerHost(),equalTo("brokerHost"));
			assertThat(c.getBrokerPort(),equalTo(12345));
			assertThat(c.getVirtualHost(),equalTo("virtualHost"));
		}
	}

	/**
	 * TODO: Update intialization logic
	 */
	@Test
	public void testDisconnectWorksConnected() throws Exception {
		new MockUp<EnhancerController>() {
			@Mock(invocations=1)
			void $init(final String target) {
				assertThat(target,equalTo(ENDPOINT));
			}
			@Mock(invocations=1)
			Enhancer getEnhancer() throws IOException {
				return new Enhancer();
			}
		};
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void connect(final List<Collector> collectors) throws ControllerException {
			}
			@Mock(invocations=1)
			void disconnect() {}
		};
		final NotificationManager sut = NotificationManager.newInstance(URI.create(ENDPOINT), this.listener);
		sut.start();
		sut.shutdown();
	}

	/**
	 * TODO: Update intialization logic
	 */
	@Test
	public void testDisconnectWorksDisconnected() throws Exception {
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void disconnect() {}
		};
		final NotificationManager sut = NotificationManager.newInstance(URI.create(ENDPOINT), this.listener);
		sut.shutdown();
	}

}
