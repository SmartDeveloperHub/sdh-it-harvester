package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;

import java.util.HashSet;
import java.util.Objects;
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

		Objects.requireNonNull(topIssues, "Top Issues cannot be null");
		Objects.requireNonNull(issues, "Issues cannot be null");

		Project project = new Project();
		project.setId(jiraProject.getKey());
		project.setTitle(jiraProject.getName());
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
