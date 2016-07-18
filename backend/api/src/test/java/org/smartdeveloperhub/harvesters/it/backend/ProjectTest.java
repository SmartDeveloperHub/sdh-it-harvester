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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.2.0-SNAPSHOT
 *   Bundle      : it-backend-api-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;

import com.google.common.collect.ImmutableSet;

public class ProjectTest {

	@Test
	public void canMarshallAndUnmarshallProjects() throws IOException {
		final Project one = defaultProject();
		final String str = Entities.marshallEntity(one);
		final Project other = Entities.unmarshallEntity(str,Project.class);
		assertThat(other.getId(),equalTo(one.getId()));
		assertThat(other.getVersions(),equalTo(one.getVersions()));
		assertThat(other.getComponents(),equalTo(one.getComponents()));
		assertThat(other.getTopIssues(),equalTo(one.getTopIssues()));
		assertThat(other.getIssues(),equalTo(one.getIssues()));
	}

	@Test
	public void projectsHaveCustomToString() {
		final Project sut = defaultProject();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	private Project defaultProject() {
		final Project project = new Project();
		project.setId("id");
		project.setComponents(ImmutableSet.of("c1","c2"));
		project.setVersions(ImmutableSet.of("v1","v2"));
		project.setTopIssues(ImmutableSet.of("ti1","ti2"));
		project.setIssues(ImmutableSet.of("i1","i2"));
		return project;
	}

}
