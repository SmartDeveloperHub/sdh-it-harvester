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
package org.smartdeveloperhub.harvesters.it.frontend.vocabulary;


public final class PLATFORM {

	public static final String NAMESPACE = "http://www.smartdeveloperhub.org/vocabulary/platform#";
	public static final String PREFIX    = "platform";

	public static final String LINKED_DATA_MICRO_SERVICE_TYPE = term("LinkedDataMicroService");
	public static final String MICRO_SERVICE_TYPE             = term("MicroService");
	public static final String HARVESTER_TYPE                 = term("Harvester");
	public static final String VOCABULARY_TYPE                = term("Vocabulary");

	public static final String HARVESTER_VOCABULARY           = term("vocabulary");
	public static final String DOMAIN                         = term("domain");
	public static final String PROVIDES_DOMAIN                = term("providesDomain");
	public static final String HAS_RESOURCE_TYPE              = term("hasResourceType");
	public static final String HAS_RESOURCE                   = term("hasResource");
	public static final String SOURCE                         = term("source");
	public static final String IMPLEMENTS                     = term("implements");

	private PLATFORM() {
	}

	private static String term(final String localName) {
		return NAMESPACE+localName;
	}

}
