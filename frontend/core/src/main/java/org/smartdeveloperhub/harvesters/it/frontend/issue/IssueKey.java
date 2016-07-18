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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public final class IssueKey implements Comparable<IssueKey>, Serializable {

	private static final long serialVersionUID = 786423257542885122L;

	private final String projectId;
	private final String issueId;

	public IssueKey(final String projectId, final String issueId) {
		this.projectId = projectId;
		this.issueId = issueId;
	}

	public String getProjectId() {
		return this.projectId;
	}

	public String getIssueId() {
		return this.issueId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.projectId,this.issueId);
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result=false;
		if(obj instanceof IssueKey) {
			final IssueKey that=(IssueKey)obj;
			result=
				Objects.equals(this.projectId,that.projectId) &&
				Objects.equals(this.issueId,that.issueId);
		}
		return result;
	}

	@Override
	public int compareTo(final IssueKey key) {
		int result=this.projectId.compareTo(key.projectId);
		if(result==0) {
			result=this.issueId.compareTo(key.issueId);
		}
		return result;
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					omitNullValues().
					add("projectId",this.projectId).
					add("issueId", this.issueId).
					toString();
	}

}
