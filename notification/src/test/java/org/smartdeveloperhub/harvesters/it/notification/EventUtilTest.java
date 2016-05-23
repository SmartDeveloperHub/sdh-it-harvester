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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ContributorDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.Event;
import org.smartdeveloperhub.harvesters.it.notification.event.Modification;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectCreatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectDeletedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;


public class EventUtilTest {

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(EventUtil.class),equalTo(true));
	}

	@Test
	public void testReadContributorCreatedEvent() throws IOException {
		final ContributorCreatedEvent originalEvent = new ContributorCreatedEvent();
		originalEvent.setNewContributors(Arrays.asList("23","24"));
		fillInBasicEvent(originalEvent);

		final ContributorCreatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					ContributorCreatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getNewContributors(),equalTo(originalEvent.getNewContributors()));
	}

	@Test
	public void testReadContributorDeletedEvent() throws IOException {
		final ContributorDeletedEvent originalEvent = new ContributorDeletedEvent();
		originalEvent.setDeletedContributors(Arrays.asList("3","4"));
		fillInBasicEvent(originalEvent);

		final ContributorDeletedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					ContributorDeletedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getDeletedContributors(),equalTo(originalEvent.getDeletedContributors()));
	}

	@Test
	public void testReadProjectCreatedEvent() throws IOException {
		final ProjectCreatedEvent originalEvent = new ProjectCreatedEvent();
		originalEvent.setNewProjects(Arrays.asList("1","2","3"));
		fillInBasicEvent(originalEvent);

		final ProjectCreatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					ProjectCreatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getNewProjects(),equalTo(originalEvent.getNewProjects()));
	}

	@Test
	public void testReadProjectUpdatedEvent() throws IOException {
		final ProjectUpdatedEvent originalEvent = new ProjectUpdatedEvent();
		originalEvent.setProject("19");
		originalEvent.
			append(Modification.create().component("c1").authors("ac1")).
			append(Modification.delete().component("c2").authors("ac2")).
			append(Modification.update().component("c3").authors("ac3")).
			append(Modification.create().version("v1").authors("av1")).
			append(Modification.delete().version("v2").authors("av2")).
			append(Modification.update().version("v3").authors("av3")).
			append(Modification.create().issue("i1").authors("ai1")).
			append(Modification.delete().issue("i2").authors("ai2")).
			append(Modification.update().issue("i3").authors("ai3"));
		fillInBasicEvent(originalEvent);

		final ProjectUpdatedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					ProjectUpdatedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getProject(),equalTo(originalEvent.getProject()));
		assertThat(readEvent.getChanges(),equalTo(originalEvent.getChanges()));
	}

	@Test
	public void testReadProjectDeletedEvent() throws IOException {
		final ProjectDeletedEvent originalEvent = new ProjectDeletedEvent();
		originalEvent.setDeletedProjects(Arrays.asList("1","2","3"));
		fillInBasicEvent(originalEvent);

		final ProjectDeletedEvent readEvent=
			EventUtil.
				unmarshall(
					EventUtil.marshall(originalEvent),
					ProjectDeletedEvent.class);

		verifyBasicEvent(originalEvent, readEvent);
		assertThat(readEvent.getDeletedProjects(),equalTo(originalEvent.getDeletedProjects()));
	}

	private void fillInBasicEvent(final Event event) {
		event.setInstance("http://russell.dia.fi.upm.es:5000/api");
		event.setTimestamp(System.currentTimeMillis());
	}

	private void verifyBasicEvent(final Event expectedEvent, final Event actualEvent) {
		assertThat(actualEvent.getInstance(),equalTo(expectedEvent.getInstance()));
		assertThat(actualEvent.getTimestamp(),equalTo(expectedEvent.getTimestamp()));
	}

}