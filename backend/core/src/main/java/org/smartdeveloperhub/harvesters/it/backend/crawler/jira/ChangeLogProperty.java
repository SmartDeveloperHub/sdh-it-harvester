package org.smartdeveloperhub.harvesters.it.backend.crawler.jira;

/**
 *
 * Enumeration for mapping Jira ChangeLog attributes with IT ontology.
 * @author imolina
 *
 */
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
	BLOCKERS("Link"),
	CHILD("epic child");
	
	private String name;
	
	private ChangeLogProperty(String name) {
		
		this.name = name;
	}

	/**
	 * Method that check if the given name refer to an specific property. 
	 * @param property name by which property is referenced.
	 * @return if given property name is truly the property.
	 */
	public boolean is(String property) {

		return this.name.equals(property);
	}
}
