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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Identifiable.ID,
	Commit.REPOSITORY,
	Commit.BRANCH,
	Commit.HASH
})
public final class Commit extends Identifiable<String> {

	static final String REPOSITORY="repository";
	static final String BRANCH="branch";
	static final String HASH="hash";

	private String repository;
	private String branch;
	private String hash;

	public String getRepository() {
		return this.repository;
	}

	public void setRepository(final String repository) {
		this.repository = repository;
	}

	public String getBranch() {
		return this.branch;
	}

	public void setBranch(final String branch) {
		this.branch = branch;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(final String hash) {
		this.hash = hash;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add("repository",this.repository).
				add("branch",this.branch).
				add("hash",this.hash);
	}

}
