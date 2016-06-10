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

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	State.JIRA_API_VERSION,
	State.LAST_CRAWLING_DATE,
	State.STATUS_MAPPINGS,
	State.ACTIVITY
})
public final class State extends Entity {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({
		Activity.TIMESTAMP,
		Activity.ACTION,
		Activity.CATEGORY
	})
	public static final class Activity extends Entity {

		static final String TIMESTAMP = "timestamp";
		static final String ACTION    = "action";
		static final String CATEGORY  = "category";

		enum Category {
			FAILED,
			SUCCEDED
		}

		private DateTime timestamp;
		private String action;
		private Category category;

		public DateTime getTimestamp() {
			return this.timestamp;
		}

		public void setTimestamp(final DateTime timestamp) {
			this.timestamp = timestamp;
		}

		public String getAction() {
			return this.action;
		}

		public void setAction(final String action) {
			this.action = action;
		}

		public Category getCategory() {
			return this.category;
		}

		public void setCategory(final Category category) {
			this.category = category;
		}

		@Override
		protected ToStringHelper stringHelper() {
			return
				super.
					stringHelper().
						add(TIMESTAMP,this.timestamp).
						add(ACTION,this.action).
						add(CATEGORY,this.category);
		}

	}

	static final String JIRA_API_VERSION   = "jiraApiVersion";
	static final String LAST_CRAWLING_DATE = "lastCrawlingDate";
	static final String STATUS_MAPPINGS    = "statusMappings";
	static final String ACTIVITY           = "activity";

	private String jiraApiVersion;
	private DateTime lastCrawlingDate;
	private Map<String,String> statusMappings;
	private List<Activity> activity;

	public State() {
		this.activity=Lists.newArrayList();
		this.statusMappings=Maps.newLinkedHashMap();
	}

	public String getJiraApiVersion() {
		return this.jiraApiVersion;
	}

	public void setJiraApiVersion(final String jiraApiVersion) {
		this.jiraApiVersion = jiraApiVersion;
	}

	public DateTime getLastCrawlingDate() {
		return this.lastCrawlingDate;
	}

	public void setLastCrawlingDate(final DateTime lastCrawlingDate) {
		this.lastCrawlingDate = lastCrawlingDate;
	}

	public Map<String, String> getStatusMappings() {
		return this.statusMappings;
	}

	public void setStatusMappings(final Map<String, String> statusMappings) {
		this.statusMappings = statusMappings;
	}

	public List<Activity> getActivity() {
		return this.activity;
	}

	public void setActivity(final List<Activity> activity) {
		this.activity = activity;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.
				stringHelper().
					add(JIRA_API_VERSION,this.jiraApiVersion).
					add(LAST_CRAWLING_DATE,this.lastCrawlingDate).
					add(STATUS_MAPPINGS,this.statusMappings).
					add(ACTIVITY,this.activity);
	}
}
