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

import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * TODO: For the time being, the issue only identifies the issues blocked from the same project.
 */
public final class Issue extends Titled<String> implements ProjectScoped {

	public enum Type {
		BUG,
		IMPROVEMENT,
		TASK
	}

	static final String PROJECT_ID="projectId";

	private String projectId;

	private Type type;
	private String description;
	private Status status;
	private Severity severity;
	private Priority priority;
	private DateTime createdAt;
	private DateTime opened;
	private DateTime closed;
	private DateTime dueTo;
	private long estimatedEffortInMinutes;
	private String version;
	private String component;
	private String reporter;
	private String assignee;
	private Set<String> tags;
	private Set<String> commits;
	private Set<String> childIssues;
	private Set<String> blockedIssues;
	private ChangeLog changes;

	public Issue() {
		this.tags=Sets.newLinkedHashSet();
		this.commits=Sets.newLinkedHashSet();
		this.childIssues=Sets.newLinkedHashSet();
		this.blockedIssues=Sets.newLinkedHashSet();
	}

	@Override
	public String getProjectId() {
		return this.projectId;
	}

	@Override
	public void setProjectId(final String projectId) {
		this.projectId = projectId;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public Severity getSeverity() {
		return this.severity;
	}

	public void setSeverity(final Severity severity) {
		this.severity = severity;
	}

	public Priority getPriority() {
		return this.priority;
	}

	public void setPriority(final Priority priority) {
		this.priority = priority;
	}

	public DateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(final DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public DateTime getOpened() {
		return this.opened;
	}

	public void setOpened(final DateTime opened) {
		this.opened = opened;
	}

	public DateTime getClosed() {
		return this.closed;
	}

	public void setClosed(final DateTime closed) {
		this.closed = closed;
	}

	public DateTime getDueTo() {
		return this.dueTo;
	}

	public void setDueTo(final DateTime dueTo) {
		this.dueTo = dueTo;
	}

	public long getEstimatedEffortInMinutes() {
		return this.estimatedEffortInMinutes;
	}

	public void setEstimatedEffortInMinutes(final long estimatedEffortInMinutes) {
		this.estimatedEffortInMinutes = estimatedEffortInMinutes;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getComponent() {
		return this.component;
	}

	public void setComponent(final String component) {
		this.component = component;
	}

	public String getReporter() {
		return this.reporter;
	}

	public void setReporter(final String reporter) {
		this.reporter = reporter;
	}

	public String getAssignee() {
		return this.assignee;
	}

	public void setAssignee(final String assignee) {
		this.assignee = assignee;
	}

	public Set<String> getTags() {
		return this.tags;
	}

	public void setTags(final Set<String> tags) {
		this.tags = tags;
	}

	public Set<String> getCommits() {
		return this.commits;
	}

	public void setCommits(final Set<String> commits) {
		this.commits = commits;
	}

	public Set<String> getChildIssues() {
		return this.childIssues;
	}

	public void setChildIssues(final Set<String> childIssues) {
		this.childIssues = childIssues;
	}

	public Set<String> getBlockedIssues() {
		return this.blockedIssues;
	}

	public void setBlockedIssues(final Set<String> blockedIssues) {
		this.blockedIssues = blockedIssues;
	}

	public ChangeLog getChanges() {
		return this.changes;
	}

	public void setChanges(final ChangeLog changes) {
		this.changes = changes;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add(PROJECT_ID,this.projectId).
				add("type",this.type).
				add("description",this.description).
				add("status",this.status).
				add("severity",this.severity).
				add("priority",this.priority).
				add("createdAt",this.createdAt).
				add("opened",this.opened).
				add("closed",this.closed).
				add("dueTo",this.dueTo).
				add("estimatedEffortInMinutes",this.estimatedEffortInMinutes).
				add("version",this.version).
				add("component",this.component).
				add("reported",this.reporter).
				add("assignee",this.assignee).
				add("tags",this.tags).
				add("commits",this.commits).
				add("childIssues",this.childIssues).
				add("blockedIssues",this.blockedIssues).
				add("changes",this.changes);
	}

}
