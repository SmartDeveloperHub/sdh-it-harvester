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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

public class RemoteBackendController implements BackendController {

	private final URI target;
	private final URI original;

	public RemoteBackendController(final URI target) {
		this.target=normalize(target);
		this.original=target;
	}

	@Override
	public URI getTarget() {
		return this.original;
	}

	@Override
	public Collector getCollector() throws IOException {
		return Client.resource(this.target,Collector.class).get("api");
	}

	@Override
	public State getState() throws IOException {
		return Client.resource(this.target,State.class).get("api/state");
	}

	@Override
	public List<String> getContributors() throws IOException {
		return Client.list(this.target,String.class).get("api/contributors");
	}

	@Override
	public List<String> getCommits() throws IOException {
		return Client.list(this.target,String.class).get("api/commits");
	}

	@Override
	public List<String> getProjects() throws IOException {
		return Client.list(this.target,String.class).get("api/projects");
	}

	@Override
	public Contributor getContributor(final String contributorId) throws IOException {
		return Client.resource(this.target,Contributor.class).get("api/contributors/%s",contributorId);
	}

	@Override
	public Commit getCommit(final String commitId) throws IOException {
		return Client.resource(this.target,Commit.class).get("api/commits/%s",commitId);
	}

	@Override
	public Project getProject(final String projectId) throws IOException {
		return Client.resource(this.target,Project.class).get("api/projects/%s",projectId);
	}

	@Override
	public Component getProjectComponent(final String projectId, final String componentId) throws IOException {
		return Client.resource(this.target,Component.class).get("api/projects/%s/components/%s",projectId,componentId);
	}

	@Override
	public Version getProjectVersion(final String projectId, final String versionId) throws IOException {
		return Client.resource(this.target,Version.class).get("api/projects/%s/versions/%s",projectId,versionId);
	}

	@Override
	public Issue getProjectIssue(final String projectId, final String issueId) throws IOException {
		return Client.resource(this.target,Issue.class).get("api/projects/%s/issues/%s",projectId,issueId);
	}

	private static URI normalize(final URI target) {
		if(target.getPath().endsWith("/")) {
			return target;
		}
		return URI.create(target.toString()+"/");
	}


}