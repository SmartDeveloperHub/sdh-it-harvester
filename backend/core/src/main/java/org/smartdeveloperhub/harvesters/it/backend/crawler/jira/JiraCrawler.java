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
package org.smartdeveloperhub.harvesters.it.backend.crawler.jira;

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ProjectFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class for crawling information from the issue tracker Jira. 
 * @author imolina
 *
 */
public class JiraCrawler implements Crawler {

	private static final Logger logger =
									LoggerFactory.getLogger(JiraCrawler.class);

	private AsynchronousJiraRestClientFactory jiraClientFactory;
	private URI uri;
	private String username;
	private String password;
	private ProjectFactory projectFactory;
	private IssueFactory issueFactory;

	public JiraCrawler(String url, String username, String password,
					ProjectFactory projectFactory, IssueFactory issueFactory)
													throws URISyntaxException {

		this.uri = new URI(url);
		this.username = Objects.requireNonNull(username,
												"Username can't be null.");
		this.password = Objects.requireNonNull(password,
												"Password can't be null.");
		this.projectFactory = Objects.requireNonNull(projectFactory,
												"ProjectFactory cannot be null");
		this.issueFactory = Objects.requireNonNull(issueFactory,
												"IssueFactory cannot be null.");
		this.jiraClientFactory = new AsynchronousJiraRestClientFactory();
	}

	public void collect(long lastUpdateTimeStamp) {

		logger.info("Started crawling services...");

		try(JiraRestClient client =
				jiraClientFactory.createWithBasicHttpAuthentication(uri,
																	username,
																	password)) {

			Set<org.smartdeveloperhub.harvesters.it.backend.Project> projects = new HashSet<>();

			for (Project jiraProject : getProjects(client)) {

				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> topIssues = new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();
				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> issues = new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();

				for (Issue jiraIssue : getProjectIssues(client, jiraProject.getKey(),
														lastUpdateTimeStamp)) {

					org.smartdeveloperhub.harvesters.it.backend.Issue issue = issueFactory.createIssue(jiraIssue);

					topIssues.add(issue);
				}

				projects.add(projectFactory.createProject(jiraProject,
															topIssues,
															issues));

				// Store components
//				database.store(getAllComponents(Components));
				// Store versions
//				database.store(getAllVersions(versions))
				// Store new issues
//				database.store(issues);
			}

			// Store Project
			System.out.println(projects);
//			database.store(projects)

			lastUpdateTimeStamp = System.currentTimeMillis();
		} catch (IOException e) {

			logger.error("Exception in client connection. {}", e);
		}

		logger.info("Finished crawling services.");
	}

	private Set<Project> getProjects(JiraRestClient client) {

		Set<Project> projects = new HashSet<>();
		ProjectRestClient projectClient = client.getProjectClient();
		
		for (BasicProject project : projectClient.getAllProjects().claim()) {
			
			projects.add(projectClient.getProject(project.getKey()).claim());
		}

		return projects;
	}

	private Iterable<Issue> getProjectIssues(JiraRestClient client,
												String projectId,
												long lastUpdate) {

		Set<Issue> issues = new HashSet<>();

		Date update=new Date(lastUpdate);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String updateStr = df2.format(update);

		// Filters separated by (and, or, not, empty, null, order by)
		String query = "project = \"" + projectId + "\" and updated > \" " +
						updateStr + " \"";

		SearchResult searchResult = client.getSearchClient()
												.searchJql(query)
													.claim();

		// Unrolling paged response
		int maxResult = searchResult.getMaxResults();
		int total = searchResult.getTotal();
		for (int i= 0; i < total; i += maxResult) {

			for (Issue issue : searchResult.getIssues()) {

				// Retrieve changeLog information
					Issue jiraIssue = client.getIssueClient()
												.getIssue(issue.getKey(),
														Arrays.asList(new Expandos[] {
																Expandos.CHANGELOG}))
														.claim();
					issues.add(jiraIssue);
			}

			searchResult = client.getSearchClient()
									.searchJql(query, maxResult, i + maxResult,
												null)
										.claim();
		}

		return issues;
	}
}