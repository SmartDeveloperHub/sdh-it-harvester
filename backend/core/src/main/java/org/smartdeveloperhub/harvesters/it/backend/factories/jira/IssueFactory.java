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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-backend:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-backend-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.factories.jira;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Version;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.ChangeLogProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class factory for building {@link Issue}s.
 * @author imolina
 *
 */
public class IssueFactory {

	private Map<String, Status> statusMapping;
	private Map<String, Priority> priorityMapping;
	private Map<String, Severity> severityMapping;
	private Map<String, Type> typeMapping;

	public IssueFactory(Map<String, Status> statusMapping,
						Map<String, Priority> priorityMapping,
						Map<String, Severity> severityMapping,
						Map<String, Type> typeMapping) {

		// TODO: Version check >= 6.1
		this.statusMapping = Objects.requireNonNull(
										statusMapping,
										"Status Mapping cannot be null.");
		this.priorityMapping = Objects.requireNonNull(
										priorityMapping,
										"Priority Mapping cannot be null.");
		this.severityMapping = Objects.requireNonNull(
										severityMapping,
										"Severity Mapping cannot be null.");
		this.typeMapping = Objects.requireNonNull(
										typeMapping,
										"Type Mapping cannot be null.");
	}

	/**
	 * This method creates an {@link Issue} from Jira issues.
	 * @param jiraIssue for retrieve issue information.
	 * @return {@link Issue}
	 */
	public Issue createIssue(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		Issue issue = new Issue();

		issue.setId(String.valueOf(jiraIssue.getKey()));
		issue.setProjectId(jiraIssue.getProject().getKey());
		issue.setCreationDate(jiraIssue.getCreationDate());
		issue.setDescription(jiraIssue.getDescription());
		issue.setReporter(jiraIssue.getReporter().getEmailAddress());

		issue.setTitle(jiraIssue.getSummary());
		issue.setAssignee(getAssignee(jiraIssue));
		issue.setChanges(createChangeLog(jiraIssue));
		issue.setOpened(getOpenedDate(jiraIssue, issue.getChanges()));
		issue.setClosed(getClosedDate(jiraIssue, issue.getChanges()));
		issue.setDueTo(jiraIssue.getDueDate());
		issue.setStatus(createStatus(jiraIssue));
		issue.setPriority(fromMap(jiraIssue.getPriority().getName(), priorityMapping));
		issue.setSeverity(fromMap(jiraIssue.getPriority().getName(), severityMapping));
		issue.setType(fromMap(jiraIssue.getIssueType().getName(), typeMapping));
		issue.setVersions(getVersions(jiraIssue));
		issue.setComponents(getComponents(jiraIssue));

		// TODO: explore it
//		issue.setChildIssues(childIssues);
//		issue.setBlockedIssues(blockedIssues);
		
		// TODO: not available.
//		issue.setCommits(commits);
//		issue.setTags(tags);

//		System.out.println("Estimate: " + jiraIssue.getTimeTracking().getOriginalEstimateMinutes());
//		System.out.println("Remaining: " + jiraIssue.getTimeTracking().getRemainingEstimateMinutes());
//		System.out.println("TimeSpent: " + jiraIssue.getTimeTracking().getTimeSpentMinutes());

		return issue;
	}

	private String getAssignee(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		return jiraIssue.getAssignee() != null ?
				jiraIssue.getAssignee().getEmailAddress() : null;
	}

	private Set<String> getComponents(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		Set<String> components = new HashSet<>();

		for (BasicComponent component : jiraIssue.getComponents()) {

			components.add(String.valueOf(component.getId()));
		}

		return components;
	}

	private Set<String> getVersions(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		Set<String> versions = new HashSet<>();

		for (Version version : jiraIssue.getAffectedVersions()) {

			versions.add(String.valueOf(version.getId()));
		}

		return versions;
	}

	private Status createStatus(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

//		// TODO: Jira >= 6.1 instance needed
//		Client httpClient = ClientBuilder.newClient();
//		long statusId = jiraIssue.getStatus().getId();
//
//		String token = this.username + ":" + this.password;
//
//		Response response = httpClient.target(server + "/rest/api/2/status/" + statusId)
//										.request()
////										.header("Authorization",
////												"Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8")))
//										.accept(MediaType.APPLICATION_JSON)
//										.get();
//
//		if (response.getStatus() != 200) {
//
//			throw new IllegalArgumentException();
//		}
//
//		System.out.println(response.readEntity(String.class));
//
//		return null;
//		
		return fromMap(jiraIssue.getStatus().getName(), statusMapping);
	}

	private <T> T fromMap(String key, Map<String, T> map) {
		
		T value = map.get(key);

		if (value == null) {

			throw new IllegalArgumentException("Value not mapped: " + key);
		}

		return value;
	}

