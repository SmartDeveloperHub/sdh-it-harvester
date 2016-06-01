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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-api-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Sets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Identifiable.ID,
	Titled.TITLE,
	Project.VERSIONS,
	Project.COMPONENTS,
	Project.TOP_ISSUES,
	Project.ISSUES
})
public final class Project extends Titled<String> {

	static final String VERSIONS="versions";
	static final String COMPONENTS="components";
	static final String TOP_ISSUES="topIssues";
	static final String ISSUES="issues";

	private Set<String> versions;
	private Set<String> components;
	private Set<String> topIssues;
	private Set<String> issues;

	public Project() {
		this.versions=Sets.newLinkedHashSet();
		this.components=Sets.newLinkedHashSet();
		this.topIssues=Sets.newLinkedHashSet();
		this.issues=Sets.newLinkedHashSet();
	}

	public Set<String> getVersions() {
		return this.versions;
	}

	public void setVersions(final Set<String> versions) {
		this.versions = versions;
	}

	public Set<String> getComponents() {
		return this.components;
	}

	public void setComponents(final Set<String> components) {
		this.components = components;
	}

	public Set<String> getTopIssues() {
		return this.topIssues;
	}

	public void setTopIssues(final Set<String> topIssues) {
		this.topIssues = topIssues;
	}

	public Set<String> getIssues() {
		return this.issues;
	}

	public void setIssues(final Set<String> issues) {
		this.issues = issues;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add("versions",this.versions).
				add("components",this.components).
				add("topIssues",this.topIssues).
				add("issues",this.issues);
	}

}
