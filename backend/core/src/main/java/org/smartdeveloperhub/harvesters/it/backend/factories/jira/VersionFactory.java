package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import org.smartdeveloperhub.harvesters.it.backend.Version;

import java.util.Objects;

/**
 * Class to build {@link Version}s.
 * @author imolina
 *
 */
public class VersionFactory {

	public Version createVersion(String projectId, com.atlassian.jira.rest.client.api.domain.Version jiraVersion) {

		Objects.requireNonNull(jiraVersion, "Jira User cannot be null.");

		Version version = new Version();
		version.setId(String.valueOf(jiraVersion.getId()));
		version.setProjectId(projectId);
		version.setName(jiraVersion.getName());
		return version;
	}
}