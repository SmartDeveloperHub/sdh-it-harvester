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
import java.util.List;

import org.ldp4j.application.ApplicationContext;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

final class CommitPublisherTask extends PublisherTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommitPublisherTask.class);

	CommitPublisherTask(final BackendController controller) {
		super("Commit publication",controller);
	}

	@Override
	protected final void doPublish() throws IOException {
		final List<String> commits = getController().getCommits();
		if(commits.isEmpty()) {
			LOGGER.info("No commits available");
			return;
		}
		publishCommitResources(commits);
	}

	private void publishCommitResources(final List<String> users) throws IOException {
		final ApplicationContext ctx = ApplicationContext.getInstance();
		WriteSession session=null;
		try {
			session = ctx.createSession();
			PublisherHelper.
				publishCommits(
					session,
					getController().getTarget(),
					users);
			session.saveChanges();
		} catch(final Exception e) {
			throw new IOException("Could not publish commit resources",e);
		} finally {
			PublisherHelper.closeGracefully(session);
		}
	}

}
