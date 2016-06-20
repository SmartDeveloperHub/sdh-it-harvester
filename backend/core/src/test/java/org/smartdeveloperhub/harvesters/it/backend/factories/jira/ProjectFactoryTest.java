package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;

import java.util.HashSet;
import java.util.Set;

/**
 * Unity test for Project Factory.
 * @author imolina
 *
 */
public class ProjectFactoryTest {

	private final static String PROJECT_KEY = "SDH";
	private final static String PROJECT_NAME = "SmartDeveloperHub";

	private ProjectFactory factory;
	private Set<com.atlassian.jira.rest.client.api.domain.Version> jiraVersions;
	private Set<com.atlassian.jira.rest.client.api.domain.BasicComponent> jiraComponents;
	private Set<Issue> issues;
	private Set<Issue> topIssues;

	@Mock private com.atlassian.jira.rest.client.api.domain.Project jiraProject;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);
		factory = new ProjectFactory();
		issues = new HashSet<>();
		topIssues = new HashSet<>();
		given(jiraProject.getKey()).willReturn(PROJECT_KEY);
		given(jiraProject.getName()).willReturn(PROJECT_NAME);

		jiraVersions = new HashSet<>();
		given(jiraProject.getVersions()).willReturn(jiraVersions);

		jiraComponents = new HashSet<>();
		given(jiraProject.getComponents()).willReturn(jiraComponents);
	}

	@Test()
	public void shouldReturnProjectWithoutVersionsNorComponentsNorIssues() {

		Project project = factory.createProject(jiraProject, topIssues, issues);
		then(project.getId()).isEqualTo(PROJECT_KEY);
		then(project.getName()).isEqualTo(PROJECT_NAME);
		then(project.getComponents()).isEmpty();
		then(project.getVersions()).isEmpty();
		then(project.getIssues()).isEmpty();
		then(project.getTopIssues()).isEmpty();
	}

	@Test()
	public void shouldReturnProjectWithoutComponentsNorIssues() {

		com.atlassian.jira.rest.client.api.domain.Version version =
				mock(com.atlassian.jira.rest.client.api.domain.Version.class);

		given(version.getId()).willReturn(10L);
		
		jiraVersions.add(version);
		
		Project project = factory.createProject(jiraProject, topIssues, issues);
		then(project.getId()).isEqualTo(PROJECT_KEY);
		then(project.getName()).isEqualTo(PROJECT_NAME);
		then(project.getComponents()).isEmpty();
		then(project.getIssues()).isEmpty();
		then(project.getTopIssues()).isEmpty();
		Set<String> versions = project.getVersions();
		then(versions).hasSize(1);
		then(versions.iterator().next()).isEqualTo("10");
	}

	@Test()
	public void shouldReturnProjectWithoutVersionsNorIssues() {

		com.atlassian.jira.rest.client.api.domain.BasicComponent component =
				mock(com.atlassian.jira.rest.client.api.domain.BasicComponent.class);

		given(component.getId()).willReturn(5L);
		
		jiraComponents.add(component);
		
		Project project = factory.createProject(jiraProject, topIssues, issues);
		then(project.getId()).isEqualTo(PROJECT_KEY);
		then(project.getName()).isEqualTo(PROJECT_NAME);
		then(project.getVersions()).isEmpty();
		then(project.getIssues()).isEmpty();
		then(project.getTopIssues()).isEmpty();
		Set<String> components = project.getComponents();
		then(components).hasSize(1);
		then(components.iterator().next()).isEqualTo("5");
	}

	@Test()
	public void shouldReturnProjectWithoutVersionsNorComponentsNorTopIssues() {

		Issue issue = new Issue();

		issue.setId("SDH-1");

		issues.add(issue);

		Project project = factory.createProject(jiraProject, topIssues, issues);
		then(project.getId()).isEqualTo(PROJECT_KEY);
		then(project.getName()).isEqualTo(PROJECT_NAME);
		then(project.getComponents()).isEmpty();
		then(project.getVersions()).isEmpty();
		then(project.getTopIssues()).isEmpty();
		Set<String> issues = project.getIssues();
		then(issues.iterator().next()).isEqualTo("SDH-1");
	}

	@Test()
	public void shouldReturnProjectWithOnlyTopIssues() {

		Issue issue = new Issue();

		issue.setId("SDH-1");

		topIssues.add(issue);

		Project project = factory.createProject(jiraProject, topIssues, issues);
		then(project.getId()).isEqualTo(PROJECT_KEY);
		then(project.getName()).isEqualTo(PROJECT_NAME);
		then(project.getComponents()).isEmpty();
		then(project.getVersions()).isEmpty();
		then(project.getIssues()).isEmpty();
		Set<String> issues = project.getTopIssues();
		then(issues.iterator().next()).isEqualTo("SDH-1");
	}

	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionWhenNullProject() {

		factory.createProject(null, topIssues, issues);
	}

	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionWhenNullTopIssues() {

		factory.createProject(jiraProject, null, issues);
	}

	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionWhenNullIssues() {

		factory.createProject(jiraProject, topIssues, null);
	}
}
