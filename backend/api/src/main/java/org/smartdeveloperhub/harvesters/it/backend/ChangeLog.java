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

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	ChangeLog.ENTRIES
})
public final class ChangeLog extends Entity {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		Entry.TIMESTAMP,
		Entry.AUTHOR,
		Entry.ITEMS
	})
	public static final class Entry extends Entity implements Comparable<Entry> {

		@JsonTypeInfo(
			use=JsonTypeInfo.Id.NAME,
			include=JsonTypeInfo.As.PROPERTY,
			property=Item.PROPERTY
		)
		@JsonSubTypes({
			@Type(value=AssigneeChangeItem.class,name="assignee"),
			@Type(value=BlockedIssuesChangeItem.class,name="blockedIssues"),
			@Type(value=ChildIssuesChangeItem.class,name="childIssues"),
			@Type(value=ClosedDateChangeItem.class,name="closedDate"),
			@Type(value=CommitsChangeItem.class,name="commits"),
			@Type(value=ComponentsChangeItem.class,name="components"),
			@Type(value=DescriptionChangeItem.class,name="description"),
			@Type(value=DueToDateChangeItem.class,name="dueToDate"),
			@Type(value=EstimatedTimeChangeItem.class,name="estimatedTime"),
			@Type(value=OpenedDateChangeItem.class,name="openedDate"),
			@Type(value=PriorityChangeItem.class,name="priority"),
			@Type(value=SeverityChangeItem.class,name="severity"),
			@Type(value=StatusChangeItem.class,name="status"),
			@Type(value=TagsChangeItem.class,name="tags"),
			@Type(value=TitleChangeItem.class,name="title"),
			@Type(value=TypeChangeItem.class,name="type"),
			@Type(value=VersionsChangeItem.class,name="versions")
		})
		public abstract static class Item extends Entity {

			public static final class Builder {

				public static final class ItemEditorBuilder<T> {

					private final AbstractItem<T> item;

					private ItemEditorBuilder(final AbstractItem<T> item) {
						this.item = item;
					}

					public ItemEditorBuilder<T> oldValue(final T oldValue) {
						this.item.setOldValue(oldValue);
						return this;
					}

					public ItemEditorBuilder<T> newValue(final T newValue) {
						this.item.setNewValue(newValue);
						return this;
					}

					public Item build() {
						Preconditions.checkState(!(this.item.getOldValue()==null && this.item.getNewValue()==null),"No item values defined");
						Preconditions.checkState(!Objects.equals(this.item.getNewValue(),this.item.getOldValue()),"Old and new values of an item must be different");
						return this.item;
					}

				}

				private Builder() {
				}

				public ItemEditorBuilder<String> title() {
					return new ItemEditorBuilder<String>(new TitleChangeItem());
				}

				public ItemEditorBuilder<String> description() {
					return new ItemEditorBuilder<String>(new DescriptionChangeItem());
				}

				public ItemEditorBuilder<DateTime> openedDate() {
					return new ItemEditorBuilder<DateTime>(new OpenedDateChangeItem());
				}

				public ItemEditorBuilder<DateTime> closedDate() {
					return new ItemEditorBuilder<DateTime>(new ClosedDateChangeItem());
				}

				public ItemEditorBuilder<DateTime> dueToDate() {
					return new ItemEditorBuilder<DateTime>(new DueToDateChangeItem());
				}

				public ItemEditorBuilder<Duration> estimatedTime() {
					return new ItemEditorBuilder<Duration>(new EstimatedTimeChangeItem());
				}

				public ItemEditorBuilder<String> tags() {
					return new ItemEditorBuilder<String>(new TagsChangeItem());
				}

				public ItemEditorBuilder<String> components() {
					return new ItemEditorBuilder<String>(new ComponentsChangeItem());
				}

				public ItemEditorBuilder<String> versions() {
					return new ItemEditorBuilder<String>(new VersionsChangeItem());
				}

				public ItemEditorBuilder<String> commits() {
					return new ItemEditorBuilder<String>(new CommitsChangeItem());
				}

				public ItemEditorBuilder<String> assignee() {
					return new ItemEditorBuilder<String>(new AssigneeChangeItem());
				}

				public ItemEditorBuilder<String> blockedIssues() {
					return new ItemEditorBuilder<String>(new BlockedIssuesChangeItem());
				}

				public ItemEditorBuilder<String> childIssues() {
					return new ItemEditorBuilder<String>(new ChildIssuesChangeItem());
				}

				public ItemEditorBuilder<Status> status() {
					return new ItemEditorBuilder<Status>(new StatusChangeItem());
				}

				public ItemEditorBuilder<Severity> severity() {
					return new ItemEditorBuilder<Severity>(new SeverityChangeItem());
				}

				public ItemEditorBuilder<Priority> priority() {
					return new ItemEditorBuilder<Priority>(new PriorityChangeItem());
				}

				public ItemEditorBuilder<Issue.Type> type() {
					return new ItemEditorBuilder<Issue.Type>(new TypeChangeItem());
				}

			}

			static final String PROPERTY="property";

			public abstract Object getOldValue();

			public abstract Object getNewValue();

			public abstract void accept(ItemVisitor visitor);

			public static Builder builder() {
				return new Builder();
			}

		}

		public abstract static class ItemVisitor {

			public void visitTitleChange(final TitleChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitDescriptionChange(final DescriptionChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitOpenedDateChange(final OpenedDateChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitClosedDateChange(final ClosedDateChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitDueToDateChange(final DueToDateChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitEstimatedTimeChange(final EstimatedTimeChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitTagsChange(final TagsChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitComponentsChange(final ComponentsChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitVersionsChange(final VersionsChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitCommitsChange(final CommitsChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitAssigneeChange(final AssigneeChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitStatusChange(final StatusChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitPriorityChange(final PriorityChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitSeverityChange(final SeverityChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitChildIssuesChange(final ChildIssuesChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitBlockedIssuesChange(final BlockedIssuesChangeItem item) {
				// To be overriden by subclasses
			}

			public void visitTypeChange(final TypeChangeItem item) {
				// To be overriden by subclasses
			}

		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyOrder({
			Item.PROPERTY,
			AbstractItem.OLD_VALUE,
			AbstractItem.NEW_VALUE
		})
		public abstract static class AbstractItem<T> extends Item {

			static final String OLD_VALUE="oldValue";
			static final String NEW_VALUE="newValue";

			private T oldValue;
			private T newValue;

			@Override
			public final T getOldValue() {
				return this.oldValue;
			}

			public final void setOldValue(final T oldValue) {
				this.oldValue = oldValue;
			}

			@Override
			public final T getNewValue() {
				return this.newValue;
			}

			public final void setNewValue(final T newValue) {
				this.newValue = newValue;
			}

			@Override
			public final int hashCode() {
				return Objects.hash(getClass(),this.oldValue,this.newValue);
			}

			@Override
			public final boolean equals(final Object obj) {
				boolean result = false;
				if(obj instanceof AbstractItem) {
					final AbstractItem<?> that=(AbstractItem<?>)obj;
					result=
						this.getClass()==that.getClass() &&
						Objects.equals(this.oldValue,that.oldValue) &&
						Objects.equals(this.newValue,that.newValue) ;
				}
				return result;
			}

			@Override
			protected final ToStringHelper stringHelper() {
				return
					super.stringHelper().
						add(OLD_VALUE,this.oldValue).
						add(NEW_VALUE,this.newValue);
			}

		}

		public static final class TitleChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitTitleChange(this);
			}
		}

		public static final class DescriptionChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitDescriptionChange(this);
			}
		}

		public static final class OpenedDateChangeItem extends AbstractItem<DateTime> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitOpenedDateChange(this);
			}
		}

		public static final class ClosedDateChangeItem extends AbstractItem<DateTime> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitClosedDateChange(this);
			}
		}

		public static final class DueToDateChangeItem extends AbstractItem<DateTime> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitDueToDateChange(this);
			}
		}

		public static final class EstimatedTimeChangeItem extends AbstractItem<Duration> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitEstimatedTimeChange(this);
			}
		}

		public static final class TagsChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitTagsChange(this);
			}
		}


		public static final class ComponentsChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitComponentsChange(this);
			}
		}

		public static final class VersionsChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitVersionsChange(this);
			}
		}

		public static final class CommitsChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitCommitsChange(this);
			}
		}

		public static final class AssigneeChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitAssigneeChange(this);
			}
		}

		public static final class BlockedIssuesChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitBlockedIssuesChange(this);
			}
		}

		public static final class ChildIssuesChangeItem extends AbstractItem<String> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitChildIssuesChange(this);
			}
		}

		public static final class StatusChangeItem extends AbstractItem<Status> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitStatusChange(this);
			}
		}

		public static final class PriorityChangeItem extends AbstractItem<Priority> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitPriorityChange(this);
			}
		}

		public static final class SeverityChangeItem extends AbstractItem<Severity> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitSeverityChange(this);
			}
		}

		public static final class TypeChangeItem extends AbstractItem<Issue.Type> {
			@Override
			public void accept(final ItemVisitor visitor) {
				visitor.visitTypeChange(this);
			}
		}

		static final String TIMESTAMP="timestamp";
		static final String AUTHOR="author";
		static final String ITEMS="items";

		private DateTime timeStamp;
		private String author;
		private Set<Item> items;

		public Entry() {
			this.items=Sets.newLinkedHashSet();
		}

		public DateTime getTimeStamp() {
			return this.timeStamp;
		}

		public void setTimeStamp(final DateTime timeStamp) {
			this.timeStamp = timeStamp;
		}

		public String getAuthor() {
			return this.author;
		}

		public void setAuthor(final String author) {
			this.author = author;
		}

		public Set<Item> getItems() {
			return this.items;
		}

		public void setItems(final Set<Item> items) {
			this.items=items;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.timeStamp,this.author,this.items);
		}

		@Override
		public boolean equals(final Object obj) {
			boolean result = false;
			if(obj instanceof Entry) {
				final Entry that=(Entry)obj;
				result=
					Objects.equals(this.timeStamp,that.timeStamp) &&
					Objects.equals(this.author,that.author) &&
					Objects.equals(this.items,that.items) ;
			}
			return result;
		}

		@Override
		public int compareTo(final Entry that) {
			return
				ComparisonChain.
					start().
						compare(this.timeStamp,that.timeStamp).
						compare(this.author,that.author).
						compare(this.items.size(),that.items.size()).
						result();
		}

		@Override
		protected ToStringHelper stringHelper() {
			return
				super.stringHelper().
					add(TIMESTAMP,this.timeStamp).
					add(AUTHOR,this.author).
					add(ITEMS,this.items);
		}

	}

	static final String ENTRIES="entries";

	private SortedSet<Entry> entries;

	public ChangeLog() {
		this.entries=Sets.newTreeSet();
	}

	public Set<Entry> getEntries() {
		return this.entries;
	}

	public void setEntries(final Set<Entry> entries) {
		this.entries=Sets.newTreeSet(entries);
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add(ENTRIES,this.entries);
	}

}
