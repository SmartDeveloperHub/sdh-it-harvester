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
package org.smartdeveloperhub.harvesters.it.frontend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class HarvesterConfigurationTest {

	private static final String VALID_URI = "http://www.example.com:5000/api";
	private static final String INVALID_SYNTAX_URI = "\\u128-Invalid URI-\\u128";

	private HarvesterConfiguration sut;

	@Before
	public void setUp() {
		this.sut = new HarvesterConfiguration();
	}

	@After
	public void tearDown() {
		System.clearProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY);
	}

	@Test
	public void testHasCustomNamespaces() {
		assertThat(this.sut.namespaces(),notNullValue());
		assertThat(this.sut.namespaces().getDeclaredPrefixes(),not(hasSize(0)));
	}

	@Test
	public void testTarget$noConfigurationAvailable() throws Exception {
		assertThat(this.sut.target(),nullValue());
	}

	@Test
	public void testTarget$relativeURI() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,"/api");
		assertThat(this.sut.target(),nullValue());
	}

	@Test
	public void testTarget$nonHTTPURI() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,"jar:/api");
		assertThat(this.sut.target(),nullValue());
	}

	@Test
	public void testTarget$validURIViaSystemProperty() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,VALID_URI);
		assertThat(this.sut.target(),equalTo(URI.create(VALID_URI)));
	}

	@Test
	public void testTarget$invalidURIViaSystemPropertyAndNoEnvironmentVariable() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,INVALID_SYNTAX_URI);
		assertThat(this.sut.target(),nullValue());
	}

	@Test
	public void testTarget$invalidURIViaSystemPropertyAndEnvironmentVariable() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,INVALID_SYNTAX_URI);
		new MockUp<System>() {
			@Mock
			public String getenv(final String name) {
				assertThat(name,equalTo(HarvesterConfiguration.IT_HARVESTER_ENVIRONMENT_VARIABLE));
				return INVALID_SYNTAX_URI;
			}
		};
		assertThat(this.sut.target(),nullValue());
	}

	@Test
	public void testTarget$invalidURIViaSystemPropertyAndValidURIViaEnvironmentVariable() throws Exception {
		System.setProperty(HarvesterConfiguration.IT_HARVESTER_SYSTEM_PROPERTY,INVALID_SYNTAX_URI);
		new MockUp<System>() {
			@Mock
			public String getenv(final String name) {
				assertThat(name,equalTo(HarvesterConfiguration.IT_HARVESTER_ENVIRONMENT_VARIABLE));
				return VALID_URI;
			}
		};
		assertThat(this.sut.target(),equalTo(URI.create(VALID_URI)));
	}

	@Test
	public void testTarget$noURIViaSystemPropertyAndValidURIViaEnvironmentVariable() throws Exception {
		new MockUp<System>() {
			@Mock
			public String getenv(final String name) {
				assertThat(name,equalTo(HarvesterConfiguration.IT_HARVESTER_ENVIRONMENT_VARIABLE));
				return VALID_URI;
			}
		};
		assertThat(this.sut.target(),equalTo(URI.create(VALID_URI)));
	}

	@Test
	public void testTarget$validValuesAreNotRecalculated() throws Exception {
		final AtomicInteger invocations=new AtomicInteger();
		new MockUp<System>() {
			@Mock
			public String getenv(final String name) {
				invocations.incrementAndGet();
				assertThat(name,equalTo(HarvesterConfiguration.IT_HARVESTER_ENVIRONMENT_VARIABLE));
				return VALID_URI;
			}
		};
		assertThat(this.sut.target(),equalTo(URI.create(VALID_URI)));
		assertThat(this.sut.target(),equalTo(URI.create(VALID_URI)));
		assertThat(invocations.get(),equalTo(1));
	}

	@Test
	public void testTarget$invalidValuesAreNotRecalculated() throws Exception {
		final AtomicInteger invocations=new AtomicInteger();
		new MockUp<System>() {
			@Mock
			public String getenv(final String name) {
				invocations.incrementAndGet();
				assertThat(name,equalTo(HarvesterConfiguration.IT_HARVESTER_ENVIRONMENT_VARIABLE));
				return INVALID_SYNTAX_URI;
			}
		};
		assertThat(this.sut.target(),nullValue());
		assertThat(this.sut.target(),nullValue());
		assertThat(invocations.get(),equalTo(2));
	}

}
