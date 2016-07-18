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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-api:0.1.0
 *   Bundle      : it-frontend-api-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.commons.testing.Utils;
import org.smartdeveloperhub.harvesters.it.frontend.spi.BackendControllerFactory;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class BackendControllersTest {

	private static final URI TARGET = URI.create("http://www.smartdeveloperhub.org/jira/collector/");

	private static abstract class FailingCase {

		protected final BackendController controller;

		private FailingCase(final BackendController controller) {
			this.controller = controller;

		}

		public void execute() {
			try {
				exercise(this.controller);
				Assert.fail("Method should fail");
			} catch (final IOException e) {
				assertThat(e.getMessage(),equalTo("Could not create controller for interacting with collector 'http://www.smartdeveloperhub.org/jira/collector/'"));
			}
		}

		protected abstract void exercise(BackendController controller) throws IOException;

	}

	private void assertFails(final FailingCase try1) {
		try1.execute();
	}

	@Test
	public void isValidUtilityClass() {
		assertThat(Utils.isUtilityClass(BackendControllers.class),equalTo(true));
	}

	@Test
	public void createsControllerFromDefaultFactoryWhenAvailable(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		new Expectations() {{
			factory.create(TARGET);this.result=expected;
		}};
		BackendControllers.setDefaultFactory(factory);
		WorkingBackendControllerFactory.setController(null);
		final BackendController controller = BackendControllers.createController(TARGET);
		assertThat(controller,sameInstance(expected));
	}

	@Test
	public void createsControllerFromDeclaredDefaultFactoryWhenAvailable(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		new MockUp<WorkingBackendControllerFactory>() {
			@Mock
			public BackendController create(final URI target) {
				return expected;
			}
		};
		System.setProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME,WorkingBackendControllerFactory.class.getName());
		try {
			BackendControllers.setDefaultFactory(null);
			WorkingBackendControllerFactory.setController(null);
			final BackendController controller = BackendControllers.createController(TARGET);
			assertThat(controller,sameInstance(expected));
		} finally {
			System.clearProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME);
		}
	}

	@Test
	public void createsControllerFromDiscoveredFactoriesIfDeclaredDefaultFactoryDoesNotExist(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		System.setProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME,"unknown");
		try {
			BackendControllers.setDefaultFactory(null);
			WorkingBackendControllerFactory.setController(expected);
			final BackendController controller = BackendControllers.createController(TARGET);
			assertThat(controller,sameInstance(expected));
		} finally {
			System.clearProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME);
		}
	}

	@Test
	public void createsControllerFromDiscoveredFactoryIfDeclaredDefaultFactoryIsNotValid(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		System.setProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME,String.class.getName());
		try {
			BackendControllers.setDefaultFactory(null);
			WorkingBackendControllerFactory.setController(expected);
			final BackendController controller = BackendControllers.createController(TARGET);
			assertThat(controller,sameInstance(expected));
		} finally {
			System.clearProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME);
		}
	}

	@Test
	public void createsControllerFromDiscoveredFactoryIfDeclaredDefaultFactoryCannotBeInstantiated(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		System.setProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME,CustomBackendControllerFactory.class.getName());
		try {
			BackendControllers.setDefaultFactory(null);
			WorkingBackendControllerFactory.setController(expected);
			final BackendController controller = BackendControllers.createController(TARGET);
			assertThat(controller,sameInstance(expected));
		} finally {
			System.clearProperty(BackendControllers.DEFAULT_FACTORY_CLASS_NAME);
		}
	}

	@Test
	public void createsControllerFromDiscoveredFactoriesIfDefaultFactoryFails(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		new Expectations() {{
			factory.create(TARGET);this.result=new IllegalArgumentException("");
		}};
		BackendControllers.setDefaultFactory(factory);
		WorkingBackendControllerFactory.setController(expected);
		final BackendController controller = BackendControllers.createController(TARGET);
		assertThat(controller,sameInstance(expected));
	}

	@Test
	public void createsControllerFromDiscoveredFactoriesWhenDefaultFactoryDoesNotCreateAController(@Mocked final BackendControllerFactory factory,@Mocked final BackendController expected) throws Exception {
		new Expectations() {{
			factory.create(TARGET);this.result=null;
		}};
		BackendControllers.setDefaultFactory(factory);
		WorkingBackendControllerFactory.setController(expected);
		final BackendController controller = BackendControllers.createController(TARGET);
		assertThat(controller,sameInstance(expected));
	}

	@Test
	public void createsControllerFromDiscoveredFactoriesWhenNoDefaultFactoryIsAvailable(@Mocked final BackendController expected) throws Exception {
		BackendControllers.setDefaultFactory(null);
		WorkingBackendControllerFactory.setController(expected);
		final BackendController controller = BackendControllers.createController(TARGET);
		assertThat(controller,sameInstance(expected));
	}

	@Test
	public void createsNullControllerIfNoControllerCanBeCreated() throws Exception {
		WorkingBackendControllerFactory.setController(null);
		final BackendController controller = BackendControllers.createController(TARGET);
		assertThat(controller,not(nullValue()));
		assertThat(controller.getTarget(),equalTo(TARGET));
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getCollector();
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getState();
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getCommit(null);
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getCommits();
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getProjectComponent(null,null);
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getContributor(null);
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getContributors();
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getProjectIssue(null,null);
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getProject(null);
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getProjects();
				}
			}
		);
		assertFails(
			new FailingCase(controller) {
				@Override
				protected void exercise(final BackendController controller) throws IOException {
					controller.getProjectVersion(null,null);
				}
			}
		);
	}

}
