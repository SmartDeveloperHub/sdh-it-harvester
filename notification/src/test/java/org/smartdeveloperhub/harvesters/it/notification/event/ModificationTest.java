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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.1.0
 *   Bundle      : it-harvester-notification-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Action;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Change;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class ModificationTest {

	@Mocked ProjectUpdatedEvent event;

	@Test
	public void testAttachKeepsEntries() throws Exception {
		final Map<Entity,Map<String,List<Change>>> changes=Maps.newLinkedHashMap();
		final Map<String,List<Change>> entityChanges = Maps.newLinkedHashMap();
		final List<Change> issueChanges = Lists.newArrayList();
		issueChanges.add(Change.create(Action.CREATED,"author"));
		entityChanges.put("issue",issueChanges);
		changes.put(Entity.ISSUE,entityChanges);
		new Expectations() {{
			ModificationTest.this.event.getChanges();this.result=changes;
		}};
		Modification.
			update().
				issue("issue").
					authors("another author").
					attach(this.event);
		assertThat(issueChanges.size(),equalTo(2));
		assertThat(issueChanges.get(1).getAction(),equalTo(Action.UPDATED));
		assertThat(issueChanges.get(1).getAuthors(),hasItems("another author"));
	}


	@Test
	public void testManagesAuthorCollections() {
		final Map<Entity,Map<String,List<Change>>> changes=Maps.newLinkedHashMap();
		final Map<String,List<Change>> entityChanges = Maps.newLinkedHashMap();
		final List<Change> issueChanges = Lists.newArrayList();
		issueChanges.add(Change.create(Action.CREATED,"author"));
		entityChanges.put("issue",issueChanges);
		changes.put(Entity.ISSUE,entityChanges);
		new Expectations() {{
			ModificationTest.this.event.getChanges();this.result=changes;
		}};
		Modification.
			update().
				issue("issue").
					authors(Lists.newArrayList("author1","author2")).
					attach(this.event);
		assertThat(issueChanges.size(),equalTo(2));
		assertThat(issueChanges.get(1).getAction(),equalTo(Action.UPDATED));
		assertThat(issueChanges.get(1).getAuthors(),hasItems("author1","author2"));
	}

}
