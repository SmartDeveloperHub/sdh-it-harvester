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

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.ApplicationContextException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.session.WriteSessionException;
import org.smartdeveloperhub.harvesters.it.notification.Notification;
import org.smartdeveloperhub.harvesters.it.notification.NotificationListener;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

final class PublishingNotificationListener implements NotificationListener {

	abstract class NotificationHandler<T extends Event> {

		final void consumeEvent(final Notification notification, final T event) {
			WriteSession session=null;
			try {
				session=session();
				doConsumeEvent(notification, event, session);
			} catch(final InterruptedException e) {
				Thread.currentThread().interrupt();
				notification.discard(e);
			} catch(final WriteSessionException | ApplicationContextException e) {
				notification.discard(e);
			} finally {
				PublisherHelper.closeGracefully(session);
			}
		}

		private WriteSession session() throws InterruptedException, ApplicationContextException {
			PublishingNotificationListener.this.publishingCompleted.await();
			return ApplicationContext.getInstance().createSession();
		}

		private void doConsumeEvent(final Notification notification, final T event, final WriteSession session) throws WriteSessionException {
			try {
				handleEvent(event, session);
				notification.consume();
				session.saveChanges();
			} catch (final IOException e) {
				session.discardChanges();
				notification.discard(e);
			}
		}

		protected abstract void handleEvent(final T event, final WriteSession session) throws IOException;

	}

	final class ContributorCreationHandler extends NotificationHandler<ContributorCreatedEvent> {

		@Override
		protected void handleEvent(final ContributorCreatedEvent event, final WriteSession session) {
			PublisherHelper.
				publishContributors(
					session,
					PublishingNotificationListener.this.target,
					event.getNewContributors());
		}

	}

	final class ContributorDeletionHandler extends NotificationHandler<ContributorDeletedEvent> {

		@Override
		protected void handleEvent(final ContributorDeletedEvent event, final WriteSession session) {
			PublisherHelper.unpublishContributors(session,event.getDeletedContributors());
		}

	}

	final class CommitCreationHandler extends NotificationHandler<CommitCreatedEvent> {

		@Override
		protected void handleEvent(final CommitCreatedEvent event, final WriteSession session) {
			PublisherHelper.
				publishCommits(
					session,
					PublishingNotificationListener.this.target,
					event.getNewCommits());
		}

	}

	final class CommitDeletionHandler extends NotificationHandler<CommitDeletedEvent> {

		@Override
		protected void handleEvent(final CommitDeletedEvent event, final WriteSession session) {
			PublisherHelper.unpublishCommits(session, event.getDeletedCommits());
		}

	}

	final class ProjectCreationHandler extends NotificationHandler<ProjectCreatedEvent> {

		@Override
		protected void handleEvent(final ProjectCreatedEvent event, final WriteSession session) {
			PublisherHelper.
				publishProjects(
					session,
					PublishingNotificationListener.this.target,
					event.getNewProjects());
		}

	}

	final class ProjectDeletionHandler extends NotificationHandler<ProjectDeletedEvent> {

		@Override
		protected void handleEvent(final ProjectDeletedEvent event, final WriteSession session) {
			PublisherHelper.unpublishProjects(session,event.getDeletedProjects());
		}

	}

	final class ProjectUpdateHandler extends NotificationHandler<ProjectUpdatedEvent> {

		@Override
		protected void handleEvent(final ProjectUpdatedEvent event, final WriteSession session) throws IOException {
			PublisherHelper.updateProject(session,event);
		}

	}

	private final CountDownLatch publishingCompleted;

	private final URI target;

	PublishingNotificationListener(final CountDownLatch publishingCompleted, final URI target) {
		this.publishingCompleted = publishingCompleted;
		this.target = target;
	}

	@Override
	public void onContributorCreation(final Notification notification, final ContributorCreatedEvent event) {
		new ContributorCreationHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onContributorDeletion(final Notification notification, final ContributorDeletedEvent event) {
		new ContributorDeletionHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onCommitCreation(final Notification notification, final CommitCreatedEvent event) {
		new CommitCreationHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onCommitDeletion(final Notification notification, final CommitDeletedEvent event) {
		new CommitDeletionHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onProjectCreation(final Notification notification, final ProjectCreatedEvent event) {
		new ProjectCreationHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onProjectDeletion(final Notification notification, final ProjectDeletedEvent event) {
		new ProjectDeletionHandler().
			consumeEvent(notification, event);
	}

	@Override
	public void onProjectUpdate(final Notification notification, final ProjectUpdatedEvent event) {
		new ProjectUpdateHandler().
			consumeEvent(notification, event);
	}

}