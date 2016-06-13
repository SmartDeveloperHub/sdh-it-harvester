package org.smartdeveloperhub.harvesters.it.backend.storage.redis;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;

import java.io.IOException;
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

	public void storeIssues(String projectId, Set<Issue> issues) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		for (Issue issue : issues) {

			logger.info("Insertando issue (" + issue.getId() + ")");
			server.hset("issues:" + projectId,
						issue.getId(),
						mapper.writeValueAsString(issue));
		}
	}

	public Set<Issue> loadIssues(String projectId) throws IOException {

		Set<Issue> issues = new HashSet<>();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> issuesMap = server.hgetAll("issues:" + projectId);

		for (String issueStr : issuesMap.values()) {

			issues.add(mapper.readValue(issueStr, Issue.class));
		}

		return issues;
	}

	public void storeProjects(Set<Project> projects) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		for (Project project : projects) {

			logger.info("Insertando proyecto (" + project.getId() + ")");
			server.hset("projects",
						project.getId(),
						mapper.writeValueAsString(project));
		}
	}

	public Set<Project> loadProjects() throws IOException {

		Set<Project> projects = new HashSet<>();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, String> projectsMap = server.hgetAll("projects");

		for (String projectStr : projectsMap.values()) {

			projects.add(mapper.readValue(projectStr, Project.class));
		}

		return projects;
	}

	public void storeVersions(String projectId, Set<Version> versions) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		for (Version version : versions) {

			logger.info("Insertando version (" + version.getId() + ")");
			server.hset("versions:" + projectId,
						version.getId(),
						mapper.writeValueAsString(version));
		}
	}

	public Set<Version> loadVersions(String projectId) {
		// TODO:
		return null;
	}

	public void storeComponents(String projectId, Set<Component> components) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		for (Component component: components) {
			
			logger.info("Insertando component (" + component.getId() + ")");
			server.hset("components:" + projectId,
						component.getId(),
						mapper.writeValueAsString(component));
		}
	}

	public Set<Component> loadComponents(String projectId) {
		// TODO:

		return null;
	}
}
