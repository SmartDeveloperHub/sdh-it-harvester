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
package org.smartdeveloperhub.harvesters.it.frontend.project;

import java.io.IOException;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.IndividualPropertyHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueKey;
import org.smartdeveloperhub.harvesters.it.frontend.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;


@Resource(
	id=ProjectHandler.ID,
	attachments={
		@Attachment(
			id=ProjectHandler.PROJECT_COMPONENTS,
			path="components/",
			handler=ComponentContainerHandler.class
		),
		@Attachment(
			id=ProjectHandler.PROJECT_VERSIONS,
			path="versions/",
			handler=VersionContainerHandler.class
		),
		@Attachment(
			id=ProjectHandler.PROJECT_ISSUES,
			path="issues/",
			handler=IssueContainerHandler.class
		)
	}
)
public final class ProjectHandler extends AbstractEntityResourceHandler<Project,String> {

	public static final String ID="RepositoryHandler";
	public static final String PROJECT_COMPONENTS="ProjectComponents";
	public static final String PROJECT_VERSIONS  ="ProjectVersions";
	public static final String PROJECT_ISSUES    ="ProjectIssues";

	public ProjectHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected String getId(final ResourceSnapshot resource) {
		return IdentityUtil.projectId(resource);
	}

	@Override
	protected Project getEntity(final BackendController controller, final String key) throws IOException {
		return controller.getProject(key);
	}

	@Override
	protected DataSet toDataSet(final Project project, final String repositoryId) {
		final Name<String> projectName=IdentityUtil.projectName(repositoryId);

		final DataSet dataSet=DataSets.createDataSet(projectName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		final IndividualPropertyHelper individual =
			helper.
				managedIndividual(projectName,ProjectHandler.ID).
					property(RDF.TYPE).
						withIndividual(IT.PROJECT_TYPE).
					property(IT.ID).
						withLiteral(project.getId()).
					property(IT.PROJECT_ID).
						withLiteral(project.getId()).
					property(DCTERMS.TITLE).
						withLiteral(project.getName()).
					property(IT.PROJECT_TITLE).
						withLiteral(project.getName());

		for(final String issueId:project.getTopIssues()) {
			final Name<IssueKey> issueName=
				IdentityUtil.
					issueName(
						new IssueKey(project.getId(),issueId));
			individual.
				property(IT.HAS_PROJECT_TOP_ISSUE).
					withIndividual(issueName,IssueHandler.ID);
		}

		return dataSet;
	}

}