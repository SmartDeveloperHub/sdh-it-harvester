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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-test-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static io.undertow.Handlers.pathTemplate;
import static org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.MoreHandlers.provideEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.ProjectScoped;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ActivityListener;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.NotifyingTestingCollector;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ProjectChange;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.TestingCollector;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.EntityNotFoundException;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.EntityProvider;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.MoreHandlers.APIVersion;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.Parameters;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.NotificationPublisher;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import com.google.common.collect.Maps;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathTemplateHandler;

public final class TestingService {

	public static class Builder {

		private static final class NotifyingTestingCollectorProvider implements TestingCollectorProvider {
			@Override
			public TestingCollector provide(final CollectorConfiguration configuration) {
				LOGGER.info("Using Notifying testing collector...");
				return new NotifyingTestingCollector(configuration);
			}
		}

		private int port=8080;
		private String exchangeName="it.collector.mock";
		private ActivityListener listener;
		private final Map<String,HttpHandler> endpoints;
		private APIVersion version=APIVersion.v1;
		private TestingCollectorProvider provider;

		private Builder() {
			this.provider=new NotifyingTestingCollectorProvider();
			this.endpoints=Maps.newLinkedHashMap();
		}

		public Builder port(final int port) {
			checkArgument(port>0 && port <65536, "Port '%s' cannot be used",port);
			this.port=port;
			return this;
		}

		public Builder exchangeName(final String exchangeName) {
			checkNotNull(exchangeName, "IT Collector broker exchange name cannot be null");
			checkArgument(!exchangeName.isEmpty(), "IT Collector broker exchange name cannot be empty");
			this.exchangeName=exchangeName;
			return this;
		}

		public Builder apiVersion(final APIVersion version) {
			checkNotNull(version, "IT Collector API version cannot be null");
			this.version=version;
			return this;
		}

		public Builder listener(final ActivityListener listener) {
			checkNotNull(listener, "Activity listener cannot be null");
			this.listener=listener;
			return this;
		}

		public Builder collector(final TestingCollectorProvider provider) {
			checkNotNull(provider, "Testing collector provider cannot be null");
			this.provider=provider;
			return this;
		}

		public Builder addEndpoint(final String path, final HttpHandler handler) {
			checkNotNull(path,"Path cannot be null");
			checkNotNull(handler,"Handler cannot be null");
			this.endpoints.put(path,handler);
			return this;
		}

