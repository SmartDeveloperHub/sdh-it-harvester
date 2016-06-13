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

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.Modification;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import mockit.Deencapsulation;

public class NotificationManagerITest {

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationManagerITest.class);

	private ExecutorService pool;

	@Before
	public void setUp() {
		this.pool =
			Executors.
				newFixedThreadPool(
					5,
					new ThreadFactoryBuilder().
						setNameFormat("Collector-Agent-%d").
						setUncaughtExceptionHandler(
							new UncaughtExceptionHandler() {
								@Override
								public void uncaughtException(final Thread t, final Throwable e) {
									LOGGER.error("{} thread died unexpectedly: ",t.getName(),e);
								}
							}
						).
						build());
	}

	@After
	public void tearDown() {
		if(!this.pool.isShutdown()) {
			shutdownPool();
		}
	}

	@Test
	public void testMultipleCollectors() throws IOException {
		final List<CollectorConfiguration> collectors=
			ImmutableList.
				<CollectorConfiguration>builder().
					add(collector("http://www.example.org:5000/collector/1", "exchange1")).
					add(collector("http://www.example.org:5000/collector/2", "exchange1")).
					add(collector("http://www.example.org:5000/collector/3", "exchange2")).
					build();
		final int rounds = 10;
		final CountDownLatch expectedNotifications=new CountDownLatch(collectors.size()*rounds*10);
		final CountingNotificationListener listener = new CountingNotificationListener(expectedNotifications);
		final NotificationManager sut = NotificationManager.newInstance(collectors,listener);
		LOGGER.info("Starting Notitication Manager...");
		sut.start();
		try {
			LOGGER.info("Sending notifications...");
			final CollectorAggregator aggregator=Deencapsulation.getField(sut,"aggregator");
			for(int i=0;i<rounds;i++) {
				for(final CollectorConfiguration collector:collectors) {
					publishEvent(aggregator, createContributors(collector, "cc1"+i,"cc2"+i));
					publishEvent(aggregator, deleteContributors(collector, "dc1"+i,"dc2"+i));
					publishEvent(aggregator, createCommits(collector, "ccm1"+i,"ccm2"+i));
					publishEvent(aggregator, deleteCommits(collector, "dcm1"+i,"dcm2"+i));
					publishEvent(aggregator, createProjects(collector, "r"+i+1,"r"+i+2));
					publishEvent(aggregator, deleteProjects(collector, "r"+i*rounds+1,"r"+i*rounds+2));
					publishEvent(aggregator, createProjectComponents(collector, "r"+i*rounds+1,"cpc1"+i,"cpc2"+i));
					publishEvent(aggregator, deleteProjectComponents(collector, "r"+i*rounds+2,"dpc1"+i,"dpc2"+i));
					publishEvent(aggregator, createProjectVersions(collector, "r"+i*rounds+3,"cpv1"+i,"cpv2"+i));
					publishEvent(aggregator, deleteProjectVersions(collector, "r"+i*rounds+4,"dpv1"+i,"dpv2"+i));
					publishEvent(aggregator, createProjectIssues(collector, "r"+i*rounds+3,"cpi1"+i,"cpi2"+i));
					publishEvent(aggregator, deleteProjectIssues(collector, "r"+i*rounds+4,"dpi1"+i,"dpi2"+i));
				}
			}
			shutdownPool();
			LOGGER.info("Notifications sent");
			expectedNotifications.await();
			LOGGER.info("Notifications received");
		} catch (final InterruptedException e) {
			LOGGER.warn("Interrupted while awaiting for the reception of all the notifications sent");
		} finally {
			sut.shutdown();
		}
		LOGGER.info("Summary of received notifications:");
		for(final CollectorConfiguration collector:collectors) {
			LOGGER.info(" + {}:",collector.getInstance());
			for(final String event:listener.events(collector.getInstance())) {
				LOGGER.info("  - {} : {}",event,listener.eventCount(collector.getInstance(),event));
			}
		}
	}

	private void publishEvent(final CollectorAggregator aggregator, final Event event) {
		this.pool.execute(new EventPublisher(event, aggregator));
	}

	private void shutdownPool() {
		this.pool.shutdown();
		while(!this.pool.isTerminated()) {
			try {
				this.pool.awaitTermination(100,TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				LOGGER.warn("Interrupted while awaiting for the emission of all the notifications");
			}
		}
	}

	private ContributorCreatedEvent createContributors(final CollectorConfiguration collector, final String... values) {
		final ContributorCreatedEvent event = new ContributorCreatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setNewContributors(Arrays.asList(values));
		return event;
	}

	private ContributorDeletedEvent deleteContributors(final CollectorConfiguration collector, final String... values) {
		final ContributorDeletedEvent event = new ContributorDeletedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setDeletedContributors(Arrays.asList(values));
		return event;
	}

	private CommitCreatedEvent createCommits(final CollectorConfiguration collector, final String... values) {
		final CommitCreatedEvent event = new CommitCreatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setNewCommits(Arrays.asList(values));
		return event;
	}

	private CommitDeletedEvent deleteCommits(final CollectorConfiguration collector, final String... values) {
		final CommitDeletedEvent event = new CommitDeletedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setDeletedCommits(Arrays.asList(values));
		return event;
	}

	private ProjectCreatedEvent createProjects(final CollectorConfiguration collector, final String... values) {
		final ProjectCreatedEvent event = new ProjectCreatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setNewProjects(Arrays.asList(values));
		return event;
	}

	private ProjectDeletedEvent deleteProjects(final CollectorConfiguration collector, final String... values) {
		final ProjectDeletedEvent event = new ProjectDeletedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setDeletedProjects(Arrays.asList(values));
		return event;
	}

	private ProjectUpdatedEvent createProjectComponents(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.create().component(valueId));
		}
		return event;
	}

	private ProjectUpdatedEvent deleteProjectComponents(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.delete().component(valueId));
		}
		return event;
	}

	private ProjectUpdatedEvent createProjectVersions(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.create().version(valueId));
		}
		return event;
	}

	private ProjectUpdatedEvent deleteProjectVersions(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.delete().version(valueId));
		}
		return event;
	}

	private ProjectUpdatedEvent createProjectIssues(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.create().issue(valueId));
		}
		return event;
	}

	private ProjectUpdatedEvent deleteProjectIssues(final CollectorConfiguration collector, final String id, final String... values) {
		final ProjectUpdatedEvent event = new ProjectUpdatedEvent();
		event.setInstance(collector.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		event.setProject(id);
		for(final String valueId:values) {
			event.append(Modification.delete().issue(valueId));
		}
		return event;
	}

	private CollectorConfiguration collector(final String instance, final String exchangeName) {
		final CollectorConfiguration collector=new CollectorConfiguration();
		collector.setInstance(instance);
		collector.setBrokerHost("localhost");
		collector.setBrokerPort(5672);
		collector.setVirtualHost("/");
		collector.setExchangeName(exchangeName);
		return collector;
	}

}
