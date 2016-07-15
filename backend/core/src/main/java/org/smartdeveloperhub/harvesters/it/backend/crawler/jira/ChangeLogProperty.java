/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
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
	SEVERITY("priority"),
	ESTIMATED_TIME("timeoriginalestimate"),
	VERSION("Fix Version"),
	BLOCKERS("Link");

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
