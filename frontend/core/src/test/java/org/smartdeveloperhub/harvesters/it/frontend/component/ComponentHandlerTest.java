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
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ComponentHandlerTest {

	@Injectable private BackendController controller;

	@Mocked private ResourceSnapshot resource;
	@Mocked private IdentityUtil util;
	@Mocked private ComponentKey key;
	@Mocked private Component entity;

	@Tested
	private ComponentHandler sut;

	private Name<ComponentKey> componentName() {
		return NamingScheme.getDefault().name(this.key);
	}

	private ManagedIndividualId componentId() {
		return ManagedIndividualId.createId(componentName(),ComponentHandler.ID);
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IdentityUtil.componentId(ComponentHandlerTest.this.resource);this.result=ComponentHandlerTest.this.key;
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			ComponentHandlerTest.this.key.getComponentId();this.result="componentId";
			ComponentHandlerTest.this.key.getProjectId();this.result="1";
			ComponentHandlerTest.this.controller.getProjectComponent("1","componentId");this.result=ComponentHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet() throws Exception {
		new Expectations() {{
			IdentityUtil.componentName(ComponentHandlerTest.this.key);this.result=componentName();
			ComponentHandlerTest.this.entity.getId();this.result="componentId";
			ComponentHandlerTest.this.entity.getName();this.result="title";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(componentId());
		final IndividualHelper newHelper = DataSetUtils.newHelper(individual);
		assertThat(newHelper.types(),hasItem(URI.create(IT.COMPONENT_TYPE)));
		assertThat(newHelper.property(IT.ID).firstValue(String.class),equalTo("componentId"));
		assertThat(newHelper.property(IT.COMPONENT_ID).firstValue(String.class),equalTo("componentId"));
		assertThat(newHelper.property(DCTERMS.TITLE).firstValue(String.class),equalTo("title"));
		assertThat(newHelper.property(IT.COMPONENT_TITLE).firstValue(String.class),equalTo("title"));
	}
}
