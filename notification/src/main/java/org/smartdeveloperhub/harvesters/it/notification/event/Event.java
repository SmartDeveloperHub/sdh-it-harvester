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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

	static final String INSTANCE  = "instance";
	static final String TIMESTAMP = "timestamp";

	@JsonProperty(INSTANCE)
	private String instance;

	@JsonProperty(TIMESTAMP)
	private Long timestamp;

	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<>();


	/**
	 * Get the identifier of the instance that produced the event
	 *
	 * @return The identifier of the instance
	 */
	@JsonProperty(INSTANCE)
	public String getInstance() {
		return this.instance;
	}

	/**
	 * Set the identifier of the instance that produced the event
	 *
	 * @param id
	 *            The identifier of the instance
	 */
	@JsonProperty(INSTANCE)
	public void setInstance(final String id) {
		this.instance = id;
	}

	/**
	 * Get the Unix/POSIX time of the event
	 *
	 * @return The timestamp
	 */
	@JsonProperty(TIMESTAMP)
	public Long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Set the Unix/POSIX time of the event
	 *
	 * @param timestamp
	 *            The timestamp
	 */
	@JsonProperty(TIMESTAMP)
	public void setTimestamp(final Long timestamp) {
		this.timestamp = timestamp;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}