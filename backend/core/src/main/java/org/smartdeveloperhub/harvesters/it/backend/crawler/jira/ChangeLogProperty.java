package org.smartdeveloperhub.harvesters.it.backend.crawler.jira;

public enum ChangeLogProperty {

	STATUS("status"),
	ASSIGNEE("assignee"),
	TITLE("summary"),
	DESCRIPTION("description"),
	ISSUE_TYPE("issuetype"),
	DUE_DATE("duedate"),
	COMPONENT("Component"),
	PRIORITY("priority"),
	SEVERITY("severity"),
	ESTIMATED_TIME("timeestimate"),
	VERSION("Fix Version"),
	BLOCKERS("Link");
	
	private String name;
	
	private ChangeLogProperty(String name) {
		
		this.name = name;
	}

	public boolean is(String property) {

		return this.name.equals(property);
	}
}
