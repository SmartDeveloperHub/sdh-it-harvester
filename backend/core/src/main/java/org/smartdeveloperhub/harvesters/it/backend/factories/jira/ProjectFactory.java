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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;

import java.util.HashSet;
import java.util.Set;

/**
 * Class factory for bulding {@link Project}s.
 * @author imolina
 *
 */
public class ProjectFactory {

	/**
	 * Method for building {@link Project} from Jira projects.
	 * @param jiraProject for retrieve Project information.
	 * @param topIssues of the project.
	 * @param issues of the project.
	 * @return {@link Project}
	 */
	public Project createProject(com.atlassian.jira.rest.client.api.domain.Project jiraProject,
									Set<Issue> topIssues, Set<Issue> issues) {

		Project project = new Project();
		project.setId(jiraProject.getKey());
		project.setName(jiraProject.getName());
		project.setVersions(getVersionsByIds(jiraProject));
		project.setComponents(getComponentsByIds(jiraProject));

		project.setTopIssues(getIssuesByIds(topIssues));
		project.setIssues(getIssuesByIds(issues));

		return project;
	}

	private Set<String> getIssuesByIds(Set<Issue> issues) {

		Set<String> issuesIds = new HashSet<>();

		for (Issue issue : issues) {

			issuesIds.add(issue.getId());
		}
		return issuesIds;
	}

	private Set<String> getVersionsByIds(com.atlassian.jira.rest.client.api.domain.Project jiraProject) {

		Set<String> versionsIds = new HashSet<>();

		for (com.atlassian.jira.rest.client.api.domain.Version version : jiraProject.getVersions()) {

			versionsIds.add(String.valueOf(version.getId()));
		}
		return versionsIds;
	}

	private Set<String> getComponentsByIds(com.atlassian.jira.rest.client.api.domain.Project jiraProject) {

		Set<String> componentsIds = new HashSet<>();

		for (com.atlassian.jira.rest.client.api.domain.BasicComponent component : jiraProject.getComponents()) {

			componentsIds.add(String.valueOf(component.getId()));
		}
		return componentsIds;
	}
}
