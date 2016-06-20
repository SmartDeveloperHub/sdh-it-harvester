package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import com.atlassian.jira.rest.client.api.domain.User;

import org.smartdeveloperhub.harvesters.it.backend.Contributor;

public class ContributorFactory {

	public Contributor createContributor(User jiraUser) {

		Contributor contributor = new Contributor();

		contributor.setId(jiraUser.getEmailAddress());
		// TODO: add user display name.

		return contributor;
	}
}
