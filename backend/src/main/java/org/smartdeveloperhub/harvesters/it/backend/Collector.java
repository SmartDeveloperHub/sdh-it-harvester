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

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Class for crawling information from the issue tracker Jira. 
 * @author imolina
 *
 */
public class Collector implements Runnable {

    private static final Logger logger =
                                LoggerFactory.getLogger(Collector.class);
    
    private AsynchronousJiraRestClientFactory factory;
    private URI uri;
    private String username;
    private String password;
    private RedisStorer database;

    public Collector(String url, String username, String password,
                     RedisStorer database) throws URISyntaxException {

        this.uri = new URI(url);
        this.username = Objects.requireNonNull(username,
                                               "Username can't be null.");
        this.password = Objects.requireNonNull(password,
                                               "Password can't be null.");
        this.database = Objects.requireNonNull(database,
                                               "Database can't be null.");
        this.factory = new AsynchronousJiraRestClientFactory();
    }

    @Override
    public void run() {
        // TODO: Add functionality
        logger.info("Started crawling services...");
        JiraRestClient client = factory.createWithBasicHttpAuthentication(
                                                                      uri,
                                                                      username,
                                                                      password);

        // TODO: Refactor to erase all System.out
        exploreProjects(client);

        try {

            client.close();
        } catch (IOException e) {

            logger.error("Error when trying to close Jira Client.", e);
        }
        logger.info("Finished crawling services.");
    }


    void exploreProjects(JiraRestClient client) {

        final Iterable<BasicProject> projects = client.getProjectClient().getAllProjects().claim();
        int totalProjects = 0;
        int totalIssues = 0;
        for(final BasicProject project : projects) {
            totalProjects++;
            System.out.printf("<%s> Project %s (%s)%n",project.getSelf(),
                              project.getName(),
                              project.getKey());
            totalIssues += exploreProjectIssues(client, project);
        }
        System.out.printf(">> Total projects: %d%n", totalProjects);
        System.out.printf(">> Total issues..: %d%n", totalIssues);
    }

    void exploreIssue(JiraRestClient client, String issueKey) {

        final Issue issue = client.getIssueClient().getIssue(issueKey,
                                      Lists.newArrayList(Expandos.CHANGELOG,
                                                         Expandos.TRANSITIONS))
                                  .claim();
        if(issue != null) {

            System.out.println("Specific issue:");
            showIssue(issue);
        }
    }

    private int exploreProjectIssues(JiraRestClient client,
                                     BasicProject project) {

        final SearchResult searchResult = client.getSearchClient()
                               .searchJql("project = \"" + project.getKey() +
                                          "\"")
                               .claim();
        final int projectIssues = searchResult.getTotal();
        if(projectIssues == 0) {

            System.out.println("No issues found");
        } else {

            System.out.printf("Found %d issues (%d to %d):%n", projectIssues,
                              searchResult.getStartIndex() + 1,
                              searchResult.getStartIndex() +
                              Math.min(searchResult.getMaxResults(),
                                       projectIssues));

            for(final Issue issue : searchResult.getIssues()) {

                System.out.printf("- <%s> [%-11s] %s : %s%n", issue.getSelf(),
                                  issue.getIssueType().getName(),
                                  issue.getKey(),
                                  firstLine(issue.getDescription()));
            }
        }
        return projectIssues;
    }

    private void showIssue(final Issue issue) {

        System.out.printf("- <%s> [%-11s] %s : %s%n", issue.getSelf(),
                          issue.getIssueType().getName(), issue.getKey(),
                          firstLine(issue.getDescription()));
        System.out.printf("  * Transitions @ %s%n", issue.getTransitionsUri());
        final Iterable<ChangelogGroup> changelogs = issue.getChangelog();
        if(changelogs == null) {

            System.out.println("  * No changes");
        } else {

            System.out.println("  * Changes:");
            for(final ChangelogGroup changelog : changelogs) {
                System.out.printf("    + In %s by %s:%n",
                                  changelog.getCreated(),
                                  changelog.getAuthor().getDisplayName());
                for(final ChangelogItem item : changelog.getItems()) {
                    System.out.printf("      - %s [%s] from '%s' to '%s'%n",
                                      item.getField(),
                                      item.getFieldType().name(),
                                      item.getFromString(), item.getToString());
                }
            }
        }
    }

    private static String firstLine(final String description) {

        if(description == null) {

            return "<NO DESCRIPTION AVAILABLE>";
        }

        final String[] split = description.split("\\n(\\r)?|\\r(\\n)?");
        return split[0];
    }
}