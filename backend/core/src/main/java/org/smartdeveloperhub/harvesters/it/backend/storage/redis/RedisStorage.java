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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

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

		for (Issue issue : issues) {

			server.hset("issues:" + projectId,
						issue.getId(),
						Entities.marshallEntity(issue));
		}
	}

	public Set<Issue> loadIssues(String projectId) throws IOException {

		Set<Issue> issues = new HashSet<>();

		Map<String, String> issuesMap = server.hgetAll("issues:" + projectId);

		for (String issueStr : issuesMap.values()) {

			issues.add(Entities.unmarshallEntity(issueStr, Issue.class));
		}

		return issues;
	}

	public void storeProjects(Set<Project> projects) throws IOException {

		for (Project project : projects) {

			server.hset("projects",
						project.getId(),
						Entities.marshallEntity(project));
		}
	}

	public Set<Project> loadProjects() throws IOException {

		Set<Project> projects = new HashSet<>();

		Map<String, String> projectsMap = server.hgetAll("projects");

		for (String projectStr : projectsMap.values()) {

			projects.add(Entities.unmarshallEntity(projectStr, Project.class));
		}

		return projects;
	}

	public void storeVersions(String projectId, Collection<Version> versions)
															throws IOException {

		for (Version version : versions) {

			server.hset("versions:" + projectId,
						version.getId(),
						Entities.marshallEntity(version));
		}
	}

	public Set<Version> loadVersions(String projectId) throws IOException {

		Set<Version> versions = new HashSet<>();

		Map<String, String> versionsMap = server.hgetAll("versions:" + projectId);

		for (String versionStr : versionsMap.values()) {

			versions.add(Entities.unmarshallEntity(versionStr, Version.class));
		}

		return versions;
	}

	public void storeComponents(String projectId, Collection<Component> components)
															throws IOException {

		for (Component component: components) {
			
			server.hset("components:" + projectId,
						component.getId(),
						Entities.marshallEntity(component));
		}
	}

	public Set<Component> loadComponents(String projectId) throws IOException {

		Set<Component> components = new HashSet<>();

		Map<String, String> componentsMap = server.hgetAll("components:" + projectId);

		for (String componentStr : componentsMap.values()) {

			components.add(Entities.unmarshallEntity(componentStr, Component.class));
		}

		return components;
	}

	public void storeContriburos(Map<String, Contributor> contributors)
															throws IOException {

		for (String contributorId: contributors.keySet()) {
			
			server.hset("contributors",
						contributorId,
						Entities.marshallEntity(contributors.get(contributorId)));
		}
	}

	public Map<String, Contributor> loadContributors() throws IOException {

		Map<String, Contributor> contributors = new HashMap<>();
		Map<String, String> contributorsMap = server.hgetAll("contributors");

		for (String contributorId : contributorsMap.keySet()) {

			contributors.put(contributorId,
							 Entities.unmarshallEntity(contributorsMap.get(contributorId),
									 					Contributor.class));
		}

		return contributors;
	}

	@Override
	public void storeState(State state) throws IOException {

		State oldState = loadState();
		if (oldState != null) { // Keep previous activity if any

			state.getActivity().addAll(oldState.getActivity());
		}

		server.set("crawler:state", Entities.marshallEntity(state));
	}

	@Override
	public State loadState() throws IOException {

		String storedState = server.get("crawler:state");
		return storedState != null ?
				Entities.unmarshallEntity(storedState, State.class) :
				null;
	}
}
