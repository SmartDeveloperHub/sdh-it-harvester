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
package org.smartdeveloperhub.harvesters.it.frontend.vocabulary;

import java.net.URI;

import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AssigneeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.BlockedIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ChildIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ClosedDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.CommitsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ComponentsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DescriptionChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DueToDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.EstimatedTimeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ItemVisitor;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.OpenedDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.PriorityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.SeverityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TagsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TitleChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TypeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.VersionsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.Issue.Type;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;

public final class IT {

	private static final class PropertySelector extends ItemVisitor {

		private String property;

		@Override
		public void visitTitleChange(final TitleChangeItem item) {
			this.property=ISSUE_TITLE;
		}

		@Override
		public void visitDescriptionChange(final DescriptionChangeItem item) {
			this.property=DESCRIPTION;
		}

		@Override
		public void visitOpenedDateChange(final OpenedDateChangeItem item) {
			this.property=DATE_OPEN;
		}

		@Override
		public void visitClosedDateChange(final ClosedDateChangeItem item) {
			this.property=DATE_CLOSED;
		}

		@Override
		public void visitDueToDateChange(final DueToDateChangeItem item) {
			this.property=DUE_TO;
		}

		@Override
		public void visitEstimatedTimeChange(final EstimatedTimeChangeItem item) {
			this.property=ESTIMATED_TIME;
		}

		@Override
		public void visitTagsChange(final TagsChangeItem item) {
			this.property=ISSUE_CUSTOM_TAG;
		}

		@Override
		public void visitComponentsChange(final ComponentsChangeItem item) {
			this.property=ASSOCIATED_TO_COMPONENT;
		}

		@Override
		public void visitVersionsChange(final VersionsChangeItem item) {
			this.property=AFFECTS_VERSION;
		}

		@Override
		public void visitCommitsChange(final CommitsChangeItem item) {
			this.property=ASSOCIATED_TO_COMMIT;
		}

		@Override
		public void visitAssigneeChange(final AssigneeChangeItem item) {
			this.property=IS_ASSIGNED_TO;
		}

		@Override
		public void visitStatusChange(final StatusChangeItem item) {
			this.property=HAS_STATUS;
		}

		@Override
		public void visitPriorityChange(final PriorityChangeItem item) {
			this.property=HAS_PRIORITY;
		}

		@Override
		public void visitSeverityChange(final SeverityChangeItem item) {
			this.property=HAS_SEVERITY;
		}

		@Override
		public void visitChildIssuesChange(final ChildIssuesChangeItem item) {
			this.property=IS_COMPOSED_OF_ISSUE;
		}

		@Override
		public void visitBlockedIssuesChange(final BlockedIssuesChangeItem item) {
			this.property=BLOCKS_ISSUE;
		}

		@Override
		public void visitTypeChange(final TypeChangeItem item) {
			this.property=RDF.TYPE;
		}

		URI getProperty() {
			return URI.create(this.property);
		}

	}

	public static final String NAMESPACE = "http://www.smartdeveloperhub.org/vocabulary/it#";
	public static final String PREFIX    = "it";

	public static final String VOCABULARY_TYPE         = term("ITVocabulary");
	public static final String HARVESTER_TYPE          = term("ITHarvester");
	public static final String DOMAIN_TYPE             = term("ITDomain");

	public static final String CONTRIBUTOR_TYPE        = term("Contributor");
	public static final String COMMIT_TYPE             = term("Commit");
	public static final String PROJECT_TYPE            = term("Project");
	public static final String VERSION_TYPE            = term("Version");
	public static final String COMPONENT_TYPE          = term("Component");

	public static final String ISSUE_TYPE              = term("Issue");
	public static final String BUG_TYPE                = term("Bug");
	public static final String IMPROVEMENT_TYPE        = term("Improvement");
	public static final String TASK_TYPE               = term("Task");
	public static final String DEVELOPMENT_TASK_TYPE   = term("DevelopmentTask");
	public static final String COMPOSITE_ISSUE_TYPE    = term("CompositeIssue");

