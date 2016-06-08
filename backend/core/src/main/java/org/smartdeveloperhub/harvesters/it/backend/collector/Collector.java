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
package org.smartdeveloperhub.harvesters.it.backend.collector;

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Class for crawling information from the issue tracker Jira. 
 * @author imolina
 *
 */
public class Collector implements Runnable {

	private static final Logger logger =
									LoggerFactory.getLogger(Collector.class);

	private AsynchronousJiraRestClientFactory jiraClientFactory;
	private URI uri;
	private String username;
	private String password;
	private IssueFactory issueFactory;
	private long lastUpdateTimeStamp;

	public Collector(String url, String username, String password,
					IssueFactory issueFactory) throws URISyntaxException {

		this.uri = new URI(url);
		this.username = Objects.requireNonNull(username,
												"Username can't be null.");
		this.password = Objects.requireNonNull(password,
												"Password can't be null.");
		this.issueFactory = Objects.requireNonNull(issueFactory,
												"IssueFactory cannot be null.");
		this.jiraClientFactory = new AsynchronousJiraRestClientFactory();
		this.lastUpdateTimeStamp = 0L;
	}

	@Override
	public void run() {

		logger.info("Started crawling services...");

		try(JiraRestClient client =
				jiraClientFactory.createWithBasicHttpAuthentication(uri,
																	username,
																	password)) {

			client.getSessionClient()
			
			for (BasicProject project : getProjects(client)) {

				for (String issueId : getProjectIssues(client, project.getKey(),
														lastUpdateTimeStamp)) {

					Issue jiraIssue = client.getIssueClient().getIssue(issueId, Arrays.asList(new Expandos[] {Expandos.CHANGELOG})).get();

					org.smartdeveloperhub.harvesters.it.backend.Issue issue = issueFactory.createIssue(jiraIssue);

					// TODO: Store issue
//					System.out.println(issue);
				}
			}

			lastUpdateTimeStamp = System.currentTimeMillis();
		} catch(Exception e) {

			logger.error("Exception in Collector. {}", e);

		}

		logger.info("Finished crawling services.");
	}

	private Iterable<BasicProject> getProjects(JiraRestClient client)
							throws InterruptedException, ExecutionException {

		return client.getProjectClient().getAllProjects().get();
	}

	private Iterable<String> getProjectIssues(JiraRestClient client,
												String projectId,
												long lastUpdate) {

		Set<String> issuesIds = new HashSet<>();

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
				issuesIds.add(issue.getKey());
			}

			searchResult = client.getSearchClient()
									.searchJql(query, maxResult,
											i + maxResult, null)
									.claim();
		}

		return issuesIds;
	}
}