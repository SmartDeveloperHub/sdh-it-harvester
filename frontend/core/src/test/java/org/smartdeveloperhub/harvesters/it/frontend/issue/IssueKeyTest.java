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
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import org.junit.Test;

public class IssueKeyTest {

	private static final String ISSUE_ID = "issueId1";
	private static final String ALTERNATIVE_ISSUE_ID = "issueId2";

	private static final String PROJECT_ID = "1";
	private static final String ALTERNATIVE_PROJECT_ID = "2";

	@Test
	public void testKeepsProjectId() throws Exception {
		assertThat(defaultKey().getProjectId(),equalTo(PROJECT_ID));
	}

	@Test
	public void testKeepsIssueId() throws Exception {
		assertThat(defaultKey().getIssueId(),equalTo(ISSUE_ID));
	}

	@Test
	public void verifyHasCustomStringRepresentation() {
		assertThat(defaultKey().toString(),equalTo("IssueKey{projectId=1, issueId=issueId1}"));
	}

	@Test
	public void testEquals$differentType() {
		assertThat((Object)defaultKey(),not(equalTo((Object)"string")));
	}

	@Test
	public void testEquals$equalInstance() {
		final IssueKey one=defaultKey();
		final IssueKey other=defaultKey();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testEquals$differentIssueId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentIssueIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testEquals$differentProjectId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentProjectIdentifier();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testHashCode$equalInstance() {
		final IssueKey one=defaultKey();
		final IssueKey other=defaultKey();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testHashCode$differentIssueId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentIssueIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$differentProjectId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testHashCode$different() {
		final IssueKey one=defaultKey();
		final IssueKey other=alternativeKey();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testCompare$equal() {
		final IssueKey one=defaultKey();
		final IssueKey other=defaultKey();
		assertThat(one.compareTo(other),equalTo(0));
	}

	@Test
	public void testCompare$differentProjectId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentProjectIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	@Test
	public void testCompare$differentIssueId() {
		final IssueKey one=defaultKey();
		final IssueKey other=keyWithDifferentIssueIdentifier();
		assertThat(one.compareTo(other),not(greaterThanOrEqualTo(0)));
		assertThat(other.compareTo(one),greaterThan(0));
	}

	private IssueKey defaultKey() {
		return new IssueKey(PROJECT_ID,ISSUE_ID);
	}

	private IssueKey keyWithDifferentProjectIdentifier() {
		return new IssueKey(ALTERNATIVE_PROJECT_ID,ISSUE_ID);
	}

	private IssueKey keyWithDifferentIssueIdentifier() {
		return new IssueKey(PROJECT_ID,ALTERNATIVE_ISSUE_ID);
	}

	private IssueKey alternativeKey() {
		return new IssueKey(ALTERNATIVE_PROJECT_ID,ALTERNATIVE_ISSUE_ID);
	}
}
