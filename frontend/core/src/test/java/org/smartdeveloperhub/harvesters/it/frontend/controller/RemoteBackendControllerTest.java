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
package org.smartdeveloperhub.harvesters.it.frontend.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.smartdeveloperhub.harvesters.it.backend.Collector;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entity;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

public class RemoteBackendControllerTest {

	private static final URI BASE = URI.create("http://www.example.org:5000/controller");

	private static final URI NORMALIZED = URI.create("http://www.example.org:5000/controller/");

	private RemoteBackendController sut;

	@Before
	public void setUp() {
		this.sut = new RemoteBackendController(BASE);
	}

	@Test
	public void keepsOriginalTarget$nonNormalized() {
		assertThat(new RemoteBackendController(BASE).getTarget(),equalTo(BASE));
	}

	@Test
	public void keepsOriginalTarget$normalized() {
		assertThat(new RemoteBackendController(NORMALIZED).getTarget(),equalTo(NORMALIZED));
	}

	@Test
	public void usesProperCollectorEndpoint(@Mocked final Collector entity) throws Exception {
		setUpEntityClient(entity, Collector.class, "api");
		final Collector result = this.sut.getCollector();
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperStateEndpoint(@Mocked final State entity) throws Exception {
		setUpEntityClient(entity, State.class, "api/state");
		final State result = this.sut.getState();
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperCommitsEndpoint(@Mocked final List<String> entity) throws Exception {
		setUpListClient(entity,"api/commits");
		final List<String> result = this.sut.getCommits();
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperContributorsEndpoint(@Mocked final List<String> entity) throws Exception {
		setUpListClient(entity,"api/contributors");
		final List<String> result = this.sut.getContributors();
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperProjectsEndpoint(@Mocked final List<String> entity) throws Exception {
		setUpListClient(entity,"api/projects");
		final List<String> result = this.sut.getProjects();
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperContributorEndpoint(@Mocked final Contributor entity) throws Exception {
		setUpEntityClient(entity,Contributor.class,"api/contributors/%s","id");
		final Contributor result = this.sut.getContributor("id");
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperCommitEndpoint(@Mocked final Commit entity) throws Exception {
		setUpEntityClient(entity,Commit.class,"api/commits/%s","id");
		final Commit result = this.sut.getCommit("id");
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperProjectEndpoint(@Mocked final Project entity) throws Exception {
		setUpEntityClient(entity,Project.class,"api/projects/%s","id");
		final Project result = this.sut.getProject("id");
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperProjectComponentEndpoint(@Mocked final Component entity) throws Exception {
		setUpEntityClient(entity,Component.class,"api/projects/%s/components/%s","id","cid");
		final Component result = this.sut.getProjectComponent("id","cid");
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperProjectVersionEndpoint(@Mocked final Version entity) throws Exception {
		setUpEntityClient(entity,Version.class,"api/projects/%s/versions/%s","id","cid");
		final Version result = this.sut.getProjectVersion("id","cid");
		assertThat(result,sameInstance(entity));
	}

	@Test
	public void usesProperProjectIssueEndpoint(@Mocked final Issue entity) throws Exception {
		setUpEntityClient(entity,Issue.class,"api/projects/%s/issues/%s","id","cid");
		final Issue result = this.sut.getProjectIssue("id","cid");
		assertThat(result,sameInstance(entity));
	}

	private <E extends Entity> void setUpEntityClient(final E entity, final Class<? extends E> expectedClazz, final String expectedFormat, final Object... expectedArgs) {
		new MockUp<Client<E>>() {
			@Mock
			E get(final String format, final Object... args) {
				assertThat(format,equalTo(expectedFormat));
				assertThat(args.length,equalTo(expectedArgs.length));
				for(int i=0;i<args.length;i++) {
					assertThat(args[i],equalTo(expectedArgs[i]));
				}
				return entity;
			}
			@Mock
			<T> Client<T> resource(final Invocation invocation, final URI target, final Class<? extends T> clazz) {
				assertThat(target,equalTo(NORMALIZED));
				assertThat(clazz,sameInstance((Object)expectedClazz));
				return invocation.proceed(target,clazz);
			}
		};
	}

	private void setUpListClient(final List<String> entity, final String relativePath) {
		new MockUp<Client<List<String>>>() {
			@Mock
			List<String> get(final String format, final Object... args) {
				assertThat(format,equalTo(relativePath));
				assertThat(args.length,equalTo(0));
				return entity;
			}
			@Mock
			<T> Client<T> resource(final Invocation invocation, final URI target, final Class<? extends T> clazz) {
				assertThat(target,equalTo(NORMALIZED));
				assertThat(clazz,sameInstance((Object)String.class));
				return invocation.proceed(target,clazz);
			}
		};
	}

}
