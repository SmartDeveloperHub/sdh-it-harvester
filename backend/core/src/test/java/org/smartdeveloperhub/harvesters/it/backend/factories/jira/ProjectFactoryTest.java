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
