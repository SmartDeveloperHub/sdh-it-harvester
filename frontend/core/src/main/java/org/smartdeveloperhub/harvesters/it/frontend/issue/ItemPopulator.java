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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import java.io.Serializable;
import java.net.URI;

import org.ldp4j.application.data.IndividualHelper;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AbstractItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AssigneeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.BlockedIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ChildIssuesChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ClosedChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.CommitsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ComponentsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DescriptionChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.DueToDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.EstimatedTimeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ItemVisitor;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.OpenedChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.PriorityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.SeverityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TagsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TitleChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.TypeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.VersionsChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;

abstract class ItemPopulator extends ItemVisitor {

	private final Issue issue;
	private final IndividualHelper item;
	private final String property;

	ItemPopulator(final Issue issue, final IndividualHelper item, final String property) {
		this.issue = issue;
		this.item = item;
		this.property = property;
	}

	protected abstract <V> V getValue(final AbstractItem<V> aItem);

	private <V> void addLiteral(final AbstractItem<V> aItem) {
		this.item.property(this.property).withLiteral(getValue(aItem));
	}

	private void addExternalIndividual(final URI externalIndividual) {
		this.item.property(this.property).withIndividual(externalIndividual);
	}

	@SuppressWarnings("unchecked")
	private <V extends Serializable,K> void addManagedIndividual(final Linker<V,K> linker,final K value) {
		linker.link(this.property,value);
	}

	@Override
	public void visitTitleChange(final TitleChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitDescriptionChange(final DescriptionChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitOpenedDateChange(final OpenedChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitClosedDateChange(final ClosedChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitDueToDateChange(final DueToDateChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitEstimatedTimeChange(final EstimatedTimeChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitTagsChange(final TagsChangeItem aItem) {
		addLiteral(aItem);
	}

	@Override
	public void visitComponentsChange(final ComponentsChangeItem aItem) {
		addManagedIndividual(new ComponentLinker(this.item,this.issue),getValue(aItem));
	}

	@Override
	public void visitVersionsChange(final VersionsChangeItem aItem) {
		addManagedIndividual(new VersionLinker(this.item,this.issue),getValue(aItem));
	}

	@Override
	public void visitCommitsChange(final CommitsChangeItem aItem) {
		addManagedIndividual(new CommitLinker(this.item),getValue(aItem));
	}

	@Override
	public void visitAssigneeChange(final AssigneeChangeItem aItem) {
		addManagedIndividual(new ContributorLinker(this.item),getValue(aItem));
	}

	@Override
	public void visitChildIssuesChange(final ChildIssuesChangeItem aItem) {
		addManagedIndividual(new IssueLinker(this.item,this.issue),getValue(aItem));
	}

	@Override
	public void visitBlockedIssuesChange(final BlockedIssuesChangeItem aItem) {
		addManagedIndividual(new IssueLinker(this.item,this.issue),getValue(aItem));
	}

	@Override
	public void visitStatusChange(final StatusChangeItem aItem) {
		addExternalIndividual(IT.forStatus(getValue(aItem)));
	}

	@Override
	public void visitPriorityChange(final PriorityChangeItem aItem) {
		addExternalIndividual(IT.forPriority(getValue(aItem)));
	}

	@Override
	public void visitSeverityChange(final SeverityChangeItem aItem) {
		addExternalIndividual(IT.forSeverity(getValue(aItem)));
	}

	@Override
	public void visitTypeChange(final TypeChangeItem aItem) {
		addExternalIndividual(IT.forType(getValue(aItem)));
	}
}