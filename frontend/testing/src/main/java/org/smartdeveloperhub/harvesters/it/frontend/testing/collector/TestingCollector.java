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
package org.smartdeveloperhub.harvesters.it.frontend.testing.collector;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Identifiable;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Notifications;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ActivityTracker.ActivityContext;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.EntityNotFoundException;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TestingCollector implements BackendController {

	private final class InnerOwner implements ActivityContext {

		@Override
		public String resolve(final String path, final Object... args) {
			return TestingCollector.this.target.resolve(String.format(path,args)).toString();
		}

		@Override
		public void onActivity(final Activity<?> activity) {
			final ActivityListener current=TestingCollector.this.listener;
			if(current!=null) {
				current.onActivity(activity);
			}
		}

	}

	private final URI target;
	private final Map<String,Contributor> contributors;
	private final Map<String,Commit> commits;
	private final Map<String,Project> projects;
	private final Map<String,Map<String,Component>> projectComponents;
	private final Map<String,Map<String,Version>> projectVersions;
	private final Map<String,Map<String,Issue>> projectIssues;
	private ActivityListener listener;
	private final CollectorConfiguration config;

	public TestingCollector(final CollectorConfiguration config) {
		this.config = config;
		this.target = URI.create(config.getInstance());
		this.contributors=Maps.newLinkedHashMap();
		this.commits=Maps.newLinkedHashMap();
		this.projects=Maps.newLinkedHashMap();
		this.projectComponents=Maps.newLinkedHashMap();
		this.projectVersions=Maps.newLinkedHashMap();
		this.projectIssues=Maps.newLinkedHashMap();
	}

	private <V,T extends Identifiable<V>> List<V> getIdentifiers(final Map<V,T> identifiables) {
		final List<V> result=Lists.newArrayList();
		result.addAll(identifiables.keySet());
		return result;
	}

	private <V,T extends Identifiable<V>> T findIdentifiable(final Map<V,T> identifiables, final V identifier) {
		if(identifiables==null) {
			return null;
		}
		return identifiables.get(identifier);
	}

	private <T> T checkNotNull(final T element, final String format, final Object... args) throws EntityNotFoundException {
		if(element!=null) {
			return element;
		}
		throw new EntityNotFoundException("Could not find "+String.format(format, args));
	}

	private <V,T extends Identifiable<V>> void create(final List<V> processedIdentifiers, final Map<V,T> localIdentifiables, @SuppressWarnings("unchecked") final T... identifiables) {
		ActivityTracker.useContext(new InnerOwner());
		final ActivityTracker currentTracker = ActivityTracker.currentTracker();
		try {
			for(final T identifiable:identifiables) {
				final V id = identifiable.getId();
				if(!localIdentifiables.containsKey(id)) {
					localIdentifiables.put(id, identifiable);
					currentTracker.created(identifiable);
					processedIdentifiers.add(id);
				}
			}
		} finally {
			ActivityTracker.remove();
		}
	}

	private <V,T extends Identifiable<V>> void delete(final List<V> processedIdentifiers, final Map<V,T> localIdentifiables, @SuppressWarnings("unchecked") final T... identifiables) {
		ActivityTracker.useContext(new InnerOwner());
		final ActivityTracker currentTracker = ActivityTracker.currentTracker();
		try {
			for(final T identifiable:identifiables) {
				final V id = identifiable.getId();
				if(localIdentifiables.containsKey(id)) {
					localIdentifiables.remove(id);
					currentTracker.deleted(identifiable);
					processedIdentifiers.add(id);
				}
			}
		} finally {
			ActivityTracker.remove();
		}
	}

	public TestingCollector registerListener(final ActivityListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public URI getTarget() {
		return this.target;
	}

	@Override
	public Collector getCollector() {
		final Notifications notifications = new Notifications();
		notifications.setBrokerHost(this.config.getBrokerHost());
		notifications.setBrokerPort(this.config.getBrokerPort());
		notifications.setVirtualHost(this.config.getVirtualHost());
		notifications.setExchangeName(this.config.getExchangeName());
		final Collector en = new Collector();
		en.setVersion("0.1.0-SNAPSHOT");
		en.setNotifications(notifications);
		return en;
	}

	@Override
	public State getState() {
		return new State();
	}

	@Override
	public List<String> getContributors() {
		return getIdentifiers(this.contributors);
	}

	@Override
	public Contributor getContributor(final String contributorId) throws EntityNotFoundException  {
		return
			checkNotNull(
				findIdentifiable(this.contributors,contributorId),
				"contributor '%s'",contributorId);
	}

	@Override
	public List<String> getCommits() {
		return getIdentifiers(this.commits);
	}

	@Override
	public Commit getCommit(final String commitId) throws EntityNotFoundException {
		return
			checkNotNull(
				findIdentifiable(this.commits,commitId),
				"commit '%s'",commitId);
	}

	@Override
	public List<String> getProjects() {
		return getIdentifiers(this.projects);
	}

	@Override
	public Project getProject(final String projectId) throws EntityNotFoundException {
		return
			checkNotNull(
				findIdentifiable(this.projects,projectId),
				"project '%s'",projectId);
	}

	@Override
	public Component getProjectComponent(final String projectId, final String componentId) throws EntityNotFoundException {
		return
			checkNotNull(
				findIdentifiable(this.projectComponents.get(projectId),componentId),
				"component '%s' of project '%s'",componentId,projectId);
	}

	@Override
	public Version getProjectVersion(final String projectId, final String versionId) throws EntityNotFoundException {
		return
			checkNotNull(
				findIdentifiable(this.projectVersions.get(projectId),versionId),
				"version '%s' of project '%s'",versionId,projectId);
	}

	@Override
	public Issue getProjectIssue(final String projectId, final String issueId) throws EntityNotFoundException {
		return
			checkNotNull(
				findIdentifiable(this.projectIssues.get(projectId),issueId),
				"issue '%s' of project '%s'",issueId,projectId);
	}

	public CommitCreatedEvent createCommits(final Commit... commits) {
		final CommitCreatedEvent event=new CommitCreatedEvent();
		create(event.getNewCommits(),this.commits,commits);
		return event;
	}

	public ContributorCreatedEvent createContributors(final Contributor... contributors) {
		final ContributorCreatedEvent event=new ContributorCreatedEvent();
		create(event.getNewContributors(),this.contributors,contributors);
		return event;
	}

	public ProjectCreatedEvent createProjects(final Project... projects) {
		final ProjectCreatedEvent event=new ProjectCreatedEvent();
		create(event.getNewProjects(),this.projects,projects);
		for(final String deletedProject:event.getNewProjects()) {
			this.projectComponents.put(deletedProject,Maps.<String,Component>newLinkedHashMap());
			this.projectVersions.put(deletedProject,Maps.<String,Version>newLinkedHashMap());
			this.projectIssues.put(deletedProject,Maps.<String,Issue>newLinkedHashMap());
		}
		return event;
	}

	public CommitDeletedEvent deleteCommits(final Commit... commits) {
		final CommitDeletedEvent event=new CommitDeletedEvent();
		delete(event.getDeletedCommits(),this.commits,commits);
		return event;
	}

	public ContributorDeletedEvent deleteContributors(final Contributor... contributors) {
		final ContributorDeletedEvent event=new ContributorDeletedEvent();
		delete(event.getDeletedContributors(),this.contributors,contributors);
		return event;
	}

	public ProjectDeletedEvent deleteProjects(final Project... projects) {
		final ProjectDeletedEvent event=new ProjectDeletedEvent();
		delete(event.getDeletedProjects(),this.projects,projects);
		for(final String deletedProject:event.getDeletedProjects()) {
			this.projectComponents.remove(deletedProject);
			this.projectVersions.remove(deletedProject);
			this.projectIssues.remove(deletedProject);
		}
		return event;
	}

}
