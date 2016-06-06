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
package org.smartdeveloperhub.harvesters.it.frontend.contributor;

import java.io.IOException;
import java.net.URI;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.IndividualPropertyHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;


@Resource(
	id=ContributorHandler.ID
)
public final class ContributorHandler extends AbstractEntityResourceHandler<Contributor,String> {

	public static final String ID="ContributorHandler";

	public ContributorHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected String getId(final ResourceSnapshot resource) {
		return IdentityUtil.contributorId(resource);
	}

	@Override
	protected Contributor getEntity(final BackendController controller, final String key) throws IOException {
		return controller.getContributor(key);
	}

	@Override
	protected DataSet toDataSet(final Contributor contributor, final String contributorId) {
		final Name<String> contributorName=IdentityUtil.contributorName(contributorId);

		final DataSet dataSet=DataSets.createDataSet(contributorName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		final IndividualPropertyHelper individual=
			helper.
				managedIndividual(contributorName,ContributorHandler.ID).
					property(RDF.TYPE).
						withIndividual(IT.CONTRIBUTOR_TYPE).
					property(IT.ID).
						withLiteral(contributor.getId()).
					property(IT.CONTRIBUTOR_ID).
						withLiteral(contributor.getId());

		for(final String email:contributor.getEmails()) {
			individual.
				property(IT.MBOX).
					withLiteral(URI.create(email));
		}

		return dataSet;
	}

}