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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ProjectContentPublisherTaskTest {

	@Mocked private BackendController controller;
	@Mocked private ApplicationContext context;
	@Mocked private WriteSession session;
	@Mocked private PublisherHelper helper;

	private ProjectContentPublisherTask sut;

	@Before
	public void setUp() {
		this.sut=new ProjectContentPublisherTask(this.controller);
	}

	@Test
	public void testProcessFailure() throws Exception {
		final URI target=URI.create("target");
		final List<String> projects = Arrays.asList("1","2");
		new Expectations() {{
			ProjectContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			ProjectContentPublisherTaskTest.this.controller.getProjects();this.result=projects;
			ApplicationContext.getInstance();this.result=ProjectContentPublisherTaskTest.this.context;
			ProjectContentPublisherTaskTest.this.context.createSession();this.result=ProjectContentPublisherTaskTest.this.session;
			ProjectContentPublisherTaskTest.this.controller.getProject("1");this.result=new IOException("Failure");
			PublisherHelper.closeGracefully(ProjectContentPublisherTaskTest.this.session);
		}};
		this.sut.call();
	}

	@Test
	public void testProcessError() throws Exception {
		final URI target=URI.create("target");
		final List<String> projects = Arrays.asList("1","2");
		new Expectations() {{
			ProjectContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			ProjectContentPublisherTaskTest.this.controller.getProjects();this.result=projects;
			ApplicationContext.getInstance();this.result=ProjectContentPublisherTaskTest.this.context;
			ProjectContentPublisherTaskTest.this.context.createSession();this.result=ProjectContentPublisherTaskTest.this.session;
			ProjectContentPublisherTaskTest.this.controller.getProject("1");this.result=new Error("Failure");
			PublisherHelper.closeGracefully(ProjectContentPublisherTaskTest.this.session);
		}};
		try {
			this.sut.call();
			fail("Should fail on error");
		} catch (final Error e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testProcessOk(@Mocked final Project project) throws Exception {
		final URI target=URI.create("target");
		final List<String> projects = Arrays.asList("1","2");
		new Expectations() {{
			ProjectContentPublisherTaskTest.this.controller.getTarget();this.result=target;
			ProjectContentPublisherTaskTest.this.controller.getProjects();this.result=projects;
			ApplicationContext.getInstance();this.result=ProjectContentPublisherTaskTest.this.context;this.times=2;
			ProjectContentPublisherTaskTest.this.context.createSession();this.result=ProjectContentPublisherTaskTest.this.session;this.times=2;
			ProjectContentPublisherTaskTest.this.controller.getProject("1");this.result=project;this.times=1;
			ProjectContentPublisherTaskTest.this.controller.getProject("2");this.result=project;this.times=1;
			PublisherHelper.publishProject(ProjectContentPublisherTaskTest.this.session, target, project);this.times=2;
			ProjectContentPublisherTaskTest.this.session.saveChanges();this.times=2;
			PublisherHelper.closeGracefully(ProjectContentPublisherTaskTest.this.session);this.times=2;
		}};
		this.sut.call();
	}

}