	public static final String CHANGE_LOG_TYPE         = term("ChangeLog");
	public static final String CHANGE_LOG_ENTRY_TYPE   = term("ChangeLogEntry");
	public static final String CHANGE_LOG_ITEM_TYPE    = term("ChangeLogItem");
	public static final String UPDATE_LOG_ITEM_TYPE    = term("ChangeLogItemUpdateValue");
	public static final String ADD_LOG_ITEM_TYPE       = term("ChangeLogItemAddValue");
	public static final String DELETE_LOG_ITEM_TYPE    = term("ChangeLogItemDeleteValue");

	public static final String STATUS_TYPE             = term("Status");
	public static final String OPEN                    = term("open");
	public static final String CLOSED                  = term("closed");
	public static final String IN_PROGRESS             = term("inProgress");

	public static final String PRIORITY_TYPE           = term("Priority");
	public static final String VERY_HIGH_PRIORITY      = term("veryHighPriority");
	public static final String HIGH_PRIORITY           = term("highPriority");
	public static final String MEDIUM_PRIORITY         = term("mediumPriority");
	public static final String LOW_PRIORITY            = term("lowPriority");

	public static final String SEVERITY_TYPE           = term("Severity");
	public static final String BLOCKER_SEVERITY        = term("blockerSeverity");
	public static final String CRITICAL_SEVERITY       = term("criticalSeverity");
	public static final String GRAVE_SEVERITY          = term("graveSeverity");
	public static final String NORMAL_SEVERITY         = term("normalSeverity");
	public static final String TRIVIAL_SEVERITY        = term("trivialSeverity");

	public static final String HAS_PROJECT             = "http://www.smartdeveloperhub.org/vocabulary/it#hasProject";
	public static final String HAS_CONTRIBUTOR         = "http://www.smartdeveloperhub.org/vocabulary/it#hasContributor";
	public static final String HAS_COMMIT              = "http://www.smartdeveloperhub.org/vocabulary/it#hasCommit";
	public static final String HAS_VERSION             = "http://www.smartdeveloperhub.org/vocabulary/it#hasVersion";
	public static final String HAS_COMPONENT           = "http://www.smartdeveloperhub.org/vocabulary/it#hasComponent";
	public static final String HAS_PROJECT_ISSUE       = "http://www.smartdeveloperhub.org/vocabulary/it#hasProjectIssue";
	public static final String HAS_PROJECT_TOP_ISSUE   = term("hasProjectTopIssue");

	public static final String ID                      = term("id");

	public static final String CONTRIBUTOR_ID          = term("contributorId");
	public static final String CONTRIBUTOR_NAME        = term("contributorName");
	public static final String MBOX                    = term("mbox");

	public static final String PROJECT_ID              = term("projectId");
	public static final String PROJECT_TITLE           = term("projectTitle");

	public static final String VERSION_ID              = term("versionId");
	public static final String VERSION_NAME            = term("versionName");

	public static final String COMPONENT_ID            = term("componentId");
	public static final String COMPONENT_TITLE         = term("componentTitle");

	public static final String ISSUE_ID                = term("issueId");
	public static final String ISSUE_TITLE             = term("issueTitle");
	public static final String DESCRIPTION             = term("description");
	public static final String ISSUE_CUSTOM_TAG        = term("issueCustomTag");
	public static final String DATE_CREATION           = term("dateCreation");
	public static final String DATE_OPEN               = term("dateOpen");
	public static final String DATE_CLOSED             = term("dateClosed");
	public static final String DUE_TO                  = term("dueTo");
	public static final String ESTIMATED_TIME          = term("estimatedTime");
	public static final String HAS_STATUS              = term("hasStatus");
	public static final String HAS_PRIORITY            = term("hasPriority");
	public static final String HAS_SEVERITY            = term("hasSeverity");
	public static final String AFFECTS_VERSION         = term("affectsVersion");
	public static final String IS_ISSUE_OF_PROJECT     = term("isIssueOfProject");
	public static final String ASSOCIATED_TO_COMPONENT = term("associatedToComponent");
	public static final String ASSOCIATED_TO_COMMIT    = term("associatedToCommit");
	public static final String IS_ASSIGNED_TO          = term("isAssignedTo");
	public static final String IS_REPORTED_BY          = term("isReportedBy");
	public static final String BLOCKS_ISSUE            = term("blocksIssue");
	public static final String IS_COMPOSED_OF_ISSUE    = term("isComposedOfIssue");

