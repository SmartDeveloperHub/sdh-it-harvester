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
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.session.AttachmentSnapshot;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.NotificationListener;
import org.smartdeveloperhub.harvesters.it.notification.NotificationManager;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class DynamicPublisherTest {

	private static final String ENDPOINT = "http://www.example.org:5000/api";
	private static final URI JIRA_COLLECTOR = URI.create(ENDPOINT);

	private static final Logger LOGGER=LoggerFactory.getLogger(DynamicPublisher.class);

	private final Random random = new Random(System.nanoTime());
	private CollectorConfiguration configuration;
	private final List<CollectorConfiguration> configurations=Lists.newArrayList();

	@Rule
	public TestName name=new TestName();

	private Project defaultGetProject(final String repoId) {
		final Project repo = createProject(repoId);
		try {
			TimeUnit.MILLISECONDS.sleep(100+this.random.nextInt(500));
		} catch (final InterruptedException e) {
		}
		return repo;
	}

	private List<String> defaultGetProjects() {
		return Lists.newArrayList("1","2","3","4","5","6","7","8","9","10");
	}

	private List<String> defaultGetContributors() {
		return Lists.newArrayList("contributor1","contributor2");
	}

	private List<String> defaultGetCommits() {
		return Lists.newArrayList("commit1","commit2");
	}

	private Project createProject(final String projectId) {
		final Project project = new Project();
		project.setId(projectId);
		project.setName(this.name.getMethodName());
		project.getComponents().addAll(createIdentity(projectId,"component",1+this.random.nextInt(7)));
		project.getVersions().addAll(createIdentity(projectId,"version",1+this.random.nextInt(7)));
		project.getIssues().addAll(createIdentity(projectId,"issues",5+this.random.nextInt(50)));
		return project;
	}

	private List<String> createIdentity(final String repoId, final String tag, final int count) {
		final List<String> values=new ArrayList<>();
		for(int j=0;j<count;j++) {
			values.add("r_"+repoId+"_"+tag+"_"+j);

		}
		return values;
	}

	@Before
	public void setUp() {
		this.configuration=new CollectorConfiguration();
		this.configuration.setInstance(ENDPOINT);
		this.configuration.setBrokerHost("localhost");
		this.configuration.setExchangeName("test");
		this.configuration.setVirtualHost("/");

	}

	@Test
	public void testInitialize(@Mocked final PublisherHelper helper, @Mocked final WriteSession session, @Mocked final BackendController controller) throws Exception {
		final DynamicPublisher sut = new DynamicPublisher(controller,this.configurations);
		final List<String> projects=Arrays.asList("1","2");
		new Expectations() {{
			controller.getTarget();this.result=JIRA_COLLECTOR;
			controller.getProjects();this.result=projects;
			PublisherHelper.publishHarvester(session, JIRA_COLLECTOR, projects);
		}};
		sut.initialize(session);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle(
			@Mocked final ApplicationContext context,
			@Mocked final WriteSession session,
			@Mocked final ContainerSnapshot snapshot,
			@Mocked final AttachmentSnapshot attachmentSnapshot) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
				@Mock
				List<String> getContributors() {
					return defaultGetContributors();
				}
				@Mock
				List<String> getCommits() {
					return defaultGetCommits();
				}
				@Mock
				Project getProject(final String repoId) {
					return defaultGetProject(repoId);
				}
				@Mock
				List<String> getProjects() {
					return defaultGetProjects();
				}
			}.
			getMockInstance();
		new Expectations() {{
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			session.find(ContainerSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			session.find(ResourceSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.addMember((Name<?>)this.any);this.result=snapshot;
			snapshot.attachmentById((String)this.any);this.result=attachmentSnapshot;
			attachmentSnapshot.resource();this.result=snapshot;
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		sut.start();
		try {
			sut.awaitPublicationCompletion();
			LOGGER.info("Detected publication completion...");
		} finally {
			sut.stop();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle$stopBeforePublicationCompletion(
			@Mocked final ApplicationContext context,
			@Mocked final WriteSession session,
			@Mocked final ContainerSnapshot snapshot,
			@Mocked final AttachmentSnapshot attachmentSnapshot) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
				@Mock
				List<String> getContributors() {
					return defaultGetContributors();
				}
				@Mock
				List<String> getCommits() {
					return defaultGetCommits();
				}
				@Mock
				Project getProject(final String repoId) {
					return defaultGetProject(repoId);
				}
				@Mock
				List<String> getProjects() {
					return defaultGetProjects();
				}
			}.
			getMockInstance();
		new NonStrictExpectations() {{
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			session.find(ContainerSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			session.find(ResourceSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.addMember((Name<?>)this.any);this.result=snapshot;
			snapshot.attachmentById((String)this.any);this.result=attachmentSnapshot;
			attachmentSnapshot.resource();this.result=snapshot;
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		sut.start();
		final Thread thread=new Thread() {
			@Override
			public void run() {
				sut.stop();
			};
		};
		thread.start();
		thread.interrupt();
		thread.join();
		final ListeningExecutorService pool = Deencapsulation.getField(sut, ListeningExecutorService.class);
		while(!pool.isTerminated()) {
			LOGGER.debug("Awaiting internal pool termination");
			try {
				pool.awaitTermination(250,TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
			}
		}
		LOGGER.debug("Internal pool terminated");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle$failContributorPublication(
			@Mocked final ApplicationContext context,
			@Mocked final WriteSession session,
			@Mocked final ContainerSnapshot snapshot,
			@Mocked final AttachmentSnapshot attachmentSnapshot) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
				@Mock
				List<String> getContributors() throws IOException {
					throw new IOException("Failure");
				}
				@Mock
				List<String> getCommits() {
					return defaultGetCommits();
				}
				@Mock
				Project getProject(final String repoId) {
					return defaultGetProject(repoId);
				}
				@Mock
				List<String> getProjects() {
					return defaultGetProjects();
				}
			}.
			getMockInstance();
		new Expectations() {{
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			session.find(ResourceSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.addMember((Name<?>)this.any);this.result=snapshot;
			snapshot.attachmentById((String)this.any);this.result=attachmentSnapshot;
			attachmentSnapshot.resource();this.result=snapshot;
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		sut.start();
		try {
			sut.awaitPublicationCompletion();
			LOGGER.info("Detected publication completion...");
		} finally {
			sut.stop();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle$errorContributorPublication(
			@Mocked final ApplicationContext context,
			@Mocked final WriteSession session,
			@Mocked final ContainerSnapshot snapshot,
			@Mocked final AttachmentSnapshot attachmentSnapshot) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
				@Mock
				List<String> getContributors() throws IOException {
					throw new Error("Failure");
				}
				@Mock
				List<String> getCommits() {
					return defaultGetCommits();
				}
				@Mock
				Project getProject(final String repoId) {
					return defaultGetProject(repoId);
				}
				@Mock
				List<String> getProjects() {
					return defaultGetProjects();
				}
			}.
			getMockInstance();
		new Expectations() {{
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			session.find(ResourceSnapshot.class, (Name<?>)this.any, (Class<? extends ResourceHandler>)this.any);this.result=snapshot;
			snapshot.addMember((Name<?>)this.any);this.result=snapshot;
			snapshot.attachmentById((String)this.any);this.result=attachmentSnapshot;
			attachmentSnapshot.resource();this.result=snapshot;
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		sut.start();
		try {
			sut.awaitPublicationCompletion();
			LOGGER.info("Detected publication completion...");
		} finally {
			sut.stop();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle$notificationManagerFail(@Mocked final NotificationManager manager) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
			}.
			getMockInstance();
		new Expectations() {{
			NotificationManager.newInstance((List<CollectorConfiguration>)this.any, (NotificationListener)this.any);this.result=manager;
			manager.start();this.result=new IOException("Failure");
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		sut.start();
		new Verifications() {{
			List<CollectorConfiguration> collectors;
			NotificationManager.newInstance(collectors=withCapture(), (NotificationListener)this.any);
			assertThat(collectors,sameInstance(DynamicPublisherTest.this.configurations));
		}};
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLifecycle$unexpectedRuntimeExceptionOnStart(@Mocked final NotificationManager manager) throws Exception {
		final BackendController controller=
			new MockUp<BackendController>() {
				@Mock
				URI getTarget() {
					return JIRA_COLLECTOR;
				}
			}.
			getMockInstance();
		new Expectations() {{
			NotificationManager.newInstance((List<CollectorConfiguration>)this.any, (NotificationListener)this.any);this.result=manager;
			manager.start();this.result=new RuntimeException("Failure");
		}};
		final DynamicPublisher sut=new DynamicPublisher(controller,this.configurations);
		try {
			sut.start();
			fail("Should not start if something prevents start-up");
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
		new Verifications() {{
			List<CollectorConfiguration> collectors;
			NotificationManager.newInstance(collectors=withCapture(), (NotificationListener)this.any);
			assertThat(collectors,sameInstance(DynamicPublisherTest.this.configurations));
		}};
	}

}
