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
package org.smartdeveloperhub.harvesters.it.frontend.commit;

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
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class CommitHandlerTest {

	@Injectable private BackendController controller;

	private final String key="1";

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private Commit entity;

	@Tested
	private CommitHandler sut;

	private Name<String> commitName() {
		return NamingScheme.getDefault().name(this.key);
	}

	private ManagedIndividualId commitId() {
		return ManagedIndividualId.createId(commitName(), CommitHandler.ID);
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.commitId(CommitHandlerTest.this.resource);this.result=CommitHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			CommitHandlerTest.this.controller.getCommit(CommitHandlerTest.this.key);this.result=CommitHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller,this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet() throws Exception {
		new Expectations() {{
			IdentityUtil.commitName(CommitHandlerTest.this.key);this.result=commitName();
			CommitHandlerTest.this.entity.getId();this.result=CommitHandlerTest.this.key;
			CommitHandlerTest.this.entity.getRepository();this.result="repository";
			CommitHandlerTest.this.entity.getBranch();this.result="branch";
			CommitHandlerTest.this.entity.getHash();this.result="hash";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(commitId());
		final IndividualHelper newHelper = DataSetUtils.newHelper(individual);
		assertThat(newHelper.types(),hasItem(URI.create(IT.COMMIT_TYPE)));
		assertThat(newHelper.property(IT.ID).firstValue(String.class),equalTo(this.key));
		assertThat(newHelper.property(IT.COMMIT_ID).firstValue(String.class),equalTo(this.key));
		assertThat(newHelper.property(IT.REPOSITORY).firstValue(URI.class),equalTo(URI.create("repository")));
		assertThat(newHelper.property(IT.BRANCH).firstValue(String.class),equalTo("branch"));
		assertThat(newHelper.property(IT.HASH).firstValue(String.class),equalTo("hash"));
	}

}
