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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class NotificationManagerTest {

	@Mocked NotificationListener listener;
	@Mocked CollectorConfiguration collector1;
	@Mocked CollectorConfiguration collector2;

	private List<CollectorConfiguration> collectors() {
		return Lists.newArrayList(this.collector1,this.collector2);
	}

	@Test
	public void testAcceptEmptyControllerList() throws Exception {
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void connect(final List<CollectorConfiguration> collectors) throws ControllerException {
				assertThat(collectors,hasSize(0));
			}
		};
		final NotificationManager sut=NotificationManager.newInstance(Collections.<CollectorConfiguration>emptyList(),this.listener);
		sut.start();
	}

	@Test
	public void testConnectionFailsIfAggregatorFails() throws Exception {
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void connect(final List<CollectorConfiguration> collectors) throws ControllerException {
				assertThat(collectors,equalTo(collectors()));
				throw new ControllerException("brokerHost", 12345, "virtualHost", "message", null);
			}
		};
		final NotificationManager sut = NotificationManager.newInstance(collectors(),this.listener);
		try {
			sut.start();
			fail("Should not be able to connect if we cannot discover the enhancer");
		} catch (final IOException e) {
			assertThat(e.getMessage(),startsWith("Could not connect to collectors of "));
			assertThat(e.getCause(),instanceOf(ControllerException.class));
			final ControllerException c=(ControllerException) e.getCause();
			assertThat(c.getMessage(),equalTo("message"));
			assertThat(c.getBrokerHost(),equalTo("brokerHost"));
			assertThat(c.getBrokerPort(),equalTo(12345));
			assertThat(c.getVirtualHost(),equalTo("virtualHost"));
		}
	}

	@Test
	public void testDisconnectWorksConnected() throws Exception {
		new MockUp<CollectorAggregator>() {
			@Mock(invocations=1)
			void $init(final String name,final NotificationListener listener) {
				Amqp.validateName(name, "Collector aggregator name");
				assertThat(listener,equalTo(NotificationManagerTest.this.listener));
			}
			@Mock(invocations=1)
			void connect(final List<CollectorConfiguration> collectors) throws ControllerException {
				assertThat(collectors,equalTo(collectors()));
			}
			@Mock(invocations=1)
			void disconnect() {}
		};
		final NotificationManager sut = NotificationManager.newInstance(collectors(), this.listener);
		sut.start();
		sut.shutdown();
	}

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
		final NotificationManager sut = NotificationManager.newInstance(collectors(), this.listener);
		sut.shutdown();
	}

}
