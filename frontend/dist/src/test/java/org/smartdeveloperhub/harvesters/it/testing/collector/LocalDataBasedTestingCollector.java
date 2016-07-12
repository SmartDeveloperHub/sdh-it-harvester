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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.collector;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.ProjectScoped;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ActivityListener;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ProjectChange;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.TestingCollector;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.EntityNotFoundException;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

final class LocalDataBasedTestingCollector implements TestingCollector {

	private final BackendController delegate;
	private final URI target;

	LocalDataBasedTestingCollector(final BackendController delegate, final URI target) {
		this.delegate = delegate;
		this.target = target;
	}

	@Override
	public URI getTarget() {
		return this.target;
	}

	@Override
	public Collector getCollector() {
		try {
			return this.delegate.getCollector();
		} catch (final IOException e) {
			throw new IllegalStateException("Should not fail",e);
		}
	}

	@Override
	public State getState() {
		try {
			return this.delegate.getState();
		} catch (final IOException e) {
			throw new IllegalStateException("Should not fail",e);
		}
	}

	@Override
	public List<String> getContributors() {
		try {
			return this.delegate.getContributors();
		} catch (final IOException e) {
			throw new IllegalStateException("Should not fail",e);
		}
	}

	@Override
	public List<String> getCommits() {
		try {
			return this.delegate.getCommits();
		} catch (final IOException e) {
			throw new IllegalStateException("Should not fail",e);
		}
	}

	@Override
	public List<String> getProjects() {
		try {
			return this.delegate.getProjects();
		} catch (final IOException e) {
			throw new IllegalStateException("Should not fail",e);
		}
	}

	@Override
	public Contributor getContributor(final String contributorId) throws EntityNotFoundException {
		try {
			return this.delegate.getContributor(contributorId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve contributor "+contributorId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public Commit getCommit(final String commitId) throws EntityNotFoundException {
		try {
			return this.delegate.getCommit(commitId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve commit "+commitId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public Project getProject(final String projectId) throws EntityNotFoundException {
		try {
			return this.delegate.getProject(projectId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve project "+projectId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public Component getProjectComponent(final String projectId, final String componentId) throws EntityNotFoundException {
		try {
			return this.delegate.getProjectComponent(projectId, componentId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve project "+projectId+" component "+componentId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public Version getProjectVersion(final String projectId, final String versionId) throws EntityNotFoundException {
		try {
			return this.delegate.getProjectVersion(projectId,versionId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve project "+projectId+" version "+versionId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public Issue getProjectIssue(final String projectId, final String issueId) throws EntityNotFoundException {
		try {
			return this.delegate.getProjectIssue(projectId,issueId);
		} catch (final IOException e) {
			throw new EntityNotFoundException("Could not retrieve project "+projectId+" issue "+issueId+" ("+e.getMessage()+")");
		}
	}

	@Override
	public TestingCollector registerListener(final ActivityListener listener) {
		// Nothing to do
		return this;
	}

	@Override
	public CommitCreatedEvent createCommits(final Commit... commits) {
		// Nothing to do
		return new CommitCreatedEvent();
	}

	@Override
	public ContributorCreatedEvent createContributors(final Contributor... contributors) {
		// Nothing to do
		return new ContributorCreatedEvent();
	}

	@Override
	public ProjectCreatedEvent createProjects(final Project... projects) {
		// Nothing to do
		return new ProjectCreatedEvent();
	}

	@Override
	public CommitDeletedEvent deleteCommits(final Commit... commits) {
		// Nothing to do
		return new CommitDeletedEvent();
	}

	@Override
	public ContributorDeletedEvent deleteContributors(final Contributor... contributors) {
		// Nothing to do
		return new ContributorDeletedEvent();
	}

	@Override
	public ProjectDeletedEvent deleteProjects(final Project... projects) {
		// Nothing to do
		return new ProjectDeletedEvent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ProjectScoped<String>> List<ProjectUpdatedEvent> updateProjects(final ProjectChange<T>... changes) {
		// Nothing to do
		return Collections.emptyList();
	}
}