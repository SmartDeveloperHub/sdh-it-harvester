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

import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.UnknownResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Serviceable {

	private final Logger logger; // NOSONAR

	public Serviceable() {
		this.logger=LoggerFactory.getLogger(getClass());
	}

	protected final String trace(final String message, final Object... arguments) {
		final String result = String.format(message,arguments);
		this.logger.trace(result);
		return result;
	}

	protected final String debug(final String message, final Object... arguments) {
		final String result = String.format(message,arguments);
		this.logger.debug(result);
		return result;
	}

	protected final String info(final String message, final Object... arguments) {
		final String result = String.format(message,arguments);
		this.logger.info(result);
		return result;
	}

	protected final ApplicationRuntimeException unexpectedFailure(final Throwable failure, final String message, final Object... args) {
		final String result = String.format(message,args);
		this.logger.error(result.concat(". Full stacktrace follows"),failure);
		final String errorMessage=result;
		return new ApplicationRuntimeException(errorMessage,failure);
	}

	protected final ApplicationRuntimeException unexpectedFailure(final String message, final Object... args) {
		final String result = String.format(message,args);
		this.logger.error(result);
		return new ApplicationRuntimeException(result);
	}

	protected final UnknownResourceException unknownResource(final Object resourceId, final String resourceType) {
		final String errorMessage = String.format("Could not find %s resource for %s",resourceType,resourceId);
		this.logger.error(errorMessage);
		return new UnknownResourceException(errorMessage);
	}

}
