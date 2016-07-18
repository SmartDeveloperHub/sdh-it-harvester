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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Event.INSTANCE,
	Event.TIMESTAMP,
	ProjectUpdatedEvent.PROJECT,
	ProjectUpdatedEvent.CHANGES,
})
public class ProjectUpdatedEvent extends Event {

	public enum Entity {
		COMPONENT,
		VERSION,
		ISSUE
	}

	public enum Action {
		CREATED,
		UPDATED,
		DELETED
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		Change.ACTION,
		Change.AUTHORS
	})
	public static class Change {

		static final String ACTION   = "action";
		static final String AUTHORS  = "authors";

		@JsonProperty(ACTION)
		private Action action;

		@JsonProperty(AUTHORS)
		private List<String> authors;

		/**
		 * Create a new empty change
		 */
		public Change() {
			setAuthors(Lists.<String>newArrayList());
		}

		private Change(final Action action, final List<String> authors) {
			setAction(action);
			setAuthors(authors);
		}

		/**
		 * Get the action of this change
		 *
		 * @return the action of this change
		 */
		public Action getAction() {
			return this.action;
		}

		/**
		 * Set the action of this change
		 *
		 * @param action
		 *            the action for this change
		 */
		public void setAction(final Action action) {
			this.action = action;
		}

		/**
		 * Get the authors of this change
		 *
		 * @return the identities of the authors of this change
		 */
		public List<String> getAuthors() {
			return this.authors;
		}

		/**
		 * Set the authors of this change
		 *
		 * @param authors
		 *            the identities of the authors of this change
		 */
		public void setAuthors(final List<String> authors) {
			this.authors = authors;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.action,this.authors);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {
			boolean result = false;
			if(obj instanceof Change) {
				final Change that=(Change)obj;
				result=
					Objects.equals(this.action, that.action) &&
					Objects.equals(this.authors, that.authors);
			}
			return result;
		}

		/**
		 * Create a change with the specified action and authors
		 *
		 * @param action
		 *            the action for the change
		 * @param authors
		 *            the authors of the change
		 * @return a populated change instance
		 */
		public static Change create(final Action action, final String... authors) {
			List<String> cAuthors=Lists.newArrayList();
			if(authors!=null) {
				cAuthors=ImmutableList.copyOf(Sets.newHashSet(authors));
			}
			return new Change(action,cAuthors);
		}

	}

	static final String PROJECT = "project";
	static final String CHANGES = "changes";

	@JsonProperty(PROJECT)
	private String project;

	@JsonProperty(CHANGES)
	private Map<Entity,Map<String,List<Change>>> changes;

	@JsonIgnore
	private int size;

	/**
	 * Create a new instance with no changes.
	 */
	public ProjectUpdatedEvent() {
		this.size=0;
		this.changes=Maps.newLinkedHashMap();
		for(final Entity entity:Entity.values()) {
			this.changes.put(entity,Maps.<String, List<Change>>newLinkedHashMap());
		}
	}

	/**
	 * Get the identifier of the updated project
	 *
	 * @return The project identifier
	 */
	@JsonProperty(PROJECT)
	public String getProject() {
		return this.project;
	}

	/**
	 * Set the identifier of the updated project
	 *
	 * @param project
	 *            The identifier of the project
	 */
	@JsonProperty(PROJECT)
	public void setProject(final String project) {
		this.project = project;
	}

	/**
	 * Get the changes that happened to the project
	 *
	 * @return The project changes
	 */
	@JsonProperty(CHANGES)
	public Map<Entity, Map<String, List<Change>>> getChanges() {
		return this.changes;
	}

	/**
	 * Set the changes that happened to the project project
	 *
	 * @param changes
	 *            The identifier of the project
	 */
	@JsonProperty(CHANGES)
	public void setChanges(final Map<Entity, Map<String, List<Change>>> changes) {
		this.changes = changes;
		refreshSize(changes);
	}

	/**
	 * Add a new modification to the event.
	 *
	 * @param modification
	 *            the modification to be added
	 * @return the updated event
	 */
	public ProjectUpdatedEvent append(final Modification modification) {
		modification.attach(this);
		this.size++;
		return this;
	}

	/**
	 * Returns the number of changes in this event. If the event contains more
	 * than <tt>Long.MAX_VALUE</tt> elements, returns <tt>Long.MAX_VALUE</tt>.
	 *
	 * @return the number of changes in this event
	 */
	public long size() {
		this.changes.size();
		return this.size;
	}

	/**
	 * Returns <tt>true</tt> if this event contains no changes.
	 *
	 * @return <tt>true</tt> if this event contains no changes
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return this.size==0;
	}

	private void refreshSize(final Map<Entity, Map<String, List<Change>>> changes) {
		for(final Map<String,List<Change>> entityChanges:changes.values()) {
			for(final List<Change> entityActionChanges:entityChanges.values()) {
				this.size+=entityActionChanges.size();
			}
		}
	}

}
