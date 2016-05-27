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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

final class NotificationConsumer extends DefaultConsumer {

	private interface NotificationHandler {

		boolean canHandle(String routingKey);

		SuspendedNotification suspend(AcknowledgeableNotification notification, String payload) throws IOException;

	}

	protected abstract static class CustomSuspendedNotification<T extends Event> implements SuspendedNotification {

		private final AcknowledgeableNotification notification;
		private final T event;

		protected CustomSuspendedNotification(final AcknowledgeableNotification notification, final T event) {
			this.notification = notification;
			this.event = event;
		}

		protected final T getEvent() {
			return this.event;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void consume() {
			this.notification.consume();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void discard(final Throwable exception) {
			this.notification.discard(exception);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void resume(final NotificationListener listener) {
			try {
				doResume(listener);
			} finally {
				enforceAcknowledgement();
			}
		}

		private void enforceAcknowledgement() {
			if(!this.notification.isAcknowledged()) {
				this.notification.acknowledge();
			}
		}

		protected abstract void doResume(NotificationListener listener);

	}

	private abstract static class CustomNotificationHandler<T extends Event> implements NotificationHandler {

		private final String acceptedKey;
		private final Class<? extends T> clazz;

		protected CustomNotificationHandler(final Class<? extends T> clazz) {
			this.clazz = clazz;
			this.acceptedKey=Notifications.routingKey(clazz);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean canHandle(final String routingKey) {
			return this.acceptedKey.equals(routingKey);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final SuspendedNotification suspend(final AcknowledgeableNotification notification, final String payload) throws IOException {
			return
				createPropagator(
					notification,
					EventUtil.unmarshall(payload, this.clazz));
		}

		protected abstract CustomSuspendedNotification<T> createPropagator(AcknowledgeableNotification notification, T event);

	}

	private static final class ContributorCreatedNotificationHandler extends CustomNotificationHandler<ContributorCreatedEvent> {

		protected ContributorCreatedNotificationHandler() {
			super(ContributorCreatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<ContributorCreatedEvent> createPropagator(final AcknowledgeableNotification notification, final ContributorCreatedEvent event) {
			return new CustomSuspendedNotification<ContributorCreatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onContributorCreation(this, super.getEvent());
				}
			};
		}

	}

	private static final class ContributorDeletedNotificationHandler extends CustomNotificationHandler<ContributorDeletedEvent> {

		protected ContributorDeletedNotificationHandler() {
			super(ContributorDeletedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<ContributorDeletedEvent> createPropagator(final AcknowledgeableNotification notification, final ContributorDeletedEvent event) {
			return new CustomSuspendedNotification<ContributorDeletedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onContributorDeletion(this,super.getEvent());
				}
			};
		}

	}
	private static final class ProjectCreatedNotificationHandler extends CustomNotificationHandler<ProjectCreatedEvent> {

		protected ProjectCreatedNotificationHandler() {
			super(ProjectCreatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<ProjectCreatedEvent> createPropagator(final AcknowledgeableNotification notification, final ProjectCreatedEvent event) {
			return new CustomSuspendedNotification<ProjectCreatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onProjectCreation(this,super.getEvent());
				}
			};
		}
	}

	private static final class ProjectDeletedNotificationHandler extends CustomNotificationHandler<ProjectDeletedEvent> {

		protected ProjectDeletedNotificationHandler() {
			super(ProjectDeletedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<ProjectDeletedEvent> createPropagator(final AcknowledgeableNotification notification, final ProjectDeletedEvent event) {
			return new CustomSuspendedNotification<ProjectDeletedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onProjectDeletion(this,super.getEvent());
				}
			};
		}
	}

	private static final class ProjectUpdatedNotificationHandler extends CustomNotificationHandler<ProjectUpdatedEvent> {

		protected ProjectUpdatedNotificationHandler() {
			super(ProjectUpdatedEvent.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected CustomSuspendedNotification<ProjectUpdatedEvent> createPropagator(final AcknowledgeableNotification notification, final ProjectUpdatedEvent event) {
			return new CustomSuspendedNotification<ProjectUpdatedEvent>(notification, event) {
				@Override
				protected void doResume(final NotificationListener listener) {
					listener.onProjectUpdate(this,super.getEvent());
				}
			};
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationConsumer.class);

	private final BlockingQueue<SuspendedNotification> notifications;
	private final List<NotificationHandler> handlers;

	NotificationConsumer(final Channel channel, final BlockingQueue<SuspendedNotification> pendingNotifications) {
		super(channel);
		this.notifications = pendingNotifications;
		this.handlers=
			ImmutableList.
				<NotificationHandler>builder().
					add(new ContributorCreatedNotificationHandler()).
					add(new ContributorDeletedNotificationHandler()).
					add(new ProjectCreatedNotificationHandler()).
					add(new ProjectDeletedNotificationHandler()).
					add(new ProjectUpdatedNotificationHandler()).
					build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties properties, final byte[] body) throws IOException {
		final String payload=new String(body, "UTF-8");
		final String routingKey=envelope.getRoutingKey();
		final AcknowledgeableNotification notification=
			new AcknowledgeableNotification(
				super.getChannel(),
				envelope.getDeliveryTag());
		try {
			verifyHeader(properties);
			this.notifications.
				offer(
					findHandler(routingKey).
						suspend(notification,payload));
		} catch(final Exception e) {
			LOGGER.error("Discarding message:\n{}\nReason:\n",payload,e);
			notification.acknowledge();
		}
	}

	private NotificationHandler findHandler(final String routingKey) {
		for(final NotificationHandler handler:this.handlers) {
			if(handler.canHandle(routingKey)) {
				return handler;
			}
		}
		throw new IllegalStateException("Unsupported routing key "+routingKey);
	}

	private void verifyHeader(final BasicProperties properties) throws IOException {
		final Object header = properties.getHeaders().get(HttpHeaders.CONTENT_TYPE);
		checkNotNull(header,"No %s header defined",HttpHeaders.CONTENT_TYPE);
		checkArgument(Notifications.MIME.equals(header.toString()),"Unsupported %s header (%s)",HttpHeaders.CONTENT_TYPE,header);
	}

}