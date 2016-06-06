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
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import java.io.IOException;

import org.joda.time.DateTime;
import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.DataSetHelper;
import org.ldp4j.application.data.DataSetUtils;
import org.ldp4j.application.data.DataSets;
import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.annotations.Resource;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.AbstractEntityResourceHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.DCTERMS;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;

@Resource(id=IssueHandler.ID)
public final class IssueHandler extends AbstractEntityResourceHandler<Issue,IssueKey> {

	public static final String ID="IssueHandler";

	public IssueHandler(final BackendController backendController) {
		super(backendController);
	}

	@Override
	protected IssueKey getId(final ResourceSnapshot resource) {
		return IdentityUtil.issueId(resource);
	}

	@Override
	protected Issue getEntity(final BackendController controller, final IssueKey key) throws IOException {
		return controller.getProjectIssue(key.getProjectId(),key.getIssueId());
	}

	@Override
	protected DataSet toDataSet(final Issue issue, final IssueKey key) {
		final Name<IssueKey> issueName=IdentityUtil.issueName(key);
		final Name<String> projectName=IdentityUtil.projectName(key.getProjectId());
		final Name<String> changeLogName=NamingScheme.getDefault().name(issue.getId(),"changeLog");

		final DataSet dataSet=DataSets.createDataSet(issueName);
		final DataSetHelper helper=DataSetUtils.newHelper(dataSet);

		final DateTime closedDate = dateTime(issue.getClosed(),false,"closed",issue).orNull();
		final DateTime dueToDate = dateTime(issue.getDueTo(),false,"dueTo",issue).orNull();

		final IndividualHelper individual=
			helper.
				managedIndividual(issueName,IssueHandler.ID).
					property(RDF.TYPE).
						withIndividual(IT.ISSUE_TYPE).
					property(IT.ID).
						withLiteral(issue.getId()).
					property(IT.ISSUE_ID).
						withLiteral(issue.getId()).
					property(DCTERMS.TITLE).
						withLiteral(issue.getTitle()).
					property(IT.ISSUE_TITLE).
						withLiteral(issue.getTitle()).
					property(DCTERMS.DESCRIPTION).
						withLiteral(issue.getDescription()).
					property(IT.DESCRIPTION).
						withLiteral(issue.getDescription()).
					property(IT.IS_ISSUE_OF_PROJECT).
						withIndividual(projectName,ProjectHandler.ID).
					property(IT.HAS_CHANGE_LOG).
						withIndividual(changeLogName).
					property(IT.HAS_STATUS).
						withIndividual(IT.forStatus(issue.getStatus())).
					property(IT.HAS_SEVERITY).
						withIndividual(IT.forSeverity(issue.getSeverity())).
					property(IT.HAS_PRIORITY).
						withIndividual(IT.forPriority(issue.getPriority())).
					property(IT.DATE_CREATION).
						withLiteral(dateTime(issue.getCreationDate(),true,"createdAt",issue).get()).
					property(IT.DATE_OPEN).
						withLiteral(dateTime(issue.getOpened(),true,"opened",issue).get()).
					property(DCTERMS.DATE).
						withLiteral(closedDate).
					property(IT.DATE_CLOSED).
						withLiteral(closedDate).
					property(DCTERMS.DATE).
						withLiteral(dueToDate).
					property(IT.DUE_TO).
						withLiteral(dueToDate).
					property(IT.ESTIMATED_TIME).
						withLiteral(issue.getEstimatedTime());

		populateTags(issue,individual);

		populateVersion(issue,individual);

		populateComponents(issue,individual);

		populateReporter(issue,individual);

		populateAssignee(issue,individual);

		populateBlockedIssues(issue,individual);

		populateChildIssues(issue,individual);

		populateCommits(issue,individual);

		populateChangeLog(issue,changeLogName,helper);

		return dataSet;
	}

	private void populateCommits(final Issue issue, final IndividualHelper individual) {
		new CommitLinker(individual).
			infer(IT.TASK_TYPE).
			infer(IT.DEVELOPMENT_TASK_TYPE).
			link(IT.ASSOCIATED_TO_COMMIT,issue.getCommits());
	}

	private void populateChildIssues(final Issue issue, final IndividualHelper individual) {
		new IssueLinker(individual,issue).
			infer(IT.COMPOSITE_ISSUE_TYPE).
			link(IT.IS_COMPOSED_OF_ISSUE,issue.getChildIssues());
	}

