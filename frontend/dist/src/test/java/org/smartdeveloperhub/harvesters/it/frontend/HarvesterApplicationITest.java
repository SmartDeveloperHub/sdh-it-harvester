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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.testing.LDPUtil;
import org.smartdeveloperhub.harvesters.it.testing.QueryHelper;
import org.smartdeveloperhub.harvesters.it.testing.QueryHelper.ResultProcessor;
import org.smartdeveloperhub.harvesters.it.testing.SmokeTest;
import org.smartdeveloperhub.harvesters.it.testing.TestingUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@RunWith(Arquillian.class)
public class HarvesterApplicationITest {

	private static final String SERVICE = "ldp4j/api/service/";

	@Rule
	public TestName test=new TestName();

	@Deployment(name="default",testable=false)
	@TargetsContainer("tomcat")
	public static WebArchive createDeployment() throws Exception {
		return SmokeTest.createWebArchive("default-harvester.war");
	}

	@Test
	@OperateOnDeployment("default")
	public void checkTwoContributorsArePublished(@ArquillianResource final URL contextURL) throws Exception {
		final List<String> contributors = getContributors(contextURL);
		verifyElements(contributors);
	}

	@Test
	@OperateOnDeployment("default")
	public void checkTwoCommitsArePublished(@ArquillianResource final URL contextURL) throws Exception {
		final List<String> commits = getCommits(contextURL);
		verifyElements(commits);
	}

	@Test
	@OperateOnDeployment("default")
	public void checkTwoProjectsArePublished(@ArquillianResource final URL contextURL) throws Exception {
		final List<String> projects = getProjects(contextURL);
		assertThat(projects,hasSize(2));
		checkProject(projects.get(0));
		checkProject(projects.get(1));
	}

	private void checkProject(final String project) throws IOException {
		final List<String> components = queryResourceVariable(project, "queries/components.sparql", "component");
		verifyElements(components);
		final List<String> versions = queryResourceVariable(project, "queries/versions.sparql", "version");
		verifyElements(versions);
		final List<String> issues = queryResourceVariable(project, "queries/issues.sparql", "issue");
		verifyElements(issues);
	}

	private void verifyElements(final List<String> components) {
		assertThat(components,hasSize(2));
		LDPUtil.assertIsAccessible(components.get(0));
		LDPUtil.assertIsAccessible(components.get(1));
	}

	private static final List<String> getContributors(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/contributors.sparql", "contributor");
	}

	private static final List<String> getCommits(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/commits.sparql", "commit");
	}

	private static final List<String> getProjects(final URL contextURL) throws IOException {
		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/projects.sparql", "project");
	}

	private static List<String> queryResourceVariable(final String resource, final String query, final String variable) throws IOException {
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

}