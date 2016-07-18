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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-test-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.handlers;

import java.util.Set;

import com.google.common.collect.Sets;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

final class ContentTypeConsumerHandler extends HandlerUtil implements HttpHandler {

	private final Set<String> mimes;
	private HttpHandler next;

	private ContentTypeConsumerHandler() {
		this.mimes=Sets.newLinkedHashSet();
	}

	ContentTypeConsumerHandler consumes(final String mime) {
		if(mime!=null) {
			this.mimes.add(mime);
		}
		return this;
	}

	ContentTypeConsumerHandler setNext(final HttpHandler aHandler) {
		this.next=aHandler;
		return this;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
		final HeaderValues contentTypeHeader = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE);
		if(contentTypeHeader==null || contentTypeHeader.isEmpty()) {
			fail(exchange,StatusCodes.UNSUPPORTED_MEDIA_TYPE,"No Content-Type specified");
		} else {
			final Set<String> supportedMimes=Sets.newHashSet(contentTypeHeader);
			supportedMimes.retainAll(this.mimes);
			if(supportedMimes.isEmpty()) {
				fail(exchange,StatusCodes.UNSUPPORTED_MEDIA_TYPE,"Expected Content-Type one of %s not one of %s",this.mimes,contentTypeHeader);
			} else {
				if(this.next!=null) {
					this.next.handleRequest(exchange);
				}
			}
		}
	}

	static ContentTypeConsumerHandler create() {
		return new ContentTypeConsumerHandler();
	}
}