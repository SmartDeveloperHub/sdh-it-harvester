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

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.JiraCrawler;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.Exhibitor;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.ExhibitorService;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.ITHarvesterEntityProvider;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ComponentFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ContributorFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.ProjectFactory;
import org.smartdeveloperhub.harvesters.it.backend.factories.jira.VersionFactory;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;
import org.smartdeveloperhub.harvesters.it.backend.storage.redis.RedisStorage;
import org.smartdeveloperhub.harvesters.it.backend.utils.MappingLoader;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.NotificationPublisher;

import java.io.File;
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

	private static final Logger LOGGER =
									LoggerFactory.getLogger(Orchestrator.class);

	private final static String SOFTWARE_VERSION = "softwareVersion";
	private final static String JIRA_URL = "jiraUrl";
	private final static String JIRA_USERNAME = "jiraUsername";
	private final static String JIRA_PASSWORD = "jiraPassword";
	private final static String REDIS_SERVER = "redisServer";
	private final static String REDIS_PORT = "redisPort";

	private final static String INSTANCE = "instance";
	private final static String BROKER_HOST = "brokerHost";
	private final static String BROKER_PORT = "brokerPort";
	private final static String VIRTUAL_HOST = "virtualHost";
	private final static String EXCHANGE_NAME = "exchange_name";

	private final static String SERVLET_PORT = "servletPort";
	private final static String SERVLET_PATH = "servletPath";
	private final static String CRAWLER_PERIOD = "collectorPeriodicity";

	private ExecutorService executorService;
	private NotificationPublisher publisher;

	public Orchestrator(ExecutorService executor) {

		this.executorService = Objects.requireNonNull(
											executor,
											"ExecutorService can't be null.");
	}

	public void start() throws IOException, LifecycleException, URISyntaxException {

		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("/config.properties"));

		// Read config
		String version = properties.getProperty(SOFTWARE_VERSION);
		String url = properties.getProperty(JIRA_URL);
		String username = properties.getProperty(JIRA_USERNAME);
		String password = properties.getProperty(JIRA_PASSWORD);

		String redisServer = properties.getProperty(REDIS_SERVER);
		int redisPort = Integer.parseInt(properties.getProperty(REDIS_PORT));

		int servletPort = Integer.parseInt(properties.getProperty(SERVLET_PORT));
		String servletPath = properties.getProperty(SERVLET_PATH);
		long crawlerTime = Long.parseLong(properties.getProperty(CRAWLER_PERIOD));

		String instance = properties.getProperty(INSTANCE, "http://localhost");
		String brokerHost = properties.getProperty(BROKER_HOST, "localhost");
		int brokerPort = Integer.parseInt(properties.getProperty(BROKER_PORT, "5672"));
		String virtualHost = properties.getProperty(VIRTUAL_HOST, "/");
		String exchangeName = properties.getProperty(EXCHANGE_NAME, "itcollector");

		// Setup of the notification publisher
		CollectorConfiguration amqpConfig = new CollectorConfiguration();
		amqpConfig.setInstance(instance);
		amqpConfig.setBrokerHost(brokerHost);
		amqpConfig.setBrokerPort(brokerPort);
		amqpConfig.setVirtualHost(virtualHost);
		amqpConfig.setExchangeName(exchangeName);

		publisher = NotificationPublisher.newInstance(amqpConfig);
		publisher.start();

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
		ContributorFactory contributorFactory = new ContributorFactory();
		IssueFactory issueFactory = new IssueFactory(statusMapping,
													priorityMapping,
													severityMapping,
													typeMapping);
		VersionFactory versionFactory = new VersionFactory();
		ComponentFactory componentFactory = new ComponentFactory();
		Storage storage = new RedisStorage(redisServer, redisPort);
		Exhibitor exhibitor = new Exhibitor(version, storage);

		// Deploying tomcat to listen incoming CAPs
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(servletPort);

		File base = new File(System.getProperty("java.io.tmpdir"));
		Context rootCtx = tomcat.addContext("", base.getAbsolutePath());

		ServletContainer servlet = new ServletContainer(
										new ResourceConfig()
											.register(ITHarvesterEntityProvider.class)
											.register(new ExhibitorService(exhibitor)));

		Tomcat.addServlet(rootCtx, "ITHarvester", servlet);
		rootCtx.addServletMapping(servletPath, "ITHarvester");

		tomcat.start();
		
		Crawler crawler = new JiraCrawler(url, username, password, publisher,
											instance, storage,
											projectFactory, contributorFactory,
											issueFactory, versionFactory,
											componentFactory);
		Fetcher fetcher = new Fetcher(crawler);

		((ScheduledThreadPoolExecutor) executorService).scheduleAtFixedRate(
				fetcher, 0, crawlerTime, TimeUnit.MINUTES);

	}

	public void shutdown() {

		executorService.shutdown();
		if (publisher != null) {

			publisher.shutdown();
		}
	}

	public static void main(String[] args) {

		Orchestrator orchestrator = new Orchestrator(
										Executors.newScheduledThreadPool(1));

		try {

			orchestrator.start();

		} catch (Exception e) {

			LOGGER.error("Exception while running service. {}", e);
			orchestrator.shutdown();
		}
	}
}