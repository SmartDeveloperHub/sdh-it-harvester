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
package org.smartdeveloperhub.harvesters.it.frontend;

import java.io.IOException;
import java.net.URI;

import org.ldp4j.application.ext.Application;
import org.ldp4j.application.ext.ApplicationInitializationException;
import org.ldp4j.application.ext.ApplicationSetupException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.application.setup.Bootstrap;
import org.ldp4j.application.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentHandler;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorHandler;
import org.smartdeveloperhub.harvesters.it.frontend.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueHandler;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectHandler;
import org.smartdeveloperhub.harvesters.it.frontend.publisher.Publisher;
import org.smartdeveloperhub.harvesters.it.frontend.publisher.PublisherFactory;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionHandler;

public final class HarvesterApplication extends Application<HarvesterConfiguration> {

	private static final Logger LOGGER=LoggerFactory.getLogger(HarvesterApplication.class);

	private static final String SERVICE_PATH="service/";

	private URI target;

	private Publisher publisher;

	@Override
	public void setup(final Environment environment, final Bootstrap<HarvesterConfiguration> bootstrap) throws ApplicationSetupException{
		LOGGER.info("Starting IT Harvester Application configuration...");
		final HarvesterConfiguration configuration = bootstrap.configuration();
		this.target=configuration.target();
		if(this.target==null) {
			final String errorMessage="No target Jira Collector configured";
			LOGGER.error("IT Harvester Application configuration failed: {}",errorMessage);
			throw new ApplicationSetupException(errorMessage);
		}
		LOGGER.info("- Target..: {}",configuration.target());

		final BackendController controller = BackendControllers.createController(this.target);

		try {
			this.publisher = PublisherFactory.createPublisher(controller);
		} catch (final IOException e) {
			final String errorMessage = "Could not create publisher for target '"+this.target+"'";
			LOGGER.error("IT Harvester Application configuration failed: {}. Full stacktrace follows: ",errorMessage,e);
			throw new ApplicationSetupException(errorMessage,e);
		}

		environment.lifecycle().register(this.publisher);

		bootstrap.addHandler(new HarvesterHandler());
		bootstrap.addHandler(new ContributorHandler(controller));
		bootstrap.addHandlerClass(ContributorContainerHandler.class);
		bootstrap.addHandler(new CommitHandler(controller));
		bootstrap.addHandlerClass(CommitContainerHandler.class);
		bootstrap.addHandler(new ProjectHandler(controller));
		bootstrap.addHandlerClass(ProjectContainerHandler.class);
		bootstrap.addHandler(new ComponentHandler(controller));
		bootstrap.addHandlerClass(ComponentContainerHandler.class);
		bootstrap.addHandler(new VersionHandler(controller));
		bootstrap.addHandlerClass(VersionContainerHandler.class);
		bootstrap.addHandler(new IssueHandler(controller));
		bootstrap.addHandlerClass(IssueContainerHandler.class);

		environment.
			publishResource(
				IdentityUtil.collectorName(this.target),
				HarvesterHandler.class,
				SERVICE_PATH);

		LOGGER.info("IT Harvester Application configuration completed.");
	}

	@Override
	public void initialize(final WriteSession session) throws ApplicationInitializationException {
		LOGGER.info("Initializing IT Harvester Application...");
		try {
			this.publisher.initialize(session);
			session.saveChanges();
			LOGGER.info("IT Harvester Application initialization completed.");
		} catch (final Exception e) {
			final String errorMessage = "IT Harvester Application initialization failed";
			LOGGER.warn(errorMessage+". Full stacktrace follows: ",e);
			throw new ApplicationInitializationException(errorMessage,e);
		}
	}

	@Override
	public void shutdown() {
		LOGGER.info("Starting IT Harvester Application shutdown...");
		LOGGER.info("IT Harvester Application shutdown completed.");
	}

}
