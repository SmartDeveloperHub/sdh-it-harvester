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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0
 *   Bundle      : it-frontend-dist-0.1.0.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.collector;

import java.io.File;
import java.net.URI;

import org.smartdeveloperhub.harvesters.it.frontend.controller.LocalBackendControllerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.testing.TestingCollectorProvider;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.TestingCollector;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;

final class LocalDataBasedCollectorProvider implements TestingCollectorProvider {

	private final File localData;
	private final int port;

	LocalDataBasedCollectorProvider(final int port, final File localData) {
		this.port=port;
		this.localData=localData;
	}

	@Override
	public TestingCollector provide(final CollectorConfiguration configuration) {
		System.out.printf("Using a local-data based backend%n");
		final URI target = URI.create("http://localhost:"+this.port+"/collector/");
		return
			new LocalDataBasedTestingCollector(
				new LocalBackendControllerFactory().
					create(target,this.localData.toPath()),
				target,
				configuration);
	}
}