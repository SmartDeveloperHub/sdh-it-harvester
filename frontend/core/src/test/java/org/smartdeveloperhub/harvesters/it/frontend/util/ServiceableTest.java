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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.ext.UnknownResourceException;
import org.slf4j.Logger;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ServiceableTest {

	@Test
	public void testUnknownResource() throws Exception {
		final UnknownResourceException failure = new Serviceable() {}.unknownResource("resourceId","resourceType");
		assertThat(failure.getMessage(),equalTo("Could not find resourceType resource for resourceId"));
	}

	@Test
	public void testUnexpectedFailure$withException() throws Exception {
		final ApplicationRuntimeException failure = new Serviceable() {}.unexpectedFailure(new RuntimeException("Failure"), "Message %s","arg");
		assertThat(failure.getMessage(),equalTo("Message arg"));
		assertThat(failure.getCause(),instanceOf(RuntimeException.class));
		assertThat(failure.getCause().getMessage(),equalTo("Failure"));
	}

	@Test
	public void testInfo(@Mocked final Logger logger) throws Exception {
		final Serviceable sut = new Serviceable() {};
		Deencapsulation.setField(sut,logger);
		new Expectations() {{
			logger.info("Message arg");
		}};
		sut.info("Message %s","arg");
	}

	@Test
	public void testDebug(@Mocked final Logger logger) throws Exception {
		final Serviceable sut = new Serviceable() {};
		Deencapsulation.setField(sut,logger);
		new Expectations() {{
			logger.debug("Message arg");
		}};
		sut.debug("Message %s","arg");
	}


}
