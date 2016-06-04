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
package org.smartdeveloperhub.harvesters.it.frontend.project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueKey;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

import com.google.common.collect.Sets;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ProjectHandlerTest {

	@Injectable private BackendController controller;

	private final String key="1";

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private Project entity;

	@Tested
	private ProjectHandler sut;

	private Name<String> projectName() {
		return NamingScheme.getDefault().name(this.key);
	}

	private ManagedIndividualId projectId() {
		return ManagedIndividualId.createId(projectName(), ProjectHandler.ID);
	}

	private IssueKey issueKey(final String issueId) {
		return new IssueKey(this.key,issueId);
	}

	private Name<IssueKey> issueName(final String issueId) {
		return IdentityUtil.issueName(issueKey(issueId));
	}

	private ManagedIndividualId issueId(final String issueId) {
		return ManagedIndividualId.createId(issueName(issueId), IssueHandler.ID);
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.projectId(ProjectHandlerTest.this.resource);this.result=ProjectHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			ProjectHandlerTest.this.controller.getProject(ProjectHandlerTest.this.key);this.result=ProjectHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller,this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$supportsProjectsWithEmails() throws Exception {
		new Expectations() {{
			IdentityUtil.issueName(issueKey("topIssue1"));this.result=projectName();
			IdentityUtil.issueName(issueKey("topIssue2"));this.result=projectName();
			IdentityUtil.projectName(ProjectHandlerTest.this.key);this.result=projectName();
			ProjectHandlerTest.this.entity.getId();this.result=ProjectHandlerTest.this.key;
			ProjectHandlerTest.this.entity.getTitle();this.result="title";
			ProjectHandlerTest.this.entity.getTopIssues();this.result=Sets.newHashSet("topIssue1","topIssue2");
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(projectId());
		final IndividualHelper newHelper = DataSetUtils.newHelper(individual);
		assertThat(newHelper.types(),hasItem(URI.create(IT.PROJECT_TYPE)));
		assertThat(newHelper.property(IT.PROJECT_ID).firstValue(String.class),equalTo(this.key));
		assertThat(newHelper.property(IT.PROJECT_TITLE).firstValue(String.class),equalTo("title"));
		assertThat(
			individual.
				property(URI.create(IT.HAS_PROJECT_TOP_ISSUE)).
					hasIdentifiedIndividual(issueId("topIssue1")),
			equalTo(true));
		assertThat(
			individual.
				property(URI.create(IT.HAS_PROJECT_TOP_ISSUE)).
					hasIdentifiedIndividual(issueId("topIssue2")),
			equalTo(true));
	}

	@Test
	public void testToDataSet$supportsProjectsWithNoEmails() throws Exception {
		new Expectations() {{
			IdentityUtil.projectName(ProjectHandlerTest.this.key);this.result=projectName();
			ProjectHandlerTest.this.entity.getId();this.result=ProjectHandlerTest.this.key;
			ProjectHandlerTest.this.entity.getTitle();this.result="title";
			ProjectHandlerTest.this.entity.getTopIssues();this.result=Sets.newHashSet();
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(projectId());
		final IndividualHelper newHelper = DataSetUtils.newHelper(individual);
		assertThat(newHelper.types(),hasItem(URI.create(IT.PROJECT_TYPE)));
		assertThat(newHelper.property(IT.PROJECT_ID).firstValue(String.class),equalTo(this.key));
		assertThat(newHelper.property(IT.PROJECT_TITLE).firstValue(String.class),equalTo("title"));
	}

}
