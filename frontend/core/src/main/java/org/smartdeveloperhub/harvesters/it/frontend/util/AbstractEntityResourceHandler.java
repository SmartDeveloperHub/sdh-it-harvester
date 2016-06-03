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
package org.smartdeveloperhub.harvesters.it.frontend.util;

import java.io.IOException;

import org.joda.time.DateTime;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.ResourceHandler;
import org.ldp4j.application.session.ResourceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Identifiable;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import com.google.common.base.Optional;

public abstract class AbstractEntityResourceHandler<E,K> extends Serviceable implements ResourceHandler {

	private final BackendController backendController;
	private final Logger logger; // NOSONAR

	public AbstractEntityResourceHandler(final BackendController backendController) {
		this.backendController = backendController;
		this.logger=LoggerFactory.getLogger(getClass());
	}

	@Override
	public final DataSet get(final ResourceSnapshot resource) {
		final K userId=getId(resource);
		try {
			return
				toDataSet(
					getEntity(this.backendController,userId),
					userId);
		} catch(final Exception e){
			 throw new ApplicationRuntimeException(e);
		}
	}

	protected final <T extends Identifiable<?>> Optional<DateTime> dateTime(final DateTime result, final boolean mandatory, final String property, final T entity) {
		if(result!=null) {
			return Optional.of(result);
		} else if(!mandatory) {
			this.logger.debug("Ignored date for missing property {} of {} {} ({})",property,entity.getClass().getSimpleName().toLowerCase(),entity.getId(),entity);
			return Optional.absent();
		} else {
			this.logger.warn("Could not create date for property {} of {} {} ({})",property,entity.getClass().getSimpleName().toLowerCase(),entity.getId(),entity);
			throw new ApplicationRuntimeException("Could not create date for property "+property+" of "+entity.getClass().getSimpleName().toLowerCase()+" "+entity);
		}
	}


	protected abstract E getEntity(BackendController controller, final K key) throws IOException;

	protected abstract K getId(final ResourceSnapshot resource) ;

	protected abstract DataSet toDataSet(E entity, K key);

}
