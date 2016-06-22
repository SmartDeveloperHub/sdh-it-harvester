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
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ComponentFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ContributorFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ProjectFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.VersionFactory;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class for crawling information from the issue tracker Jira. 
 * @author imolina
 *
 */
public class JiraCrawler implements Crawler {

	private static final Logger LOGGER =
									LoggerFactory.getLogger(JiraCrawler.class);

	private AsynchronousJiraRestClientFactory jiraClientFactory;
	private URI uri;
	private String username;
	private String password;
	private Storage storage;
	private ProjectFactory projectFactory;
	private ContributorFactory contributorFactory;
	private IssueFactory issueFactory;
	private VersionFactory versionFactory;
	private ComponentFactory componentFactory;

	public JiraCrawler(String url, String username, String password, Storage storage,
					ProjectFactory projectFactory, ContributorFactory contributorFactory,
					IssueFactory issueFactory, VersionFactory versionFactory,
					ComponentFactory componentFactory) throws URISyntaxException {

		this.uri = new URI(url);
		this.username = Objects.requireNonNull(username,
												"Username can't be null.");
		this.password = Objects.requireNonNull(password,
												"Password can't be null.");

		// Storage
		this.storage = Objects.requireNonNull(storage, "Storage cannot be null");
		
		// Jira Factories
		this.projectFactory = Objects.requireNonNull(projectFactory,
												"ProjectFactory cannot be null");
		this.contributorFactory = Objects.requireNonNull(contributorFactory,
												"ContributorFactory cannot be null");
		this.issueFactory = Objects.requireNonNull(issueFactory,
												"IssueFactory cannot be null.");
		this.versionFactory = Objects.requireNonNull(versionFactory,
												"VersionFactory cannot be null.");
		this.componentFactory = Objects.requireNonNull(componentFactory,
												"ComponentFactory cannot be null.");

		// Jira Client
		this.jiraClientFactory = new AsynchronousJiraRestClientFactory();
	}

	public void collect(long lastUpdate) {

		LOGGER.info("Started crawling services...");

		try(JiraRestClient client =
				jiraClientFactory.createWithBasicHttpAuthentication(uri,
																	username,
																	password)) {

			Set<org.smartdeveloperhub.harvesters.it.backend.Project> projects = new HashSet<>();

			LOGGER.info("Loading stored contributors");
			// load Contributors from storage
			Map<String, Contributor> contributors = storage.loadContributors();

			LOGGER.info("Exploring projects");
			for (Project jiraProject : getProjects(client)) {

				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> topIssues =
						new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();
				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> childIssues =
						new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();
				Map<String, org.smartdeveloperhub.harvesters.it.backend.Issue> issues =
						new HashMap<String, org.smartdeveloperhub.harvesters.it.backend.Issue>();

				LOGGER.info("Retrieving project issues.");
				Iterable<Issue> jiraIssues = getProjectIssues(client,
																jiraProject.getKey(),
																lastUpdate);

				LOGGER.info("Updating contributors");
				// Scan for new contributors
				updateContributors(contributors, jiraIssues);

				LOGGER.info("Creating issues");
				for (Issue jiraIssue : jiraIssues) {

					org.smartdeveloperhub.harvesters.it.backend.Issue issue =
											issueFactory.createIssue(jiraIssue,
																	contributors);

						issues.put(issue.getId(), issue);
				}

				getTopAndChildIssues(issues, topIssues, childIssues);

				projects.add(projectFactory.createProject(jiraProject,
															topIssues,
															childIssues));

				LOGGER.info("Storing issues and components and versions.");
				// Store components
				storage.storeComponents(jiraProject.getKey(),
										getAllComponents(jiraProject.getKey(),
														jiraProject.getComponents()));
				// Store versions
				storage.storeVersions(jiraProject.getKey(),
										getAllVersions(jiraProject.getKey(),
														jiraProject.getVersions()));
				// Store new issues
				storage.storeIssues(jiraProject.getKey(), issues.values());
			}

			storage.storeContriburos(contributors);
			// Store Project
			storage.storeProjects(projects);

			String jiraVersion = client.getMetadataClient()
											.getServerInfo().claim()
												.getVersion();

			// Storing crawling State for meta-information
			State state = new State();
			state.setJiraApiVersion(jiraVersion);
			state.setLastCrawlingDate(new DateTime());
			state.setStatusMappings(issueFactory.getStatusMapping());
			// TODO: set activities
//			state.setActivity(activities);

			storage.storeState(state);

		} catch (IOException e) {

			LOGGER.error("Exception in client connection. {}", e);
		}

		LOGGER.info("Finished crawling services.");
	}

	private void getTopAndChildIssues(
			Map<String, org.smartdeveloperhub.harvesters.it.backend.Issue> issues,
			Set<org.smartdeveloperhub.harvesters.it.backend.Issue> topIssues,
			Set<org.smartdeveloperhub.harvesters.it.backend.Issue> childIssues) {

		Map<String, org.smartdeveloperhub.harvesters.it.backend.Issue> auxIssues = new HashMap<>(issues);

		// first, take the children
		for (org.smartdeveloperhub.harvesters.it.backend.Issue issue : issues.values()) {

			for (String childId : issue.getChildIssues()) {

				org.smartdeveloperhub.harvesters.it.backend.Issue childIssue = auxIssues.remove(childId);
				childIssues.add(childIssue);
			}
		}

		// Last, the remainder must be Top
		topIssues.addAll(auxIssues.values());
	}

	private void updateContributors(Map<String, Contributor> contributors,
									Iterable<Issue> jiraIssues) {

		for (Issue jiraIssue : jiraIssues) {

			if (jiraIssue.getReporter() != null) {

				Contributor contributor = contributorFactory.createContributor(jiraIssue.getReporter());
				contributors.put(contributor.getId(), contributor);
			}
			if (jiraIssue.getAssignee() != null) {

				Contributor contributor = contributorFactory.createContributor(jiraIssue.getAssignee());
				contributors.put(contributor.getId(), contributor);
			}
		}
	}

	private Set<org.smartdeveloperhub.harvesters.it.backend.Component>
			getAllComponents(String projectId, Iterable<BasicComponent> jiraComponents) {

		Set<org.smartdeveloperhub.harvesters.it.backend.Component> components = new HashSet<>();

		for (BasicComponent component : jiraComponents) {

			components.add(componentFactory.createComponent(projectId, component));
		}

		return components;
	}

	private Set<org.smartdeveloperhub.harvesters.it.backend.Version>
			getAllVersions(String projectId, Iterable<Version> jiraVersions) {

		Set<org.smartdeveloperhub.harvesters.it.backend.Version> versions = new HashSet<>();

		for (Version version : jiraVersions) {

			versions.add(versionFactory.createVersion(projectId, version));
		}

		return versions;
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

		Date update = new Date(lastUpdate);
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