	private void populateBlockedIssues(final Issue issue, final IndividualHelper individual) {
		new IssueLinker(individual,issue).
			link(IT.BLOCKS_ISSUE,issue.getBlockedIssues());
	}

	private void populateAssignee(final Issue issue, final IndividualHelper individual) {
		new ContributorLinker(individual).
			link(IT.IS_ASSIGNED_TO,issue.getAssignee());
	}

	private void populateReporter(final Issue issue, final IndividualHelper individual) {
		new ContributorLinker(individual).
			link(IT.IS_REPORTED_BY,issue.getReporter());
	}

	private void populateComponents(final Issue issue, final IndividualHelper individual) {
		new ComponentLinker(individual,issue).
			link(IT.ASSOCIATED_TO_COMPONENT,issue.getComponents());
	}

	private void populateVersion(final Issue issue, final IndividualHelper individual) {
		new VersionLinker(individual,issue).
			link(IT.AFFECTS_VERSION,issue.getVersions());
	}

	private void populateTags(final Issue issue, final IndividualHelper individual) {
		for(final String tag:issue.getTags()) {
			individual.
				property(IT.ISSUE_CUSTOM_TAG).
					withLiteral(tag);
		}
	}

	private void populateChangeLog(final Issue issue, final Name<String> changeLogName, final DataSetHelper helper) {
		final IndividualHelper changeLog =
			helper.
				localIndividual(changeLogName).
					property(RDF.TYPE).
						withIndividual(IT.CHANGE_LOG_TYPE);

		int entryCount=0;
		for(final Entry entry:issue.getChanges().getEntries()) {
			final Name<String> entryName=
				NamingScheme.
					getDefault().
						name(
							changeLogName.id(),
							"entry",
							Integer.toString(entryCount++));
			populateChangeLogEntry(issue,entry,entryName,helper,changeLog);
		}
	}

	private void populateChangeLogEntry(final Issue issue, final Entry entryData, final Name<String> entryName, final DataSetHelper helper, final IndividualHelper changeLog) {
		final IndividualHelper entry =
			helper.
				localIndividual(entryName);

		changeLog.
			property(IT.HAS_CHANGE_LOG_ENTRY).
				withIndividual(entryName);

		entry.
			property(RDF.TYPE).
				withIndividual(IT.CHANGE_LOG_ENTRY_TYPE).
			property(IT.ENTRY_TIME_STAMP).
				withLiteral(entryData.getTimeStamp());

		/**
		 * TODO: Should fail if no author is defined
		 */
		new ContributorLinker(entry).
			link(IT.TRIGGERED_BY,entryData.getAuthor());

		int itemCount=0;
		for(final Item item:entryData.getItems()) {
			final Name<String> itemName=NamingScheme.getDefault().name(entryName.id(),"item",Integer.toString(itemCount++));
			populateChangeLogItem(issue,item, itemName, helper, changeLog, entry);
		}
	}

	private void populateChangeLogItem(final Issue issue, final Item itemData, final Name<String> itemName, final DataSetHelper helper, final IndividualHelper changeLog, final IndividualHelper entry) {
		changeLog.
			property(IT.IS_COMPOSED_BY_CHANGE_LOG_ITEM).
				withIndividual(itemName);
		entry.
			property(IT.HAS_CHANGE_LOG_ITEM).
				withIndividual(itemName);

		final IndividualHelper item=
			helper.
				localIndividual(itemName).
					property(RDF.TYPE).
						withIndividual(IT.CHANGE_LOG_ITEM_TYPE).
					property(IT.ON_PROPERTY).
						withIndividual(IT.propertyOf(itemData));

		if(itemData.getOldValue()==null) {
			item.
				property(RDF.TYPE).
					withIndividual(IT.ADD_LOG_ITEM_TYPE);
			itemData.accept(new NewValueItemPopulator(issue,item,IT.ADDED_VALUE));
		} else if(itemData.getNewValue()==null){
			item.
				property(RDF.TYPE).
					withIndividual(IT.DELETE_LOG_ITEM_TYPE);
			itemData.accept(new OldValueItemPopulator(issue,item,IT.DELETED_VALUE));
		} else {
			item.
				property(RDF.TYPE).
					withIndividual(IT.UPDATE_LOG_ITEM_TYPE);
			itemData.accept(new NewValueItemPopulator(issue,item,IT.NEW_VALUE));
			itemData.accept(new OldValueItemPopulator(issue,item,IT.PREVIOUS_VALUE));
		}
	}

}