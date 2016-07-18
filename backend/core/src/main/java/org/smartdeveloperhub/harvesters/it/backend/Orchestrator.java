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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0
 *   Bundle      : it-backend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ComponentFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ContributorFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.IssueFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.ProjectFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories.VersionFactory;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.Exhibitor;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.ExhibitorService;
import org.smartdeveloperhub.harvesters.it.backend.exhibitor.ITHarvesterEntityProvider;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;
import org.smartdeveloperhub.harvesters.it.backend.storage.redis.RedisStorage;
import org.smartdeveloperhub.harvesters.it.backend.utils.MappingLoader;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;
import org.smartdeveloperhub.harvesters.it.notification.NotificationPublisher;

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
	private final static String CONTRIBUTORS_FILE = "contributorsFile";

	private final ExecutorService executorService;
	private NotificationPublisher publisher;

	public Orchestrator(final ExecutorService executor) {

		this.executorService = Objects.requireNonNull(
											executor,
											"ExecutorService can't be null.");
	}

	public void start(final Properties properties) throws IOException, LifecycleException, URISyntaxException {


		// Read config
		final String version = properties.getProperty(SOFTWARE_VERSION);
		final String url = properties.getProperty(JIRA_URL);
		final String username = properties.getProperty(JIRA_USERNAME);
		final String password = properties.getProperty(JIRA_PASSWORD);

		final String redisServer = properties.getProperty(REDIS_SERVER);
		final int redisPort = Integer.parseInt(properties.getProperty(REDIS_PORT));

		final int servletPort = Integer.parseInt(properties.getProperty(SERVLET_PORT));
		final String servletPath = properties.getProperty(SERVLET_PATH);
		final long crawlerTime = Long.parseLong(properties.getProperty(CRAWLER_PERIOD));

		final String instance = properties.getProperty(INSTANCE, "http://localhost");
		final String brokerHost = properties.getProperty(BROKER_HOST, "localhost");
		final int brokerPort = Integer.parseInt(properties.getProperty(BROKER_PORT, "5672"));
		final String virtualHost = properties.getProperty(VIRTUAL_HOST, "/");
		final String exchangeName = properties.getProperty(EXCHANGE_NAME, "itcollector");

		final String contributorsFile = properties.getProperty(CONTRIBUTORS_FILE, "");

		// Setup of the notification publisher
		final CollectorConfiguration amqpConfig = new CollectorConfiguration();
		amqpConfig.setInstance(instance);
		amqpConfig.setBrokerHost(brokerHost);
		amqpConfig.setBrokerPort(brokerPort);
		amqpConfig.setVirtualHost(virtualHost);
		amqpConfig.setExchangeName(exchangeName);
		final Notifications notifications = new Notifications();
		notifications.setBrokerHost(brokerHost);
		notifications.setBrokerPort(brokerPort);
		notifications.setExchangeName(exchangeName);
		notifications.setVirtualHost(virtualHost);

		this.publisher = NotificationPublisher.newInstance(amqpConfig);
		this.publisher.start();

		// Loading mappings values
		final MappingLoader mappingLoader = new MappingLoader();
		final Map<String, Status> statusMapping =
								mappingLoader.load("/mappings/status.properties",
													Status.class);
		final Map<String, Priority> priorityMapping =
								mappingLoader.load("/mappings/priority.properties",
													Priority.class);
		final Map<String, Severity> severityMapping =
								mappingLoader.load("/mappings/severity.properties",
													Severity.class);
		final Map<String, Type> typeMapping =
								mappingLoader.load("/mappings/type.properties",
													Type.class);

		final ProjectFactory projectFactory = new ProjectFactory();
		final ContributorFactory contributorFactory = new ContributorFactory();
		final IssueFactory issueFactory = new IssueFactory(statusMapping,
													priorityMapping,
													severityMapping,
													typeMapping);
		final VersionFactory versionFactory = new VersionFactory();
		final ComponentFactory componentFactory = new ComponentFactory();
		final Storage storage = new RedisStorage(redisServer, redisPort);

		/*
		 * TODO: remove this section
		 * Contributors pre load <PROVISIONAL>
		 */

		if (Files.exists(Paths.get(contributorsFile))) {

			LOGGER.info("Loading contributors from {}.", Paths.get(contributorsFile).toAbsolutePath());
			final List<String> contributorsInfo = Files.readAllLines(Paths.get(contributorsFile), Charset.defaultCharset());
			final Map<String, Contributor> contributors = new HashMap<>();

			for (final String contributorInfo : contributorsInfo) {

				final String id = contributorInfo.split(" ")[0];
				final String mail = contributorInfo.split(" ")[1];

				final Contributor contributor = new Contributor();
				contributor.setId(id);
				contributor.getEmails().add(mail);
				contributors.put(id, contributor);
			}

			storage.storeContriburos(contributors);
		}
		/*
		 * End of provisional section
		 */

		final Exhibitor exhibitor = new Exhibitor(version, notifications, storage);

		// Deploying tomcat
		final Tomcat tomcat = new Tomcat();
		tomcat.setPort(servletPort);

		final File base = new File(System.getProperty("java.io.tmpdir"));
		final Context rootCtx = tomcat.addContext("", base.getAbsolutePath());

		final ServletContainer servlet = new ServletContainer(
										new ResourceConfig()
											.register(ITHarvesterEntityProvider.class)
											.register(new ExhibitorService(exhibitor)));

		Tomcat.addServlet(rootCtx, "ITHarvester", servlet);
		rootCtx.addServletMapping(servletPath, "ITHarvester");

		tomcat.start();

		final Crawler crawler = new JiraCrawler(url, username, password, this.publisher,
											instance, storage,
											projectFactory, contributorFactory,
											issueFactory, versionFactory,
											componentFactory);
		final Fetcher fetcher = new Fetcher(crawler);

		((ScheduledThreadPoolExecutor) this.executorService).scheduleAtFixedRate(
				fetcher, 0, crawlerTime, TimeUnit.MINUTES);

	}

	public void shutdown() {

		this.executorService.shutdown();
		if (this.publisher != null) {

			this.publisher.shutdown();
		}
	}

	public static void main(final String[] args) {
		if(args.length==0) {
			System.err.println("ERROR: No configuration file specified");
			System.exit(-1);
		}
		final Path path=Paths.get(args[0]);
		final File file = path.toFile();
		if(!file.exists()) {
			System.err.println("ERROR: File '"+path+"' not found");
			System.exit(-2);
		} else if(file.isDirectory()) {
			System.err.println("ERROR: Path '"+path+"' points to a directory");
			System.exit(-3);
		}
		try(FileReader reader = new FileReader(file)) {
			final Properties properties = new Properties();
			properties.load(reader);
			final Orchestrator orchestrator =
				new Orchestrator(Executors.newScheduledThreadPool(1));
			try {
				orchestrator.start(properties);
			} catch (final Exception e) {
				LOGGER.error("Exception while running service. Full stacktrace follows",e);
				orchestrator.shutdown();
				System.exit(-5);
			}
		} catch(final Exception e) {
			LOGGER.error("Could not load configuration file {}. Full stacktrace follows",file.getAbsolutePath(),e);
			System.exit(-5);
		}

	}
}