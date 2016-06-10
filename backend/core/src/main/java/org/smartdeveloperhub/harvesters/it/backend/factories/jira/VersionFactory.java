package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import org.smartdeveloperhub.harvesters.it.backend.Version;

/**
 * Class to build {@link Version}s.
 * @author imolina
 *
 */
public class VersionFactory {

	public Version createVersion(com.atlassian.jira.rest.client.api.domain.Version jiraVersion, com.atlassian.jira.rest.client.api.domain.Project jiraProject) {

		Version version = new Version();
		version.setId(jiraVersion.getName());
		version.setProjectId(jiraProject.getKey());
		return version;
	}
}