	public static final String COMMIT_ID               = term("commitId");
	public static final String REPOSITORY              = term("repository");
	public static final String HASH                    = term("hash");
	public static final String BRANCH                  = term("branch");

	public static final String HAS_CHANGE_LOG                 = term("hasChangeLog");
	public static final String HAS_CHANGE_LOG_ENTRY           = term("hasChangeLogEntry");
	public static final String HAS_CHANGE_LOG_ITEM            = term("hasChangeLogItem");
	public static final String IS_COMPOSED_BY_CHANGE_LOG_ITEM = term("isComposedByChangeLogItem");
	public static final String ENTRY_TIME_STAMP               = term("entryTimeStamp");
	public static final String TRIGGERED_BY                   = term("triggeredBy");
	public static final String ON_PROPERTY                    = term("onProperty");
	public static final String PREVIOUS_VALUE                 = term("previousValue");
	public static final String NEW_VALUE                      = term("newValue");
	public static final String ADDED_VALUE                    = term("addedValue");
	public static final String DELETED_VALUE                  = term("deletedValue");

	private IT() {
	}

	private static String term(final String localName) {
		return NAMESPACE+localName;
	}

	public static URI sourceCode() {
		return URI.create("http://www.smartdeveloperhub.org/vocabulary/v1/it.ttl");
	}

	public static URI forStatus(final Status status) {
		if(status==null) {
			return null;
		}
		String individual=null;
		switch(status) {
		case CLOSED:
			individual=CLOSED;
			break;
		case IN_PROGRESS:
			individual=IN_PROGRESS;
			break;
		default:
			individual=OPEN;
			break;
		}
		return URI.create(individual);
	}

	public static URI forSeverity(final Severity severity) {
		if(severity==null) {
			return null;
		}
		String individual=null;
		switch(severity) {
		case BLOCKER:
			individual=BLOCKER_SEVERITY;
			break;
		case CRITICAL:
			individual=CRITICAL_SEVERITY;
			break;
		case SEVERE:
			individual=GRAVE_SEVERITY;
			break;
		case LOW:
			individual=NORMAL_SEVERITY;
			break;
		default:
			individual=TRIVIAL_SEVERITY;
			break;
		}
		return URI.create(individual);
	}

	public static URI forPriority(final Priority priority) {
		if(priority==null) {
			return null;
		}
		String individual=null;
		switch(priority) {
		case VERY_HIGH:
			individual=VERY_HIGH_PRIORITY;
			break;
		case HIGH:
			individual=HIGH_PRIORITY;
			break;
		case MEDIUM:
			individual=MEDIUM_PRIORITY;
			break;
		default:
			individual=LOW_PRIORITY;
			break;
		}
		return URI.create(individual);
	}

	public static URI forType(final Type type) {
		if(type==null) {
			return null;
		}
		String individual=null;
		switch(type) {
		case BUG:
			individual=BUG_TYPE;
			break;
		case IMPROVEMENT:
			individual=IMPROVEMENT_TYPE;
			break;
		default:
			individual=TASK_TYPE;
			break;
		}
		return URI.create(individual);
	}

	public static URI propertyOf(final Item item) {
		if(item==null) {
			return null;
		}
		final PropertySelector selector = new PropertySelector();
		item.accept(selector);
		return selector.getProperty();
	}

}
