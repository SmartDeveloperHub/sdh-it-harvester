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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Literals;
import org.ldp4j.application.data.LocalIndividual;
import org.ldp4j.application.data.ManagedIndividual;
import org.ldp4j.application.data.ManagedIndividualId;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.Property;
import org.ldp4j.application.data.Value;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentKey;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionKey;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IssueHandlerTest {

	private abstract static class Collector<V extends Serializable,K> {

		final Set<K> collected=Sets.newHashSet();

		protected abstract String managerId();
		protected abstract Class<V> nameValueClass();
		protected abstract void check(V value);

		protected void collect(final K value) {
			this.collected.add(value);
		}

		void assertCollected(final Set<K> expected) {
			assertThat(this.collected,equalTo(expected));
		}
	}

	private final class IssueCollector extends Collector<IssueKey, String> {
		@Override
		protected String managerId() {
			return IssueHandler.ID;
		}
		@Override
		protected Class<IssueKey> nameValueClass() {
			return IssueKey.class;
		}
		@Override
		protected void check(final IssueKey value) {
			assertThat(value.getProjectId(),equalTo(IssueHandlerTest.this.key.getProjectId()));
			collect(value.getIssueId());
		}
	}

	private final class ComponentCollector extends Collector<ComponentKey, String> {
		@Override
		protected String managerId() {
			return ComponentHandler.ID;
		}
		@Override
		protected Class<ComponentKey> nameValueClass() {
			return ComponentKey.class;
		}
		@Override
		protected void check(final ComponentKey value) {
			assertThat(value.getProjectId(),equalTo(IssueHandlerTest.this.key.getProjectId()));
			collect(value.getComponentId());
		}
	}

	private final class VersionCollector extends Collector<VersionKey, String> {
		@Override
		protected String managerId() {
			return VersionHandler.ID;
		}
		@Override
		protected Class<VersionKey> nameValueClass() {
			return VersionKey.class;
		}
		@Override
		protected void check(final VersionKey value) {
			assertThat(value.getProjectId(),equalTo(IssueHandlerTest.this.key.getProjectId()));
			collect(value.getVersionId());
		}
	}

	private final class ContributorCollector extends Collector<String,String> {
		@Override
		protected String managerId() {
			return ContributorHandler.ID;
		}
		@Override
		protected Class<String> nameValueClass() {
			return String.class;
		}
		@Override
		protected void check(final String value) {
			collect(value);
		}
	}

	private final class CommitCollector extends Collector<String,String> {
		@Override
		protected String managerId() {
			return CommitHandler.ID;
		}
		@Override
		protected Class<String> nameValueClass() {
			return String.class;
		}
		@Override
		protected void check(final String value) {
			collect(value);
		}
	}

	@Injectable private BackendController controller;

	private final IssueKey key=new IssueKey("1","issueId");

	@Mocked private ResourceSnapshot resource;
	@Mocked private Issue entity;

	@Tested
	private IssueHandler sut;

	private Name<IssueKey> issueName() {
		return IdentityUtil.issueName(this.key);
	}

	private ManagedIndividualId issueId() {
		return ManagedIndividualId.createId(issueName(),IssueHandler.ID);
	}

	private <V extends Serializable,K> void checkLinks(final Individual<?,?> individual, final String property, final Set<K> expectations, final Collector<V,K> collector) {
		for(final Value value:individual.property(URI.create(property)).values()) {
			assertThat(value,instanceOf(ManagedIndividual.class));
			final ManagedIndividualId id = ((ManagedIndividual)value).id();
			assertThat(id.managerId(),equalTo(collector.managerId()));
			assertThat(id.name().id(),instanceOf(collector.nameValueClass()));
			collector.check(collector.nameValueClass().cast(id.name().id()));
		}
		collector.assertCollected(expectations);
	}

	@Test
	public void testGetId() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.resource.name();this.result=issueName();
		}};
		assertThat(this.sut.getId(this.resource),sameInstance(this.key));
	}

	@Test
	public void testGetEntity() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.controller.getProjectIssue("1","issueId");this.result=IssueHandlerTest.this.entity;
		}};
		assertThat(this.sut.getEntity(this.controller, this.key),sameInstance(this.entity));
	}

	@Test
	public void testToDataSet$regularData() throws Exception {
		final DateTime expected = new DateTime();
		new Expectations() {{
			IssueHandlerTest.this.entity.getId();this.result=IssueHandlerTest.this.key.getIssueId();
			IssueHandlerTest.this.entity.getName();this.result="title";
			IssueHandlerTest.this.entity.getDescription();this.result="description";
			IssueHandlerTest.this.entity.getEstimatedTime();this.result=null;
			IssueHandlerTest.this.entity.getCreationDate();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		final IndividualHelper newHelper = DataSetUtils.newHelper(individual);
		assertThat(newHelper.types(),hasItem(URI.create(IT.ISSUE_TYPE)));
		assertThat(newHelper.property(IT.ID).firstValue(String.class),equalTo("issueId"));
		assertThat(newHelper.property(IT.ISSUE_ID).firstValue(String.class),equalTo("issueId"));
		assertThat(newHelper.property(DCTERMS.TITLE).firstValue(String.class),equalTo("title"));
		assertThat(newHelper.property(IT.ISSUE_TITLE).firstValue(String.class),equalTo("title"));
		assertThat(newHelper.property(DCTERMS.DESCRIPTION).firstValue(String.class),equalTo("description"));
		assertThat(newHelper.property(IT.DESCRIPTION).firstValue(String.class),equalTo("description"));
		assertThat(newHelper.property(DCTERMS.CREATED).firstValue(DateTime.class),equalTo(expected));
		assertThat(newHelper.property(IT.DATE_CREATION).firstValue(DateTime.class),equalTo(expected));
	}

	@Test
	public void testToDataSet$withTags() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.entity.getTags();this.result=Sets.newHashSet("tag1","tag2");
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(
			individual.
				property(URI.create(IT.ISSUE_CUSTOM_TAG)).
					hasLiteralValue(Literals.of("tag1")),
			equalTo(true));
		assertThat(
			individual.
				property(URI.create(IT.ISSUE_CUSTOM_TAG)).
					hasLiteralValue(Literals.of("tag2")),
			equalTo(true));
	}

	@Test
	public void testToDataSet$withBlockedIssues() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1","id2");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getBlockedIssues();this.result=ids;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.BLOCKS_ISSUE,ids,new IssueCollector());
	}

	@Test
	public void testToDataSet$withChildIssues() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1","id2");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getChildIssues();this.result=ids;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.IS_COMPOSED_OF_ISSUE,ids,new IssueCollector());
		assertThat(DataSetUtils.newHelper(individual).types(),hasItem(URI.create(IT.COMPOSITE_ISSUE_TYPE)));
	}

	@Test
	public void testToDataSet$withComponents() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1","id2");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getComponents();this.result=ids;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.ASSOCIATED_TO_COMPONENT,ids,new ComponentCollector());
	}

	@Test
	public void testToDataSet$withVersions() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1","id2");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getVersions();this.result=ids;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.AFFECTS_VERSION,ids,new VersionCollector());
	}

	@Test
	public void testToDataSet$withCommits() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1","id2");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getCommits();this.result=ids;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.ASSOCIATED_TO_COMMIT,ids,new CommitCollector());
	}

	@Test
	public void testToDataSet$withAssignee() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getAssignee();this.result="id1";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.IS_ASSIGNED_TO,ids,new ContributorCollector());
	}

	@Test
	public void testToDataSet$withReporter() throws Exception {
		final Set<String> ids = Sets.newHashSet("id1");
		new Expectations() {{
			IssueHandlerTest.this.entity.getProjectId();this.result=IssueHandlerTest.this.key.getProjectId();
			IssueHandlerTest.this.entity.getReporter();this.result="id1";
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		checkLinks(individual,IT.IS_REPORTED_BY,ids,new ContributorCollector());
	}

	@Test
	public void testToDataSet$withoutCreationDate() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.entity.getCreationDate();this.result=null;
		}};
		try {
			this.sut.toDataSet(this.entity,this.key);
			fail("Should fail if no creation date is provided");
		} catch (final Exception e) {
			assertThat(e.getMessage(),startsWith("Could not create date for property createdAt"));
		}
	}

	@Test
	public void testToDataSet$withOpenedDate() throws Exception {
		final DateTime expected = new DateTime();
		new Expectations() {{
			IssueHandlerTest.this.entity.getOpened();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
		assertThat(individual.property(URI.create(IT.DATE_OPEN)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
	}

	@Test
	public void testToDataSet$withoutOpenDate() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.entity.getOpened();this.result=null;
		}};
		try {
			this.sut.toDataSet(this.entity,this.key);
			fail("Should fail if no open date is provided");
		} catch (final Exception e) {
			assertThat(e.getMessage(),startsWith("Could not create date for property open"));
		}
	}

	@Test
	public void testToDataSet$withClosedDate() throws Exception {
		final DateTime expected = new DateTime();
		new Expectations() {{
			IssueHandlerTest.this.entity.getClosed();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(this.entity.getOpened())),equalTo(true));
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
		assertThat(individual.property(URI.create(IT.DATE_CLOSED)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
	}

	@Test
	public void testToDataSet$withDueDate() throws Exception {
		final DateTime expected = new DateTime();
		new Expectations() {{
			IssueHandlerTest.this.entity.getDueTo();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(this.entity.getOpened())),equalTo(true));
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
		assertThat(individual.property(URI.create(IT.DUE_TO)).hasLiteralValue(Literals.newLiteral(expected)),equalTo(true));
	}

	@Test
	public void testToDataSet$withoutOptionalDates() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.entity.getClosed();this.result=null;
			IssueHandlerTest.this.entity.getDueTo();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(individual.hasProperty(URI.create(IT.DATE_CLOSED)),equalTo(false));
		assertThat(individual.hasProperty(URI.create(IT.DUE_TO)),equalTo(false));
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(this.entity.getOpened())),equalTo(true));
	}

	@Test
	public void testToDataSet$withBothOptionalDates() throws Exception {
		final DateTime closedDate = new DateTime();
		final DateTime dueTo = new DateTime();
		new Expectations() {{
			IssueHandlerTest.this.entity.getClosed();this.result=closedDate;
			IssueHandlerTest.this.entity.getDueTo();this.result=dueTo;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(this.entity.getOpened())),equalTo(true));
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(closedDate)),equalTo(true));
		assertThat(individual.property(URI.create(DCTERMS.DATE)).hasLiteralValue(Literals.newLiteral(dueTo)),equalTo(true));
		assertThat(individual.property(URI.create(IT.DATE_CLOSED)).hasLiteralValue(Literals.newLiteral(closedDate)),equalTo(true));
		assertThat(individual.property(URI.create(IT.DUE_TO)).hasLiteralValue(Literals.newLiteral(dueTo)),equalTo(true));
	}

	@Test
	public void testToDataSet$withoutChangeLog() throws Exception {
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=null;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		final Property property = individual.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(property.numberOfValues(),equalTo(1));
		final LocalIndividual clInd = getFirstLocal(property);
		assertThat(DataSetUtils.newHelper(clInd).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		assertThat(clInd.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY)),nullValue());
		assertThat(clInd.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM)),nullValue());
	}

	@Test
	public void testToDataSet$withChangeLogWithoutEntries() throws Exception {
		final ChangeLog expected = new ChangeLog();
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=expected;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> individual=dataSet.individualOfId(issueId());
		final Property property = individual.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(property.numberOfValues(),equalTo(1));
		final LocalIndividual clInd = getFirstLocal(property);
		assertThat(DataSetUtils.newHelper(clInd).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		assertThat(clInd.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY)),nullValue());
		assertThat(clInd.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM)),nullValue());
	}

	@Test
	public void testToDataSet$withChangeLogWithEmptyEntry() throws Exception {
		final ChangeLog expectedChangeLog = new ChangeLog();
		final Entry expectedEntry = new Entry();
		expectedEntry.setAuthor("author");
		expectedEntry.setTimeStamp(new DateTime());
		expectedChangeLog.getEntries().add(expectedEntry);
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=expectedChangeLog;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> issue=dataSet.individualOfId(issueId());
		final Property hasChangeLog = issue.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(hasChangeLog.numberOfValues(),equalTo(1));
		final LocalIndividual changeLog = getFirstLocal(hasChangeLog);
		assertThat(DataSetUtils.newHelper(changeLog).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		assertThat(changeLog.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM)),nullValue());
		final Property hasChangeLogEntry = changeLog.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual entry = getFirstLocal(hasChangeLogEntry);
		final IndividualHelper entryHelper = DataSetUtils.newHelper(entry);
		assertThat(entryHelper.types(),hasItem(URI.create(IT.CHANGE_LOG_ENTRY_TYPE)));
		assertThat(entryHelper.property(IT.ENTRY_TIME_STAMP).firstValue(DateTime.class),equalTo(expectedEntry.getTimeStamp()));
		assertThat(entry.property(URI.create(IT.HAS_CHANGE_LOG_ITEM)),nullValue());
		checkLinks(entry,IT.TRIGGERED_BY,Sets.newHashSet(expectedEntry.getAuthor()),new ContributorCollector());
	}

	@Test
	public void testToDataSet$withChangeLogWithEntryWithUpdateItem() throws Exception {
		final ChangeLog expectedChangeLog = new ChangeLog();
		final Entry expectedEntry = new Entry();
		expectedEntry.setAuthor("author");
		expectedEntry.setTimeStamp(new DateTime());
		final Item expectedItem = Item.builder().title().oldValue("old").newValue("new").build();
		expectedEntry.getItems().add(expectedItem);
		expectedChangeLog.getEntries().add(expectedEntry);
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=expectedChangeLog;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> issue=dataSet.individualOfId(issueId());
		final Property hasChangeLog = issue.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(hasChangeLog.numberOfValues(),equalTo(1));
		final LocalIndividual changeLog = getFirstLocal(hasChangeLog);
		assertThat(DataSetUtils.newHelper(changeLog).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		final Property isComposedByChangeLogItem = changeLog.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM));
		assertThat(isComposedByChangeLogItem.numberOfValues(),equalTo(1));
		final Property hasChangeLogEntry = changeLog.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual entry = getFirstLocal(hasChangeLogEntry);
		final Property hasChangeLogItem = entry.property(URI.create(IT.HAS_CHANGE_LOG_ITEM));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual item = getFirstLocal(hasChangeLogItem);
		assertThat(item,equalTo(Iterables.getFirst(isComposedByChangeLogItem.values(),null)));
		final IndividualHelper itemHelper = DataSetUtils.newHelper(item);
		assertThat(itemHelper.types(),hasItems(URI.create(IT.CHANGE_LOG_ITEM_TYPE),URI.create(IT.UPDATE_LOG_ITEM_TYPE)));
		assertThat(item.property(URI.create(IT.ON_PROPERTY)).hasIdentifiedIndividual(IT.propertyOf(expectedItem)),equalTo(true));
		assertThat(itemHelper.property(IT.NEW_VALUE).firstValue(String.class),equalTo("new"));
		assertThat(itemHelper.property(IT.PREVIOUS_VALUE).firstValue(String.class),equalTo("old"));
		assertThat(item.property(URI.create(IT.ADDED_VALUE)),nullValue());
		assertThat(item.property(URI.create(IT.DELETED_VALUE)),nullValue());
	}

	@Test
	public void testToDataSet$withChangeLogWithEntryWithAddedItem() throws Exception {
		final ChangeLog expectedChangeLog = new ChangeLog();
		final Entry expectedEntry = new Entry();
		expectedEntry.setAuthor("author");
		expectedEntry.setTimeStamp(new DateTime());
		final Item expectedItem = Item.builder().description().newValue("new").build();
		expectedEntry.getItems().add(expectedItem);
		expectedChangeLog.getEntries().add(expectedEntry);
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=expectedChangeLog;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> issue=dataSet.individualOfId(issueId());
		final Property hasChangeLog = issue.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(hasChangeLog.numberOfValues(),equalTo(1));
		final LocalIndividual changeLog = getFirstLocal(hasChangeLog);
		assertThat(DataSetUtils.newHelper(changeLog).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		final Property isComposedByChangeLogItem = changeLog.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM));
		assertThat(isComposedByChangeLogItem.numberOfValues(),equalTo(1));
		final Property hasChangeLogEntry = changeLog.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual entry = getFirstLocal(hasChangeLogEntry);
		final Property hasChangeLogItem = entry.property(URI.create(IT.HAS_CHANGE_LOG_ITEM));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual item = getFirstLocal(hasChangeLogItem);
		assertThat(item,equalTo(Iterables.getFirst(isComposedByChangeLogItem.values(),null)));
		final IndividualHelper itemHelper = DataSetUtils.newHelper(item);
		assertThat(itemHelper.types(),hasItems(URI.create(IT.CHANGE_LOG_ITEM_TYPE),URI.create(IT.ADD_LOG_ITEM_TYPE)));
		assertThat(item.property(URI.create(IT.ON_PROPERTY)).hasIdentifiedIndividual(IT.propertyOf(expectedItem)),equalTo(true));
		assertThat(itemHelper.property(IT.ADDED_VALUE).firstValue(String.class),equalTo("new"));
		assertThat(item.property(URI.create(IT.PREVIOUS_VALUE)),nullValue());
		assertThat(item.property(URI.create(IT.NEW_VALUE)),nullValue());
		assertThat(item.property(URI.create(IT.DELETED_VALUE)),nullValue());
	}

	@Test
	public void testToDataSet$withChangeLogWithEntryWithDeletedItem() throws Exception {
		final ChangeLog expectedChangeLog = new ChangeLog();
		final Entry expectedEntry = new Entry();
		expectedEntry.setAuthor("author");
		expectedEntry.setTimeStamp(new DateTime());
		final Item expectedItem = Item.builder().tags().oldValue("old").build();
		expectedEntry.getItems().add(expectedItem);
		expectedChangeLog.getEntries().add(expectedEntry);
		new Expectations() {{
			IssueHandlerTest.this.entity.getChanges();this.result=expectedChangeLog;
		}};
		final DataSet dataSet = this.sut.toDataSet(this.entity,this.key);
		assertThat(dataSet,notNullValue());
		final Individual<?,?> issue=dataSet.individualOfId(issueId());
		final Property hasChangeLog = issue.property(URI.create(IT.HAS_CHANGE_LOG));
		assertThat(hasChangeLog.numberOfValues(),equalTo(1));
		final LocalIndividual changeLog = getFirstLocal(hasChangeLog);
		assertThat(DataSetUtils.newHelper(changeLog).types(),hasItem(URI.create(IT.CHANGE_LOG_TYPE)));
		final Property isComposedByChangeLogItem = changeLog.property(URI.create(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM));
		assertThat(isComposedByChangeLogItem.numberOfValues(),equalTo(1));
		final Property hasChangeLogEntry = changeLog.property(URI.create(IT.HAS_CHANGE_LOG_ENTRY));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual entry = getFirstLocal(hasChangeLogEntry);
		final Property hasChangeLogItem = entry.property(URI.create(IT.HAS_CHANGE_LOG_ITEM));
		assertThat(hasChangeLogEntry.numberOfValues(),equalTo(1));
		final LocalIndividual item = getFirstLocal(hasChangeLogItem);
		assertThat(item,equalTo(Iterables.getFirst(isComposedByChangeLogItem.values(),null)));
		final IndividualHelper itemHelper = DataSetUtils.newHelper(item);
		assertThat(itemHelper.types(),hasItems(URI.create(IT.CHANGE_LOG_ITEM_TYPE),URI.create(IT.DELETE_LOG_ITEM_TYPE)));
		assertThat(item.property(URI.create(IT.ON_PROPERTY)).hasIdentifiedIndividual(IT.propertyOf(expectedItem)),equalTo(true));
		assertThat(itemHelper.property(IT.DELETED_VALUE).firstValue(String.class),equalTo("old"));
		assertThat(item.property(URI.create(IT.PREVIOUS_VALUE)),nullValue());
		assertThat(item.property(URI.create(IT.NEW_VALUE)),nullValue());
		assertThat(item.property(URI.create(IT.ADDED_VALUE)),nullValue());
	}

	private LocalIndividual getFirstLocal(final Property hasChangeLog) {
		final Value first = Iterables.getFirst(hasChangeLog.values(),null);
		assertThat(first,instanceOf(LocalIndividual.class));
		final LocalIndividual clInd=(LocalIndividual)first;
		return clInd;
	}

}
