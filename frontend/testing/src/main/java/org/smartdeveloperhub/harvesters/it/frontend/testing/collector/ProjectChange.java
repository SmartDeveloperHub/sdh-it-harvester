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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-test-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.collector;

import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.ProjectScoped;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.notification.event.Modification;
import org.smartdeveloperhub.harvesters.it.notification.event.Modification.EntitySelector;

import com.google.common.base.Optional;

public class ProjectChange<T extends ProjectScoped<String>> {

	interface ProjectContext<T> {

		public boolean contains(String id);

		public void create(T entity);

		public void delete(T entity);

		public void update(T entity);

	}

	private final T entity;
	private final boolean deletion;
	private String[] authors;

	private ProjectChange(final T entity, final boolean deletion) {
		this.entity = entity;
		this.deletion = deletion;
	}

	public ProjectChange<T> authors(final String... authors) {
		this.authors = authors;
		return this;
	}

	String projectId() {
		return this.entity.getProjectId();
	}

	boolean is(final Class<?> clazz) {
		return clazz.isInstance(this.entity);
	}

	T entity() {
		return this.entity;
	}

	boolean isDeletion() {
		return this.deletion;
	}

	Optional<Modification> apply(final ProjectContext<T> context) {
		EntitySelector selector=null;
		if(context.contains(this.entity.getId())) {
			if(!this.deletion) {
				selector=Modification.update();
				context.update(this.entity);
			} else {
				selector=Modification.delete();
				context.delete(this.entity);
			}
		} else if(!this.deletion) {
			selector=Modification.create();
			context.create(this.entity);
		} else {
			return Optional.absent();
		}
		Modification result=null;
		if(this.entity instanceof Component) {
			result=selector.component(this.entity.getId());
		} else if(this.entity instanceof Version) {
			result=selector.version(this.entity.getId());
		} else if(this.entity instanceof Issue) {
			result=selector.issue(this.entity.getId());
		} else {
			return Optional.absent();
		}
		result.authors(this.authors);
		return Optional.of(result);
	}

	public static <E extends ProjectScoped<String>> ProjectChange<E> createOrUpdate(final E entity) {
		return new ProjectChange<>(entity,false);
	}

	public static <E extends ProjectScoped<String>> ProjectChange<E> delete(final E entity) {
		return new ProjectChange<>(entity,true);
	}

}
