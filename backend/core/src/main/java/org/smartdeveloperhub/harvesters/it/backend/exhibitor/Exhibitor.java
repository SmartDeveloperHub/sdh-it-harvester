package org.smartdeveloperhub.harvesters.it.backend.exhibitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Notifications;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;
import org.smartdeveloperhub.harvesters.it.notification.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Exhibitor {

	private static final Logger LOGGER =
									LoggerFactory.getLogger(Exhibitor.class);
	private String version;
	private Notifications notifications;
	private Storage storage;

	public Exhibitor(String version, Notifications notifications, Storage storage) {

		this.version = Objects.requireNonNull(version, "Version cannot be null.");
		this.storage = Objects.requireNonNull(storage, "Storage cannot be null.");
		this.notifications = Objects.requireNonNull(notifications, "Notifications cannot be null.");
	}

	public Collector getApi() {

		Collector collector = new Collector();

		collector.setVersion(version);
		collector.setNotifications(notifications);

		return collector;
	}

	public Object getState() {
		// TODO:
		return null;
	}

	public Collection<String> getContributors() {

		Collection<String> contributors = new ArrayList<>();

		try {
			contributors.addAll(storage.loadContributors().keySet());
		} catch (IOException e) {
			LOGGER.error("Exception when trying to retrieve stored " +
						"contributors from Storage. {}", e);
		}

		return contributors;
	}

	public Contributor getContributor(String contributorId) {

		Contributor contributor = null;

		try {

			contributor = storage.loadContributors().get(contributorId);
		} catch (IOException e) {

			LOGGER.error("Exception when trying to retrieve stored " +
						 "contributors from Storage. {}", e);
		}

		return contributor;
	}

	public Collection<String> getCommits() {
		// TODO:
		return null;
	}

	public Commit getCommit(String commitId) {
		// TODO:
		return null;
	}

	public Collection<String> getProjects() {

		Collection<String> projects = new ArrayList<>();

		try {
			projects.addAll(storage.loadProjects().keySet());
		} catch (IOException e) {
			LOGGER.error("Exception when trying to retrieve stored " +
						"projects from Storage. {}", e);
		}

		return projects;
	}

	public Project getProject(String projectId) {

		Project project = null;

		try {

			project = storage.loadProjects().get(projectId);
		} catch (IOException e) {

			LOGGER.error("Exception when trying to retrieve a stored " +
						 "project from Storage. {}", e);
		}

		return project;
	}

	public Component getProjectComponent(String projectId, String componentId) {

		Component component = null;

		try {

			component = storage.loadComponents(projectId).get(componentId);
		} catch (IOException e) {

			LOGGER.error("Exception when trying to retrieve a stored " +
						 "component from Storage. {}", e);
		}

		return component;
	}

	public Version getProjectVersion(String projectId, String versionId) {

		Version version = null;

		try {

			version = storage.loadVersions(projectId).get(versionId);
		} catch (IOException e) {

			LOGGER.error("Exception when trying to retrieve a stored " +
						 "version from Storage. {}", e);
		}

		return version;
	}

	public Issue getProjectIssue(String projectId, String issueId) {

		Issue issue = null;

		try {

			issue = storage.loadIssues(projectId).get(issueId);
		} catch (IOException e) {

			LOGGER.error("Exception when trying to retrieve a stored " +
						 "issue from Storage. {}", e);
		}

		return issue;
	}
}
