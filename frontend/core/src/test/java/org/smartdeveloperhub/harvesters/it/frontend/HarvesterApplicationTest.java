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
package org.smartdeveloperhub.harvesters.it.frontend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ext.ApplicationInitializationException;
import org.ldp4j.application.ext.ApplicationSetupException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.publisher.Publisher;
import org.smartdeveloperhub.harvesters.it.frontend.publisher.PublisherFactory;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionContainerHandler;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class HarvesterApplicationTest {

	@Injectable
	private Publisher publisher;

	@Injectable
	private URI target;

	@Tested
	private HarvesterApplication sut;

	@Test
	public void testShutdown() throws Exception {
		this.sut.shutdown();
	}

	@Test
	public void testSetup$happyPath(@Mocked final Environment environment, @Mocked final Bootstrap<HarvesterConfiguration> bootstrap, @Mocked final HarvesterConfiguration configuration, @Mocked final BackendController controller) throws Exception {
		new MockUp<BackendControllers>() {
			@Mock
			public BackendController createController(final URI aTarget) {
				assertThat(aTarget,equalTo(HarvesterApplicationTest.this.target));
				return controller;
			}
		};
		new MockUp<PublisherFactory>() {
			@Mock
			Publisher createPublisher(final BackendController aController) {
				assertThat(aController,sameInstance(controller));
				return HarvesterApplicationTest.this.publisher;
			}
		};
		new Expectations() {{
			bootstrap.configuration();this.result=configuration;
			configuration.target();this.result=HarvesterApplicationTest.this.target;
			environment.lifecycle().register((Publisher)this.any);this.times=1;
			bootstrap.addHandler((ResourceHandler)this.any);this.times=7;
			bootstrap.addHandlerClass(IssueContainerHandler.class);
			bootstrap.addHandlerClass(VersionContainerHandler.class);
			bootstrap.addHandlerClass(ComponentContainerHandler.class);
			bootstrap.addHandlerClass(ProjectContainerHandler.class);
			bootstrap.addHandlerClass(CommitContainerHandler.class);
			bootstrap.addHandlerClass(ContributorContainerHandler.class);
			environment.publishResource(IdentityUtil.collectorName(HarvesterApplicationTest.this.target), HarvesterHandler.class, "service/");
		}};
		this.sut.setup(environment, bootstrap);
	}

	@Test
	public void testSetup$failure(@Mocked final Environment environment, @Mocked final Bootstrap<HarvesterConfiguration> bootstrap, @Mocked final HarvesterConfiguration configuration, @Mocked final BackendController controller) throws Exception {
		new MockUp<BackendControllers>() {
			@Mock
			public BackendController createController(final URI aTarget) {
				assertThat(aTarget,equalTo(HarvesterApplicationTest.this.target));
				return controller;
			}
		};
		new MockUp<PublisherFactory>() {
			@Mock
			Publisher createPublisher(final BackendController aController) throws IOException {
				throw new IOException("failure");
			}
		};
		new Expectations() {{
			bootstrap.configuration();this.result=configuration;
			configuration.target();this.result=HarvesterApplicationTest.this.target;
		}};
		try {
			this.sut.setup(environment, bootstrap);
			fail("Should not complete if a failure happens");
		} catch (final ApplicationSetupException e) {
			assertThat(e.getMessage(),equalTo("Could not create publisher for target '"+this.target+"'"));
		}
	}

	@Test
	public void testSetup$failure(@Mocked final Environment environment, @Mocked final Bootstrap<HarvesterConfiguration> bootstrap, @Mocked final HarvesterConfiguration configuration) throws Exception {
		new Expectations() {{
			bootstrap.configuration();this.result=configuration;
			configuration.target();this.result=null;
		}};
		try {
			this.sut.setup(environment, bootstrap);
			fail("Should not complete if a failure happens");
		} catch (final ApplicationSetupException e) {
			assertThat(e.getMessage(),equalTo("No target Jira Collector configured"));
		}
	}

	@Test
	public void testInitialize$happyPath(@Mocked final WriteSession session) throws Exception {
		new Expectations() {{
			HarvesterApplicationTest.this.publisher.initialize(session);
			session.saveChanges();
		}};
		this.sut.initialize(session);
	}

	@Test
	public void testInitialize$failure(@Mocked final WriteSession session) throws Exception {
		new Expectations() {{
			HarvesterApplicationTest.this.publisher.initialize(session);
			session.saveChanges();this.result=new WriteSessionException("failure");
		}};
		try {
			this.sut.initialize(session);
			fail("Should not complete if a failure happens");
		} catch (final ApplicationInitializationException e) {
			assertThat(e.getMessage(),equalTo("IT Harvester Application initialization failed"));
			assertThat(e.getCause(),instanceOf(WriteSessionException.class));
			assertThat(e.getCause().getMessage(),equalTo("failure"));
		}
	}

}
