package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import com.atlassian.jira.rest.client.api.domain.User;
import com.google.common.collect.Sets;

import org.smartdeveloperhub.harvesters.it.backend.Contributor;

public class ContributorFactory {

	public Contributor createContributor(User jiraUser) {

		Contributor contributor = new Contributor();

		contributor.setId(jiraUser.getEmailAddress());
		contributor.setName(jiraUser.getDisplayName());
		contributor.setEmails(Sets.newHashSet(jiraUser.getEmailAddress()));

		return contributor;
	}
}
