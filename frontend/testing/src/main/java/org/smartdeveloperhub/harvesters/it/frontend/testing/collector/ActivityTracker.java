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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-test-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entity;
import org.smartdeveloperhub.harvesters.it.backend.Identifiable;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.Activity.Action;

final class ActivityTracker {

	private final class EntityLocator {

		private String location;

		void visitContributor(final Contributor state) {
			this.location=CURRENT.get().resolve("/contributors/%s", state.getId());
		}

		void visitCommit(final Commit state) {
			this.location=CURRENT.get().resolve("/commits/%s",state.getId());
		}

		void visitProject(final Project state) {
			this.location=CURRENT.get().resolve("/projects/%s", state.getId());
		}

		void visitComponent(final Component state) {
			this.location=CURRENT.get().resolve("/projects/%s/components/%s",state.getProjectId(),state.getId());
		}

		void visitVersion(final Version state) {
			this.location=CURRENT.get().resolve("/projects/%s/versions/%s",state.getProjectId(),state.getId());
		}

		void visitIssue(final Issue state) {
			this.location=CURRENT.get().resolve("/projects/%s/issues/%s",state.getProjectId(),state.getId());
		}

		String resolve(final Entity state) {
			this.location=null;
			if(state instanceof Contributor) {
				visitContributor((Contributor)state);
			} else if(state instanceof Commit) {
				visitCommit((Commit)state);
			} else if(state instanceof Project) {
				visitProject((Project)state);
			} else if(state instanceof Component) {
				visitComponent((Component)state);
			} else if(state instanceof Version) {
				visitVersion((Version)state);
			} else if(state instanceof Issue) {
				visitIssue((Issue)state);
			}
			return this.location;
		}
	}

	interface ActivityContext extends ActivityListener {

		String resolve(String path, Object... args);

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(ActivityTracker.class);

	private static final ThreadLocal<ActivityContext> CURRENT=new ThreadLocal<ActivityContext>() {

		@Override
		protected ActivityContext initialValue() {
			return new ActivityContext() {
				@Override
				public void onActivity(final Activity<?> activity) {
				}
				@Override
				public String resolve(final String path, final Object... args) {
					return null;
				}
			};
		}

	};

	private ActivityTracker() {
	}

	private <K,E extends Identifiable<K>> void submitActivity(final E entity, final Action action) {
		final Activity<K> activity =
			Activity.
				<K>builder().
					action(action).
					entity(Activity.Entity.of(entity)).
					targetId(entity.getId()).
					representation(entity).
					targetLocation(new EntityLocator().resolve(entity)).
					build();
		LOGGER.debug(activity.getDescription());
		CURRENT.get().onActivity(activity);
	}

	void log(final String format, final Object... args) {
		final Activity<String> activity =
			Activity.
				<String>builder().
					action(Action.LOG).
					description(format,args).
					build();
		LOGGER.debug(activity.getDescription());
		CURRENT.get().onActivity(activity);
	}

	<K,E extends Identifiable<K>> void created(final E entity) {
		submitActivity(entity,Action.CREATED);
	}

	<K,E extends Identifiable<K>> void updated(final E entity) {
		submitActivity(entity,Action.UPDATED);
	}

	<K,E extends Identifiable<K>> void deleted(final E entity) {
		submitActivity(entity,Action.DELETED);
	}

	static ActivityTracker currentTracker() {
		return new ActivityTracker();
	}

	static ActivityTracker useContext(final ActivityContext consumer) {
		if(consumer==null) {
			CURRENT.remove();
		} else {
			CURRENT.set(consumer);
		}
		return new ActivityTracker();
	}

	static void remove() {
		CURRENT.remove();
	}

}