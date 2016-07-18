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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.2.0-SNAPSHOT
 *   Bundle      : it-backend-api-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Collector.VERSION,
	Collector.NOTIFICATIONS
})
public final class Collector extends Entity {

	static final String VERSION = "version";
	static final String NOTIFICATIONS = "notifications";

	@JsonProperty(VERSION)
	private String version;

	@JsonProperty(NOTIFICATIONS)
	private Notifications notifications;

	/**
	 * Get the collector's API version
	 *
	 * @return the version
	 */
	@JsonProperty(VERSION)
	public String getVersion() {
		return this.version;
	}

	/**
	 * Set the collector's API version
	 *
	 * @param instance
	 *            the collector's API version
	 */
	@JsonProperty(VERSION)
	public void setVersion(final String instance) {
		this.version = instance;
	}

	/**
	 * Get the configuration used by the collector for the notification
	 * mechanism.
	 *
	 * @return the notification mechanism configuration
	 */
	@JsonProperty(NOTIFICATIONS)
	public Notifications getNotifications() {
		return this.notifications;
	}

	/**
	 * Set the configuration used by the collector for the configuration
	 * mechanism.
	 *
	 * @param notifications
	 *            the notification mechanism configuration
	 */
	@JsonProperty(NOTIFICATIONS)
	public void setNotifications(final Notifications notifications) {
		this.notifications = notifications;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add(VERSION,this.version).
				add(NOTIFICATIONS,this.notifications);
	}

}
