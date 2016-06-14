package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import org.smartdeveloperhub.harvesters.it.backend.Component;

public class ComponentFactory {

	public Component createComponent(String projectId, com.atlassian.jira.rest.client.api.domain.BasicComponent jiraComponent) {

		Component component = new Component();
		component.setId(String.valueOf(jiraComponent.getId()));
		component.setName(jiraComponent.getName());
		component.setProjectId(projectId);

		return component;
	}
}
