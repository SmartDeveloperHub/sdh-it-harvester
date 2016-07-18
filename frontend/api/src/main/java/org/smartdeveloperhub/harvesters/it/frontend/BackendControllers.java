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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-api:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-api-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.spi.BackendControllerFactory;

public final class BackendControllers {

	private static final class NullBackendController implements BackendController {

		private final URI target;

		private NullBackendController(final URI target) {
			this.target = target;
		}

		private IOException fail() {
			return new IOException("Could not create controller for interacting with collector '"+getTarget()+"'");
		}

		@Override
		public URI getTarget() {
			return this.target;
		}

		@Override
		public Collector getCollector() throws IOException {
			throw fail();
		}

		@Override
		public State getState() throws IOException {
			throw fail();
		}

		@Override
		public List<String> getContributors() throws IOException {
			throw fail();
		}

		@Override
		public List<String> getCommits() throws IOException {
			throw fail();
		}

		@Override
		public List<String> getProjects() throws IOException {
			throw fail();
		}

		@Override
		public Contributor getContributor(final String id) throws IOException {
			throw fail();
		}

		@Override
		public Commit getCommit(final String commitId) throws IOException {
			throw fail();
		}

		@Override
		public Project getProject(final String id) throws IOException {
			throw fail();
		}

		@Override
		public Component getProjectComponent(final String projectId, final String componentId) throws IOException {
			throw fail();
		}

		@Override
		public Version getProjectVersion(final String projectId, final String versionId) throws IOException {
			throw fail();
		}

		@Override
		public Issue getProjectIssue(final String projectId, final String issueId) throws IOException {
			throw fail();
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(BackendControllers.class);
	private static final AtomicReference<BackendControllerFactory> DEFAULT_FACTORY=new AtomicReference<>();

	public static final String DEFAULT_FACTORY_CLASS_NAME="it.harvester.defaultControllerFactory";

	private BackendControllers() {
	}


	public static void setDefaultFactory(final BackendControllerFactory factory) {
		if(factory==null) {
			LOGGER.debug("Discarded default factory");
		} else {
			LOGGER.debug("Using {} ({}) as default factory",factory,factory.getClass().getName());
		}
		BackendControllers.DEFAULT_FACTORY.set(factory);
	}

	public static BackendController createController(final URI target) {
		final BackendControllerFactory factory = getDefaultFactory();
		if(factory!=null) {
			final BackendController controller = createController(target, factory, true);
			if(controller!=null) {
				return controller;
			}
		}
		return createDynamicController(target);
	}


	private static BackendControllerFactory getDefaultFactory() {
		BackendControllerFactory factory = DEFAULT_FACTORY.get();
		if(factory==null) {
			factory=loadDefaultFactory();
		}
		return factory;
	}


	private static BackendControllerFactory loadDefaultFactory() {
		final String defaultFactoryClassName = System.getProperty(DEFAULT_FACTORY_CLASS_NAME);
		if(defaultFactoryClassName==null) {
			LOGGER.debug("No default factory class name specified");
			return null;
		}
		try {
			return instantiateDefaultFactory(Class.forName(defaultFactoryClassName));
		} catch (final ClassNotFoundException e) {
			LOGGER.warn("Default factory class {} could not be found. Full stacktrace follows",defaultFactoryClassName,e);
			return null;
		}
	}


	private static BackendControllerFactory instantiateDefaultFactory(final Class<?> clazz) {
		if(!BackendControllerFactory.class.isAssignableFrom(clazz)) {
			LOGGER.warn("Default factory class {} does not implement {}",BackendControllerFactory.class.getName());
			return null;
		}
		final Class<? extends BackendControllerFactory> factoryClass = clazz.asSubclass(BackendControllerFactory.class);
		try {
			final BackendControllerFactory factory=factoryClass.newInstance();
			setDefaultFactory(factory);
			return factory;
		} catch (final Exception e) {
			LOGGER.warn("Default factory class {} could not be instantiated. Full stacktrace follows",clazz.getName(),e);
			return null;
		}
	}

	private static BackendController createDynamicController(final URI target) {
		final ServiceLoader<BackendControllerFactory> factories=ServiceLoader.load(BackendControllerFactory.class);
		for(final BackendControllerFactory factory:factories) {
			final BackendController controller = createController(target, factory, false);
			if(controller!=null) {
				return controller;
			}
		}
		LOGGER.error("Could not create controller for target '{}'",target);
		return new NullBackendController(target);
	}


	private static BackendController createController(final URI target, final BackendControllerFactory factory, final boolean isDefault) {
		BackendController controller=null;
		try {
			controller=factory.create(target);
			if(controller!=null) {
				LOGGER.debug("Created controller {} for target '{}'{}",controller.getClass().getName(),target,isDefault?"using default factory":"");
			} else {
				LOGGER.warn("{}ackend controller factory '{}' ({}) could not create factory for target '{}'",isDefault?"Default b":"B",factory,factory.getClass().getName(),target);
			}
		} catch (final Exception e) {
			LOGGER.warn("{}ackend controller factory '{}' ({}) failed to create factory for target '{}'",isDefault?"Default b":"B",factory,factory.getClass().getName(),target,e);
		}
		return controller;
	}

}

