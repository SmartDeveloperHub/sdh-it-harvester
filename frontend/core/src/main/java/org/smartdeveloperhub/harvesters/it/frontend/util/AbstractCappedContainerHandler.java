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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.util;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.ext.ContainerHandler;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;

import com.google.common.base.CaseFormat;

public abstract class AbstractCappedContainerHandler extends Serviceable implements ContainerHandler {

	private final String artifact;

	public AbstractCappedContainerHandler(final String artifact) {
		this.artifact = artifact;
	}

	@Override
	public final DataSet get(final ResourceSnapshot resource) {
		return DataSets.createDataSet(resource.name());
	}

	@Override
	public final ResourceSnapshot create(final ContainerSnapshot container, final DataSet representation, final WriteSession session) {
		trace("Requested %s creation from: %n%s",this.artifact,representation);
		throw super.unexpectedFailure("%s creation is not supported",CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, this.artifact));
	}

}
