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
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.ApplicationContextException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.smartdeveloperhub.harvesters.it.notification.Notification;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PublishingNotificationListenerTest {

	private static final URI TARGET=URI.create("target");

	@Mocked private Notification notification;

	private PublishingNotificationListener sut;

	@Test
	public void testOnContributorCreation(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ContributorCreatedEvent event = new ContributorCreatedEvent();
		event.setInstance(TARGET.toString());
		final List<String> contributors = Arrays.asList("contributor1","contributor2");
		event.setNewContributors(contributors);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.publishContributors(session, TARGET, contributors);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onContributorCreation(this.notification, event);
	}

	@Test
	public void testOnContributorDeletion(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ContributorDeletedEvent event = new ContributorDeletedEvent();
		event.setInstance(TARGET.toString());
		final List<String> contributors = Arrays.asList("contributor1","contributor2");
		event.setDeletedContributors(contributors);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.unpublishContributors(session,contributors);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onContributorDeletion(this.notification, event);
	}

	@Test
	public void testOnCommitCreation(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final CommitCreatedEvent event = new CommitCreatedEvent();
		event.setInstance(TARGET.toString());
		final List<String> commits = Arrays.asList("commit1","commit2");
		event.setNewCommits(commits);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.publishCommits(session, TARGET, commits);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onCommitCreation(this.notification, event);
	}

	@Test
	public void testOnCommitDeletion(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final CommitDeletedEvent event = new CommitDeletedEvent();
		event.setInstance(TARGET.toString());
		final List<String> commits = Arrays.asList("commit1","commit2");
		event.setDeletedCommits(commits);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.unpublishCommits(session,commits);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onCommitDeletion(this.notification, event);
	}

	@Test
	public void testOnProjectCreation(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectCreatedEvent event = new ProjectCreatedEvent();
		event.setInstance(TARGET.toString());
		final List<String> projects = Arrays.asList("1","2");
		event.setNewProjects(projects);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.publishProjects(session, TARGET, projects);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onProjectCreation(this.notification, event);
	}

	@Test
	public void testOnProjectDeletion(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectDeletedEvent event = new ProjectDeletedEvent();
		event.setInstance(TARGET.toString());
		final List<String> projects = Arrays.asList("1","2");
		event.setDeletedProjects(projects);
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.unpublishProjects(session,projects);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onProjectDeletion(this.notification, event);
	}

	@Test
	public void testOnProjectUpdate(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateProject(session, event);
			session.saveChanges();
			PublishingNotificationListenerTest.this.notification.consume();
		}};
		this.sut.onProjectUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnPublisherHelperFailure(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		final IOException failure = new IOException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateProject(session, event);this.result=failure;
			session.discardChanges();
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onProjectUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnWriteSessionException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		final WriteSessionException failure = new WriteSessionException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateProject(session, event);this.result=failure;
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onProjectUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnApplicationContextException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		final ApplicationContextException failure = new ApplicationContextException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=failure;
			PublishingNotificationListenerTest.this.notification.discard(failure);
		}};
		this.sut.onProjectUpdate(this.notification, event);
	}

	@Test
	public void testProcessingFailsOnInterruptionWhileWaiting(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper) throws Exception {
		final CountDownLatch latch=new CountDownLatch(1);
		this.sut=new PublishingNotificationListener(latch,TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		new Expectations() {{
			ApplicationContext.getInstance();this.times=0;
			PublishingNotificationListenerTest.this.notification.discard((Exception)this.any);
		}};
		final CountDownLatch l=new CountDownLatch(1);
		final Thread t1 = new Thread() {
			@Override
			public void run() {
				l.countDown();
				PublishingNotificationListenerTest.this.sut.onProjectUpdate(PublishingNotificationListenerTest.this.notification,event);
				assertThat(Thread.interrupted(),equalTo(true));
			}

		};
		t1.start();
		final Thread t2 = new Thread() {
			@Override
			public void run() {
				t1.interrupt();
			}
		};
		l.await();
		t2.start();
		t2.join();
		t1.join();
	}

	@Test
	public void testListenerFailsOnUnexpectedException(@Mocked final ApplicationContext context, @Mocked final WriteSession session, @Mocked final PublisherHelper helper, @Mocked final CountDownLatch publishingCompleted) throws Exception {
		this.sut=new PublishingNotificationListener(publishingCompleted, TARGET);
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(TARGET.toString());
		final RuntimeException failure = new RuntimeException("Failure");
		new Expectations() {{
			publishingCompleted.await();
			ApplicationContext.getInstance();this.result=context;
			context.createSession();this.result=session;
			PublisherHelper.updateProject(session, event);this.result=failure;
			PublisherHelper.closeGracefully(session);
		}};
		try {
			this.sut.onProjectUpdate(this.notification, event);
			fail("Should fail on runtime exception");
		} catch (final RuntimeException e) {
			assertThat(e.getMessage(),equalTo("Failure"));
		}
	}

}
