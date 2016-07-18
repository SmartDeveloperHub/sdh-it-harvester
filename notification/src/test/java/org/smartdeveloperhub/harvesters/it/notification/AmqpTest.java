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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-notification:0.2.0-SNAPSHOT
 *   Bundle      : it-harvester-notification-0.2.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;

import com.google.common.base.Strings;

public class AmqpTest {

	@Test
	public void testIsUtilityClass() throws Exception {
		assertThat(Utils.isUtilityClass(Amqp.class),equalTo(true));
	}

	@Test
	public void testValidateName$invalid$null() throws Exception {
		try {
			Amqp.validateName(null,"Name");
			fail("Should not accept null");
		} catch (final NullPointerException e) {
			assertThat(e.getMessage(),equalTo("Name cannot be null"));
		}
	}

	@Test
	public void testValidateName$invalid$tooLong() throws Exception {
		try {
			Amqp.validateName(Strings.padStart("", 256, 'a'),"Name");
			fail("Should not accept names longer that the AMQP limit");
		} catch (final IllegalArgumentException e) {
		}
	}

	@Test
	public void testValidateName$invalid$badChars() throws Exception {
		try {
			Amqp.validateName("white spaces not allowed","Name");
			fail("Should not accept string with invalid characters");
		} catch (final IllegalArgumentException e) {
			assertThat(e.getMessage(),equalTo("Invalid name syntax (white spaces not allowed)"));
		}
	}

}
