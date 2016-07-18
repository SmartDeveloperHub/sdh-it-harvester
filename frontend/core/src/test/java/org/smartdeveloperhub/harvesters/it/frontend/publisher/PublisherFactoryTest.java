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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Fixture;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PublisherFactoryTest {

	@Test
	public void verifyIsValidUtilityClass() {
		assertThat(Utils.isUtilityClass(PublisherFactory.class),equalTo(true));
	}

	@Test
	public void failsIfCannotConnect(@Mocked final BackendController controller) throws IOException {
		new Expectations() {{
			controller.getCollector();this.result=new IOException("failure");
		}};
		try {
			PublisherFactory.createPublisher(controller);
			fail("Should fail creation if the controller fails");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("failure"));
		}
	}

	@Test
	public void createsDynamicPublisherIfCollectorDefinesNotifications(@Mocked final BackendController controller) throws Exception {
		final Collector collector = Fixture.defaultCollector();
		final URI target = URI.create("target");
		new Expectations() {{
			controller.getCollector();this.result=collector;
			controller.getTarget();this.result=target;
		}};
		new MockUp<DynamicPublisher>() {
			@Mock
			void $init(final BackendController aController, final List<CollectorConfiguration> configurations) {
				assertThat(configurations,hasSize(1));
				final CollectorConfiguration aConfig=configurations.get(0);
				assertThat(aController,sameInstance(controller));
				assertThat(aConfig.getInstance(),equalTo(target.toString()));
				assertThat(aConfig.getBrokerHost(),equalTo(collector.getNotifications().getBrokerHost()));
				assertThat(aConfig.getBrokerPort(),equalTo(collector.getNotifications().getBrokerPort()));
				assertThat(aConfig.getVirtualHost(),equalTo(collector.getNotifications().getVirtualHost()));
				assertThat(aConfig.getExchangeName(),equalTo(collector.getNotifications().getExchangeName()));
			}
		};
		final Publisher created = PublisherFactory.createPublisher(controller);
		assertThat(created,notNullValue());
	}

	@Test
	public void createsDynamicPublisherIfCollectorDoesNotDefineNotifications(@Mocked final BackendController controller, @Mocked final Collector collector) throws Exception {
		final URI target = URI.create("target");
		new Expectations() {{
			controller.getCollector();this.result=collector;
			collector.getNotifications();this.result=null;
			controller.getTarget();this.result=target;
		}};
		new MockUp<DynamicPublisher>() {
			@Mock
			void $init(final BackendController aController, final List<CollectorConfiguration> configurations) {
				assertThat(configurations,hasSize(0));
			}
		};
		final Publisher created = PublisherFactory.createPublisher(controller);
		assertThat(created,notNullValue());
	}

}
