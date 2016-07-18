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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.2.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;

import com.google.common.collect.ImmutableList;

final class ProjectConfiguration {

	private final String id;
	private final String name;
	private final ImmutableList<Contributor> contributors;
	private final LocalDate startedOn;
	private final Days duration;

	private ProjectConfiguration(
			final String id,
			final String name,
			final ImmutableList<Contributor> contributors,
			final LocalDate startedOn,
			final Days duration) {
		this.id = id;
		this.name = name;
		this.contributors = contributors;
		this.startedOn = startedOn;
		this.duration = duration;
	}

	String id() {
		return this.id;
	}

	String name() {
		return this.name;
	}

	List<Contributor> contributors() {
		return this.contributors;
	}

	LocalDate startedOn() {
		return this.startedOn;
	}

	Duration duration() {
		return this.duration.toStandardDuration();
	}

	static Builder builder() {
		return new Builder();
	}

	static final class Builder {

		private final ImmutableList.Builder<Contributor> contributors;

		private String id;
		private String name;
		private LocalDate startedOn;
		private Days duration;

		private Builder() {
			this.contributors=ImmutableList.<Contributor>builder();
		}

		Builder id(final String id) {
			this.id = id;
			return this;
		}

		Builder name(final String name) {
			this.name = name;
			return this;
		}

		Builder contributors(final Contributor... contributor) {
			this.contributors.add(contributor);
			return this;
		}

		Builder startedOn(final String dateTime) {
			this.startedOn=new DateTime(dateTime).toLocalDate();
			this.duration=Days.daysBetween(this.startedOn,new DateTime().toLocalDate());
			return this;
		}

		ProjectConfiguration build() {
			return
				new ProjectConfiguration(
					Objects.requireNonNull(this.id,"Project identifier cannot be null"),
					Objects.requireNonNull(this.name,"Project name cannot be null"),
					this.contributors.build(),
					Objects.requireNonNull(this.startedOn,"Project start date cannot be null"),
					this.duration);
		}

	}

}
