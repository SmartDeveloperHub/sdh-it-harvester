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
package org.smartdeveloperhub.harvesters.it.frontend.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.net.URI;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.Fixture;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;


public class ITTest {

	@Test
	public void verifyIsValidUtilityClass() {
		assertThat(Utils.isUtilityClass(IT.class),equalTo(true));
	}

	@Test
	public void translatesStatusProperly() {
		assertThat(IT.forStatus(Status.OPEN),equalTo(term(IT.OPEN)));
		assertThat(IT.forStatus(Status.IN_PROGRESS),equalTo(term(IT.IN_PROGRESS)));
		assertThat(IT.forStatus(Status.CLOSED),equalTo(term(IT.CLOSED)));
		assertThat(IT.forStatus(null),nullValue());
	}
	@Test
	public void translatesSeverityProperly() {
		assertThat(IT.forSeverity(Severity.BLOCKER),equalTo(term(IT.BLOCKER_SEVERITY)));
		assertThat(IT.forSeverity(Severity.CRITICAL),equalTo(term(IT.CRITICAL_SEVERITY)));
		assertThat(IT.forSeverity(Severity.SEVERE),equalTo(term(IT.GRAVE_SEVERITY)));
		assertThat(IT.forSeverity(Severity.LOW),equalTo(term(IT.NORMAL_SEVERITY)));
		assertThat(IT.forSeverity(Severity.TRIVIAL),equalTo(term(IT.TRIVIAL_SEVERITY)));
		assertThat(IT.forSeverity(null),nullValue());
	}

	@Test
	public void translatesPriorityProperly() {
		assertThat(IT.forPriority(Priority.VERY_HIGH),equalTo(term(IT.VERY_HIGH_PRIORITY)));
		assertThat(IT.forPriority(Priority.HIGH),equalTo(term(IT.HIGH_PRIORITY)));
		assertThat(IT.forPriority(Priority.MEDIUM),equalTo(term(IT.MEDIUM_PRIORITY)));
		assertThat(IT.forPriority(Priority.LOW),equalTo(term(IT.LOW_PRIORITY)));
		assertThat(IT.forPriority(null),nullValue());
	}

	@Test
	public void translatesTypeProperly() {
		assertThat(IT.forType(Type.BUG),equalTo(term(IT.BUG_TYPE)));
		assertThat(IT.forType(Type.IMPROVEMENT),equalTo(term(IT.IMPROVEMENT_TYPE)));
		assertThat(IT.forType(Type.TASK),equalTo(term(IT.TASK_TYPE)));
		assertThat(IT.forType(null),nullValue());
	}

	@Test
	public void translatesPropertyOfItemProperly() {
		assertThat(IT.propertyOf(Fixture.assigneeChangeItem()),equalTo(term(IT.IS_ASSIGNED_TO)));
		assertThat(IT.propertyOf(Fixture.blockedIssuesChangeItem()),equalTo(term(IT.BLOCKS_ISSUE)));
		assertThat(IT.propertyOf(Fixture.childIssuesChangeItem()),equalTo(term(IT.IS_COMPOSED_OF_ISSUE)));
		assertThat(IT.propertyOf(Fixture.closedDateChangeItem()),equalTo(term(IT.DATE_CLOSED)));
		assertThat(IT.propertyOf(Fixture.commitsChangeItem()),equalTo(term(IT.ASSOCIATED_TO_COMMIT)));
		assertThat(IT.propertyOf(Fixture.componentsChangeItem()),equalTo(term(IT.ASSOCIATED_TO_COMPONENT)));
		assertThat(IT.propertyOf(Fixture.descriptionChangeItem()),equalTo(term(IT.DESCRIPTION)));
		assertThat(IT.propertyOf(Fixture.dueToDateChangeItem()),equalTo(term(IT.DUE_TO)));
		assertThat(IT.propertyOf(Fixture.estimatedTimeChangeItem()),equalTo(term(IT.ESTIMATED_TIME)));
		assertThat(IT.propertyOf(Fixture.openedDateChangeItem()),equalTo(term(IT.DATE_OPEN)));
		assertThat(IT.propertyOf(Fixture.priorityChangeItem()),equalTo(term(IT.HAS_PRIORITY)));
		assertThat(IT.propertyOf(Fixture.severityChangeItem()),equalTo(term(IT.HAS_SEVERITY)));
		assertThat(IT.propertyOf(Fixture.statusChangeItem()),equalTo(term(IT.HAS_STATUS)));
		assertThat(IT.propertyOf(Fixture.tagsChangeItem()),equalTo(term(IT.ISSUE_CUSTOM_TAG)));
		assertThat(IT.propertyOf(Fixture.titleChangeItem()),equalTo(term(IT.ISSUE_TITLE)));
		assertThat(IT.propertyOf(Fixture.typeChangeItem()),equalTo(term(RDF.TYPE)));
		assertThat(IT.propertyOf(Fixture.versionsChangeItem()),equalTo(term(IT.AFFECTS_VERSION)));
		assertThat(IT.propertyOf(null),equalTo(null));
	}

	@Test
	public void definesProperVocabularySourceCode() {
		assertThat(IT.sourceCode().toString(),equalTo("http://www.smartdeveloperhub.org/vocabulary/v1/it.ttl"));
	}

	private URI term(final String term) {
		return URI.create(term);
	}

}
