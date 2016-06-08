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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import java.util.List;
import java.util.Map;

import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entity;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LocalData extends Entity {

	private Collector collector;
	private List<Contributor> contributors;
	private List<Commit> commits;
	private List<Project> projects;
	private Map<String,List<Component>> projectComponents;
	private Map<String,List<Version>> projectVersions;
	private Map<String,List<Issue>> projectIssues;

	public LocalData() {
		this.collector=new Collector();
		this.contributors=Lists.newArrayList();
		this.commits=Lists.newArrayList();
		this.projects=Lists.newArrayList();
		this.projectComponents=Maps.newLinkedHashMap();
		this.projectVersions=Maps.newLinkedHashMap();
		this.projectIssues=Maps.newLinkedHashMap();
	}

	public Collector getCollector() {
		return this.collector;
	}

	public void setCollector(final Collector collector) {
		this.collector = collector;
	}

	public List<Contributor> getContributors() {
		return this.contributors;
	}

	public void setContributors(final List<Contributor> contributors) {
		this.contributors = contributors;
	}

	public List<Commit> getCommits() {
		return this.commits;
	}

	public void setCommits(final List<Commit> commits) {
		this.commits = commits;
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(final List<Project> projects) {
		this.projects = projects;
	}

	public Map<String, List<Component>> getProjectComponents() {
		return this.projectComponents;
	}

	public void setProjectComponents(final Map<String, List<Component>> projectComponents) {
		this.projectComponents = projectComponents;
	}

	public Map<String, List<Version>> getProjectVersions() {
		return this.projectVersions;
	}

	public void setProjectVersions(final Map<String, List<Version>> projectVersions) {
		this.projectVersions = projectVersions;
	}

	public Map<String, List<Issue>> getProjectIssues() {
		return this.projectIssues;
	}

	public void setProjectIssues(final Map<String, List<Issue>> projectIssues) {
		this.projectIssues = projectIssues;
	}

}
