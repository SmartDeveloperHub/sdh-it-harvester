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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import org.joda.time.DateTime;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import com.google.common.collect.Lists;

public class LocalDataTest {

	@Test
	public void testLocalData() throws Exception {
		final LocalData entity = new LocalData();
		entity.getCollector().setVersion("1.0.0");
		entity.getCommits().add(createCommit("commit1"));
		entity.getCommits().add(createCommit("commit2"));
		entity.getContributors().add(createContributor("contributor1"));
		entity.getContributors().add(createContributor("contributor2"));
		createProject("project1",entity);
		createProject("project2",entity);
		System.out.println(Entities.marshallEntity(entity));
	}

	private void createProject(final String projectId, final LocalData entity) {
		final Project project = new Project();
		project.setId(projectId);
		project.setName("Project '"+projectId+"'");
		project.getComponents().add("component1");
		project.getComponents().add("component2");
		project.getVersions().add("version1");
		project.getVersions().add("version2");
		project.getIssues().add("issue1");
		project.getIssues().add("issue2");
		project.getTopIssues().add("issue1");
		entity.getProjects().add(project);
		entity.
			getProjectComponents().
				put(
					projectId,
					Lists.
						newArrayList(
							createProjectComponent(projectId,"component1"),
							createProjectComponent(projectId,"component2")
						)
				);
		entity.
			getProjectVersions().
				put(
					projectId,
					Lists.
						newArrayList(
							createProjectVersion(projectId,"version1"),
							createProjectVersion(projectId,"version2")
						)
				);
		entity.
			getProjectIssues().
				put(
					projectId,
					Lists.
						newArrayList(
							createProjectIssue(projectId,"issue1"),
							createProjectIssue(projectId,"issue2")
						)
				);
	}

	private Component createProjectComponent(final String projectId, final String componentId) {
		final Component component = new Component();
		component.setId(componentId);
		component.setProjectId(projectId);
		component.setName("Component '"+componentId+"' of project '"+projectId+"'");
		return component;
	}

	private Version createProjectVersion(final String projectId, final String versionId) {
		final Version component = new Version();
		component.setId(versionId);
		component.setProjectId(projectId);
		component.setName("Version '"+versionId+"' of project '"+projectId+"'");
		return component;
	}

	private Issue createProjectIssue(final String projectId, final String issueId) {
		final Issue issue = new Issue();
		issue.setId(issueId);
		issue.setProjectId(projectId);
		issue.setName("Issue '"+issueId+"' of project '"+projectId+"'");
		issue.setCreationDate(new DateTime());
		issue.setOpened(issue.getCreationDate());
		issue.setStatus(Status.IN_PROGRESS);
		issue.setSeverity(Severity.BLOCKER);
		issue.setPriority(Priority.VERY_HIGH);
		issue.getCommits().add("commit1");
		issue.getComponents().add("component1");
		issue.getVersions().add("version1");
		issue.setReporter("contributor1");
		issue.setAssignee("contributor2");
		final Entry assignement = new Entry();
		assignement.setAuthor("contributor2");
		assignement.setTimeStamp(new DateTime().plusHours(1));
		assignement.getItems().add(Item.builder().assignee().newValue("contributor2").build());
		assignement.getItems().add(Item.builder().status().oldValue(Status.OPEN).newValue(Status.IN_PROGRESS).build());
		final Entry identify = new Entry();
		identify.setAuthor("contributor2");
		identify.setTimeStamp(assignement.getTimeStamp().plusHours(2));
		identify.getItems().add(Item.builder().versions().newValue("version1").build());
		identify.getItems().add(Item.builder().components().newValue("component1").build());
		final Entry commit = new Entry();
		commit.setAuthor("contributor2");
		commit.setTimeStamp(assignement.getTimeStamp().plusHours(4));
		commit.getItems().add(Item.builder().commits().newValue("commit1").build());
		final ChangeLog changeLog=new ChangeLog();
		changeLog.getEntries().add(assignement);
		changeLog.getEntries().add(commit);
		changeLog.getEntries().add(identify);
		issue.setChanges(changeLog);
		return issue;
	}

	private Contributor createContributor(final String contributorId) {
		final Contributor contributor = new Contributor();
		contributor.setId(contributorId);
		contributor.setName("Contributor "+contributorId);
		contributor.getEmails().add(contributorId+"@example.org");
		return contributor;
	}

	private Commit createCommit(final String commitId) {
		final Commit commit = new Commit();
		commit.setId(commitId);
		commit.setRepository("http://www.example.org/repositories/project.git");
		commit.setBranch("develop");
		commit.setHash(Integer.toHexString(commitId.hashCode()));
		return commit;
	}

}
