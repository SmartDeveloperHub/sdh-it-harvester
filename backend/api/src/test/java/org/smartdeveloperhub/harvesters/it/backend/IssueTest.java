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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;

public class IssueTest {

	@Test
	public void canMarshallAndUnmarshallIssues() throws IOException {
		final Issue one = Fixture.defaultIssue();
		final String str = Entities.marshallEntity(one);
		final Issue other = Entities.unmarshallEntity(str,Issue.class);
		assertThat(other.getAssignee(),equalTo(one.getAssignee()));
		assertThat(other.getBlockedIssues(),equalTo(one.getBlockedIssues()));
		assertThat(other.getChanges().getEntries(),equalTo(one.getChanges().getEntries()));
		assertThat(other.getChildIssues(),equalTo(one.getChildIssues()));
		assertThat(other.getClosed(),equalTo(one.getClosed()));
		assertThat(other.getCommits(),equalTo(one.getCommits()));
		assertThat(other.getComponents(),equalTo(one.getComponents()));
		assertThat(other.getCreationDate(),equalTo(one.getCreationDate()));
		assertThat(other.getDescription(),equalTo(one.getDescription()));
		assertThat(other.getDueTo(),equalTo(one.getDueTo()));
		assertThat(other.getEstimatedTime(),equalTo(one.getEstimatedTime()));
		assertThat(other.getId(),equalTo(one.getId()));
		assertThat(other.getOpened(),equalTo(one.getOpened()));
		assertThat(other.getPriority(),equalTo(one.getPriority()));
		assertThat(other.getReporter(),equalTo(one.getReporter()));
		assertThat(other.getSeverity(),equalTo(one.getSeverity()));
		assertThat(other.getStatus(),equalTo(one.getStatus()));
		assertThat(other.getTags(),equalTo(one.getTags()));
		assertThat(other.getType(),equalTo(one.getType()));
		assertThat(other.getVersions(),equalTo(one.getVersions()));
	}

	@Test
	public void issuesHaveCustomToString() {
		final Issue sut = Fixture.defaultIssue();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void allValuesAreListed() {
		assertThat(
			Arrays.asList(Type.values()),
			contains(
				Type.BUG,
				Type.IMPROVEMENT,
				Type.TASK
			)
		);
	}

	@Test
	public void valuesCanBeFound() {
		for(final Type value:Type.values()) {
			assertThat(Type.valueOf(value.name()),equalTo(value));
		}
	}

}
