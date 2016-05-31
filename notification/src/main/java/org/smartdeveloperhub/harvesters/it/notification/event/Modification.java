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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Action;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Change;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Entity;

import com.google.common.collect.Lists;

public final class Modification {

	public static final class EntitySelector {

		private final Action action;

		private EntitySelector(final Action action) {
			this.action = action;
		}

		public Modification component(final String target) {
			return forward(target, Entity.COMPONENT);
		}

		public Modification version(final String target) {
			return forward(target, Entity.VERSION);
		}

		public Modification topIssue(final String target) {
			return forward(target, Entity.TOP_ISSUE);
		}

		public Modification issue(final String target) {
			return forward(target, Entity.ISSUE);
		}

		private Modification forward(final String target, final Entity entity) {
			return new Modification(this.action,entity,target,null);
		}

	}

	private final Action action;
	private final Entity entity;
	private final String target;
	private final String[] authors;

	private Modification(final Action action,final Entity entity,final String target, final String[] authors) {
		this.action = action;
		this.entity = entity;
		this.target = target;
		this.authors = authors;
	}

	public Modification authors(final String... authors) {
		return new Modification(this.action,this.entity,this.target,authors);
	}

	void attach(final ProjectUpdatedEvent event) {
		checkNotNull(this.entity,"Entity cannot be null");
		checkNotNull(this.target,"Target cannot be null");
		checkNotNull(this.action,"Action cannot be null");
		final Map<String, List<Change>> entityChanges = event.getChanges().get(this.entity);
		List<Change> targetChanges=entityChanges.get(this.target);
		if(targetChanges==null) {
			targetChanges=Lists.newArrayList();
			entityChanges.put(this.target,targetChanges);
		}
		targetChanges.add(Change.create(this.action,this.authors));
	}

	public static EntitySelector create() {
		return new EntitySelector(Action.CREATED);
	}

	public static EntitySelector update() {
		return new EntitySelector(Action.UPDATED);
	}

	public static EntitySelector delete() {
		return new EntitySelector(Action.DELETED);
	}

}