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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;
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

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonPropertyOrder({
			Item.PROPERTY,
			Item.OLD_VALUE,
			Item.NEW_VALUE
		})
		public static final class Item extends Entity {

			static final String PROPERTY="property";
			static final String OLD_VALUE="oldValue";
			static final String NEW_VALUE="newValue";

			private String property;
			private Object oldValue;
			private Object newValue;

			public String getProperty() {
				return this.property;
			}

			public void setProperty(final String property) {
				this.property = property;
			}

			public Object getOldValue() {
				return this.oldValue;
			}

			public void setOldValue(final Object oldValue) {
				this.oldValue = oldValue;
			}

			public Object getNewValue() {
				return this.newValue;
			}

			public void setNewValue(final Object newValue) {
				this.newValue = newValue;
			}

			@Override
			public int hashCode() {
				return Objects.hash(this.property,this.oldValue,this.newValue);
			}

			@Override
			public boolean equals(final Object obj) {
				boolean result = false;
				if(obj instanceof Item) {
					final Item that=(Item)obj;
					result=
						Objects.equals(this.property,that.property) &&
						Objects.equals(this.oldValue,that.oldValue) &&
						Objects.equals(this.newValue,that.newValue) ;
				}
				return result;
			}

			@Override
			protected ToStringHelper stringHelper() {
				return
					super.stringHelper().
						add(PROPERTY,this.property).
						add(OLD_VALUE,this.oldValue).
						add(NEW_VALUE,this.newValue);
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
