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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.1.0
 *   Bundle      : it-backend-api-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AssigneeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.BlockedIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ChildIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ClosedChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.CommitsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ComponentsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DescriptionChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DueToDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.EstimatedTimeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ItemVisitor;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.OpenedChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.PriorityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.SeverityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TagsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TitleChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TypeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.VersionsChangeItem;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ChangeLogTest {

	static final DateTime TIME_STAMP = new DateTime();
	static final DateTime ANOTHER_TIME_STAMP = new DateTime().plusDays(1);

	private TitleChangeItem defaultItem() {
		final TitleChangeItem item = new TitleChangeItem();
		item.setOldValue("defaultOldValue");
		item.setNewValue("defaultNewValue");
		return item;
	}

	private TitleChangeItem alternativeOldValueItem() {
		final TitleChangeItem item = defaultItem();
		item.setOldValue("alternativeOldValue");
		return item;
	}

	private TitleChangeItem alternativeNewValueItem() {
		final TitleChangeItem item = defaultItem();
		item.setNewValue("alternativeNewValue");
		return item;
	}

	private TitleChangeItem alternativeValuesItem() {
		final TitleChangeItem item=alternativeOldValueItem();
		item.setNewValue("alternativeNewValue");
		return item;
	}

	@Test
	public void itemsHaveCustomToString() {
		final Item sut = defaultItem();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void itemsCanOnlyBeEqualToOtherItems() throws Exception {
		assertThat((Object)defaultItem(),not(equalTo((Object)"value")));
	}

	@Test
	public void itemsWithSameAttributeValuesAreEqual() throws Exception {
		assertThat(defaultItem(),equalTo(defaultItem()));
	}

	@Test
	public void itemsWithDifferentOldValueAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativeOldValueItem())));
	}

	@Test
	public void itemsWithDifferentNewValueAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativeNewValueItem())));
	}

	@Test
	public void itemsWithDifferentValuesAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativeValuesItem())));
	}

	@Test
	public void itemsWithSameAttributeValuesHaveSameHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),equalTo(defaultItem().hashCode()));
	}

	@Test
	public void itemsWithDifferentOldValueHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativeOldValueItem().hashCode())));
	}

	@Test
	public void itemsWithDifferentNewValueHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativeNewValueItem().hashCode())));
	}

	@Test
	public void itemsWithDifferentValuesHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativeValuesItem().hashCode())));
	}

	private Entry alternativeAuthorEntry() {
		final Entry entry = Fixture.defaultEntry();
		entry.setAuthor("alternativeAuthor");
		return entry;
	}

	private Entry alternativeTimeStampEntry() {
		final Entry entry = Fixture.defaultEntry();
		entry.setTimeStamp(new DateTime());
		return entry;
	}

	private Entry alternativeItemsEntry() {
		final Entry entry = Fixture.defaultEntry();
		entry.setItems(ImmutableSet.<Item>of(alternativeValuesItem()));
		return entry;
	}

	private Entry alternativeAuthorAndTimeStampEntry() {
		final Entry entry = alternativeAuthorEntry();
		entry.setTimeStamp(new DateTime());
		return entry;
	}

	private Entry alternativeAuthorAndItemsEntry() {
		final Entry entry = alternativeAuthorEntry();
		entry.setItems(ImmutableSet.<Item>of(alternativeValuesItem()));
		return entry;
	}

	private Entry alternativeTimeStampAndItemsEntry() {
		final Entry entry = alternativeTimeStampEntry();
		entry.setItems(ImmutableSet.<Item>of(alternativeValuesItem()));
		return entry;
	}

	@Test
	public void entriesHaveCustomToString() {
		final Entry sut = Fixture.defaultEntry();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void entriesCanOnlyBeEqualToOtherEntries() throws Exception {
		assertThat((Object)Fixture.defaultEntry(),not(equalTo((Object)"value")));
	}

	@Test
	public void entriesWithSameAttributeValuesAreEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),equalTo(Fixture.defaultEntry()));
	}

	@Test
	public void entriesWithDifferentAuthorAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeAuthorEntry())));
	}

	@Test
	public void entriesWithDifferentTimeStampAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeTimeStampEntry())));
	}

	@Test
	public void entriesWithDifferentItemsAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeItemsEntry())));
	}

	@Test
	public void entriesWithDifferentAuthorAndTimeStampAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeAuthorAndTimeStampEntry())));
	}

	@Test
	public void entriesWithDifferentAuthorAndItemsAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeAuthorAndItemsEntry())));
	}

	@Test
	public void entriesWithDifferentTimeStampAndItemsAreNotEqual() throws Exception {
		assertThat(Fixture.defaultEntry(),not(equalTo(alternativeTimeStampAndItemsEntry())));
	}

	@Test
	public void entriesWithSameAttributeValuesHaveSameHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),equalTo(Fixture.defaultEntry().hashCode()));
	}

	@Test
	public void entriesWithDifferentAuthorHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeAuthorEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentTimeStampHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeTimeStampEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentItemsHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeItemsEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentAuthorAndTimeStampHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeAuthorAndTimeStampEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentAuthorAndItemsHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeAuthorAndItemsEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentTimeStampAndItemsHaveDifferentHashCode() throws Exception {
		assertThat(Fixture.defaultEntry().hashCode(),not(equalTo(alternativeTimeStampAndItemsEntry().hashCode())));
	}

	@Test
	public void canMarshallAndUnmarshallChangeLogs() throws IOException {
		final ChangeLog one = Fixture.defaultChangeLog();
		final String str = Entities.marshallEntity(one);
		final ChangeLog other = Entities.unmarshallEntity(str,ChangeLog.class);
		assertThat(other.getEntries(),equalTo(one.getEntries()));
	}

	@Test
	public void changeLogsHaveCustomToString() {
		final ChangeLog sut = Fixture.defaultChangeLog();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void supportsPolymorphism() throws IOException {
		class Verifier extends ItemVisitor {

			private final Set<String> added=Sets.newLinkedHashSet();

			@Override
			public void visitTitleChange(final TitleChangeItem item) {
				assertThat(item,equalTo(Fixture.titleChangeItem()));
				count(item);
			}

			private void count(final Item item) {
				assertThat(this.added.add(item.getClass().getName()),equalTo(true));
			}

			@Override
			public void visitDescriptionChange(final DescriptionChangeItem item) {
				assertThat(item,equalTo(Fixture.descriptionChangeItem()));
				count(item);
			}

			@Override
			public void visitOpenedDateChange(final OpenedChangeItem item) {
				assertThat(item,equalTo(Fixture.openedDateChangeItem()));
				count(item);
			}

			@Override
			public void visitClosedDateChange(final ClosedChangeItem item) {
				assertThat(item,equalTo(Fixture.closedDateChangeItem()));
				count(item);
			}

			@Override
			public void visitDueToDateChange(final DueToDateChangeItem item) {
				assertThat(item,equalTo(Fixture.dueToDateChangeItem()));
				count(item);
			}

			@Override
			public void visitEstimatedTimeChange(final EstimatedTimeChangeItem item) {
				assertThat(item,equalTo(Fixture.estimatedTimeChangeItem()));
				count(item);
			}

			@Override
			public void visitTagsChange(final TagsChangeItem item) {
				assertThat(item,equalTo(Fixture.tagsChangeItem()));
				count(item);
			}

			@Override
			public void visitComponentsChange(final ComponentsChangeItem item) {
				assertThat(item,equalTo(Fixture.componentsChangeItem()));
				count(item);
			}

			@Override
			public void visitVersionsChange(final VersionsChangeItem item) {
				assertThat(item,equalTo(Fixture.versionsChangeItem()));
				count(item);
			}

			@Override
			public void visitCommitsChange(final CommitsChangeItem item) {
				assertThat(item,equalTo(Fixture.commitsChangeItem()));
				count(item);
			}

			@Override
			public void visitAssigneeChange(final AssigneeChangeItem item) {
				assertThat(item,equalTo(Fixture.assigneeChangeItem()));
				count(item);
			}

			@Override
			public void visitStatusChange(final StatusChangeItem item) {
				assertThat(item,equalTo(Fixture.statusChangeItem()));
				count(item);
			}

			@Override
			public void visitPriorityChange(final PriorityChangeItem item) {
				assertThat(item,equalTo(Fixture.priorityChangeItem()));
				count(item);
			}

			@Override
			public void visitSeverityChange(final SeverityChangeItem item) {
				assertThat(item,equalTo(Fixture.severityChangeItem()));
				count(item);
			}

			@Override
			public void visitChildIssuesChange(final ChildIssuesChangeItem item) {
				assertThat(item,equalTo(Fixture.childIssuesChangeItem()));
				count(item);
			}

			@Override
			public void visitBlockedIssuesChange(final BlockedIssuesChangeItem item) {
				assertThat(item,equalTo(Fixture.blockedIssuesChangeItem()));
				count(item);
			}

			@Override
			public void visitTypeChange(final TypeChangeItem item) {
				assertThat(item,equalTo(Fixture.typeChangeItem()));
				count(item);
			}

		};

		final Entry entry=new Entry();
		entry.setAuthor("author");
		entry.setTimeStamp(new DateTime());
		entry.setItems(
			ImmutableSet.
				<Item>of(
					Fixture.titleChangeItem(),
					Fixture.descriptionChangeItem(),
					Fixture.closedDateChangeItem(),
					Fixture.openedDateChangeItem(),
					Fixture.dueToDateChangeItem(),
					Fixture.estimatedTimeChangeItem(),
					Fixture.typeChangeItem(),
					Fixture.statusChangeItem(),
					Fixture.severityChangeItem(),
					Fixture.priorityChangeItem(),
					Fixture.childIssuesChangeItem(),
					Fixture.blockedIssuesChangeItem(),
					Fixture.componentsChangeItem(),
					Fixture.versionsChangeItem(),
					Fixture.commitsChangeItem(),
					Fixture.tagsChangeItem(),
					Fixture.assigneeChangeItem()
				)
		);
		final ChangeLog changeLog=new ChangeLog();
		changeLog.setEntries(ImmutableSet.of(entry));
		final String str = Entities.marshallEntity(changeLog);
		final ChangeLog parsed = Entities.unmarshallEntity(str, ChangeLog.class);
		for(final Entry pEntry:parsed.getEntries()) {
			final Verifier verifier = new Verifier();
			for(final Item pItem:pEntry.getItems()) {
				pItem.accept(verifier);
				pItem.accept(new ItemVisitor() {});
			}
			assertThat(verifier.added,hasSize(entry.getItems().size()));
		}
	}

	@Test
	public void cannotBuildItemsWithoutValues() {
		try {
			Item.builder().title().build();
			fail("Should not be able to build an item without values");
		} catch(final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("No item values defined"));
		}
	}

	@Test
	public void cannotBuildItemsWithSameOldAndNewValue() {
		try {
			Item.builder().title().oldValue("value").newValue("value").build();
			fail("Should not be able to build items with same old and new value");
		} catch(final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Old and new values of an item must be different"));
		}
	}

	@Test
	public void canBuildItemsWithDifferentValues() {
		final Item build = Item.builder().title().oldValue("value1").newValue("value2").build();
		assertThat(build.getOldValue(),equalTo((Object)"value1"));
		assertThat(build.getNewValue(),equalTo((Object)"value2"));
	}

	@Test
	public void canBuildItemsWithOnlyOldValue() {
		final Item build = Item.builder().title().oldValue("value").build();
		assertThat(build.getOldValue(),equalTo((Object)"value"));
		assertThat(build.getNewValue(),nullValue());
	}

	@Test
	public void canBuildItemsWithOnlyNewValue() {
		final Item build = Item.builder().title().newValue("value").build();
		assertThat(build.getNewValue(),equalTo((Object)"value"));
		assertThat(build.getOldValue(),nullValue());
	}

}