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
import static org.smartdeveloperhub.testing.hamcrest.RDFMatchers.hasTriple;
import static org.smartdeveloperhub.testing.hamcrest.References.property;
import static org.smartdeveloperhub.testing.hamcrest.References.typedLiteral;
import static org.smartdeveloperhub.testing.hamcrest.References.uriRef;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.frontend.testing.TestingService;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.Activity;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ActivityListener;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.MoreHandlers.APIVersion;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.IT;
import org.smartdeveloperhub.harvesters.it.testing.LDPUtil;
import org.smartdeveloperhub.harvesters.it.testing.SmokeTest;
import org.smartdeveloperhub.harvesters.it.testing.TestingUtil;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.jayway.restassured.response.Response;

@RunWith(Arquillian.class)
public class RemoteHarvesterApplicationITest {

	private static final String XML_SCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";
	private static TestingService service;

	private static TestingService startMockService() throws IOException {
		return
			TestingService.
				builder().
					port(port()).
					apiVersion(APIVersion.v1).
					exchangeName("it.collector.mock").
					listener(new ActivityListener() {
						@Override
						public void onActivity(final Activity<?> activity) {
							try {
								System.out.println(Entities.marshallEntity(activity));
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}).
					build().
					start();
	}

	private static int port() {
		final String rawPort = System.getProperty("undertow.http.port","8080");
		final int port = Integer.parseInt(rawPort);
		return port;
	}

	@Deployment(name="default",testable=false)
	@TargetsContainer("tomcat")
	public static WebArchive createDeployment() throws Exception {
		service=startMockService();
		return SmokeTest.remotelyBackedCollector("default-harvester.war");
	}

	@AfterClass
	public static void tearDown() {
		if(service!=null) {
			service.shutdown();
		}
	}

	@Rule
	public TestName test=new TestName();

	@Test
	@OperateOnDeployment("default")
	public void contributorsCanBeCreatedDinamically(@ArquillianResource final URL contextURL) throws Exception {
		createContributors(contextURL,HarvesterTester.getContributors(contextURL));
	}

	@Test
	@OperateOnDeployment("default")
	public void commitsCanBeCreatedDinamically(@ArquillianResource final URL contextURL) throws Exception {
		createCommits(contextURL,HarvesterTester.getCommits(contextURL));
	}

	private List<String> createContributors(final URL contextURL, final List<String> originalContributors) throws Exception {
		createContributor();
		System.out.println("Verifying contributor availability...");
		final List<String> afterCreatingContributors = Lists.newArrayList(HarvesterTester.getContributors(contextURL));
		afterCreatingContributors.removeAll(originalContributors);
		assertThat(afterCreatingContributors,hasSize(1));
		contributorHasIdentifier(afterCreatingContributors.get(0),contributorId());
		return afterCreatingContributors;
	}

	private void createContributor() throws InterruptedException {
		final Contributor contributor = new Contributor();
		contributor.setId(contributorId());
		contributor.setName(contributorId()+" Name");
		contributor.getEmails().add(contributorId()+"@example.org");
		service.createContributors(contributor);
		System.out.println("Created contributor "+contributorId()+". Awaiting frontend update");
		TimeUnit.SECONDS.sleep(2);
	}

	private void contributorHasIdentifier(final String committer, final String id) {
		final Response response = LDPUtil.assertIsAccessible(committer);
		final Model model = TestingUtil.asModel(response,committer);
		assertThat(
			model,
			hasTriple(
				uriRef(committer),
				property(IT.CONTRIBUTOR_ID),
				typedLiteral(id,XML_SCHEMA_STRING)));
	}

	private String contributorId() {
		return this.test.getMethodName();
	}

	private List<String> createCommits(final URL contextURL, final List<String> originalCommits) throws Exception {
		createCommit();
		System.out.println("Verifying commit availability...");
		final List<String> afterCreatingContributors = Lists.newArrayList(HarvesterTester.getCommits(contextURL));
		afterCreatingContributors.removeAll(originalCommits);
		assertThat(afterCreatingContributors,hasSize(1));
		commitHasIdentifier(afterCreatingContributors.get(0),commitId());
		return afterCreatingContributors;
	}

	private void createCommit() throws InterruptedException {
		final Commit commit = new Commit();
		commit.setId(contributorId());
		commit.setRepository("repository/"+commitId());
		commit.setBranch("branch "+commitId());
		commit.setHash("hash "+commitId());
		service.createCommits(commit);
		System.out.println("Created commit "+commitId()+". Awaiting frontend update");
		TimeUnit.SECONDS.sleep(2);
	}

	private void commitHasIdentifier(final String commit, final String id) {
		final Response response = LDPUtil.assertIsAccessible(commit);
		final Model model = TestingUtil.asModel(response,commit);
		assertThat(
			model,
			hasTriple(
				uriRef(commit),
				property(IT.COMMIT_ID),
				typedLiteral(id,XML_SCHEMA_STRING)));
	}

	private String commitId() {
		return this.test.getMethodName();
	}

}