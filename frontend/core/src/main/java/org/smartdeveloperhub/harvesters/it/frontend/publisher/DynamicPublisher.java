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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.NotificationManager;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

final class DynamicPublisher implements Publisher {

	private final class PublicationTerminationHandler implements FutureCallback<Boolean> {
		@Override
		public void onSuccess(final Boolean result) {
			DynamicPublisher.this.publishingCompleted.countDown();
			if(result) {
				LOGGER.info("Initial publication completed. Started notification handling...");
			} else {
				DynamicPublisher.this.manager.shutdown();
				LOGGER.warn("Initial publication failed. Notification handling aborted.");
			}
		}

		@Override
		public void onFailure(final Throwable t) {
			DynamicPublisher.this.publishingCompleted.countDown();
			LOGGER.error("Publication failed", t);
		}
	}

	private final class PublicationTerminationAggregator implements Function<List<Boolean>, Boolean> {
		@Override
		public Boolean apply(final List<Boolean> terminations) {
			for(final boolean completed:terminations) {
				if(!completed) {
					return false;
				}
			}
			return true;
		}
	}

	private static final Logger LOGGER=LoggerFactory.getLogger(DynamicPublisher.class);

	private final ListeningExecutorService pool;

	private final NotificationManager manager;

	private final CountDownLatch publishingCompleted;

	private final BackendController controller;

	DynamicPublisher(final BackendController controller, final CollectorConfiguration config) {
		this.controller = controller;
		this.pool =
			MoreExecutors.
				listeningDecorator(
					Executors.
						newFixedThreadPool(
							10,
							new ThreadFactoryBuilder().
								setNameFormat("Publisher-thread-%d").
								build()
							)
				);
		this.publishingCompleted = new CountDownLatch(1);
		this.manager=
			NotificationManager.
				newInstance(
					ImmutableList.of(config),
					new PublishingNotificationListener(
						this.publishingCompleted,
						controller.getTarget()));
	}

	void awaitPublicationCompletion() throws InterruptedException {
		this.publishingCompleted.await();
	}

	@Override
	public void initialize(final WriteSession session) throws IOException {
		LOGGER.info("Publishing IT Harvester Resource...");
		PublisherHelper.
			publishHarvester(
				session,
				this.controller.getTarget(),
				this.controller.getProjects());
		LOGGER.info("Published IT Harvester Resource");
	}

	@Override
	public void start() {
		LOGGER.info("Starting publisher...");
		try {
			LOGGER.info("Starting notification manager...");
			this.manager.start();
			LOGGER.info("Started notification manager. Starting initial publication...");

			// Queue the publisher tasks...
			@SuppressWarnings("unchecked")
			final ListenableFuture<List<Boolean>> terminations =
				Futures.<Boolean>
					allAsList(
						submitTask(new ProjectContentPublisherTask(this.controller)),
						submitTask(new ContributorPublisherTask(this.controller)),
						submitTask(new CommitPublisherTask(this.controller)));

			// Combine their result
			final ListenableFuture<Boolean> aggregatedTermination=
				Futures.
					transform(
						terminations,
						new PublicationTerminationAggregator(),
						this.pool);

			// ... and take an action
			Futures.
				addCallback(
					aggregatedTermination,
					new PublicationTerminationHandler(),
					this.pool);
		} catch (final IOException e) {
			LOGGER.error("Could not start notification manager. Full stacktrace follows:",e);
		} finally {
			LOGGER.info("Publisher started.");
		}
	}

	@Override
	public void stop() {
		LOGGER.info("Finishing publisher...");
		shutdownPool();
		this.manager.shutdown();
		LOGGER.info("Publisher finished.");
	}

	private ListenableFuture<Boolean> submitTask(final PublisherTask task) {
		return this.pool.submit(task);
	}

	private void shutdownPool() {
		this.pool.shutdown();
		boolean interrupted=false;
		while(!this.pool.isTerminated() && !interrupted) {
			try {
				this.pool.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				interrupted=true;
			}
		}
	}

}
