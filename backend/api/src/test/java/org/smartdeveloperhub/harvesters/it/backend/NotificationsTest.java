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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;

public class NotificationsTest {

	@Test
	public void canMarshallAndUnmarshallNotifications() throws IOException {
		final Notifications one = Fixture.defaultNotifications();
		final String str = Entities.marshallEntity(one);
		final Notifications other = Entities.unmarshallEntity(str,Notifications.class);
		assertEqual(one, other);
	}

	static void assertEqual(final Notifications one, final Notifications other) {
		assertThat(other.getBrokerHost(),equalTo(one.getBrokerHost()));
		assertThat(other.getBrokerPort(),equalTo(one.getBrokerPort()));
		assertThat(other.getVirtualHost(),equalTo(one.getVirtualHost()));
		assertThat(other.getExchangeName(),equalTo(one.getExchangeName()));
	}

	@Test
	public void notificationsHaveCustomToString() {
		final Notifications sut = Fixture.defaultNotifications();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

}
