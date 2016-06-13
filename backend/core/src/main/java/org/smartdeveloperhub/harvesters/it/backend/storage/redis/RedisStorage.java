package org.smartdeveloperhub.harvesters.it.backend.storage.redis;

import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;

import java.util.Set;

public class RedisStorage implements Storage {

	private String server;
	
	public RedisStorage(String server) {
		this.server = server;
	}

	public void storeIssues(Set<Issue> issues) {
		//TODO:
		System.out.println("Issues Size: " + issues.size());
	}

	public void storeProjects(Set<Project> projects) {
		//TODO:
		System.out.println(projects.size());
		for (Project project : projects) {
			System.out.println(project);
		}
	}

	public void storeVersions(Set<Version> versions) {
		//TODO:
	}

	public void storeComponents(Set<Component> components) {
		//TODO:
	}
}
