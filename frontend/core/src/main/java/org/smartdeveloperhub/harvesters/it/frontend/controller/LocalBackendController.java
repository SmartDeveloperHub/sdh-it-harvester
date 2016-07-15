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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Identifiable;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

final class LocalBackendController implements BackendController {

	public static final String DEFAULT_CONFIG_FILE = "local-data.json";

	public static final String CONFIG_FILE_LOCATION = "it.harvester.localController";

	private static final Logger LOGGER=LoggerFactory.getLogger(LocalBackendController.class);

	private final URI target;
	private Path path;
	private LocalData data;

	LocalBackendController(final URI target, final Path path) {
		this.target = target;
		if(path!=null) {
			this.path=path;
		} else {
			String location = System.getProperty(CONFIG_FILE_LOCATION);
			if(location==null) {
				location=DEFAULT_CONFIG_FILE;
			}
			this.path=Paths.get(location);
		}
	}

	LocalBackendController(final URI target) {
		this(target,null);
	}

	private synchronized LocalData getLocalData() throws IOException {
		if(this.data==null) {
			this.data=loadLocalData();
		}
		return this.data;
	}

	private LocalData loadLocalData() throws IOException {
		if(!Files.isRegularFile(this.path)) {
			LOGGER.error("Could not find local configuration file '{}'",this.path);
			throw new IOException("Could not find local configuration file '"+this.path+"'");
		}
		try {
			final String content=
				Resources.
					toString(
						this.path.toFile().toURI().toURL(),
						StandardCharsets.UTF_8);
			return Entities.unmarshallEntity(content,LocalData.class);
		} catch (final IOException e) {
			LOGGER.error("Could not load local configuration file '{}'. Full stacktrace follows",this.path,e);
			throw new IOException("Could not load local configuration file '"+this.path+"'",e);
		}
	}

	private <V,T extends Identifiable<V>> List<V> getIdentifiers(final List<T> identifiables) {
		final List<V> result=Lists.newArrayList();
		if(identifiables!=null) {
			for(final T identifiable:identifiables) {
				final V id = identifiable.getId();
				if(id!=null) {
					result.add(id);
				}
			}
		}
		return result;
	}

	private <V,T extends Identifiable<V>> T findIdentifiable(final List<T> identifiables, final V identifier) throws IOException {
		if(identifiables!=null) {
			for(final T identifiable:identifiables) {
				if(identifier.equals(identifiable.getId())) {
					return identifiable;
				}
			}
		}
		return null;
	}

	private <T> T checkNotNull(final T element, final String format, final Object... args) throws IOException {
		if(element!=null) {
			return element;
		}
		throw new IOException("Could not find "+String.format(format, args));
	}

	@Override
	public URI getTarget() {
		return this.target;
	}

	@Override
	public Collector getCollector() throws IOException {
		return checkNotNull(getLocalData().getCollector(),"collector");
	}

	@Override
	public State getState() throws IOException {
		return new State();
	}

	@Override
	public List<String> getContributors() throws IOException {
		return getIdentifiers(getLocalData().getContributors());
	}

	@Override
	public Contributor getContributor(final String contributorId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getContributors(),contributorId),
				"contributor '%s'",contributorId);
	}

	@Override
	public List<String> getCommits() throws IOException {
		return getIdentifiers(getLocalData().getCommits());
	}

	@Override
	public Commit getCommit(final String commitId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getCommits(),commitId),
				"commit '%s'",commitId);
	}

	@Override
	public List<String> getProjects() throws IOException {
		return getIdentifiers(getLocalData().getProjects());
	}

	@Override
	public Project getProject(final String projectId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getProjects(),projectId),
				"project '%s'",projectId);
	}

	@Override
	public Component getProjectComponent(final String projectId, final String componentId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getProjectComponents().get(projectId),componentId),
				"component '%s' of project '%s'",componentId,projectId);
	}

	@Override
	public Version getProjectVersion(final String projectId, final String versionId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getProjectVersions().get(projectId),versionId),
				"version '%s' of project '%s'",versionId,projectId);
	}

	@Override
	public Issue getProjectIssue(final String projectId, final String issueId) throws IOException {
		return
			checkNotNull(
				findIdentifiable(getLocalData().getProjectIssues().get(projectId),issueId),
				"issue '%s' of project '%s'",issueId,projectId);
	}

}