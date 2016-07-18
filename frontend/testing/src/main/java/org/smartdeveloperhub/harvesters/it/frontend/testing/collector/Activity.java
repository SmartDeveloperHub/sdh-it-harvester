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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0
 *   Bundle      : it-frontend-test-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.collector;

import java.util.UUID;

import org.smartdeveloperhub.harvesters.it.backend.Commit;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Activity.ID,
	Activity.TIMESTAMP,
	Activity.ACTION,
	Activity.DESCRIPTION,
	Activity.ENTITY,
	Activity.TARGET_ID,
	Activity.TARGET_LOCATION,
	Activity.REPRESENTATION,
})
public final class Activity<T> extends org.smartdeveloperhub.harvesters.it.backend.Entity {

	public enum Entity {
		CONTRIBUTOR(Contributor.class),
		COMMIT(Commit.class),
		PROJECT(Project.class),
		COMPONENT(Component.class),
		VERSION(Version.class),
		ISSUE(Issue.class),
		;
		private final Class<? extends org.smartdeveloperhub.harvesters.it.backend.Entity> clazz;

		private Entity(final Class<? extends org.smartdeveloperhub.harvesters.it.backend.Entity> clazz) {
			this.clazz=clazz;
		}

		static Entity of(final org.smartdeveloperhub.harvesters.it.backend.Entity instance) {
			for(final Entity entity:values()) {
				if(entity.clazz.isInstance(instance)) {
					return entity;
				}
			}
			throw new IllegalStateException("Unsupported entity "+instance);
		}
	}

	public enum Action {
		LOG,
		CREATED,
		UPDATED,
		DELETED,
	}

	static final String ID              = "id";
	static final String TIMESTAMP       = "timestamp";
	static final String ACTION          = "action";
	static final String ENTITY          = "entity";
	static final String TARGET_ID       = "targetId";
	static final String DESCRIPTION     = "description";
	static final String TARGET_LOCATION = "targetLocation";
	static final String REPRESENTATION  = "representation";

	@JsonProperty(Activity.ID)
	private UUID id;

	@JsonProperty(Activity.TIMESTAMP)
	private long timestamp;

	@JsonProperty(Activity.ACTION)
	private Action action;

	@JsonProperty(Activity.ENTITY)
	private Entity entity;

	@JsonProperty(Activity.TARGET_ID)
	private T targetId;

	@JsonProperty(Activity.DESCRIPTION)
	private String description;

	@JsonProperty(Activity.TARGET_LOCATION)
	private String targetLocation;

	@JsonProperty(Activity.REPRESENTATION)
	private Object representation;

	/**
	 * @return the id
	 */
	@JsonProperty(ID)
	public UUID getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	@JsonProperty(ID)
	public void setId(final UUID id) {
		this.id = id;
	}

	/**
	 * @return the timestamp
	 */
	@JsonProperty(Activity.TIMESTAMP)
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	@JsonProperty(Activity.TIMESTAMP)
	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the action
	 */
	@JsonProperty(Activity.ACTION)
	public Action getAction() {
		return this.action;
	}

	/**
	 * @param action the action to set
	 */
	@JsonProperty(Activity.ACTION)
	public void setAction(final Action action) {
		this.action = action;
	}

	/**
	 * @return the entity
	 */
	@JsonProperty(Activity.ENTITY)
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * @param entity the entity to set
	 */
	@JsonProperty(Activity.ENTITY)
	public void setEntity(final Entity entity) {
		this.entity = entity;
	}

	/**
	 * @return the targetId
	 */
	@JsonProperty(Activity.TARGET_ID)
	public T getTargetId() {
		return this.targetId;
	}

	/**
	 * @param targetId the targetId to set
	 */
	@JsonProperty(Activity.TARGET_ID)
	public void setTargetId(final T targetId) {
		this.targetId = targetId;
	}

	/**
	 * @return the message
	 */
	@JsonProperty(Activity.DESCRIPTION)
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the message to set
	 */
	@JsonProperty(Activity.DESCRIPTION)
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the targetLocation
	 */
	@JsonProperty(Activity.TARGET_LOCATION)
	public String getTargetLocation() {
		return this.targetLocation;
	}

	/**
	 * @param targetLocation the targetLocation to set
	 */
	@JsonProperty(Activity.TARGET_LOCATION)
	public void setTargetLocation(final String targetLocation) {
		this.targetLocation = targetLocation;
	}

	/**
	 * @return the representation
	 */
	@JsonProperty(Activity.REPRESENTATION)
	public Object getRepresentation() {
		return this.representation;
	}

	/**
	 * @param representation the representation to set
	 */
	@JsonProperty(Activity.REPRESENTATION)
	public void setRepresentation(final Object representation) {
		this.representation = representation;
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static class Builder<T> {

		private Activity<T> build;

		private Builder() {
			this.build=new Activity<T>();
		}

		public Builder<T> action(final Action action) {
			this.build.setAction(action);
			return this;
		}

		public Builder<T> entity(final Entity entity) {
			this.build.setEntity(entity);
			return this;
		}

		public Builder<T> description(final String format, final Object... args) {
			this.build.setDescription(String.format(format,args));
			return this;
		}

		public Builder<T> targetId(final T id) {
			this.build.setTargetId(id);
			return this;
		}

		public Builder<T> targetLocation(final String targetLocation) {
			this.build.setTargetLocation(targetLocation);
			return this;
		}

		public Builder<T> representation(final Object representation) {
			this.build.setRepresentation(representation);
			return this;
		}

		public Activity<T> build() {
			final Activity<T> result=this.build;
			this.build=new Activity<T>();
			result.setId(UUID.randomUUID());
			result.setTimestamp(System.currentTimeMillis());
			return result;
		}

	}

}
