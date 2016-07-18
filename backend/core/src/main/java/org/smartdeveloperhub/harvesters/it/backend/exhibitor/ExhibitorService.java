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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0
 *   Bundle      : it-backend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.exhibitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/api/")
public class ExhibitorService {

	private static final Logger LOGGER =
								LoggerFactory.getLogger(ExhibitorService.class);
	private static final String MEDIA_TYPE = "application/psr.sdh.itcollector.entity+json";

	private Exhibitor exhibitor;
	
	public ExhibitorService(Exhibitor exhibitor) {

		this.exhibitor = Objects.requireNonNull(exhibitor, "Exhibitor cannot be null.");
	}

	@GET
	@Produces(MEDIA_TYPE)
	public Response api() {

		return Response.ok().entity(exhibitor.getApi()).build();
	}

	@GET
	@Path("/state")
	@Produces(MEDIA_TYPE)
	public Response getState() {

		return Response.ok().entity(exhibitor.getState()).build();
	}

	@GET
	@Path("/contributors")
	@Produces(MEDIA_TYPE)
	public Response getContributors() {

		return Response.ok().entity(exhibitor.getContributors()).build();
	}

	@GET
	@Path("/contributors/{contributorId}")
	@Produces(MEDIA_TYPE)
	public Response getContributor(@PathParam("contributorId") String contributorId) {

		Contributor contributor = exhibitor.getContributor(contributorId);

		if (contributor != null) {

			return Response.ok().entity(contributor).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}

	@GET
	@Path("/commits")
	@Produces(MEDIA_TYPE)
	public Response getCommits() {

		return Response.ok().entity(exhibitor.getCommits()).build();
	}

	@GET
	@Path("/commits/{commitId}")
	@Produces(MEDIA_TYPE)
	public Response getCommit(@PathParam("commitId") String commitId) {

		Commit commit = exhibitor.getCommit(commitId);

		if (commit != null) {

			return Response.ok().entity(commit).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}

	@GET
	@Path("/projects")
	@Produces(MEDIA_TYPE)
	public Response getProjects() {

		return Response.ok().entity(exhibitor.getProjects()).build();
	}

	@GET
	@Path("/projects/{projectId}")
	@Produces(MEDIA_TYPE)
	public Response getProject(@PathParam("projectId") String projectId) {

		Project project = exhibitor.getProject(projectId);

		if (project != null) {

			return Response.ok().entity(project).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}

	@GET
	@Path("/projects/{projectId}/components/{componentId}")
	@Produces(MEDIA_TYPE)
	public Response getProjectComponent(@PathParam("projectId") String projectId,
										@PathParam("componentId") String componentId) {

		Component component = exhibitor.getProjectComponent(projectId, componentId);

		if (component != null) {

			return Response.ok().entity(component).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}

	@GET
	@Path("/projects/{projectId}/versions/{versionId}")
	@Produces(MEDIA_TYPE)
	public Response getProjectVersion(@PathParam("projectId") String projectId,
										@PathParam("versionId") String versionId) {

		Version version = exhibitor.getProjectVersion(projectId, versionId);

		if (version != null) {

			return Response.ok().entity(version).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}

	@GET
	@Path("/projects/{projectId}/issues/{issueId}")
	@Produces(MEDIA_TYPE)
	public Response getProjectIssue(@PathParam("projectId") String projectId,
									@PathParam("issueId") String issueId) {

		Issue issue = exhibitor.getProjectIssue(projectId, issueId);

		if (issue != null) {

			return Response.ok().entity(issue).build();
		}

		return Response.status(Status.NOT_FOUND).entity("").build();
	}
}
