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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.session.WriteSession;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class CommitPublisherTaskTest {

	@Mocked private BackendController controller;
	@Mocked private ApplicationContext context;
	@Mocked private WriteSession session;
	@Mocked private PublisherHelper helper;

	private CommitPublisherTask sut;

	@Before
	public void setUp() {
		this.sut=new CommitPublisherTask(this.controller);
	}

	@Test
	public void doesNothingIfThereAreNoCommits() throws IOException {
		new Expectations() {{
			CommitPublisherTaskTest.this.controller.getCommits();this.result=Collections.emptyList();
		}};
		this.sut.call();
	}

	@Test
	public void publishesAvailableCommits() throws Exception {
		final URI target=URI.create("target");
		final List<String> commits = Arrays.asList("commit1","commit2");
		new Expectations() {{
			CommitPublisherTaskTest.this.controller.getTarget();this.result=target;
			CommitPublisherTaskTest.this.controller.getCommits();this.result=commits;
			ApplicationContext.getInstance();this.result=CommitPublisherTaskTest.this.context;
			CommitPublisherTaskTest.this.context.createSession();this.result=CommitPublisherTaskTest.this.session;
			PublisherHelper.publishCommits(CommitPublisherTaskTest.this.session, target, commits);
			PublisherHelper.closeGracefully(CommitPublisherTaskTest.this.session);
		}};
		this.sut.call();
	}

	@Test
	public void doesNotPublisheAvailableCommitsIfHelperFails() throws Exception {
		final URI target=URI.create("target");
		final List<String> commits = Arrays.asList("commit1","commit2");
		new Expectations() {{
			CommitPublisherTaskTest.this.controller.getTarget();this.result=target;
			CommitPublisherTaskTest.this.controller.getCommits();this.result=commits;
			ApplicationContext.getInstance();this.result=CommitPublisherTaskTest.this.context;
			CommitPublisherTaskTest.this.context.createSession();this.result=CommitPublisherTaskTest.this.session;
			PublisherHelper.publishCommits(CommitPublisherTaskTest.this.session, target, commits);this.result=new IOException("Failure");
			PublisherHelper.closeGracefully(CommitPublisherTaskTest.this.session);
		}};
		this.sut.call();
	}

}
