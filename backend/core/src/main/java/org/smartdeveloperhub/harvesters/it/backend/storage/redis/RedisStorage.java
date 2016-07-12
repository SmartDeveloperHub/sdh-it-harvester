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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.storage.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class RedisStorage implements Storage {

	private static final Logger logger =
			LoggerFactory.getLogger(RedisStorage.class);

	private Jedis server;

	public RedisStorage(String server, int port) {

		logger.info("Redis Server: " + server);
		this.server = new Jedis(server, port);
	}

	public void storeIssues(String projectId, Collection<Issue> issues)
															throws IOException {

		try {

			for (Issue issue : issues) {

				server.hset("issues:" + projectId,
							issue.getId(),
							Entities.marshallEntity(issue));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	public Map<String, Issue> loadIssues(String projectId) throws IOException {

		Map<String, Issue> issues = new HashMap<>();

		try {

			Map<String, String> issuesStr = server.hgetAll("issues:" + projectId);

			for (String issueId : issuesStr.keySet()) {

				issues.put(issueId,
							Entities.unmarshallEntity(issuesStr.get(issueId),
														Issue.class));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}

		return issues;
	}

	public void storeProjects(Collection<Project> projects) throws IOException {

		try {

			for (Project project : projects) {

				server.hset("projects",
							project.getId(),
							Entities.marshallEntity(project));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	public Map<String, Project> loadProjects() throws IOException {

		Map<String, Project> projects = new HashMap<>();

		try {

			Map<String, String> projectsStr = server.hgetAll("projects");

			for (String projectId : projectsStr.keySet()) {

				projects.put(projectId,
							Entities.unmarshallEntity(projectsStr.get(projectId),
														Project.class));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}

		return projects;
	}

	public void storeVersions(String projectId, Collection<Version> versions)
															throws IOException {

		try {

			for (Version version : versions) {

				server.hset("versions:" + projectId,
							version.getId(),
							Entities.marshallEntity(version));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	public Map<String, Version> loadVersions(String projectId) throws IOException {

		Map<String, Version> versions = new HashMap<>();

		try {

			Map<String, String> versionsStr = server.hgetAll("versions:" + projectId);

			for (String versionId : versionsStr.keySet()) {

				versions.put(versionId,
							Entities.unmarshallEntity(versionsStr.get(versionId),
														Version.class));
			}

		} catch (JedisException e) {

			throw new IOException(e);
		}

		return versions;
	}

	public void storeComponents(String projectId, Collection<Component> components)
															throws IOException {

		try {
			for (Component component: components) {

				server.hset("components:" + projectId,
							component.getId(),
							Entities.marshallEntity(component));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	public Map<String, Component> loadComponents(String projectId) throws IOException {

		Map<String, Component> components = new HashMap<>();

		try {

			Map<String, String> componentsStr = server.hgetAll("components:" + projectId);

			for (String componentId : componentsStr.keySet()) {

				components.put(componentId,
								Entities.unmarshallEntity(componentsStr.get(componentId),
															Component.class));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}

		return components;
	}

	public void storeContriburos(Map<String, Contributor> contributors)
															throws IOException {

		try {

			for (String contributorId: contributors.keySet()) {

				server.hset("contributors",
							contributorId,
							Entities.marshallEntity(contributors.get(contributorId)));
			}
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	public Map<String, Contributor> loadContributors() throws IOException {

		Map<String, Contributor> contributors = new HashMap<>();
		try {

			Map<String, String> contributorsMap = server.hgetAll("contributors");
	
			for (String contributorId : contributorsMap.keySet()) {
	
				contributors.put(contributorId,
								 Entities.unmarshallEntity(contributorsMap.get(contributorId),
										 					Contributor.class));
			}

		} catch (JedisException e) {

			throw new IOException(e);
		}

		return contributors;
	}

	@Override
	public void storeState(State state) throws IOException {

		try {

			State oldState = loadState();
			if (oldState != null) { // Keep previous activity if any
	
				state.getActivity().addAll(oldState.getActivity());
			}
	
			server.set("crawler:state", Entities.marshallEntity(state));
		} catch (JedisException e) {

			throw new IOException(e);
		}
	}

	@Override
	public State loadState() throws IOException {

		String storedState = null;

		try {

			storedState = server.get("crawler:state");
		} catch (JedisException e) {

			throw new IOException(e);
		}

		return storedState != null ?
				Entities.unmarshallEntity(storedState, State.class) :
				null;
	}
}
