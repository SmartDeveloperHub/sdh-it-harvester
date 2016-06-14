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

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HarvesterApplicationITestToBe {

//	private static final String SERVICE = "ldp4j/api/service/";
//
////	private static TestingService service;
////
////	private static TestingService startMockService() throws IOException {
////		final String property = System.getProperty("undertow.http.port","8080");
////		return
////			TestingService.
////				builder().
////					port(Integer.parseInt(property)).
////					build().
////						start();
////	}
//
//	@Rule
//	public TestName test=new TestName();
//
//	@Deployment(name="default",testable=false)
//	@TargetsContainer("tomcat")
//	public static WebArchive createDeployment() throws Exception {
////		service=startMockService();
//		return SmokeTest.createWebArchive("default-harvester.war");
//	}
//
//	@AfterClass
//	public static void tearDown() {
////		if(service!=null) {
////			service.shutdown();
////		}
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testCommitterCreation(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter(contextURL, getCommitters(contextURL));
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testCommitterDeletion(@ArquillianResource final URL contextURL) throws Exception {
//		final List<String> originalCommitters =
//				getCommitters(contextURL);
//
//		final List<String> afterCreatingCommitters =
//				createCommitter(contextURL, originalCommitters);
//
//		deleteCommitter();
//
//		System.out.println("Verifying committer availability...");
//		final List<String> finalCommitters = Lists.newArrayList(getCommitters(contextURL));
//		finalCommitters.removeAll(originalCommitters);
//		assertThat(finalCommitters,hasSize(0));
//		LDPUtil.assertIsGone(afterCreatingCommitters.get(0));
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testRepositoryCreation(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//		createRepository(
//			contextURL,
//			getRepositories(contextURL));
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testRepositoryDeletion(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//
//		final List<String> originalRepositories = getRepositories(contextURL);
//
//		final List<String> afterCreatingRepositories = createRepository(contextURL,originalRepositories);
//
//		deleteRepository();
//
//		System.out.println("Verifying repository availability...");
//		final List<String> finalRepositories = Lists.newArrayList(getRepositories(contextURL));
//		finalRepositories.removeAll(originalRepositories);
//		assertThat(finalRepositories,hasSize(0));
//		LDPUtil.assertIsGone(afterCreatingRepositories.get(0));
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testBranchCreation(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//
//		final List<String> createdRepositories=
//				createRepository(contextURL,getRepositories(contextURL));
//
//		final List<String> originalBranches =
//				Lists.newArrayList(getBranches(createdRepositories.get(0)));
//
//		createBranch();
//
//		System.out.println("Verifying branch availability...");
//		final List<String> finalBranches = Lists.newArrayList(getBranches(createdRepositories.get(0)));
//		finalBranches.removeAll(originalBranches);
//		assertThat(finalBranches,hasSize(1));
//		branchHasName(finalBranches.get(0),service.getBranch(repositoryId(),branchId()).getName());
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testBranchDeletion(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//
//		final List<String> createdRepositories=
//				createRepository(contextURL,getRepositories(contextURL));
//
//		final List<String> originalBranches =
//				Lists.newArrayList(getBranches(createdRepositories.get(0)));
//
//		createBranch();
//
//		System.out.println("Verifying branch availability...");
//		final List<String> afterCreatingBranches = Lists.newArrayList(getBranches(createdRepositories.get(0)));
//		afterCreatingBranches.removeAll(originalBranches);
//		assertThat(afterCreatingBranches,hasSize(1));
//		branchHasName(afterCreatingBranches.get(0),service.getBranch(repositoryId(),branchId()).getName());
//
//		deleteBranch();
//
//		System.out.println("Verifying branch availability...");
//		final List<String> finalBranches = Lists.newArrayList(getBranches(createdRepositories.get(0)));
//		finalBranches.removeAll(originalBranches);
//		assertThat(finalBranches,hasSize(0));
//		LDPUtil.assertIsGone(afterCreatingBranches.get(0));
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testCommitCreation(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//
//		final List<String> createdRepositories=
//				createRepository(contextURL,getRepositories(contextURL));
//
//		final List<String> originalCommits =
//				Lists.newArrayList(getCommits(createdRepositories.get(0)));
//
//		createCommit();
//
//		System.out.println("Verifying commit availability...");
//		final List<String> finalCommits = Lists.newArrayList(getCommits(createdRepositories.get(0)));
//		finalCommits.removeAll(originalCommits);
//		assertThat(finalCommits,hasSize(1));
//		commitHasIdentifier(finalCommits.get(0),commitId());
//	}
//
//	@Test
//	@OperateOnDeployment("default")
//	public void testCommitDeletion(@ArquillianResource final URL contextURL) throws Exception {
//		createCommitter();
//
//		final List<String> createdRepositories=
//				createRepository(contextURL,getRepositories(contextURL));
//
//		final List<String> originalCommits =
//				Lists.newArrayList(getCommits(createdRepositories.get(0)));
//
//		createCommit();
//
//		System.out.println("Verifying commit availability...");
//		final List<String> afterCreatingCommits = Lists.newArrayList(getCommits(createdRepositories.get(0)));
//		afterCreatingCommits.removeAll(originalCommits);
//		assertThat(afterCreatingCommits,hasSize(1));
//		commitHasIdentifier(afterCreatingCommits.get(0),commitId());
//
//		deleteCommit();
//
//		System.out.println("Verifying commit availability...");
//		final List<String> finalCommits = Lists.newArrayList(getCommits(createdRepositories.get(0)));
//		finalCommits.removeAll(originalCommits);
//		assertThat(finalCommits,hasSize(0));
//		LDPUtil.assertIsGone(afterCreatingCommits.get(0));
//	}
//
//	private String committerId() {
//		return this.test.getMethodName();
//	}
//
//	private String repositoryId() {
//		return this.test.getMethodName();
//	}
//
//	private String branchId() {
//		return this.test.getMethodName();
//	}
//
//	private String commitId() {
//		return String.format("c%08X",this.test.getMethodName().hashCode());
//	}
//
//	private List<String> createCommitter(final URL contextURL, final List<String> originalCommitters) throws InterruptedException, IOException {
//		createCommitter();
//
//		System.out.println("Verifying committer availability...");
//		final List<String> afterCreatingCommitters = Lists.newArrayList(getCommitters(contextURL));
//		afterCreatingCommitters.removeAll(originalCommitters);
//		assertThat(afterCreatingCommitters,hasSize(1));
//		commiterHasIdentifier(afterCreatingCommitters.get(0),committerId());
//		return afterCreatingCommitters;
//	}
//
//	private List<String> createRepository(final URL contextURL, final List<String> originalRepositories) throws InterruptedException, IOException {
//		createRepository();
//
//		System.out.println("Verifying repository availability...");
//		final List<String> newRepositories = Lists.newArrayList(getRepositories(contextURL));
//		newRepositories.removeAll(originalRepositories);
//		assertThat(newRepositories,hasSize(1));
//		repositoryHasIdentifier(newRepositories.get(0),repositoryId());
//		return newRepositories;
//	}
//
//	private void createRepository() throws InterruptedException {
//		final RepositoryCreatedEvent rEvent=new RepositoryCreatedEvent();
//		rEvent.getNewRepositories().add(repositoryId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Created repository "+repositoryId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void deleteRepository() throws InterruptedException {
//		final RepositoryDeletedEvent rEvent=new RepositoryDeletedEvent();
//		rEvent.getDeletedRepositories().add(repositoryId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Deleted repository "+repositoryId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void createCommitter() throws InterruptedException {
//		final CommitterCreatedEvent event = new CommitterCreatedEvent();
//		event.getNewCommitters().add(committerId());
//		final UpdateReport report = service.update(event);
//		assumeThat(report.notificationSent(),equalTo(true));
//		System.out.println("Created committer "+committerId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void deleteCommitter() throws InterruptedException {
//		final CommitterDeletedEvent event = new CommitterDeletedEvent();
//		event.getDeletedCommitters().add(committerId());
//		final UpdateReport report = service.update(event);
//		assumeThat(report.notificationSent(),equalTo(true));
//		System.out.println("Deleted committer "+committerId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void createBranch() throws InterruptedException {
//		final RepositoryUpdatedEvent rEvent=new RepositoryUpdatedEvent();
//		rEvent.setRepository(repositoryId());
//		rEvent.getNewBranches().add(branchId());
//		rEvent.getContributors().add(committerId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Created branch "+branchId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void deleteBranch() throws InterruptedException {
//		final RepositoryUpdatedEvent rEvent=new RepositoryUpdatedEvent();
//		rEvent.setRepository(repositoryId());
//		rEvent.getDeletedBranches().add(branchId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Deleted branch "+branchId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void createCommit() throws InterruptedException {
//		final RepositoryUpdatedEvent rEvent=new RepositoryUpdatedEvent();
//		rEvent.setRepository(repositoryId());
//		rEvent.getNewCommits().add(commitId());
//		rEvent.getContributors().add(committerId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Created commit "+commitId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void deleteCommit() throws InterruptedException {
//		final RepositoryUpdatedEvent rEvent=new RepositoryUpdatedEvent();
//		rEvent.setRepository(repositoryId());
//		rEvent.getDeletedCommits().add(commitId());
//		final UpdateReport rReport = service.update(rEvent);
//		assumeThat(rReport.notificationSent(),equalTo(true));
//		System.out.println("Deleted commit "+commitId()+". Awaiting frontend update");
//		TimeUnit.SECONDS.sleep(2);
//	}
//
//	private void commiterHasIdentifier(final String committer, final String id) {
//		final Response response = LDPUtil.assertIsAccessible(committer);
//		final Model model = TestingUtil.asModel(response,committer);
//		assertThat(
//			model,
//			hasTriple(
//				uriRef(committer),
//				property("http://www.smartdeveloperhub.org/vocabulary/scm#committerId"),
//				typedLiteral(id,"http://www.w3.org/2001/XMLSchema#string")));
//	}
//
//	private void repositoryHasIdentifier(final String resource, final String id) {
//		final Response response = LDPUtil.assertIsAccessible(resource);
//		final Model model = TestingUtil.asModel(response,resource);
//		assertThat(
//			model,
//			hasTriple(
//				uriRef(resource),
//				property("http://www.smartdeveloperhub.org/vocabulary/scm#repositoryId"),
//				typedLiteral(id,"http://www.w3.org/2001/XMLSchema#string")));
//	}
//
//	private void branchHasName(final String resource, final String name) {
//		final Response response = LDPUtil.assertIsAccessible(resource);
//		final Model model = TestingUtil.asModel(response,resource);
//		assertThat(
//			model,
//			hasTriple(
//				uriRef(resource),
//				property("http://usefulinc.com/ns/doap#name"),
//				typedLiteral(name,"http://www.w3.org/2001/XMLSchema#string")));
//	}
//
//	private void commitHasIdentifier(final String commit, final String id) {
//		final Response response = LDPUtil.assertIsAccessible(commit);
//		final Model model = TestingUtil.asModel(response,commit);
//		assertThat(
//			model,
//			hasTriple(
//				uriRef(commit),
//				property("http://www.smartdeveloperhub.org/vocabulary/scm#commitId"),
//				typedLiteral(id,"http://www.w3.org/2001/XMLSchema#string")));
//	}
//
//	private static final List<String> getCommitters(final URL contextURL) throws IOException {
//		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/committers.sparql", "committer");
//	}
//
//	private static final List<String> getRepositories(final URL contextURL) throws IOException {
//		return queryResourceVariable(TestingUtil.resolve(contextURL,SERVICE), "queries/repositories.sparql", "repository");
//	}
//
//	private static final List<String> getBranches(final String resource) throws IOException {
//		return queryResourceVariable(resource, "queries/branches.sparql", "branch");
//	}
//
//	private static final List<String> getCommits(final String resource) throws IOException {
//		return queryResourceVariable(resource, "queries/commits.sparql", "commit");
//	}
//
//	private static List<String> queryResourceVariable(final String resource, final String query, final String variable) throws IOException {
//		return
//			QueryHelper.
//				newInstance().
//					withModel(
//						TestingUtil.
//							asModel(
//								LDPUtil.assertIsAccessible(resource),
//								resource)).
//					withQuery().
//						fromResource(query).
//						withURIRefParam("service",resource).
//					select(
//						new ResultProcessor<List<String>>() {
//							private final List<String> bindings=Lists.newArrayList();
//							@Override
//							protected void processSolution() {
//								this.bindings.add(resource(variable).getURI());
//							}
//							@Override
//							public List<String> getResult() {
//								return ImmutableList.copyOf(this.bindings);
//							}
//						}
//					);
//	}
//
}