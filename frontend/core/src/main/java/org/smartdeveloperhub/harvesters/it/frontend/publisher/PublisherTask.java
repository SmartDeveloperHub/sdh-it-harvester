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
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;

import com.google.common.base.Stopwatch;

abstract class PublisherTask implements Callable<Boolean> {

	private static final Logger LOGGER=LoggerFactory.getLogger(PublisherTask.class);

	private final BackendController controller;
	private final String taskName;

	PublisherTask(final String taskName, final BackendController controller) {
		this.taskName = taskName;
		this.controller = controller;
	}

	@Override
	public final Boolean call() {
		LOGGER.info("Starting {} task...",this.taskName);
		final Stopwatch watch = Stopwatch.createStarted();
		try {
			doPublish();
			LOGGER.info("{} completed.",this.taskName);
			return true;
		} catch(final Exception e) {
			LOGGER.warn("{} failed. Full stacktrace follows.",this.taskName,e);
			return false;
		} finally {
			watch.stop();
			LOGGER.info("{} task finished. Elapsed time (ms): {}",this.taskName,watch.elapsed(TimeUnit.MILLISECONDS));
		}
	}

	protected abstract void doPublish() throws Exception; // NOSONAR

	protected final BackendController getController() {
		return this.controller;
	}

}
