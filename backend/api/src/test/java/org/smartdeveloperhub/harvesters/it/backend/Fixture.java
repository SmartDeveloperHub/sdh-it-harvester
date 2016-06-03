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

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;

import com.google.common.collect.ImmutableSet;

public final class Fixture {

	public static Version defaultVersion() {
		final Version version = new Version();
		version.setId("id");
		version.setProjectId("projectId");
		return version;
	}

	public static Component defaultComponent() {
		final Component component = new Component();
		component.setId("id");
		component.setProjectId("projectId");
		return component;
	}

	public static Commit defaultCommit() {
		final Commit commit = new Commit();
		commit.setId("id");
		commit.setRepository("repository");
		commit.setBranch("branch");
		commit.setHash("hash");
		return commit;
	}

	public static Collector defaultCollector() {
		final Collector collector = new Collector();
		collector.setVersion("version");
		collector.setNotifications(Fixture.defaultNotifications());
		return collector;
	}

	public static Notifications defaultNotifications() {
		final Notifications notifications = new Notifications();
		notifications.setBrokerHost("brokerHost");
		notifications.setBrokerPort(12345);
		notifications.setVirtualHost("virtualHost");
		notifications.setExchangeName("exchangeName");
		return notifications;
	}

	public static Contributor defaultContributor() {
		final Contributor contributor = new Contributor();
		contributor.setId("id");
		contributor.setEmails(ImmutableSet.of("email1","email2"));
		return contributor;
	}

	public static Issue defaultIssue() {
		final Issue issue = new Issue();
		issue.setAssignee("assignee");
		issue.setBlockedIssues(ImmutableSet.of("bi1","bi2"));
		issue.setChanges(Fixture.defaultChangeLog());
		issue.setChildIssues(ImmutableSet.of("ci1","ci2"));
		issue.setClosed(new DateTime());
		issue.setCreationDate(new DateTime());
		issue.setCommits(ImmutableSet.of("c1","c2"));
		issue.setComponents(ImmutableSet.of("cc1","cc2"));
		issue.setDescription("description");
		issue.setDueTo(new DateTime());
		issue.setEstimatedTime(Minutes.minutes(60).toStandardDuration());
		issue.setId("id");
		issue.setOpened(new DateTime());
		issue.setPriority(Priority.VERY_HIGH);
		issue.setReporter("reporter");
		issue.setSeverity(Severity.BLOCKER);
		issue.setStatus(Status.CLOSED);
		issue.setTags(ImmutableSet.of("t1","t2"));
		issue.setType(Type.BUG);
		issue.setVersions(ImmutableSet.of("v1","v2"));
		return issue;
	}

	public static ChangeLog defaultChangeLog() {
		final ChangeLog one = new ChangeLog();
		final Entry entry = defaultEntry();
		entry.setAuthor("alternativeAuthor");
		one.setEntries(ImmutableSet.of(defaultEntry(),entry));
		return one;
	}

	public static Entry defaultEntry() {
		final Entry entry = new Entry();
		entry.setAuthor("defaultAuthor");
		entry.setItems(ImmutableSet.of(titleChangeItem(),assigneeChangeItem()));
		entry.setTimeStamp(ChangeLogTest.TIME_STAMP);
		return entry;
	}

	public static Item titleChangeItem() {
		return
			Item.
				builder().
					title().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item descriptionChangeItem() {
		return
			Item.
				builder().
					description().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item openedDateChangeItem() {
		return
			Item.
				builder().
					openedDate().
						oldValue(ChangeLogTest.TIME_STAMP).
						newValue(ChangeLogTest.ANOTHER_TIME_STAMP).
						build();
	}

	public static Item assigneeChangeItem() {
		return
			Item.
				builder().
					assignee().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item blockedIssuesChangeItem() {
		return
			Item.
				builder().
					blockedIssues().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item childIssuesChangeItem() {
		return
			Item.
				builder().
					childIssues().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item closedDateChangeItem() {
		return
			Item.
				builder().
					closedDate().
						oldValue(ChangeLogTest.TIME_STAMP).
						newValue(ChangeLogTest.ANOTHER_TIME_STAMP).
						build();
	}

	public static Item estimatedTimeChangeItem() {
		return
			Item.
				builder().
					estimatedTime().
						oldValue(Minutes.minutes(1).toStandardDuration()).
						newValue(Minutes.minutes(3).toStandardDuration()).
						build();
	}

	public static Item typeChangeItem() {
		return
			Item.
				builder().
					type().
						oldValue(Issue.Type.BUG).
						newValue(Issue.Type.IMPROVEMENT).
						build();
	}

	public static Item dueToDateChangeItem() {
		return
			Item.
				builder().
					dueToDate().
						oldValue(ChangeLogTest.TIME_STAMP).
						newValue(ChangeLogTest.ANOTHER_TIME_STAMP).
						build();
	}

	public static Item statusChangeItem() {
		return
			Item.
				builder().
					status().
						oldValue(Status.IN_PROGRESS).
						newValue(Status.CLOSED).
						build();
	}

	public static Item severityChangeItem() {
		return
			Item.
				builder().
					severity().
						oldValue(Severity.LOW).
						newValue(Severity.BLOCKER).
						build();
	}

	public static Item priorityChangeItem() {
		return
			Item.
				builder().
					priority().
						oldValue(Priority.LOW).
						newValue(Priority.VERY_HIGH).
						build();
	}

	public static Item tagsChangeItem() {
		return
			Item.
				builder().
					tags().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item commitsChangeItem() {
		return
			Item.
				builder().
					commits().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item componentsChangeItem() {
		return
			Item.
				builder().
					components().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

	public static Item versionsChangeItem() {
		return
			Item.
				builder().
					versions().
						oldValue("oldValue").
						newValue("newValue").
						build();
	}

}
