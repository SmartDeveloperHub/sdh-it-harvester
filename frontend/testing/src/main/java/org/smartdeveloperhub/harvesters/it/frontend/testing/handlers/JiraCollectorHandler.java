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

import java.util.List;

import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Entity;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.MoreHandlers.APIVersion;

import com.google.common.base.Throwables;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

final class JiraCollectorHandler<T> extends HandlerUtil implements HttpHandler {

	private static final String MEDIA_TYPE = "application/psr.sdh.itcollector.entity+json";;

	@SuppressWarnings("unused")
	private final APIVersion version;

	private EntityProvider<T> provider;

	private JiraCollectorHandler(final APIVersion version) {
		this.version = version;
	}

	JiraCollectorHandler<T> entityProvider(final EntityProvider<T> provider) {
		this.provider = provider;
		return this;
	}

	@Override
	public void handleRequest(final HttpServerExchange exchange) {
		if(!Methods.GET.equals(exchange.getRequestMethod())) {
			fail(exchange, StatusCodes.METHOD_NOT_ALLOWED, "Only GET is allowed");
			exchange.getResponseHeaders().put(Headers.ALLOW,Methods.GET_STRING);
		} else if(!MEDIA_TYPE.equals(exchange.getRequestHeaders().get(Headers.ACCEPT).getFirst())) {
			fail(exchange, StatusCodes.NOT_ACCEPTABLE, "Only %s representations can be retrieved", MEDIA_TYPE);
		} else {
			try {
				final T entity=this.provider.getEntity(new Parameters(exchange));
				if(entity instanceof Entity) {
					answer(exchange,StatusCodes.OK,MEDIA_TYPE,Entities.marshallEntity((Entity)entity));
				} else if(entity instanceof List<?>) {
					answer(exchange,StatusCodes.OK,MEDIA_TYPE,Entities.marshallList((List<?>)entity));
				} else {
					fail(exchange,StatusCodes.INTERNAL_SERVER_ERROR,"Unsupported resource '%s': %s",exchange.getRequestURL(),entity);
				}
			} catch (final EntityNotFoundException e) {
				fail(exchange,StatusCodes.NOT_FOUND,"Unknown resource '%s'",exchange.getRequestURL());
			} catch (final Exception e) {
				fail(exchange,e,"Upps!!\n%s",Throwables.getStackTraceAsString(e));
			}
		}
	}

	static <T> JiraCollectorHandler<T> create(final APIVersion version) {
		return new JiraCollectorHandler<T>(version);
	}

}