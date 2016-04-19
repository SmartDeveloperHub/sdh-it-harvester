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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-backend:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-backend-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Lists;

public class Explorer implements Closeable {


	private final JiraRestClient client;

	private Explorer(final URI jiraInstance, final String username, final String password) {
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		this.client =
			factory.
				createWithBasicHttpAuthentication(
					jiraInstance,
					username,
					password);
	}

	public static void main(final String... args) throws IOException {
		if(args.length!=3) {
			System.err.printf("Expected 3 arguments (got %d)%n",args.length);
			System.exit(-1);
		}
		try {
			final URI jiraInstance=new URI(args[0]);
			final String username = args[1];
			final String password = args[2];
			final Explorer explorer = new Explorer(jiraInstance,username,password);
			try {
				explorer.exploreProjects();
				explorer.exploreIssue("OSSRH-15633");
			} finally {
				explorer.close();
			}
		} catch (final URISyntaxException e) {
			System.err.printf("Invalid Jira instance URI (%s): %s%n",args[0],e.getMessage());
			System.exit(-2);
		}
	}

	void exploreProjects() {
		final Iterable<BasicProject> projects=
			this.client.
				getProjectClient().
					getAllProjects().
						claim();
		int totalProjects=0;
		int totalIssues=0;
		for(final BasicProject project:projects) {
			totalProjects++;
			System.out.printf("<%s> Project %s (%s)%n",project.getSelf(),project.getName(),project.getKey());
			totalIssues+=exploreProjectIssues(project);
		}
		System.out.printf(">> Total projects: %d%n",totalProjects);
		System.out.printf(">> Total issues..: %d%n",totalIssues);
	}

	void exploreIssue(final String issueKey) {
		final Issue issue = this.client.getIssueClient().getIssue(issueKey,Lists.newArrayList(Expandos.CHANGELOG,Expandos.TRANSITIONS)).claim();
		if(issue!=null) {
			System.out.println("Specific issue:");
			showIssue(issue);
		}
	}

	private int exploreProjectIssues(final BasicProject project) {
		final SearchResult searchResult=
			this.client.
				getSearchClient().
					searchJql("project = \""+project.getKey()+"\"").
						claim();
		final int projectIssues = searchResult.getTotal();
		if(projectIssues==0) {
			System.out.println("No issues found");
		} else {
			System.out.printf(
				"Found %d issues (%d to %d):%n",
				projectIssues,
				searchResult.getStartIndex()+1,
				searchResult.getStartIndex()+Math.min(searchResult.getMaxResults(),projectIssues));
			for(final Issue issue:searchResult.getIssues()) {
				System.out.printf("- <%s> [%-11s] %s : %s%n",issue.getSelf(),issue.getIssueType().getName(),issue.getKey(),firstLine(issue.getDescription()));
			}
		}
		return projectIssues;
	}

	private static void showIssue(final Issue issue) {
		System.out.printf("- <%s> [%-11s] %s : %s%n",issue.getSelf(),issue.getIssueType().getName(),issue.getKey(),firstLine(issue.getDescription()));
		System.out.printf("  * Transitions @ %s%n", issue.getTransitionsUri());
		final Iterable<ChangelogGroup> changelogs = issue.getChangelog();
		if(changelogs==null) {
			System.out.println("  * No changes");
		} else {
			System.out.println("  * Changes:");
			for(final ChangelogGroup changelog:changelogs) {
				System.out.printf("    + In %s by %s:%n",changelog.getCreated(),changelog.getAuthor().getDisplayName());
				for(final ChangelogItem item:changelog.getItems()) {
					System.out.printf("      - %s [%s] from '%s' to '%s'%n",item.getField(),item.getFieldType().name(),item.getFromString(),item.getToString());
				}
			}
		}
	}

	private static String firstLine(final String description) {
		if(description==null) {
			return "<NO DESCRIPTION AVAILABLE>";
		}
		final String[] split = description.split("\\n(\\r)?|\\r(\\n)?");
		return split[0];
	}

	@Override
	public void close() throws IOException {
		this.client.close();
	}

	static Explorer newInstance(final URI jiraInstance, final String username, final String password) {
		Objects.requireNonNull(jiraInstance, "Jira instance URI cannot be null");
		Objects.requireNonNull(username, "User name cannot be null");
		Objects.requireNonNull(password, "User password cannot be null");
		return new Explorer(jiraInstance, username, password);
	}

}
