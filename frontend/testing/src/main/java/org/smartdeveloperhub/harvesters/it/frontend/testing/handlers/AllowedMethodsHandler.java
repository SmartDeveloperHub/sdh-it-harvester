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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-test-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.handlers;

import java.util.Set;

import com.google.common.collect.Sets;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;

final class AllowedMethodsHandler extends HandlerUtil implements HttpHandler {

	private final Set<String> methods;
	private HttpHandler next;

	private AllowedMethodsHandler() {
		this.methods=Sets.newLinkedHashSet();
	}

	AllowedMethodsHandler allow(final HttpString method) {
		if(method!=null) {
			this.methods.add(method.toString());
		}
		return this;
	}

	AllowedMethodsHandler setNext(final HttpHandler aHandler) {
		this.next=aHandler;
		return this;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
			if(!this.methods.contains(exchange.getRequestMethod().toString())) {
				fail(exchange,StatusCodes.METHOD_NOT_ALLOWED,"Unsupported method (%s)",exchange.getRequestMethod());
				exchange.getResponseHeaders().putAll(Headers.ALLOW,this.methods);
			} else {
				if(this.next!=null) {
					this.next.handleRequest(exchange);
				}
			}
	}

	static AllowedMethodsHandler create() {
		return new AllowedMethodsHandler();
	}

}