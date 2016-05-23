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
package org.smartdeveloperhub.harvesters.it.notification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.io.IOException;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.rabbitmq.client.Channel;

@RunWith(JMockit.class)
public class AcknowledgeableNotificationTest {

	@Mocked Channel channel;

	private final long deliveryTag=1000;

	private AcknowledgeableNotification sut;

	@Before
	public void setUp() {
		this.sut=new AcknowledgeableNotification(this.channel, this.deliveryTag);
	}

	@Test
	public void testAcknowledgMarksAcknowledgement() {
		this.sut.acknowledge();
		assertThat(this.sut.isAcknowledged(),equalTo(true));
	}

	@Test
	public void testOnlyAcknowledgesOnce() {
		this.sut.acknowledge();
		try {
			this.sut.acknowledge();
			fail("Should only acknowledge once");
		} catch(final IllegalStateException e) {
			assertThat(e.getMessage(),equalTo("Notification for message 1000 has been already acknowledged"));
		}
	}

	@Test
	public void testOnAcknowledgeFailureAllowsRetrying() throws IOException {
		new Expectations() {{
			AcknowledgeableNotificationTest.this.channel.basicAck(AcknowledgeableNotificationTest.this.deliveryTag, false);this.result=new IOException("Failure");
		}};
		this.sut.acknowledge();
		assertThat(this.sut.isAcknowledged(),equalTo(false));
	}

}
