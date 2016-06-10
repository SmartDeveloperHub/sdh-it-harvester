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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.JiraCrawler;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ProjectFactory;
import org.smartdeveloperhub.harvesters.it.backend.utils.MappingLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class for the backend of the Issue Tracker Harvester.
 * @author imolina
 *
 */
public class Orchestrator {

	private static final Logger logger =
									LoggerFactory.getLogger(Orchestrator.class);

//	private final static String SOFTWARE_NAME = "softwareName";
//	private final static String SOFTWARE_VERSION = "softwareVersion";
	private final static String JIRA_URL = "jiraUrl";
	private final static String JIRA_USERNAME = "jiraUsername";
	private final static String JIRA_PASSWORD = "jiraPassword";
//	private final static String REDIS_URL = "redisUrl";
//	private final static String SERVLET_PORT = "servletPort";
//	private final static String SERVLET_PATH = "servletPath";
	private final static String CRAWLER_PERIOD = "collectorPeriodicity";
	
	private Fetcher fetcher;
	private ExecutorService executorService;

	public Orchestrator(ExecutorService executor) {

		this.executorService = Objects.requireNonNull(
											executor,
											"ExecutorService can't be null.");
	}

	public void start() throws IOException {

		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/config.properties"));

		// Read config
//		String name = properties.getProperty(SOFTWARE_NAME);
//		String version = properties.getProperty(SOFTWARE_VERSION);
		String url = properties.getProperty(JIRA_URL);
		String username = properties.getProperty(JIRA_USERNAME);
		String password = properties.getProperty(JIRA_PASSWORD);

//		String redisServer = properties.getProperty(REDIS_URL);

//		int servletPort = Integer.parseInt(properties.getProperty(SERVLET_PORT));
//		String servletPath = properties.getProperty(SERVLET_PATH);
		long crawlerTime = Long.parseLong(properties.getProperty(CRAWLER_PERIOD));

		// Loading mappings values
		MappingLoader mappingLoader = new MappingLoader();
		Map<String, Status> statusMapping =
								mappingLoader.load("/mappings/status.properties",
													Status.class);
		Map<String, Priority> priorityMapping =
								mappingLoader.load("/mappings/priority.properties",
													Priority.class);
		Map<String, Severity> severityMapping =
								mappingLoader.load("/mappings/severity.properties",
													Severity.class);
		Map<String, Type> typeMapping =
								mappingLoader.load("/mappings/type.properties",
													Type.class);

		ProjectFactory projectFactory = new ProjectFactory();
		IssueFactory issueFactory = new IssueFactory(statusMapping,
													priorityMapping,
													severityMapping,
													typeMapping);

		try {

			Crawler crawler = new JiraCrawler(url, username, password,
												projectFactory, issueFactory);
			fetcher = new Fetcher(crawler);

		((ScheduledThreadPoolExecutor) executorService).scheduleAtFixedRate(
				fetcher, 0, crawlerTime, TimeUnit.MINUTES);

		} catch (URISyntaxException e) {

			logger.error("Error when trying to construct collector. {}", e);
		}

	}

	public static void main(String[] args) {

		Orchestrator orchestrator = new Orchestrator(
				Executors.newScheduledThreadPool(1));

		try {

			orchestrator.start();

		} catch (IOException e) {

			logger.error("Error when starting services. {}", e);
		}
	}
}