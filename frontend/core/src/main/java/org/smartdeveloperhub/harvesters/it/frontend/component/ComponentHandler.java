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
package org.smartdeveloperhub.harvesters.it.frontend.component;

import java.io.IOException;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;

@Resource(id=ComponentHandler.ID)
public final class ComponentHandler extends AbstractEntityResourceHandler<Component,ComponentKey> {

	public static final String ID="ComponentHandler";

	public ComponentHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected ComponentKey getId(final ResourceSnapshot resource) {
		return IdentityUtil.componentId(resource);
	}

	@Override
	protected Component getEntity(final BackendController controller, final ComponentKey key) throws IOException {
		return controller.getProjectComponent(key.getProjectId(),key.getComponentId());
	}

	@Override
	protected DataSet toDataSet(final Component component, final ComponentKey key) {
		final Name<ComponentKey> componentName=IdentityUtil.componentName(key);

		final DataSet dataSet=DataSets.createDataSet(componentName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		helper.
			managedIndividual(componentName,ComponentHandler.ID).
				property(RDF.TYPE).
					withIndividual(IT.COMPONENT_TYPE).
				property(IT.ID).
					withLiteral(component.getId()).
				property(IT.COMPONENT_ID).
					withLiteral(component.getId()).
				property(DCTERMS.TITLE).
					withLiteral(component.getTitle()).
				property(IT.COMPONENT_TITLE).
					withLiteral(component.getTitle());

		return dataSet;
	}

}