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
package org.smartdeveloperhub.harvesters.it.frontend.version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class VersionKeyTest {

	private static final String VERSION_ID = "versionId1";
	private static final String ALTERNATIVE_VERSION_ID = "versionId2";

	private static final String PROJECT_ID = "1";
	private static final String ALTERNATIVE_PROJECT_ID = "2";

	@Test
	public void testKeepsProjectId() throws Exception {
		assertThat(defaultKey().getProjectId(),equalTo(PROJECT_ID));
	}

	@Test
	public void testKeepsVersionId() throws Exception {
		assertThat(defaultKey().getVersionId(),equalTo(VERSION_ID));
	}

	@Test
	public void verifyHasCustomStringRepresentation() {
		assertThat(defaultKey().toString(),equalTo("VersionKey{projectId=1, versionId=versionId1}"));
	}

	@Test
	public void testEquals$differentType() {
		assertThat((Object)defaultKey(),not(equalTo((Object)"string")));
	}

	@Test
	public void testEquals$equalInstance() {
		final VersionKey one=defaultKey();
		final VersionKey other=defaultKey();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testEquals$differentVersionId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentVersionIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testEquals$differentProjectId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentProjectIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testHashCode$equalInstance() {
		final VersionKey one=defaultKey();
		final VersionKey other=defaultKey();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testHashCode$differentVersionId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentVersionIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$differentProjectId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$different() {
		final VersionKey one=defaultKey();
		final VersionKey other=alternativeKey();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testCompare$equal() {
		final VersionKey one=defaultKey();
		final VersionKey other=defaultKey();
		assertThat(one.compareTo(other),equalTo(0));
	}

	@Test
	public void testCompare$differentProjectId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	@Test
	public void testCompare$differentVersionId() {
		final VersionKey one=defaultKey();
		final VersionKey other=keyWithDifferentVersionIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	private VersionKey defaultKey() {
		return new VersionKey(PROJECT_ID,VERSION_ID);
	}

	private VersionKey keyWithDifferentProjectIdentifier() {
		return new VersionKey(ALTERNATIVE_PROJECT_ID,VERSION_ID);
	}

	private VersionKey keyWithDifferentVersionIdentifier() {
		return new VersionKey(PROJECT_ID,ALTERNATIVE_VERSION_ID);
	}

	private VersionKey alternativeKey() {
		return new VersionKey(ALTERNATIVE_PROJECT_ID,ALTERNATIVE_VERSION_ID);
	}
}
