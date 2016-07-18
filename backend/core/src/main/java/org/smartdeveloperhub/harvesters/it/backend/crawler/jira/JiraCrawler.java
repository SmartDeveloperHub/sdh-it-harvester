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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-backend-core-0.2.0-SNAPSHOT.jar
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
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ComponentFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ContributorFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ProjectFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.VersionFactory;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;
import org.smartdeveloperhub.harvesters.it.notification.NotificationPublisher;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.Modification;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

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
	private NotificationPublisher publisher;
	private String instance;
	private Storage storage;
	private ProjectFactory projectFactory;
	private ContributorFactory contributorFactory;
	private IssueFactory issueFactory;
	private VersionFactory versionFactory;
	private ComponentFactory componentFactory;

	public JiraCrawler(String url, String username, String password,
					NotificationPublisher publisher, String instance, Storage storage,
					ProjectFactory projectFactory, ContributorFactory contributorFactory,
					IssueFactory issueFactory, VersionFactory versionFactory,
					ComponentFactory componentFactory) throws URISyntaxException {

		this.uri = new URI(url);
		this.username = Objects.requireNonNull(username,
												"Username can't be null.");
		this.password = Objects.requireNonNull(password,
												"Password can't be null.");

		this.publisher = Objects.requireNonNull(publisher,
												"NotificationPublisher cannot be null.");

		this.instance = Objects.requireNonNull(instance, "Instance cann't be null");

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

//			Set<org.smartdeveloperhub.harvesters.it.backend.Project> projects = new HashSet<>();

//			LOGGER.info("Loading stored contributors");
			// load Contributors from storage
			Map<String, Contributor> contributors = storage.loadContributors();

			// load projects from storage
			Map<String, org.smartdeveloperhub.harvesters.it.backend.Project> projects = storage.loadProjects();

//			LOGGER.info("Exploring projects");
			for (Project jiraProject : getProjects(client)) {

				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> topIssues = 
						new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();
				Set<org.smartdeveloperhub.harvesters.it.backend.Issue> childIssues =
						new HashSet<org.smartdeveloperhub.harvesters.it.backend.Issue>();
				Map<String, org.smartdeveloperhub.harvesters.it.backend.Issue> issues =
						new HashMap<String, org.smartdeveloperhub.harvesters.it.backend.Issue>();

//				LOGGER.info("Retrieving project issues.");
				Iterable<Issue> jiraIssues = getProjectIssues(client,
																jiraProject.getKey(),
																lastUpdate);

//				LOGGER.info("Updating contrCibutors");
				// Scan for new contributors
				Set<String> newContributors = updateContributors(contributors, jiraIssues);

				if (!newContributors.isEmpty()) {

					ContributorCreatedEvent event = new ContributorCreatedEvent();
					event.setNewContributors(Lists.newArrayList(newContributors));
					sendNotification(event);
				}

//				LOGGER.info("Creating issues");
				for (Issue jiraIssue : jiraIssues) {

					org.smartdeveloperhub.harvesters.it.backend.Issue issue =
											issueFactory.createIssue(jiraIssue,
																	contributors);

						issues.put(issue.getId(), issue);
				}

				getTopAndChildIssues(issues, topIssues, childIssues);

				org.smartdeveloperhub.harvesters.it.backend.Project project;
				project = projectFactory.createProject(jiraProject,
														topIssues,
														childIssues);

				// Check for all previous issues
				org.smartdeveloperhub.harvesters.it.backend.Project oldProject =
											projects.get(jiraProject.getKey());

				if (oldProject != null) {

					Set<String> newTop = Sets.difference(project.getTopIssues(), oldProject.getTopIssues());
					Set<String> newIssues = Sets.difference(project.getIssues(), oldProject.getIssues());
					Set<String> updatedTop = Sets.difference(project.getTopIssues(), newTop);
					Set<String> updatedIssues = Sets.difference(project.getIssues(), newIssues);

					project.getTopIssues().addAll(oldProject.getTopIssues());
					project.getIssues().addAll(oldProject.getIssues());

					ProjectUpdatedEvent event = new ProjectUpdatedEvent();
					Set<String> news = new HashSet<>();
					news.addAll(newTop);
					news.addAll(newIssues);
					Set<String> updated = new HashSet<>();
					updated.addAll(updatedTop);
					updated.addAll(updatedIssues);

					addIssueChanges(event, news, updated);
					event.setProject(project.getId());
					if (!news.isEmpty() || !updated.isEmpty()) {
						sendNotification(event);
					}

				} else {

					ProjectCreatedEvent event = new ProjectCreatedEvent();
					event.setNewProjects(Lists.newArrayList(jiraProject.getKey()));
					sendNotification(event);
				}

				projects.put(jiraProject.getKey(), project);

				// Store components & notifying new ones
				Set<Component> components = getAllComponents(jiraProject.getKey(), jiraProject.getComponents());

				Set<String> componentIds = new HashSet<>();
				for (Component component : components) {
					componentIds.add(component.getId());
				}

//				LOGGER.info("Storing issues and components and versions.");
				Map<String, Component> oldComponentsMap = storage.loadComponents(jiraProject.getKey());

				Set<String> newComponents = Sets.difference(componentIds, oldComponentsMap.keySet());

				ProjectUpdatedEvent event = new ProjectUpdatedEvent();
				event.setProject(jiraProject.getKey());
				for (String id : newComponents) {
					event.append(Modification.create().component(id));
				}

				storage.storeComponents(jiraProject.getKey(), components);

				// Store versions
				Set<org.smartdeveloperhub.harvesters.it.backend.Version> versions = getAllVersions(jiraProject.getKey(), jiraProject.getVersions());

				Set<String> versionIds = new HashSet<>();
				for (org.smartdeveloperhub.harvesters.it.backend.Version version : versions) {
					versionIds.add(version.getId());
				}

				Map<String, org.smartdeveloperhub.harvesters.it.backend.Version> oldVersionsMap = storage.loadVersions(jiraProject.getKey());

				Set<String> newVersions = Sets.difference(versionIds, oldVersionsMap.keySet());

				for (String id : newVersions) {
					event.append(Modification.create().version(id));
				}

				if (!newComponents.isEmpty() || !newVersions.isEmpty()) {

					sendNotification(event);
				}

				storage.storeVersions(jiraProject.getKey(), versions);
				// Store new issues
				storage.storeIssues(jiraProject.getKey(), issues.values());
			}

			storage.storeContriburos(contributors);
			// Store Project
			storage.storeProjects(projects.values());

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

	private void addIssueChanges(ProjectUpdatedEvent event, Set<String> newIssues, Set<String> updatedIssues) {

		for (String id : newIssues) {

			event.append(Modification.create().issue(id));
		}

		for (String id : updatedIssues) {

			event.append(Modification.update().issue(id));
		}
	}

	/**
	 * Method to send notification through the publisher adding timestamp and
	 * instance information.
	 * @param event Event to send.
	 * @throws IOException when an I/O exception occurs.
	 */
	private void sendNotification(Event event) throws IOException {

		event.setTimestamp(System.currentTimeMillis());
		event.setInstance(instance);
		publisher.publish(event);
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

	/**
	 * Update Contributors Map and return the new ones.
	 * @param contributors Map of contributors
	 * @param jiraIssues a jira issue
	 * @return Ids of contributors that were not present in the map.
	 */
	private Set<String> updateContributors(Map<String, Contributor> contributors,
									Iterable<Issue> jiraIssues) {

		Set<String> newContributors = new HashSet<>(); 

		for (Issue jiraIssue : jiraIssues) {

			if (jiraIssue.getReporter() != null) {

				Contributor added = addContributor(contributors, jiraIssue.getReporter());

				if (added != null) {

					newContributors.add(added.getId());
				}
			}
			if (jiraIssue.getAssignee() != null) {

				Contributor added = addContributor(contributors, jiraIssue.getAssignee());

				if (added != null) {

					newContributors.add(added.getId());
				}
			}
		}

		return newContributors;
	}

	/**
	 * Add contributor to the map and returned if there a new one.
	 * @param contributors map of contributors
	 * @param user information regarding user.
	 * @return new Contributor or null if there is not.
	 */
	private Contributor addContributor(Map<String, Contributor> contributors, User user) {

		Contributor contributor = contributorFactory.createContributor(user);
		Contributor oldContributor = contributors.get(contributor.getId());

		if (oldContributor != null) {
			// Update mails list
			contributor.getEmails().addAll(oldContributor.getEmails());

		}

		contributors.put(contributor.getId(), contributor);

		return oldContributor != null ? null : contributor;
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