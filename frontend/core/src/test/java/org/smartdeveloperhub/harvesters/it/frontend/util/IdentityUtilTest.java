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
package org.smartdeveloperhub.harvesters.it.frontend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.Serializable;
import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentKey;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueKey;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionKey;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IdentityUtilTest {

	private static final URI COLLECTOR_URI = URI.create("http://www.example.org:5000/api");

	@Mocked ResourceSnapshot resource;
	@Mocked Name<?> name;

	private void setUpMock(final Serializable id) {
		new Expectations() {{
			IdentityUtilTest.this.resource.name();this.result=IdentityUtilTest.this.name;
			IdentityUtilTest.this.name.id();this.result=id;
		}};
	}

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(IdentityUtil.class),equalTo(true));
	}

	@Test
	public void testCollectorName() throws Exception {
		assertThat(IdentityUtil.collectorName(COLLECTOR_URI).id(),equalTo(COLLECTOR_URI));
	}

	@Test
	public void testProjectName() throws Exception {
		assertThat(IdentityUtil.projectName("1").id(),equalTo("1"));
	}

	@Test
	public void testProjectId() throws Exception {
		final String id = "1";
		setUpMock(id);
		assertThat(IdentityUtil.projectId(this.resource),equalTo(id));
	}

	@Test
	public void testContributorName() throws Exception {
		assertThat(IdentityUtil.contributorName("userId").id(),equalTo("userId"));
	}

	@Test
	public void testContributorId() throws Exception {
		final String id = "userId";
		setUpMock(id);
		assertThat(IdentityUtil.contributorId(this.resource),equalTo(id));
	}

	@Test
	public void testCommitName() throws Exception {
		assertThat(IdentityUtil.commitName("commitId").id(),equalTo("commitId"));
	}

	@Test
	public void testCommitId() throws Exception {
		final String id = "commitId";
		setUpMock(id);
		assertThat(IdentityUtil.commitId(this.resource),equalTo(id));
	}

	@Test
	public void testComponentName() throws Exception {
		final ComponentKey key = new ComponentKey("1", "componentId");
		assertThat(IdentityUtil.componentName(key).id(),equalTo(key));
	}

	@Test
	public void testComponentId() throws Exception {
		final ComponentKey key = new ComponentKey("1", "componentId");
		setUpMock(key);
		assertThat(IdentityUtil.componentId(this.resource),equalTo(key));
	}

	@Test
	public void testVersionName() throws Exception {
		final VersionKey key = new VersionKey("1", "versionId");
		assertThat(IdentityUtil.versionName(key).id(),equalTo(key));
	}

	@Test
	public void testVersionId() throws Exception {
		final VersionKey key = new VersionKey("1", "versionId");
		setUpMock(key);
		assertThat(IdentityUtil.versionId(this.resource),equalTo(key));
	}

	@Test
	public void testIssueName() throws Exception {
		final IssueKey key = new IssueKey("1", "issueId");
		assertThat(IdentityUtil.issueName(key).id(),equalTo(key));
	}

	@Test
	public void testIssueId() throws Exception {
		final IssueKey key = new IssueKey("1", "issueId");
		setUpMock(key);
		assertThat(IdentityUtil.issueId(this.resource),equalTo(key));
	}

}
