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
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.io.Serializable;
import java.net.URI;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.PropertyHelper;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AbstractItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentKey;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionKey;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ItemPopulatorTest {

	@Mocked
	private Issue issue;

	@Mocked
	private IndividualHelper iHelper;

	@Mocked
	private PropertyHelper pHelper;

	private final String property="myProperty";

	private void checkLiteralRequiredItem(final Item item) {
		final ItemPopulator sut=new ItemPopulator(this.issue,this.iHelper,this.property) {
			@Override
			protected <V> V getValue(final AbstractItem<V> arg) {
				assertThat((Object)arg,sameInstance((Object)item));
				return arg.getNewValue();
			}
		};
		new Expectations() {{
			ItemPopulatorTest.this.iHelper.property(ItemPopulatorTest.this.property);this.result=ItemPopulatorTest.this.pHelper;
			ItemPopulatorTest.this.pHelper.withLiteral(item.getNewValue());
		}};
		item.accept(sut);
	}

	private void checkExternalIndividualRequiredItem(final Item item, final URI expectedIndividual) {
		final ItemPopulator sut=new ItemPopulator(this.issue,this.iHelper,this.property) {
			@Override
			protected <V> V getValue(final AbstractItem<V> arg) {
				assertThat((Object)arg,sameInstance((Object)item));
				return arg.getNewValue();
			}
		};
		new Expectations() {{
			ItemPopulatorTest.this.iHelper.property(ItemPopulatorTest.this.property);this.result=ItemPopulatorTest.this.pHelper;
			ItemPopulatorTest.this.pHelper.withIndividual(expectedIndividual);
		}};
		item.accept(sut);
	}

	private void checkManagedIndividualRequiredItem(final Item item, final Serializable key, final String managerId) {
		final ItemPopulator sut=new ItemPopulator(this.issue,this.iHelper,this.property) {
			@Override
			protected <V> V getValue(final AbstractItem<V> arg) {
				assertThat((Object)arg,sameInstance((Object)item));
				return arg.getNewValue();
			}
		};
		new Expectations() {{
			ItemPopulatorTest.this.iHelper.property(ItemPopulatorTest.this.property);this.result=ItemPopulatorTest.this.pHelper;
		}};
		item.accept(sut);
		new Verifications() {{
			Name<?> specifiedName;
			String specifiedManagerId;
			ItemPopulatorTest.this.pHelper.withIndividual(specifiedName=withCapture(),specifiedManagerId=withCapture());
			assertThat(specifiedManagerId,equalTo(managerId));
			assertThat(specifiedName.id(),equalTo(key));
		}};
	}

	@Test
	public void processesTitleChangeItems() throws Exception {
		final Item item=Item.builder().title().newValue("value").build();
		checkLiteralRequiredItem(item);
	}


	@Test
	public void processesDescriptionChangeItems() throws Exception {
		final Item item=Item.builder().description().newValue("value").build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesOpenDateChangeItems() throws Exception {
		final Item item=Item.builder().openedDate().newValue(new DateTime()).build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesClosedDateChangeItems() throws Exception {
		final Item item=Item.builder().closedDate().newValue(new DateTime()).build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesDueToDateChangeItems() throws Exception {
		final Item item=Item.builder().dueToDate().newValue(new DateTime()).build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesEstimatedTimeDateChangeItems() throws Exception {
		final Item item=Item.builder().estimatedTime().newValue(Minutes.minutes(3).toStandardDuration()).build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesTagsChangeItems() throws Exception {
		final Item item=Item.builder().tags().newValue("value").build();
		checkLiteralRequiredItem(item);
	}

	@Test
	public void processesTypeChangeItem() throws Exception {
		final Item item=Item.builder().type().newValue(Type.BUG).build();
		checkExternalIndividualRequiredItem(item,IT.forType(Type.BUG));
	}

	@Test
	public void processesStatusChangeItem() throws Exception {
		final Item item=Item.builder().status().newValue(Status.OPEN).build();
		checkExternalIndividualRequiredItem(item,IT.forStatus(Status.OPEN));
	}

	@Test
	public void processesSeverityChangeItem() throws Exception {
		final Item item=Item.builder().severity().newValue(Severity.BLOCKER).build();
		checkExternalIndividualRequiredItem(item,IT.forSeverity(Severity.BLOCKER));
	}

	@Test
	public void processesPriorityChangeItem() throws Exception {
		final Item item=Item.builder().priority().newValue(Priority.VERY_HIGH).build();
		checkExternalIndividualRequiredItem(item,IT.forPriority(Priority.VERY_HIGH));
	}

	@Test
	public void processesBlockedIssuesChangeItem() throws Exception {
		final Item item=Item.builder().blockedIssues().newValue("value").build();
		new Expectations() {{
			ItemPopulatorTest.this.issue.getProjectId();this.result="projectId";
		}};
		checkManagedIndividualRequiredItem(item,new IssueKey("projectId","value"),IssueHandler.ID);
	}

	@Test
	public void processesChildIssuesChangeItem() throws Exception {
		final Item item=Item.builder().childIssues().newValue("value").build();
		new Expectations() {{
			ItemPopulatorTest.this.issue.getProjectId();this.result="projectId";
		}};
		checkManagedIndividualRequiredItem(item,new IssueKey("projectId","value"),IssueHandler.ID);
	}

	@Test
	public void processesComponentsChangeItem() throws Exception {
		final Item item=Item.builder().components().newValue("value").build();
		new Expectations() {{
			ItemPopulatorTest.this.issue.getProjectId();this.result="projectId";
		}};
		checkManagedIndividualRequiredItem(item,new ComponentKey("projectId","value"),ComponentHandler.ID);
	}

	@Test
	public void processesVersionsChangeItem() throws Exception {
		final Item item=Item.builder().versions().newValue("value").build();
		new Expectations() {{
			ItemPopulatorTest.this.issue.getProjectId();this.result="projectId";
		}};
		checkManagedIndividualRequiredItem(item,new VersionKey("projectId","value"),VersionHandler.ID);
	}

	@Test
	public void processesAsigneeChangeItem() throws Exception {
		final Item item=Item.builder().assignee().newValue("value").build();
		checkManagedIndividualRequiredItem(item,"value",ContributorHandler.ID);
	}

	@Test
	public void processesCommitsChangeItem() throws Exception {
		final Item item=Item.builder().commits().newValue("value").build();
		checkManagedIndividualRequiredItem(item,"value",CommitHandler.ID);
	}

}
