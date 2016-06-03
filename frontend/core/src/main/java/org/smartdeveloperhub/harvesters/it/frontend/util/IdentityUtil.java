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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.util;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.net.URI;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.session.ResourceSnapshot;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentKey;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueKey;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionKey;

public final class IdentityUtil {

	private IdentityUtil() {
	}

	private static <T> T extractNameId(final Name<?> name, final String entityName, final Class<? extends T> idClazz) {
		final Serializable id=name.id();
		checkState(idClazz.isInstance(id),"%s identifier should be a %s not a %s",entityName,idClazz.getName(),id.getClass().getCanonicalName());
		return idClazz.cast(id);
	}

	public static Name<URI> collectorName(final URI target) {
		return NamingScheme.getDefault().name(target);
	}

	public static Name<String> contributorName(final String contributorId) {
		return NamingScheme.getDefault().name(contributorId);
	}

	public static String contributorId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Contributor",String.class);
	}

	public static Name<String> commitName(final String commitId) {
		return NamingScheme.getDefault().name(commitId);
	}

	public static String commitId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Commit",String.class);
	}

	public static Name<String> projectName(final String projectId) {
		return NamingScheme.getDefault().name(projectId);
	}

	public static String projectId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Repository",String.class);
	}

	public static Name<ComponentKey> componentName(final ComponentKey key) {
		return NamingScheme.getDefault().name(key);
	}

	public static ComponentKey componentId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Component",ComponentKey.class);
	}

	public static Name<VersionKey> versionName(final VersionKey key) {
		return NamingScheme.getDefault().name(key);
	}

	public static VersionKey versionId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Version",VersionKey.class);
	}

	public static Name<IssueKey> issueName(final IssueKey key) {
		return NamingScheme.getDefault().name(key);
	}

	public static IssueKey issueId(final ResourceSnapshot resource) {
		return extractNameId(resource.name(),"Issue",IssueKey.class);
	}

}
