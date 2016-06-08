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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class LocalBackendControllerTest {

	@Before
	public void setUp() {
		System.clearProperty(LocalBackendController.CONFIG_FILE_LOCATION);
	}

	@Test
	public void failsIfDefaultConfigurationFileIsNotAvailable() throws Exception {
		final LocalBackendController sut = new LocalBackendController(URI.create("http://www.example.org:5000/api"));
		try {
			sut.getCollector();
			fail("Should fail if no configuration fail is available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find local configuration file '"+LocalBackendController.DEFAULT_CONFIG_FILE+"'"));
		}
	}

	@Test
	public void failsIfConfiguredConfigurationFileIsNotAvailable() throws Exception {
		System.setProperty(LocalBackendController.CONFIG_FILE_LOCATION,"test.json");
		final LocalBackendController sut = new LocalBackendController(URI.create("http://www.example.org:5000/api"));
		try {
			sut.getCollector();
			fail("Should fail if no configuration fail is available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find local configuration file 'test.json'"));
		}
	}

	@Test
	public void failsIfCannotLoadConfiguredConfigurationFile() throws Exception {
		final Path path = Paths.get("src","test","resources","bad-local-data.json");
		System.setProperty(LocalBackendController.CONFIG_FILE_LOCATION,path.toString());
		final LocalBackendController sut = new LocalBackendController(URI.create("http://www.example.org:5000/api"));
		try {
			sut.getCollector();
			fail("Should fail if no configuration fail is available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not load local configuration file '"+path.toString()+"'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
		}
	}
	@Test
	public void returnsValueIfCanLoadConfigurationFile() throws Exception {
		final Path path = Paths.get("src","test","resources","local-data.json");
		System.setProperty(LocalBackendController.CONFIG_FILE_LOCATION,path.toString());
		final LocalBackendController sut = new LocalBackendController(URI.create("http://www.example.org:5000/api"));
		assertThat(sut.getCollector().getVersion(),equalTo("1.0.0"));
		assertThat(sut.getCollector().getNotifications(),nullValue());
	}

}
