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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.2.0-SNAPSHOT
 *   Bundle      : it-backend-api-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.Test;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.backend.State.Activity;
import org.smartdeveloperhub.harvesters.it.backend.State.Activity.Category;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class StateTest {

	@Test
	public void canMarshallAndUnmarshallStates() throws IOException {
		final State one = defaultState();
		final String str = Entities.marshallEntity(one);
		final State other = Entities.unmarshallEntity(str,State.class);
		assertThat(other.getJiraApiVersion(),equalTo(one.getJiraApiVersion()));
		assertThat(other.getLastCrawlingDate(),equalTo(one.getLastCrawlingDate()));
		assertThat(other.getStatusMappings(),equalTo(one.getStatusMappings()));
		assertThat(other.getActivity().size(),equalTo(one.getActivity().size()));
		assertEqual(other.getActivity().get(0),one.getActivity().get(0));
	}

	@Test
	public void statesHaveCustomToString() {
		final State sut = defaultState();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}
	@Test
	public void allCategoryValuesAreListed() {
		assertThat(
			Arrays.asList(Category.values()),
			contains(
				Category.FAILED,
				Category.SUCCEDED
			)
		);
	}

	@Test
	public void categoryValuesCanBeFound() {
		for(final Category value:Category.values()) {
			assertThat(Category.valueOf(value.name()),equalTo(value));
		}
	}

	private void assertEqual(final Activity other, final Activity one) {
		assertThat(other.getAction(),equalTo(one.getAction()));
		assertThat(other.getCategory(),equalTo(one.getCategory()));
		assertThat(other.getTimestamp(),equalTo(one.getTimestamp()));
	}

	private State defaultState() {
		final State state = new State();
		state.setJiraApiVersion("jiraApiVersion");
		state.setLastCrawlingDate(new DateTime());
		state.setStatusMappings(ImmutableMap.<String,String>builder().put("a1","b1").build());
		state.setActivity(ImmutableList.of(defaultActivity()));
		return state;
	}

	private Activity defaultActivity() {
		final Activity activity = new Activity();
		activity.setAction("action");
		activity.setCategory(Category.FAILED);
		activity.setTimestamp(new DateTime());
		return activity;
	}

}
