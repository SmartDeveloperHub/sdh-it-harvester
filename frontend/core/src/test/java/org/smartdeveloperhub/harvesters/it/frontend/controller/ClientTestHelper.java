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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;

public class ClientTestHelper {

	@Mocked CloseableHttpClient client;
	@Mocked CloseableHttpResponse response;
	@Mocked StatusLine statusLine;
	@Mocked HttpEntity entity;

	private String responseBody;
	private int retries;

	protected void setUpHappyPath(final String responseBody) throws IOException {
		this.responseBody=responseBody;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return ClientTestHelper.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			ClientTestHelper.this.client.execute((HttpGet)this.any);this.result=ClientTestHelper.this.response;
			ClientTestHelper.this.response.getStatusLine();this.result=ClientTestHelper.this.statusLine;
			ClientTestHelper.this.statusLine.getStatusCode();this.result=200;
			ClientTestHelper.this.response.getEntity();this.result=ClientTestHelper.this.entity;
		}};
	}

	protected void setUpNullResponse(final String responseBody) throws IOException {
		this.responseBody=responseBody;
		this.retries=2;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return ClientTestHelper.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			ClientTestHelper.this.client.execute((HttpGet)this.any);this.result=ClientTestHelper.this.response;
			ClientTestHelper.this.response.getStatusLine();this.result=ClientTestHelper.this.statusLine;
			ClientTestHelper.this.statusLine.getStatusCode();this.result=200;
			ClientTestHelper.this.response.getEntity();returns(null,ClientTestHelper.this.entity);
		}};
	}

	protected void setUpThrottling(final String responseBody) throws IOException {
		setUpRetries(responseBody,429,429,200);
	}

	protected void setUpServiceUnavailable(final String responseBody) throws IOException {
		setUpRetries(responseBody,503,503,200);
	}

	protected void setUpGatewayTimeout(final String responseBody) throws IOException {
		setUpRetries(responseBody,504,504,200);
	}

	protected void setUpUnretriableFailure() throws IOException {
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return ClientTestHelper.this.client;
			}
		};
		new Expectations(){{
			ClientTestHelper.this.client.execute((HttpGet)this.any);this.result=ClientTestHelper.this.response;
			ClientTestHelper.this.response.getStatusLine();this.result=ClientTestHelper.this.statusLine;
			ClientTestHelper.this.statusLine.getStatusCode();this.result=404;
		}};
	}

	protected void setUpServiceFailure() throws IOException {
		setUpHappyPath("false");
	}

	protected void setUpRetries(final String responseBody, final int... args) throws IOException {
		this.responseBody=responseBody;
		this.retries=args.length;
		new MockUp<HttpClients>() {
			@Mock
			public final CloseableHttpClient createDefault() {
				return ClientTestHelper.this.client;
			}
		};
		new MockUp<EntityUtils>() {
			@Mock
			String toString(final HttpEntity entity) throws IOException, ParseException {
				assertThat(entity,sameInstance(entity));
				return responseBody;
			}
		};
		new Expectations(){{
			ClientTestHelper.this.client.execute((HttpGet)this.any);this.result=ClientTestHelper.this.response;
			ClientTestHelper.this.response.getStatusLine();this.result=ClientTestHelper.this.statusLine;
			ClientTestHelper.this.statusLine.getStatusCode();returns(args);
			ClientTestHelper.this.response.getEntity();this.result=ClientTestHelper.this.entity;
		}};
	}

	protected void verifyHappyPath(final String result, final String resourcePath) throws IOException {
		assertThat(result,equalTo(this.responseBody));
		new Verifications() {{
			HttpGet method;
			ClientTestHelper.this.client.execute(method=withCapture());
			assertThat(method.getURI().toString(),equalTo(resourcePath));
		}};
	}

	protected void verifyUnretriableFailure(final ServiceFailureException e, final String resourcePath) throws IOException {
		verifyFailure(e, resourcePath, 404);
	}

	protected void verifyServiceFailure(final InvalidServiceResponseException e, final String resourcePath) throws IOException {
		assertThat(e.getResource(),equalTo(resourcePath));
		assertThat(e.getResponse(),equalTo("false"));
		assertThat(e.getExpectation(),notNullValue());
		new Verifications() {{
			HttpGet method;
			ClientTestHelper.this.client.execute(method=withCapture());
			assertThat(method.getURI().toString(),equalTo(resourcePath));
			final Header[] accept = method.getHeaders(HttpHeaders.ACCEPT);
			assertThat(accept.length,equalTo(1));
			assertThat(accept[0].getValue(),equalTo(Client.API_MIME));
		}};
	}

	protected void verifyFailure(final ServiceFailureException e, final String resourcePath, final int statusCode) throws IOException {
		assertThat(e.getStatus(),equalTo(statusCode));
		assertThat(e.getResource(),equalTo(resourcePath));
		new Verifications() {{
			HttpGet method;
			ClientTestHelper.this.client.execute(method=withCapture());
			assertThat(method.getURI().toString(),equalTo(resourcePath));
			final Header[] accept = method.getHeaders(HttpHeaders.ACCEPT);
			assertThat(accept.length,equalTo(1));
			assertThat(accept[0].getValue(),equalTo(Client.API_MIME));
		}};
	}

	protected void verifyRetries(final String result, final String resourcePath) throws IOException {
		assertThat(result,equalTo(this.responseBody));
		for(int i=0;i<ClientTestHelper.this.retries;i++) {
			new Verifications() {{
					HttpGet method;
					ClientTestHelper.this.client.execute(method=withCapture());
					assertThat(method.getURI().toString(),equalTo(resourcePath));
			}};
		}
	}

}