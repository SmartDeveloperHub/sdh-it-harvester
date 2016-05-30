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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-api-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;

import com.google.common.collect.ImmutableSet;

public class ChangeLogTest {

	private static final DateTime TIME_STAMP = new DateTime();

	private static Item defaultItem() {
		final Item item = new Item();
		item.setProperty("defaultProperty");
		item.setOldValue("defaultOldValue");
		item.setNewValue("defaultNewValue");
		return item;
	}

	private static Item alternativePropertyItem() {
		final Item item = defaultItem();
		item.setProperty("alternativeProperty");
		return item;
	}

	private Item alternativeOldValueItem() {
		final Item item = defaultItem();
		item.setOldValue("alternativeOldValue");
		return item;
	}

	private Item alternativeNewValueItem() {
		final Item item = defaultItem();
		item.setNewValue("alternativeNewValue");
		return item;
	}

	private Item alternativePropertyAndOldValueItem() {
		final Item item=alternativePropertyItem();
		item.setOldValue("alternativeOldValue");
		return item;
	}

	private Item alternativePropertyAndNewValueItem() {
		final Item item=alternativePropertyItem();
		item.setNewValue("alternativeNewValue");
		return item;
	}

	private Item alternativeValuesItem() {
		final Item item=alternativeOldValueItem();
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
	public void itemsWithDifferentPropertyAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativePropertyItem())));
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
	public void itemsWithDifferentPropertyAndOldValueAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativePropertyAndOldValueItem())));
	}

	@Test
	public void itemsWithDifferentPropertyAndNewValueAreNotEqual() throws Exception {
		assertThat(defaultItem(),not(equalTo(alternativePropertyAndNewValueItem())));
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
	public void itemsWithDifferentPropertyHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativePropertyItem().hashCode())));
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
	public void itemsWithDifferentPropertyAndOldValueHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativePropertyAndOldValueItem().hashCode())));
	}

	@Test
	public void itemsWithDifferentPropertyAndNewValueHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativePropertyAndNewValueItem().hashCode())));
	}

	@Test
	public void itemsWithDifferentValuesHaveDifferentHashCode() throws Exception {
		assertThat(defaultItem().hashCode(),not(equalTo(alternativeValuesItem().hashCode())));
	}

	private static Entry defaultEntry() {
		final Entry entry = new Entry();
		entry.setAuthor("defaultAuthor");
		entry.setItems(ImmutableSet.of(defaultItem(),alternativePropertyItem()));
		entry.setTimeStamp(TIME_STAMP);
		return entry;
	}

	private static Entry alternativeAuthorEntry() {
		final Entry entry = defaultEntry();
		entry.setAuthor("alternativeAuthor");
		return entry;
	}

	private Entry alternativeTimeStampEntry() {
		final Entry entry = defaultEntry();
		entry.setTimeStamp(new DateTime());
		return entry;
	}

	private Entry alternativeItemsEntry() {
		final Entry entry = defaultEntry();
		entry.setItems(ImmutableSet.of(alternativeValuesItem()));
		return entry;
	}

	private Entry alternativeAuthorAndTimeStampEntry() {
		final Entry entry = alternativeAuthorEntry();
		entry.setTimeStamp(new DateTime());
		return entry;
	}

	private Entry alternativeAuthorAndItemsEntry() {
		final Entry entry = alternativeAuthorEntry();
		entry.setItems(ImmutableSet.of(alternativeValuesItem()));
		return entry;
	}

	private Entry alternativeTimeStampAndItemsEntry() {
		final Entry entry = alternativeTimeStampEntry();
		entry.setItems(ImmutableSet.of(alternativeValuesItem()));
		return entry;
	}

	@Test
	public void entriesHaveCustomToString() {
		final Entry sut = defaultEntry();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void entriesCanOnlyBeEqualToOtherEntries() throws Exception {
		assertThat((Object)defaultEntry(),not(equalTo((Object)"value")));
	}

	@Test
	public void entriesWithSameAttributeValuesAreEqual() throws Exception {
		assertThat(defaultEntry(),equalTo(defaultEntry()));
	}

	@Test
	public void entriesWithDifferentAuthorAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeAuthorEntry())));
	}

	@Test
	public void entriesWithDifferentTimeStampAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeTimeStampEntry())));
	}

	@Test
	public void entriesWithDifferentItemsAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeItemsEntry())));
	}

	@Test
	public void entriesWithDifferentAuthorAndTimeStampAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeAuthorAndTimeStampEntry())));
	}

	@Test
	public void entriesWithDifferentAuthorAndItemsAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeAuthorAndItemsEntry())));
	}

	@Test
	public void entriesWithDifferentTimeStampAndItemsAreNotEqual() throws Exception {
		assertThat(defaultEntry(),not(equalTo(alternativeTimeStampAndItemsEntry())));
	}

	@Test
	public void entriesWithSameAttributeValuesHaveSameHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),equalTo(defaultEntry().hashCode()));
	}

	@Test
	public void entriesWithDifferentAuthorHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeAuthorEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentTimeStampHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeTimeStampEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentItemsHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeItemsEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentAuthorAndTimeStampHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeAuthorAndTimeStampEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentAuthorAndItemsHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeAuthorAndItemsEntry().hashCode())));
	}

	@Test
	public void entriesWithDifferentTimeStampAndItemsHaveDifferentHashCode() throws Exception {
		assertThat(defaultEntry().hashCode(),not(equalTo(alternativeTimeStampAndItemsEntry().hashCode())));
	}

	@Test
	public void canMarshallAndUnmarshallChangeLogs() throws IOException {
		final ChangeLog one = defaultChangeLog();
		final String str = Entities.marshallEntity(one);
		final ChangeLog other = Entities.unmarshallEntity(str,ChangeLog.class);
		assertThat(other.getEntries(),equalTo(one.getEntries()));
	}

	@Test
	public void changeLogsHaveCustomToString() {
		final ChangeLog sut = defaultChangeLog();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	static ChangeLog defaultChangeLog() {
		final ChangeLog one = new ChangeLog();
		one.setEntries(ImmutableSet.of(defaultEntry(),alternativeAuthorEntry()));
		return one;
	}

}
