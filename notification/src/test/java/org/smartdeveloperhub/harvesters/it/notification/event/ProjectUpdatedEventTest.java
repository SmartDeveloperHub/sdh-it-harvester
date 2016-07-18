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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0
 *   Bundle      : it-harvester-notification-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Action;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Change;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProjectUpdatedEventTest {

	private Map<Entity, Map<String, List<Change>>> changes() {
		final Map<Entity,Map<String,List<Change>>> changes=Maps.newLinkedHashMap();
		final Map<String,List<Change>> entityChanges = Maps.newLinkedHashMap();
		final List<Change> issueChanges = Lists.newArrayList();
		issueChanges.add(defaultChange());
		issueChanges.add(changeWithDifferentAction());
		entityChanges.put("issue",issueChanges);
		changes.put(Entity.ISSUE,entityChanges);
		return changes;
	}

	private Change changeWithDifferentAction() {
		return Change.create(Action.UPDATED,"author");
	}

	private Change changeWithDifferentAuthor() {
		return Change.create(Action.CREATED,"another author");
	}

	private Change totallyDifferentChange() {
		return Change.create(Action.DELETED,"another author");
	}

	private Change defaultChange() {
		return Change.create(Action.CREATED,"author");
	}

	@Test
	public void sizeIsZeroOnCreation() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		assertThat(sut.size(),equalTo(0L));
	}

	@Test
	public void eventIsEmptyOnCreation() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		assertThat(sut.isEmpty(),equalTo(true));
	}

	@Test
	public void sizeIsRefreshedWhenChangesAreUpdated() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		sut.setChanges(changes());
		assertThat(sut.size(),equalTo(2L));
	}

	@Test
	public void isNotEmptyWhenChangesAreUpdated() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		sut.setChanges(changes());
		assertThat(sut.isEmpty(),equalTo(false));
	}

	@Test
	public void sizeIsUpdatedWhenModificationsAreAppended() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		sut.append(Modification.create().issue("issue"));
		assertThat(sut.size(),equalTo(1L));
	}

	@Test
	public void isNotEmptyAfterAppendingModifications() throws Exception {
		final ProjectUpdatedEvent sut = new ProjectUpdatedEvent();
		sut.append(Modification.create().issue("issue"));
		assertThat(sut.isEmpty(),equalTo(false));
	}

	@Test
	public void testActionValues() {
		assertThat(Arrays.asList(Action.values()),contains(Action.CREATED,Action.UPDATED,Action.DELETED));
	}

	@Test
	public void testActionValueOf() {
		for(final Action value:Action.values()) {
			assertThat(Action.valueOf(value.name()),equalTo(value));
		}
	}

	@Test
	public void testEntityValues() {
		assertThat(Arrays.asList(Entity.values()),contains(Entity.COMPONENT,Entity.VERSION,Entity.ISSUE));
	}

	@Test
	public void testEntityValueOf() {
		for(final Entity value:Entity.values()) {
			assertThat(Entity.valueOf(value.name()),equalTo(value));
		}
	}

	@Test
	public void testEquivalentChangesAreEqual() {
		final Change one  =defaultChange();
		final Change other=defaultChange();
		assertThat(one,equalTo(other));
	}

	@Test
	public void testChangesWithDifferentActionAreDifferent() {
		final Change one  =defaultChange();
		final Change other=changeWithDifferentAction();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testChangesWithDifferentAuthorAreDifferent() {
		final Change one  =defaultChange();
		final Change other=changeWithDifferentAuthor();
		assertThat(one,not(equalTo(other)));
	}

	@Test
	public void testTotalDifferentChangesAreDifferent() {
		final Change one  =defaultChange();
		final Change other=totallyDifferentChange();
		assertThat(one,not(equalTo(other)));
	}
	@Test
	public void testEquivalentChangesHaveEqualHashCode() {
		final Change one  =defaultChange();
		final Change other=defaultChange();
		assertThat(one.hashCode(),equalTo(other.hashCode()));
	}

	@Test
	public void testChangesWithDifferentActionHaveDifferentHashCode() {
		final Change one  =defaultChange();
		final Change other=changeWithDifferentAction();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testChangesWithDifferentAuthorHaveDifferentHashCode() {
		final Change one  =defaultChange();
		final Change other=changeWithDifferentAuthor();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void testTotalDifferentChangesHaveDifferentHashCode() {
		final Change one  =defaultChange();
		final Change other=totallyDifferentChange();
		assertThat(one.hashCode(),not(equalTo(other.hashCode())));
	}

	@Test
	public void changesCanOnlyBeEqualToChanges() throws Exception {
		assertThat((Object)new Change(),not(equalTo((Object)"change")));
	}

}
