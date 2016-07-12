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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class LocalBackendControllerTest {

	private static final URI BASE = URI.create("http://www.example.org:5000/api");

	@Rule
	public TemporaryFolder util=new TemporaryFolder();

	@Rule
	public TestName name=new TestName();

	private void export(final LocalData data) throws IOException {
		final File tmpFile = this.util.newFile();
		final String rawData = Entities.marshallEntity(data);
		Files.write(rawData, tmpFile, StandardCharsets.UTF_8);
		System.out.printf("Prepared configuration file %s for test %s",tmpFile.getAbsolutePath(),this.name.getMethodName());
		System.setProperty(LocalBackendController.CONFIG_FILE_LOCATION,tmpFile.getAbsolutePath());
	}

	@Before
	public void setUp() {
		System.clearProperty(LocalBackendController.CONFIG_FILE_LOCATION);
	}

	@Test
	public void targetIsAlwaysAvailable() {
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getTarget(),equalTo(BASE));
	}

	@Test
	public void anEmptyStateIsAlwaysAvailable() throws IOException {
		final LocalBackendController sut = new LocalBackendController(BASE);
		final State state = sut.getState();
		assertThat(state,notNullValue());
		assertThat(state.getActivity(),hasSize(0));
		assertThat(state.getJiraApiVersion(),nullValue());
		assertThat(state.getLastCrawlingDate(),nullValue());
		assertThat(state.getStatusMappings(),notNullValue());
	}

	@Test
	public void failsIfSpecifiedConfigurationFileIsNotAvailable() throws Exception {
		final Path path = Paths.get("Unknown/path");
		final LocalBackendController sut = new LocalBackendController(BASE,path);
		try {
			sut.getCollector();
			fail("Should fail if specified configuration fail is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find local configuration file '"+path+"'"));
		}
	}

	@Test
	public void failsIfDefaultConfigurationFileIsNotAvailable() throws Exception {
		final LocalBackendController sut = new LocalBackendController(BASE);
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
		final LocalBackendController sut = new LocalBackendController(BASE);
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
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getCollector();
			fail("Should fail if no configuration fail is available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not load local configuration file '"+path.toString()+"'"));
			assertThat(e.getCause(),instanceOf(IOException.class));
		}
	}
	@Test
	public void failsToRetrieveUnknownContributors() throws Exception {
		final LocalData data = new LocalData();
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getContributor("id");
			fail("Should fail if the contributor is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find contributor 'id'"));
		}
	}

	@Test
	public void failsToRetrieveUnknownCommits() throws Exception {
		final LocalData data = new LocalData();
		data.getCommits().add(new Commit());
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getCommit("id");
			fail("Should fail if the contributor is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find commit 'id'"));
		}
	}

	@Test
	public void failsToRetrieveUnknownProjects() throws Exception {
		final LocalData data = new LocalData();
		data.setProjects(null);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getProject("id");
			fail("Should fail if the project is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find project 'id'"));
		}
	}

	@Test
	public void failsToRetrieveUnknownProjectComponents() throws Exception {
		final LocalData data = new LocalData();
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getProjectComponent("pid","cid");
			fail("Should fail if the component is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find component 'cid' of project 'pid'"));
		}
	}

	@Test
	public void failsToRetrieveUnknownProjectVersions() throws Exception {
		final LocalData data = new LocalData();
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getProjectVersion("pid","cid");
			fail("Should fail if the version is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find version 'cid' of project 'pid'"));
		}
	}

	@Test
	public void failsToRetrieveUnknownProjectIssues() throws Exception {
		final LocalData data = new LocalData();
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		try {
			sut.getProjectIssue("pid","cid");
			fail("Should fail if the issue is not available");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not find issue 'cid' of project 'pid'"));
		}
	}

	@Test
	public void retrievesEntityIfCanLoadConfigurationFileAndDataIsAvailable() throws Exception {
		final Path path = Paths.get("src","test","resources","local-data.json");
		System.setProperty(LocalBackendController.CONFIG_FILE_LOCATION,path.toString());
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getCollector().getVersion(),equalTo("1.0.0"));
		assertThat(sut.getCollector().getNotifications(),nullValue());
	}

	@Test
	public void retrievesEmptyListIfCanLoadConfigurationFileAndNoDataIsAvailable() throws Exception {
		final LocalData data = new LocalData();
		data.setProjects(null);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getProjects(),hasSize(0));
	}

	@Test
	public void retrievesIdsForExistingProjects() throws Exception {
		final LocalData data = new LocalData();
		final Project p1 = new Project();
		p1.setId("id1");
		final Project p2 = new Project();
		p2.setId("id2");
		data.getProjects().add(p1);
		data.getProjects().add(p2);
		data.getProjects().add(new Project());
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getProjects(),hasItems("id1","id2"));
	}

	@Test
	public void retrievesIdsForExistingCommits() throws Exception {
		final LocalData data = new LocalData();
		final Commit p1 = new Commit();
		p1.setId("id1");
		final Commit p2 = new Commit();
		p2.setId("id2");
		data.getCommits().add(p1);
		data.getCommits().add(p2);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getCommits(),hasItems("id1","id2"));
	}

	@Test
	public void retrievesIdsForExistingContributors() throws Exception {
		final LocalData data = new LocalData();
		final Contributor p1 = new Contributor();
		p1.setId("id1");
		final Contributor p2 = new Contributor();
		p2.setId("id2");
		data.getContributors().add(p1);
		data.getContributors().add(p2);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		assertThat(sut.getContributors(),hasItems("id1","id2"));
	}

	@Test
	public void retrievesExistingContributors() throws Exception {
		final LocalData data = new LocalData();
		final Contributor entity = new Contributor();
		entity.setId("id");
		entity.getEmails().add("email");
		data.getContributors().add(entity);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Contributor result = sut.getContributor(entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getEmails(),equalTo(entity.getEmails()));
	}

	@Test
	public void retrievesExistingCommits() throws Exception {
		final LocalData data = new LocalData();
		final Commit entity = new Commit();
		entity.setId("id");
		entity.setHash("hash");
		data.getCommits().add(entity);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Commit result = sut.getCommit(entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getHash(),equalTo(entity.getHash()));
	}

	@Test
	public void retrievesExistingProjects() throws Exception {
		final LocalData data = new LocalData();
		final Project entity = new Project();
		entity.setId("id");
		entity.setName("name");
		data.getProjects().add(entity);
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Project result = sut.getProject(entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getName(),equalTo(entity.getName()));
	}

	@Test
	public void retrievesExistingProjectComponents() throws Exception {
		final LocalData data = new LocalData();
		final Component entity = new Component();
		entity.setId("id");
		entity.setName("name");
		data.getProjectComponents().put("pid", Lists.newArrayList(entity));
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Component result = sut.getProjectComponent("pid",entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getName(),equalTo(entity.getName()));
	}

	@Test
	public void retrievesExistingVersionsComponents() throws Exception {
		final LocalData data = new LocalData();
		final Version entity = new Version();
		entity.setId("id");
		entity.setName("name");
		data.getProjectVersions().put("pid", Lists.newArrayList(entity));
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Version result = sut.getProjectVersion("pid",entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getName(),equalTo(entity.getName()));
	}

	@Test
	public void retrievesExistingProjectIssues() throws Exception {
		final LocalData data = new LocalData();
		final Issue entity = new Issue();
		entity.setId("id");
		entity.setName("name");
		data.getProjectIssues().put("pid", Lists.newArrayList(entity));
		export(data);
		final LocalBackendController sut = new LocalBackendController(BASE);
		final Issue result = sut.getProjectIssue("pid",entity.getId());
		assertThat(result,notNullValue());
		assertThat(result.getId(),equalTo(entity.getId()));
		assertThat(result.getName(),equalTo(entity.getName()));
	}

}
