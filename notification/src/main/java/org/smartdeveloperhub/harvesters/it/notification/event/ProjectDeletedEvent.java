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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0
 *   Bundle      : it-harvester-notification-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification.event;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Event.INSTANCE,
	Event.TIMESTAMP,
	ProjectDeletedEvent.DELETED_PROJECTS
})
public class ProjectDeletedEvent extends Event {

	static final String DELETED_PROJECTS = "deletedProjects";

	@JsonProperty(DELETED_PROJECTS)
	private List<String> deletedProjects = new ArrayList<>();

	/**
	 * Get the identifiers of the projects deleted from the instance
	 *
	 * @return The identifiers of the deleted projects
	 */
	@JsonProperty(DELETED_PROJECTS)
	public List<String> getDeletedProjects() {
		return this.deletedProjects;
	}

	/**
	 * Set the identifiers of the projects deleted from the instance
	 *
	 * @param projectIds
	 *            The identifiers of the projects
	 */
	@JsonProperty(DELETED_PROJECTS)
	public void setDeletedProjects(final List<String> projectIds) {
		this.deletedProjects = projectIds;
	}


}
