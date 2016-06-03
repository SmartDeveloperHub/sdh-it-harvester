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
package org.smartdeveloperhub.harvesters.it.frontend.harvester;

import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.ext.annotations.Attachment;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTYPE;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.PLATFORM;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;

@Resource(
	id=HarvesterHandler.ID,
	attachments={
		@Attachment(
			id=HarvesterHandler.HARVESTER_PROJECTS,
			path="projects/",
			handler=ProjectContainerHandler.class
		),
		@Attachment(
			id=HarvesterHandler.HARVESTER_CONTRIBUTORS,
			path="contributors/",
			handler=ContributorContainerHandler.class
		),
		@Attachment(
			id=HarvesterHandler.HARVESTER_COMMITS,
			path="commits/",
			handler=CommitContainerHandler.class
		)
	}
)
public class HarvesterHandler implements ResourceHandler {

	public static final String ID                     = "HarvesterHandler";
	public static final String HARVESTER_PROJECTS     = "HarvesterProjects";
	public static final String HARVESTER_CONTRIBUTORS = "HarvesterContributors";
	public static final String HARVESTER_COMMITS      = "HarvesterCommits";

	private static final URI VOCABULARY_PATH = URI.create("#vocabulary");

	@Override
	public DataSet get(final ResourceSnapshot resource) {
		final DataSet dataSet=DataSets.createDataSet(resource.name());
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(resource.name(), ID).
				property(RDF.TYPE).
					withIndividual(DCTYPE.SERVICE_TYPE).
					withIndividual(PLATFORM.MICRO_SERVICE_TYPE).
					withIndividual(PLATFORM.LINKED_DATA_MICRO_SERVICE_TYPE).
					withIndividual(PLATFORM.HARVESTER_TYPE).
					withIndividual(IT.HARVESTER_TYPE).
				property(PLATFORM.HARVESTER_VOCABULARY).
					withIndividual(resource.name(),HarvesterHandler.ID,VOCABULARY_PATH);

		helper.
			relativeIndividual(resource.name(),HarvesterHandler.ID,VOCABULARY_PATH).
				property(RDF.TYPE).
					withIndividual(IT.VOCABULARY_TYPE).
					withIndividual(PLATFORM.VOCABULARY_TYPE).
				property(PLATFORM.SOURCE).
					withLiteral(IT.sourcecode()).
				property(DCTERMS.SOURCE).
					withLiteral(IT.sourcecode()).
				property(PLATFORM.IMPLEMENTS).
					withIndividual(IT.DOMAIN_TYPE);

		return dataSet;
	}

}
