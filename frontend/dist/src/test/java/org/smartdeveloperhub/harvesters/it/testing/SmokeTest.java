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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.controller.LocalBackendControllerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.controller.RemoteBackendControllerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.spi.BackendControllerFactory;

public class SmokeTest {

	private static final Logger LOGGER=LoggerFactory.getLogger(SmokeTest.class);

	private static File[] applicationDependencies() {
		return
			Maven.
				configureResolver().
					loadPomFromFile("target/test-classes/pom.xml").
					importCompileAndRuntimeDependencies().
					resolve().
					withTransitivity().
					asFile();
	}

	private static WebArchive baseApplicationArchive(final String archiveName) {
		return
			ShrinkWrap.
				create(WebArchive.class,archiveName).
					addAsLibraries(applicationDependencies()).
					addAsResource("log4j.properties").
					setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
	}

	public static WebArchive locallyBackedCollector(final String archiveName) throws Exception {
		try {
			return
				baseApplicationArchive(archiveName).
					addAsServiceProvider(BackendControllerFactory.class,LocalBackendControllerFactory.class);
		} catch (final Exception e) {
			LOGGER.error("Could not create locally backed application archive",e);
			throw e;
		}
	}

	public static WebArchive remotelyBackedCollector(final String archiveName) throws Exception {
		try {
			return
				baseApplicationArchive(archiveName).
					addAsServiceProvider(BackendControllerFactory.class,RemoteBackendControllerFactory.class);
		} catch (final Exception e) {
			LOGGER.error("Could not create remotelly backed application archive",e);
			throw e;
		}
	}

}
