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
package org.smartdeveloperhub.harvesters.it.frontend.component;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class ComponentKeyTest {

	private static final String COMPONENT_ID = "componentId1";
	private static final String ALTERNATIVE_COMPONENT_ID = "componentId2";

	private static final String PROJECT_ID = "1";
	private static final String ALTERNATIVE_PROJECT_ID = "2";

	@Test
	public void testKeepsProjectId() throws Exception {
		assertThat(defaultKey().getProjectId(),equalTo(PROJECT_ID));
	}

	@Test
	public void testKeepsComponentId() throws Exception {
		assertThat(defaultKey().getComponentId(),equalTo(COMPONENT_ID));
	}

	@Test
	public void verifyHasCustomStringRepresentation() {
		assertThat(defaultKey().toString(),equalTo("ComponentKey{projectId=1, componentId=componentId1}"));
	}

	@Test
	public void testEquals$differentType() {
		assertThat((Object)defaultKey(),not(equalTo((Object)"string")));
	}

	@Test
	public void testEquals$equalInstance() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=defaultKey();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testEquals$differentComponentId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentComponentIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testEquals$differentProjectId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentProjectIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testHashCode$equalInstance() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=defaultKey();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testHashCode$differentComponentId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentComponentIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$differentProjectId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$different() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=alternativeKey();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testCompare$equal() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=defaultKey();
		assertThat(one.compareTo(other),equalTo(0));
	}

	@Test
	public void testCompare$differentProjectId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	@Test
	public void testCompare$differentComponentId() {
		final ComponentKey one=defaultKey();
		final ComponentKey other=keyWithDifferentComponentIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	private ComponentKey defaultKey() {
		return new ComponentKey(PROJECT_ID,COMPONENT_ID);
	}

	private ComponentKey keyWithDifferentProjectIdentifier() {
		return new ComponentKey(ALTERNATIVE_PROJECT_ID,COMPONENT_ID);
	}

	private ComponentKey keyWithDifferentComponentIdentifier() {
		return new ComponentKey(PROJECT_ID,ALTERNATIVE_COMPONENT_ID);
	}

	private ComponentKey alternativeKey() {
		return new ComponentKey(ALTERNATIVE_PROJECT_ID,ALTERNATIVE_COMPONENT_ID);
	}
}
