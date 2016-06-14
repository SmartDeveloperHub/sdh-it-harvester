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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Entity;

import com.google.common.collect.Lists;

import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ClientTest extends ClientTestHelper {

	public static class Resource extends Entity {

		private String value;

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

	}

	private static final URI BASE = URI.create("http://www.example.org/api/");

	private Resource expectedResource() {
		final Resource resource = new Resource();
		resource.setValue("value");
		return resource;
	}

	private Resource getResource(final String format, final Object... args) throws IOException {
		return Client.resource(BASE,Resource.class).get(format,args);
	}

	private <T> List<T> getList(final Class<? extends T> clazz,final String format, final Object... args) throws IOException {
		return Client.list(BASE,clazz).get(format,args);
	}

	private String serialize(final Resource resource) throws IOException {
		return Entities.marshallEntity(resource);
	}

	private <T> String serialize(final List<T> resource) throws IOException {
		return Entities.marshallList(resource);
	}

	@Test
	public void shouldReturnValidResourceIfRemoteEndpointReturnsAProperSerialization() throws Exception {
		final Resource resource = expectedResource();
		setUpHappyPath(serialize(resource));
		final String format = "resource/%s";
		final Resource result = getResource(format,"asdv");
		verifyHappyPath(serialize(result),BASE+"resource/asdv");
	}

	@Test
	public void shouldReturnValidListIfRemoteEndpointReturnsAProperSerialization() throws Exception {
		final List<String> resource = Lists.newArrayList("1","3","2");
		setUpHappyPath(serialize(resource));
		final String format = "resource/%s";
		final List<String> result=getList(String.class,format,"asdv");
		verifyHappyPath(serialize(result),BASE+"resource/asdv");
	}

	@Test
	public void shouldRetryOnThrottling() throws Exception {
		final Resource resource = expectedResource();
		setUpThrottling(serialize(resource));
		final Resource result  = getResource("commits","id");
		verifyRetries(serialize(result),BASE+"commits");
	}

	@Test
	public void shouldRetryOnServiceUnavailable() throws Exception {
		final Resource resource = expectedResource();
		setUpServiceUnavailable(serialize(resource));
		final Resource result  = getResource("contributors/%d",2);
		verifyRetries(serialize(result),BASE+"contributors/2");
	}

	@Test
	public void shouldRetryOnGatewayTimeout() throws Exception {
		final Resource resource = expectedResource();
		setUpGatewayTimeout(serialize(resource));
		final Resource result  = getResource("projects/%s/components/%d","abc",2);
		verifyRetries(serialize(result),BASE+"projects/abc/components/2");
	}

	@Test
	public void shouldRetryOnNullResponse() throws Exception {
		final Resource resource = expectedResource();
		setUpNullResponse(serialize(resource));
		final Resource result  = getResource("projects/%d/versions/%s",2,"abc");
		verifyRetries(serialize(result),BASE+"projects/2/versions/abc");
	}

	@Test
	public void shouldFailOnUnretriableServiceResponse() throws Exception {
		setUpUnretriableFailure();
		try {
			getResource("id");
			fail("Should fail on unretriable service response");
		} catch (final ServiceFailureException e) {
			verifyUnretriableFailure(e,BASE+"id");
		}
	}

	@Test
	public void shouldFailOnInvalidResourceServiceResponse() throws Exception {
		setUpServiceFailure();
		try {
			getResource("id");
			fail("Should fail on invalid service response when retrieving a resource");
		} catch (final InvalidServiceResponseException e) {
			verifyServiceFailure(e,BASE+"id");
		}
	}

	@Test
	public void shouldFailOnInvalidListServiceResponse() throws Exception {
		setUpServiceFailure();
		try {
			getList(String.class,"id");
			fail("Should fail on invalid service response when retrieving a list");
		} catch (final InvalidServiceResponseException e) {
			verifyServiceFailure(e,BASE+"id");
		}
	}

	@Test
	public void shouldFailIfMaxRetriesExceeded() throws Exception {
		setUpRetries(null,429,503,504,504,200);
		try {
			getResource("id");
			fail("Should fail on service failure");
		} catch (final ServiceFailureException e) {
			fail("Should not throw a service failure exception");
		} catch (final IOException e) {
			verifyRetries(null, BASE+"id");
		}
	}

}