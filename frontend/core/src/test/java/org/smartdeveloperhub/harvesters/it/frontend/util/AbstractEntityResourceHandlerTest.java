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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class AbstractEntityResourceHandlerTest {

	@Mocked private ResourceSnapshot resource;
	@Mocked private BackendController controller;
	@Mocked private DataSet dataSet;

	@Test
	public void testGet$happyPath() throws Exception {
		final AbstractEntityResourceHandler<String, String> sut =
			new AbstractEntityResourceHandler<String,String>(this.controller) {
				@Override
				protected String getEntity(final BackendController controller, final String key) throws IOException {
					return "entity";
				}
				@Override
				protected String getId(final ResourceSnapshot resource) {
					return  "key";
				}
				@Override
				protected DataSet toDataSet(final String entity, final String key) {
					return AbstractEntityResourceHandlerTest.this.dataSet;
				}
			};
		assertThat(sut.get(this.resource),sameInstance(this.dataSet));
	}

	@Test
	public void testGet$failurePath() throws Exception {
		final AbstractEntityResourceHandler<String, String> sut =
			new AbstractEntityResourceHandler<String,String>(this.controller) {
				@Override
				protected String getEntity(final BackendController controller, final String key) throws IOException {
					throw new IOException("Failure");
				}
				@Override
				protected String getId(final ResourceSnapshot resource) {
					return  "key";
				}
				@Override
				protected DataSet toDataSet(final String entity, final String key) {
					return AbstractEntityResourceHandlerTest.this.dataSet;
				}
			};
		try {
			sut.get(this.resource);
		} catch (final ApplicationRuntimeException e) {
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

}
