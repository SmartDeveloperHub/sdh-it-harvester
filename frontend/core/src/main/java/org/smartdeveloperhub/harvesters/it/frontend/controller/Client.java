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
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Entity;
import org.smartdeveloperhub.harvesters.it.frontend.util.Closeables;

final class Client<T> {

	private interface UnmarshallStrategy<T> {

		T unmarshall(String rawData) throws IOException;

	}

	private static final class EntityUnmarshallingStrategy<T extends Entity> implements UnmarshallStrategy<T> {

		private final Class<? extends T> clazz;

		private EntityUnmarshallingStrategy(final Class<? extends T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public T unmarshall(final String rawData) throws IOException {
			return Entities.unmarshallEntity(rawData, this.clazz);
		}

		@Override
		public String toString() {
			return this.clazz.getName();
		}

	}

	private static final class ListUnmarshallingStrategy<T> implements UnmarshallStrategy<List<T>> {

		private final Class<? extends T> clazz;

		private ListUnmarshallingStrategy(final Class<? extends T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public List<T> unmarshall(final String rawData) throws IOException {
			return Entities.unmarshallList(rawData, this.clazz);
		}

		@Override
		public String toString() {
			return List.class.getName()+"<"+this.clazz.getName()+">";
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(Client.class);

	static final String API_MIME     = "application/psr.sdh.itcollector.entity+json";
	static final int    MAX_ATTEMPTS = 5;

	private final URI apiBase;
	private final UnmarshallStrategy<T> strategy;

	private Client(final URI apiBase, final UnmarshallStrategy<T> unmarshaller) {
		this.apiBase=apiBase;
		this.strategy=unmarshaller;
	}

	T get(final String path, final Object... args) throws IOException {
		CloseableHttpClient client=null;
		try {
			client=HttpClients.createDefault();
			final String resource = this.apiBase.resolve(String.format(path,args)).toString();
			int attempts=0;
			while(attempts<MAX_ATTEMPTS) {
				attempts++;
				final String response = attemptRetrieval(client,resource);
				if(response!=null) {
					return processResponse(resource, response);
				}
			}
			throw new IOException("Could not retrieve '"+resource+"' after "+MAX_ATTEMPTS+" attempts");
		} finally {
			Closeables.closeQuietly(client);
		}
	}

	private T processResponse(final String resource, final String response) throws InvalidServiceResponseException {
		try {
			return this.strategy.unmarshall(response);
		} catch (final IOException e) {
			throw new InvalidServiceResponseException(resource,response,this.strategy.toString(),e);
		}
	}

	private String attemptRetrieval(final CloseableHttpClient client, final String resourcePath) throws IOException {
		final HttpGet httpRequest = new HttpGet(resourcePath);
		httpRequest.addHeader(HttpHeaders.ACCEPT,API_MIME);
		LOGGER.info("GET {}",httpRequest.getURI());
		CloseableHttpResponse httpResponse=null;
		try {
			httpResponse = client.execute(httpRequest);
			final StatusLine statusLine = httpResponse.getStatusLine();
			LOGGER.info("{}",statusLine);
			String result=null;
			final HttpEntity entity = httpResponse.getEntity();
			result = entity != null ? EntityUtils.toString(entity) : null;
			if(statusLine.getStatusCode()!=200 && !canRetry(statusLine.getStatusCode())) {
				throw new ServiceFailureException(resourcePath,statusLine,result);
			}
			return result;
		} finally {
			Closeables.closeQuietly(httpResponse);
		}
	}

	private boolean canRetry(final int status) {
		return status==429 || status==503 || status==504;
	}

	static <T extends Entity> Client<T> resource(final URI base, final Class<? extends T> clazz) {
		return new Client<>(base,new EntityUnmarshallingStrategy<T>(clazz));
	}

	static <T> Client<List<T>> list(final URI base, final Class<? extends T> clazz) {
		return new Client<>(base,new ListUnmarshallingStrategy<T>(clazz));
	}

}
