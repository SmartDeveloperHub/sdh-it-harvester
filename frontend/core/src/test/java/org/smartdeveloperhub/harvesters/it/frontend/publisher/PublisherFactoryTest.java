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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

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
	public void createsDynamicPublisherIfCanConnect(@Mocked final BackendController controller) throws Exception {
		final Collector collector = Fixture.defaultCollector();
		final URI target = URI.create("target");
		new Expectations() {{
			controller.getCollector();this.result=collector;
			controller.getTarget();this.result=target;
		}};
		new MockUp<DynamicPublisher>() {
			@Mock
			void $init(final BackendController aController, final CollectorConfiguration config) {
				assertThat(aController,sameInstance(controller));
				assertThat(config.getInstance(),equalTo(target.toString()));
				assertThat(config.getBrokerHost(),equalTo(collector.getNotifications().getBrokerHost()));
				assertThat(config.getBrokerPort(),equalTo(collector.getNotifications().getBrokerPort()));
				assertThat(config.getVirtualHost(),equalTo(collector.getNotifications().getVirtualHost()));
				assertThat(config.getExchangeName(),equalTo(collector.getNotifications().getExchangeName()));
			}
		};
		final Publisher created = PublisherFactory.createPublisher(controller);
		assertThat(created,notNullValue());
	}

}
