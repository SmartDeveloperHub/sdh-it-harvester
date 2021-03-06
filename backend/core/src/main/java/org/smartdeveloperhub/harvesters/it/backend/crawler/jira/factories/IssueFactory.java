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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0
 *   Bundle      : it-backend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.crawler.jira.factories;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import com.atlassian.jira.rest.client.api.domain.IssueLinkType.Direction;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.ChangeLogProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import jersey.repackaged.com.google.common.collect.Sets;

/**
 * Class factory for building {@link Issue}s.
 * @author imolina
 *
 */
public class IssueFactory {

	private static final Logger LOGGER =
									LoggerFactory.getLogger(IssueFactory.class);

	private Map<String, Status> statusMapping;
	private Map<String, Priority> priorityMapping;
	private Map<String, Severity> severityMapping;
	private Map<String, Type> typeMapping;

	public Map<String, String> getStatusMapping() {

		Map<String, String> mapping = new HashMap<>();

		for(String key : statusMapping.keySet()) {

			mapping.put(key, statusMapping.get(key).name());
		}

		return mapping;
	}

	public IssueFactory(Map<String, Status> statusMapping,
						Map<String, Priority> priorityMapping,
						Map<String, Severity> severityMapping,
						Map<String, Type> typeMapping) {

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
	 * @param contributors map of contributors stored by id
	 * @return {@link Issue}
	 */
	public Issue createIssue(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue,
								Map<String, Contributor> contributors) {

		Issue issue = new Issue();

		issue.setId(String.valueOf(jiraIssue.getKey()));
		issue.setProjectId(jiraIssue.getProject().getKey());
		issue.setCreationDate(jiraIssue.getCreationDate());
		issue.setDescription(jiraIssue.getDescription());
		issue.setReporter(jiraIssue.getReporter().getName());
		issue.setName(jiraIssue.getSummary());

		User assignee = jiraIssue.getAssignee();

		if (assignee != null) {

			issue.setAssignee(assignee.getName());
		}

		// Prepare structures to explore changes looking for open and close dates.
		Stack<DateTime> openDate = new Stack<>(); 
		Stack<DateTime> closeDate = new Stack<>();

		issue.setChanges(createChangeLog(jiraIssue, contributors, openDate, closeDate));
		issue.setOpened(openDate.peek());
		issue.setClosed(closeDate.peek());
		issue.setDueTo(jiraIssue.getDueDate());
		TimeTracking track = jiraIssue.getTimeTracking();
		if (track != null) {

			Integer originalEstimatedMin = track.getOriginalEstimateMinutes();
			if (originalEstimatedMin != null) {

				issue.setEstimatedTime(Duration.
										standardMinutes(originalEstimatedMin));
			} else {
				LOGGER.info("No original estimated time for Issue {}: {}",
						issue.getId(),
						issue.getEstimatedTime());
			}
		}  else {
			LOGGER.info("No time tracking available for issue {}",
						issue.getId());
		}
		issue.setStatus(createStatus(jiraIssue));
		issue.setPriority(fromMap(jiraIssue.getPriority().getName(), priorityMapping));
		issue.setSeverity(fromMap(jiraIssue.getPriority().getName(), severityMapping));
		issue.setType(fromMap(jiraIssue.getIssueType().getName(), typeMapping));

		issue.setVersions(getVersions(jiraIssue));
		issue.setComponents(getComponents(jiraIssue));

		issue.setChildIssues(getChildIssuesById(jiraIssue));
		issue.setBlockedIssues(getBlockedIssuesById(jiraIssue));
		issue.setTags(jiraIssue.getLabels());

		// TODO: not available.
//		issue.setCommits(commits);

		return issue;
	}

	/**
	 * Method that gets all the issues ids that are child of a Jira Issue.
	 * @param jiraIssue Jira Issue from which extract its children.
	 * @return list of Issues ids that are children of the Issue.
	 */
	private Set<String> getChildIssuesById(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		Set<String> children = new HashSet<>();

		// Gets all subtask
		for (Subtask sub : jiraIssue.getSubtasks()) {

			children.add(sub.getIssueKey());
		}

		// Gets all epic task (from ChangeLog)
		for (ChangelogGroup group : jiraIssue.getChangelog()) {

			for (ChangelogItem item : group.getItems()) {

				if (item.getField().equals("Epic Child")) {

					if (item.getFromString() == null && item.getToString() != null) {

						children.add(item.getToString());
					}
					if (item.getFrom() != null && item.getToString() == null) {

						children.remove(item.getFromString());
					}
				}
			}
		}

		return children;
	}

	/**
	 * Method that gets all the issues ids that are being blocked by Jira Issue.
	 * @param jiraIssue Jira Issue from which extract blocked issues.
	 * @return list of Issues ids that are being blocked by the Issue.
	 */
	private Set<String> getBlockedIssuesById(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue) {

		Set<String> blocked = new HashSet<>();

		for (IssueLink link : jiraIssue.getIssueLinks()) {

			IssueLinkType type = link.getIssueLinkType();

			if (type.getName().equals("Blocks") && type.getDirection() == Direction.OUTBOUND) {

				blocked.add(link.getTargetIssueKey());
			}
		}
		return blocked;
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

	private ChangeLog createChangeLog(com.atlassian.jira.rest.client.api.domain.Issue jiraIssue,
										Map<String, Contributor> contributors, Stack<DateTime> openDate, Stack<DateTime> closeDate) {

		ChangeLog changeLog = new ChangeLog();
		Set<Entry> entries = new HashSet<>();

		openDate.push(jiraIssue.getCreationDate());
		closeDate.push(null);

		for (com.atlassian.jira.rest.client.api.domain.ChangelogGroup group :
													jiraIssue.getChangelog()) {

			Entry entry = new Entry();
			entry.setTimeStamp(group.getCreated());

			Contributor contributor = selectContributorByName(contributors,
																group.getAuthor()
																		.getDisplayName());

			entry.setAuthor(contributor.getId());

			Set<Item> items = new HashSet<>();
			for (com.atlassian.jira.rest.client.api.domain.ChangelogItem jiraItem :
															group.getItems()) {

				// Register only changes on Jira Attributes
				if (jiraItem.getFieldType() == com.atlassian.jira.rest.client.api.domain.FieldType.JIRA) {

					try {

						items.addAll(buildChangeLogItem(jiraItem, contributors, group.getCreated(), openDate, closeDate));

					} catch (IllegalStateException e) {
						LOGGER.warn("Ignoring entry because IllegalState.\n" + 
										"Property: " + jiraItem.getField() +
										" - oldValue: " + jiraItem.getFromString() +
										" - newValue: " + jiraItem.getToString() + ". {}", e);
					}
				}
			}

			if (!items.isEmpty()) {

				entry.setItems(items);
				entries.add(entry);
			}
		}

		changeLog.setEntries(entries);

		return changeLog;
	}

	private Contributor selectContributorByName(Map<String, Contributor> contributors, String displayName) {

		for (Contributor contributor : contributors.values()) {

			if (contributor.getName().equals(displayName)) {

				return contributor;
			}
		}

		throw new IllegalArgumentException("Contributor not found.");
	}

	private Set<Item> buildChangeLogItem(ChangelogItem jiraItem,
					Map<String, Contributor> contributors, DateTime timestamp,
					Stack<DateTime> openDate, Stack<DateTime> closeDate) {

		Set<Item> items = new HashSet<>();
		String field = jiraItem.getField();
		if (ChangeLogProperty.STATUS.is(field)) {

			Status oldStatus = fromMap(jiraItem.getFromString(),
					statusMapping);
			Status newStatus = fromMap(jiraItem.getToString(),
					statusMapping);

			if (!oldStatus.equals(newStatus)) {

				if (oldStatus.equals(Status.CLOSED)) {

					items.add(Item.builder()
									.openedDate()
										.oldValue(openDate.pop())
										.newValue(openDate.push(timestamp))
										.build());
	
					items.add(Item.builder()
									.closedDate()
										.oldValue(closeDate.pop())
										.newValue(closeDate.peek())
										.build());
	
				} else if (newStatus.equals(Status.CLOSED)) {

					items.add(Item.builder()
									.closedDate()
										.oldValue(closeDate.peek())
										.newValue(closeDate.push(timestamp))
										.build());
				}

				items.add(Item.builder()
						.status()
							.oldValue(oldStatus)
							.newValue(newStatus)
							.build());
			}

		} else if (ChangeLogProperty.PRIORITY.is(field)) {

			Item item = Item.builder()
							.priority()
								.oldValue(fromMap(jiraItem.getFromString(),
													priorityMapping))
								.newValue(fromMap(jiraItem.getToString(),
													priorityMapping))
								.build();
			items.add(item);

		} else if (ChangeLogProperty.SEVERITY.is(field)) {

			Item item = Item.builder()
							.severity()
								.oldValue(fromMap(jiraItem.getFromString(),
													severityMapping))
								.newValue(fromMap(jiraItem.getToString(),
													severityMapping))
								.build();
			items.add(item);

		} else if (ChangeLogProperty.ISSUE_TYPE.is(field)) {

			Item item = Item.builder()
							.type()
								.oldValue(fromMap(jiraItem.getFromString(),
													typeMapping))
								.newValue(fromMap(jiraItem.getToString(),
													typeMapping))
								.build();
			items.add(item);

		} else if (ChangeLogProperty.DUE_DATE.is(field)) {

			DateTimeFormatter formatter =
							DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SS")
											.withZoneUTC();

			DateTime fromDate = (jiraItem.getFromString() != null ?
									formatter.parseDateTime(jiraItem.getFromString()) :
									null);
			DateTime toDate = (jiraItem.getFromString() != null ?
									formatter.parseDateTime(jiraItem.getToString()) :
									null);

			Item item = Item.builder()
							.dueToDate()
								.oldValue(fromDate)
								.newValue(toDate)
								.build();
			items.add(item);

		} else if (ChangeLogProperty.ESTIMATED_TIME.is(field)) {

			Duration oldDuration = (jiraItem.getFrom() != null ?
										Duration.standardSeconds(
													Integer.valueOf(jiraItem.getFrom())) :
										null);
			Duration newDuration = (jiraItem.getTo() != null ?
										Duration.standardSeconds(
													Integer.valueOf(jiraItem.getTo())) :
										null);

			Item item = Item.builder()
							.estimatedTime()
								.oldValue(oldDuration)
								.newValue(newDuration)
								.build();
			items.add(item);

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

				Item item = Item.builder()
								.blockedIssues()
									.oldValue(oldLink)
									.newValue(newLink)
									.build();
				items.add(item);
			}

		} else if (ChangeLogProperty.TAGS.is(field)) {

			Set<String> oldTags = Sets.
									newHashSet(jiraItem.getFromString()
															.split(" "));
			Set<String> newTags = Sets.
									newHashSet(jiraItem.getToString()
															.split(" "));

			// For these cases where there is no tag
			oldTags.remove("");
			newTags.remove("");

			Set<String> toAdd = Sets.difference(newTags, oldTags);
			Set<String> toDel = Sets.difference(oldTags, newTags);

			for (String tag : toAdd) {
				Item item = Item.builder()
									.tags()
										.newValue(tag)
										.build();
				items.add(item);
			}

			for (String tag : toDel) {
				Item item = Item.builder()
									.tags()
										.oldValue(tag)
										.build();
				items.add(item);
			}

		} else if (ChangeLogProperty.COMPONENT.is(field)) {

			Item item = Item.builder()
							.components()
								.oldValue(jiraItem.getFrom())
								.newValue(jiraItem.getTo())
								.build();
			items.add(item);

		} else if (ChangeLogProperty.TITLE.is(field)) {

			Item item = Item.builder()
							.title()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();
			items.add(item);

		} else if (ChangeLogProperty.VERSION.is(field)) {

			Item item = Item.builder()
							.versions()
								.oldValue(jiraItem.getFrom())
								.newValue(jiraItem.getTo())
								.build();
			items.add(item);

		} else if (ChangeLogProperty.ASSIGNEE.is(field)) {

			String oldAssignee = null;
			String newAssignee = null;

			if (jiraItem.getFromString() != null) {
				oldAssignee = selectContributorByName(contributors,
													jiraItem.getFromString())
														.getId();
			}

			if (jiraItem.getToString() != null) {
				newAssignee = selectContributorByName(contributors,
													jiraItem.getToString())
														.getId();
			}


			Item item = Item.builder()
							.assignee()
								.oldValue(oldAssignee)
								.newValue(newAssignee)
								.build();
			items.add(item);

		} else if (ChangeLogProperty.DESCRIPTION.is(field)) {

			Item item = Item.builder()
							.description()
								.oldValue(jiraItem.getFromString())
								.newValue(jiraItem.getToString())
								.build();
			items.add(item);

		}

		return items;
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
}