	private ChangeLog createChangeLog(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {
		
		ChangeLog changeLog = new ChangeLog();
		Set<Entry> entries = new HashSet<>();
		Set<String> failed = new HashSet<>();
		for (com.atlassian.jira.rest.client.api.domain.ChangelogGroup group :
													jiraIssue.getChangelog()) {

			Entry entry = new Entry();
			entry.setTimeStamp(group.getCreated());
			entry.setAuthor(group.getAuthor().getDisplayName());

			Set<Item> items = new HashSet<>();
			for (com.atlassian.jira.rest.client.api.domain.ChangelogItem jiraItem :
															group.getItems()) {

				// Register only changes on Jira Attributes
				if (jiraItem.getFieldType() == com.atlassian.jira.rest.client.api.domain.FieldType.JIRA) {

					Item item = null;
					try {

						item = buildChangeLogItem(jiraItem);
						if (item != null) {

							items.add(item);

						} else {

							failed.add(jiraItem.getField());
						}

					} catch (IllegalStateException e) {
//						logger.error("¡Exception! IllegalState.\n" + 
//											"Property: " + jiraItem.getField() + "\n" +
//											"\t- oldValue: " + jiraItem.getFromString() + "\n" +
//											"\t- newValue: " + jiraItem.getToString() + ".\n {}", e);
					}
				}
			}

			entry.setItems(items);
			entries.add(entry);
		}

		changeLog.setEntries(entries);

		return changeLog;
	}

	private Item buildChangeLogItem(ChangelogItem jiraItem) {

		Item item = null;
		String field = jiraItem.getField();
		if (ChangeLogProperty.STATUS.is(field)) {

			item = Item.builder()
							.status()
								.oldValue(fromMap(jiraItem.getFromString(),
													statusMapping))
								.newValue(fromMap(jiraItem.getToString(),
													statusMapping))
								.build();

		} else if (ChangeLogProperty.PRIORITY.is(field)) {

			item = Item.builder()
							.priority()
								.oldValue(fromMap(jiraItem.getFromString(),
													priorityMapping))
								.newValue(fromMap(jiraItem.getToString(),
													priorityMapping))
								.build();

		} else if (ChangeLogProperty.SEVERITY.is(field)) {

			item = Item.builder()
							.severity()
								.oldValue(fromMap(jiraItem.getFromString(),
													severityMapping))
								.newValue(fromMap(jiraItem.getToString(),
													severityMapping))
								.build();

		} else if (ChangeLogProperty.ISSUE_TYPE.is(field)) {

			item = Item.builder()
							.type()
								.oldValue(fromMap(jiraItem.getFromString(),
													typeMapping))
								.newValue(fromMap(jiraItem.getToString(),
													typeMapping))
								.build();

		} else if (ChangeLogProperty.DUE_DATE.is(field)) {

			DateTimeFormatter formatter =
							DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SS");

			DateTime fromDate = (jiraItem.getFromString() != null ?
									formatter.parseDateTime(jiraItem.getFromString()) :
									null);
			DateTime toDate = (jiraItem.getFromString() != null ?
									formatter.parseDateTime(jiraItem.getToString()) :
									null);

			item = Item.builder()
							.dueToDate()
								.oldValue(fromDate)
								.newValue(toDate)
								.build();

		} else if (ChangeLogProperty.ESTIMATED_TIME.is(field)) {

			Duration oldDuration = (jiraItem.getFrom() != null ?
										Duration.standardSeconds(
													Integer.valueOf(jiraItem.getFrom())) :
										null);
			Duration newDuration = (jiraItem.getTo() != null ?
										Duration.standardSeconds(
													Integer.valueOf(jiraItem.getTo())) :
										null);

			item = Item.builder()
							.estimatedTime()
								.oldValue(oldDuration)
								.newValue(newDuration)
								.build();

		} else if (ChangeLogProperty.BLOCKERS.is(field)) {

			// Remove null values
			String fromValue = (jiraItem.getFromString() != null ?
												jiraItem.getFromString() : "");
			String toValue = (jiraItem.getToString() != null ?
												jiraItem.getToString() : "");

			// Only takes the "is blocked" relation
			String oldLink = (fromValue.contains("This issue is blocked by") ?
												jiraItem.getFrom() : null);
			String newLink = (toValue.contains("This issue is blocked by") ?
												jiraItem.getTo() : null);

			if (oldLink != null || newLink != null) {

				item = Item.builder()
								.blockedIssues()
									.oldValue(oldLink)
									.newValue(newLink)
									.build();
			}

		} else if (ChangeLogProperty.COMPONENT.is(field)) {

			item = Item.builder()
							.components()
								.newValue(jiraItem.getFromString())
								.oldValue(jiraItem.getToString())
								.build();

		} else if (ChangeLogProperty.TITLE.is(field)) {

			item = Item.builder()
							.title()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();

		} else if (ChangeLogProperty.VERSION.is(field)) {

			item = Item.builder()
							.versions()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();

		} else if (ChangeLogProperty.ASSIGNEE.is(field)) {

			item = Item.builder()
							.assignee()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();

		} else if (ChangeLogProperty.DESCRIPTION.is(field)) {

			item = Item.builder()
							.description()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();

		} 

		return item;
	}

	private DateTime getLastStatusDate(Status status, ChangeLog changeLog) {

		LinkedList<DateTime> dates = new LinkedList<>(); 

		for (Entry entry : changeLog.getEntries()) {

			for (Item item : entry.getItems()) {

				if (item instanceof StatusChangeItem && item.getNewValue() == status) {

					dates.add(entry.getTimeStamp());
				}
			}
		}

		Collections.sort(dates);

		return dates.isEmpty() ? null : dates.getLast();
	}


	private DateTime getOpenedDate(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue,
									ChangeLog changeLog) {

		DateTime openDate = getLastStatusDate(Status.OPEN, changeLog);

		return openDate != null ? openDate : jiraIssue.getCreationDate();
	}

	private DateTime getClosedDate(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue,
									ChangeLog changeLog) {

		if (fromMap(jiraIssue.getStatus().getName(), statusMapping) == Status.CLOSED) {

			return getLastStatusDate(Status.CLOSED, changeLog);
		}
		return null;
	}
}