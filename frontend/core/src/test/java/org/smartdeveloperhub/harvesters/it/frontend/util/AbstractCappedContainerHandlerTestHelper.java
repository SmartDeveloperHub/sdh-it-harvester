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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.ldp4j.application.data.DataSet;
import org.ldp4j.application.data.Individual;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.NamingScheme;
import org.ldp4j.application.ext.ApplicationRuntimeException;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.WriteSession;

import mockit.Expectations;
import mockit.Mocked;

public abstract class AbstractCappedContainerHandlerTestHelper {

	private final class CustomDataSet implements DataSet {
		@Override
		public Iterator<Individual<?, ?>> iterator() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public Name<?> name() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public int numberOfIndividuals() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public boolean hasIndividuals() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public Collection<? extends Individual<?, ?>> individuals() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public Set<Serializable> individualIds() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public boolean hasIndividual(final Object id) {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public <T extends Serializable, S extends Individual<T, S>> S individualOfId(final T id) {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public <T extends Serializable, S extends Individual<T, S>> S individual(final T id, final Class<? extends S> clazz) {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public void remove(final Individual<?, ?> src) {
			throw new UnsupportedOperationException("Method should not be invoked");
		}
		@Override
		public String toString() {
			return "DATASET";
		}
	}

	@Mocked private ContainerSnapshot container;
	@Mocked private WriteSession      session;
	@Mocked private ResourceSnapshot  snapshot;

	protected final void verifyGetReturnsEmptyDataset(final AbstractCappedContainerHandler sut) throws Exception {
		final Name<String> name=NamingScheme.getDefault().name("id");
		new Expectations() {{
			AbstractCappedContainerHandlerTestHelper.this.snapshot.name();this.result=name;
		}};
		final DataSet result = sut.get(this.snapshot);
		assertThat((Object)result.name(),sameInstance((Object)name));
		assertThat(result.hasIndividuals(),equalTo(false));
	}

	protected final void verifyFactoryMethodIsDisabled(final String name, final AbstractCappedContainerHandler sut) {
		try {
			sut.create(this.container, new CustomDataSet(), this.session);
			fail("Factory method should be disabled");
		} catch (final ApplicationRuntimeException e) {
			assertThat(e.getMessage().toLowerCase(),equalTo(name+" creation is not supported"));
		}
	}

}