		public TestingService build() {
			return
				new TestingService(
					this.port,
					this.exchangeName,
					this.version,
					this.listener,
					this.endpoints,
					this.provider);
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(TestingService.class);

	private final CollectorConfiguration config;

	private final NotificationPublisher publisher;

	private final Undertow server;

	private final int port;

	private final APIVersion version;

	private final TestingCollector collector;

	private boolean collectorStarted;

	private boolean serverStarted;

	private TestingService(final int port, final String exchangeName, final APIVersion version, final ActivityListener listener, final Map<String, HttpHandler> endpoints, final TestingCollectorProvider provider) {
		this.port      = port;
		this.version   = version;
		this.config    = createControllerConfiguration(port, exchangeName);
		this.publisher = NotificationPublisher.newInstance(this.config);
		this.collector = provider.provide(this.config).registerListener(listener);
		final PathTemplateHandler handler=
			pathTemplate(false).
				add("/collector/api",
					provideEntity(
						this.version,
						new EntityProvider<Collector>() {
							@Override
							public Collector getEntity(final Parameters parameters) {
								return TestingService.this.collector.getCollector();
							}
						}
					)
				).
				add("/collector/api/state",
					provideEntity(
						this.version,
						new EntityProvider<State>() {
							@Override
							public State getEntity(final Parameters parameters) {
								return TestingService.this.collector.getState();
							}
						}
					)
				).
				add("/collector/api/contributors",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.collector.getContributors();
							}
						}
					)
				).
				add("/collector/api/contributors/{contributorId}",
					provideEntity(
						this.version,
						new EntityProvider<Contributor>() {
							@Override
							public Contributor getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getContributor(parameters.get("contributorId"));
							}
						}
					)
				).
				add("/collector/api/commits",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.collector.getCommits();
							}
						}
					)
				).
				add("/collector/api/commits/{commitId}",
					provideEntity(
						this.version,
						new EntityProvider<Commit>() {
							@Override
							public Commit getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getCommit(parameters.get("commitId"));
							}
						}
					)
				).
				add("/collector/api/projects",
					provideEntity(
						this.version,
						new EntityProvider<List<String>>() {
							@Override
							public List<String> getEntity(final Parameters parameters) {
								return TestingService.this.collector.getProjects();
							}
						}
					)
				).
				add("/collector/api/projects/{projectId}",
					provideEntity(
						this.version,
						new EntityProvider<Project>() {
							@Override
							public Project getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getProject(parameters.get("projectId"));
							}
						}
					)
				).
				add("/collector/api/projects/{projectId}/components/{componentId}",
					provideEntity(
						this.version,
						new EntityProvider<Component>() {
							@Override
							public Component getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getProjectComponent(parameters.get("projectId"),parameters.get("componentId"));
							}
						}
					)
				).
				add("/collector/api/projects/{projectId}/versions/{versionId}",
					provideEntity(
						this.version,
						new EntityProvider<Version>() {
							@Override
							public Version getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getProjectVersion(parameters.get("projectId"),parameters.get("versionId"));
							}
						}
					)
				).
				add("/collector/api/projects/{projectId}/issues/{issueId}",
					provideEntity(
						this.version,
						new EntityProvider<Issue>() {
							@Override
							public Issue getEntity(final Parameters parameters) throws EntityNotFoundException {
								return TestingService.this.collector.getProjectIssue(parameters.get("projectId"),parameters.get("issueId"));
							}
						}
					)
				);
		for(final Entry<String,HttpHandler> entry:endpoints.entrySet()) {
			handler.add(entry.getKey(), entry.getValue());
		}
		this.server =
			Undertow.
				builder().
					addHttpListener(port,"localhost").
					setHandler(handler).
					build();
		}

	private void update(final Event event) {
		checkState(this.serverStarted,"Testing service not started");
		event.setInstance(this.config.getInstance());
		event.setTimestamp(System.currentTimeMillis());
		try {
			this.publisher.publish(event);
		} catch (final IOException e) {
			LOGGER.error("Could not publish event {}. Full stacktrace follows",event,e);
		}
	}

	public TestingService start() throws IOException {
		LOGGER.info("Starting IT Collector Publisher Service...");
		this.publisher.start();
		LOGGER.info("IT Collector Publisher Service started. Using exchange {}",this.config.getExchangeName());
		this.collectorStarted=true;
		LOGGER.info("Starting IT Collector Service ({}) ...",this.version);
		this.server.start();
		LOGGER.info("IT Collector Service started. Service available locally at port {}",this.port);
		this.serverStarted=true;
		return this;
	}

	public void createCommits(final Commit... commits) {
		final CommitCreatedEvent event=this.collector.createCommits(commits);
		if(!event.getNewCommits().isEmpty()) {
			update(event);
		}
	}

	public void deleteCommits(final Commit... commits) {
		final CommitDeletedEvent event=this.collector.deleteCommits(commits);
		if(!event.getDeletedCommits().isEmpty()) {
			update(event);
		}
	}

	public void createContributors(final Contributor... contributors) {
		final ContributorCreatedEvent event=this.collector.createContributors(contributors);
		if(!event.getNewContributors().isEmpty()) {
			update(event);
		}
	}

	public void deleteContributors(final Contributor... contributors) {
		final ContributorDeletedEvent event=this.collector.deleteContributors(contributors);
		if(!event.getDeletedContributors().isEmpty()) {
			update(event);
		}
	}

	public void createProjects(final Project... projects) {
		final ProjectCreatedEvent event=this.collector.createProjects(projects);
		if(!event.getNewProjects().isEmpty()) {
			update(event);
		}
	}

	@SafeVarargs
	public final <T extends ProjectScoped<String>> void updateProjects(final ProjectChange<T>... changes) {
		for(final ProjectUpdatedEvent event:this.collector.updateProjects(changes)) {
			if(!event.isEmpty()) {
				update(event);
			}
		}
	}

	public void deleteProjects(final Project... projects) {
		final ProjectDeletedEvent event=this.collector.deleteProjects(projects);
		if(!event.getDeletedProjects().isEmpty()) {
			update(event);
		}
	}

	public TestingService shutdown() {
		if(this.serverStarted) {
			LOGGER.info("Stopping IT Collector Service...");
			this.server.stop();
			LOGGER.info("IT Collector Service stopped.");
			this.serverStarted=false;
		}
		if(this.collectorStarted) {
			LOGGER.info("Stopping IT Collector Publisher Service...");
			this.publisher.shutdown();
			LOGGER.info("IT Collector Publisher Service stopped.");
			this.collectorStarted=false;
		}
		return this;
	}

	public static Builder builder() {
		return new Builder();
	}

	private static CollectorConfiguration createControllerConfiguration(final int port, final String exchangeName) {
		final CollectorConfiguration config = new CollectorConfiguration();
		config.setInstance("http://localhost:"+port+"/collector/api");
		config.setBrokerHost("localhost");
		config.setExchangeName(exchangeName);
		return config;
	}

}
