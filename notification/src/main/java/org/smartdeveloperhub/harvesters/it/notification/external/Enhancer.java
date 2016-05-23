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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification.external;

import java.util.ArrayList;
import java.util.List;

import org.smartdeveloperhub.harvesters.it.notification.event.Extensible;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Enhancer.NAME,
	Enhancer.VERSION,
	Enhancer.STATUS,
	Enhancer.COLLECTORS
})
public class Enhancer extends Extensible {

	static final String STATUS     = "Status";
	static final String NAME       = "Name";
	static final String VERSION    = "Version";
	static final String COLLECTORS = "Collectors";

	@JsonIgnore
	private String id;

	@JsonProperty(STATUS)
	private String status;

	@JsonProperty(NAME)
	private String name;

	@JsonProperty(VERSION)
	private String version;

	@JsonProperty(COLLECTORS)
	private List<Collector> collectors = new ArrayList<>();

	@JsonIgnore
	private List<String> repositories = new ArrayList<>();

	@JsonIgnore
	private List<String> users = new ArrayList<>();


	/**
	 * Get the identifier of the enhancer
	 *
	 * @return The identifier
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set the identifier of the enhancer
	 *
	 * @param id
	 *            The identifier of the enhancer
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Get the status of the enhancer
	 *
	 * @return The status
	 */
	@JsonProperty(STATUS)
	public String getStatus() {
		return this.status;
	}

	/**
	 * Set the status of the enhancer
	 *
	 * @param status
	 *            The status of the enhancer
	 */
	@JsonProperty(STATUS)
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * Get the name of the enhancer
	 *
	 * @return The name
	 */
	@JsonProperty(NAME)
	public String getName() {
		return this.name;
	}

	/**
	 * Set the enhancer name
	 *
	 * @param name
	 *            The name
	 */
	@JsonProperty(NAME)
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the name of the enhancer
	 *
	 * @return The name
	 */
	@JsonProperty(VERSION)
	public String getVersion() {
		return this.version;
	}

	/**
	 * Set the enhancer version
	 *
	 * @param version
	 *            The version
	 */
	@JsonProperty(VERSION)
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * Get the configurations of the collectors used by the enhancer
	 *
	 * @return The collectors' configuration
	 */
	@JsonProperty(COLLECTORS)
	public List<Collector> getCollectors() {
		return this.collectors;
	}

	/**
	 * Set the configurations of the collectors used by the enhancer
	 *
	 * @param collectors
	 *            The collectors' configuration
	 */
	@JsonProperty(COLLECTORS)
	public void setCollectors(final List<Collector> collectors) {
		this.collectors = collectors;
	}

	/**
	 * Get the identifiers of the repositories available in the enhancer
	 * instance
	 *
	 * @return The identifiers of the available repositories
	 */
	public List<String> getRepositories() {
		return this.repositories;
	}

	/**
	 * Set the identifiers of the repositories available in the enhancer
	 *
	 * @param repositories
	 *            The identifiers of the available repositories
	 */
	public void setRepositories(final List<String> repositories) {
		this.repositories = repositories;
	}

	/**
	 * Get the identifiers of the users available in the enhancer instance
	 *
	 * @return The identifiers of the available users
	 */
	public List<String> getUsers() {
		return this.users;
	}

	/**
	 * Set the identifiers of the users available in the enhancer
	 *
	 * @param users
	 *            The identifiers of the available users
	 */
	public void setUsers(final List<String> users) {
		this.users = users;
	}

}
