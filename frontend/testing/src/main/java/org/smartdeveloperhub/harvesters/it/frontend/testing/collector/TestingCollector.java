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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0
 *   Bundle      : it-frontend-test-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.collector;

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
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.EntityNotFoundException;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.CommitDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

public interface TestingCollector extends BackendController {

	@Override
	Collector getCollector();

	@Override
	State getState();

	@Override
	List<String> getContributors();

	@Override
	List<String> getCommits();

	@Override
	List<String> getProjects();

	@Override
	Contributor getContributor(final String contributorId) throws EntityNotFoundException;

	@Override
	Commit getCommit(final String commitId) throws EntityNotFoundException;

	@Override
	Project getProject(final String projectId) throws EntityNotFoundException;

	@Override
	Component getProjectComponent(final String projectId, final String componentId) throws EntityNotFoundException;

	@Override
	Version getProjectVersion(final String projectId, final String versionId) throws EntityNotFoundException;

	@Override
	Issue getProjectIssue(final String projectId, final String issueId) throws EntityNotFoundException;

	TestingCollector registerListener(ActivityListener listener);

	CommitCreatedEvent createCommits(Commit... commits);

	ContributorCreatedEvent createContributors(Contributor... contributors);

	ProjectCreatedEvent createProjects(Project... projects);

	CommitDeletedEvent deleteCommits(Commit... commits);

	ContributorDeletedEvent deleteContributors(Contributor... contributors);

	ProjectDeletedEvent deleteProjects(Project... projects);

	<T extends ProjectScoped<String>> List<ProjectUpdatedEvent> updateProjects(@SuppressWarnings("unchecked") ProjectChange<T>... changes);

}