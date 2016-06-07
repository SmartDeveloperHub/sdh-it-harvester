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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StaticBackendController implements BackendController {

	private final URI target;

	StaticBackendController(final URI target) {
		this.target = target;
	}

	private String idNumber(final String id) {
		return id.substring(id.length()-1,id.length());
	}

	@Override
	public URI getTarget() {
		return this.target;
	}

	@Override
	public Collector getCollector() throws IOException {
		final Collector collector = new Collector();
		collector.setVersion("0.1.0-SNAPSHOT");
		final Notifications notifications = new Notifications();
		notifications.setBrokerHost("localhost");
		notifications.setExchangeName("it.frontend.test");
		collector.setNotifications(notifications);
		return collector;
	}

	@Override
	public State getState() throws IOException {
		final State state = new State();
		return state;
	}

	@Override
	public List<String> getContributors() throws IOException {
		return Lists.newArrayList("contributor1","contributor2");
	}

	@Override
	public List<String> getCommits() throws IOException {
		return Lists.newArrayList("commit1","commit2");
	}

	@Override
	public List<String> getProjects() throws IOException {
		return Lists.newArrayList("project1","project2");
	}

	@Override
	public Contributor getContributor(final String contributorId) throws IOException {
		final Contributor contributor = new Contributor();
		contributor.setId(contributorId);
		contributor.setEmails(Sets.newHashSet(contributorId+"@example.org"));
		return contributor;
	}

	@Override
	public Commit getCommit(final String commitId) throws IOException {
		final Commit commit = new Commit();
		commit.setId(commitId);
		commit.setRepository(this.target.resolve("../repositories/repo"+idNumber(commitId)+".git").toString());
		commit.setBranch("master");
		commit.setHash(Integer.toHexString(commitId.hashCode()));
		return commit;
	}

	@Override
	public Project getProject(final String projectId) throws IOException {
		final Project project = new Project();
		project.setId(projectId);
		project.setTitle("Project "+idNumber(projectId));
		return project;
	}

	@Override
	public Component getProjectComponent(final String projectId, final String componentId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getProjectVersion(final String projectId, final String versionId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue getProjectIssue(final String projectId, final String issueId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
