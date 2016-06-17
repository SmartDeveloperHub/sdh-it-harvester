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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.smartdeveloperhub.harvesters.it.testing.LDPUtil;
import org.smartdeveloperhub.harvesters.it.testing.QueryHelper;
import org.smartdeveloperhub.harvesters.it.testing.QueryHelper.ResultProcessor;
import org.smartdeveloperhub.harvesters.it.testing.TestingUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

final class HarvesterTester {

	private static final String SERVICE = "ldp4j/api/service/";

	private HarvesterTester() {
	}

	static List<String> queryResourceVariable(final String resource, final String query, final String variable) throws IOException {
		return
			QueryHelper.
				newInstance().
					withModel(
						TestingUtil.
							asModel(
								LDPUtil.assertIsAccessible(resource),
								resource)).
					withQuery().
						fromResource(query).
						withURIRefParam("service",resource).
					select(
						new ResultProcessor<List<String>>() {
							private final List<String> bindings=Lists.newArrayList();
							@Override
							protected void processSolution() {
								this.bindings.add(resource(variable).getURI());
							}
							@Override
							public List<String> getResult() {
								return ImmutableList.copyOf(this.bindings);
							}
						}
					);
	}

	static final List<String> getContributors(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,HarvesterTester.SERVICE), "queries/contributors.sparql", "contributor");
	}

	static final List<String> getCommits(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,HarvesterTester.SERVICE), "queries/commits.sparql", "commit");
	}

	static final List<String> getProjects(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,HarvesterTester.SERVICE), "queries/projects.sparql", "project");
	}

}
