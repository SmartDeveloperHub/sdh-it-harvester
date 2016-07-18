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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0
 *   Bundle      : it-frontend-test-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

abstract class HandlerUtil {

	private final Logger logger; // NOSONAR

	HandlerUtil() {
		this.logger = LoggerFactory.getLogger(getClass());
	}

	final void fail(final HttpServerExchange exchange, final Throwable e, final int statusCode, final String message, final Object... args) {
		String logSuffix="";
		String responseSuffix="";
		if(e!=null) {
			logSuffix=": ";
			responseSuffix=": "+e.getMessage();
		}
		answer(exchange,statusCode,message+responseSuffix,args);
		this.logger.warn(String.format(message,args)+logSuffix,e);
	}

	final void fail(final HttpServerExchange exchange, final Throwable e, final String message, final Object... args) {
		fail(exchange,e,StatusCodes.INTERNAL_SERVER_ERROR,message,args);
	}

	final void fail(final HttpServerExchange exchange, final int statusCode, final String message, final Object... args) {
		fail(exchange,null,statusCode,message,args);
	}

	final void answer(final HttpServerExchange exchange, final int statusCode, final String message, final Object... args) {
		answer(exchange, statusCode, "text/plain", message, args);
	}

	final void answer(final HttpServerExchange exchange, final int statusCode, final String contentType, final String message, final Object... args) {
		exchange.setStatusCode(statusCode);
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
		exchange.getResponseSender().send(String.format(message,args));
		this.logger.debug("{} : {{}}\n{}",statusCode,contentType,String.format(message, args));
	}

}