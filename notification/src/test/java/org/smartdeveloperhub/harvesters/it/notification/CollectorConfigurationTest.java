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
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

import org.junit.Test;
import org.ldp4j.commons.testing.Utils;

public class CollectorConfigurationTest {

	@Test
	public void hasCustomStringRepresentation() throws Exception {
		final CollectorConfiguration sut=new CollectorConfiguration();
		assertThat(sut.toString(),not(equalTo(Utils.defaultToString(sut))));
	}

	@Test
	public void nonNullBrokerPortsAreMemoized() throws Exception {
		final CollectorConfiguration sut = new CollectorConfiguration();
		sut.setBrokerPort(1234);
		assertThat(sut.getBrokerPort(),equalTo(1234));
	}

	@Test
	public void nullBrokerPortsAreMemoizedAsDefaultPortValue() throws Exception {
		final CollectorConfiguration sut = new CollectorConfiguration();
		final Integer defaultValue = sut.getBrokerPort();
		sut.setBrokerPort(1234);
		assumeThat("Could not change broker port",sut.getBrokerPort(),equalTo(1234));
		sut.setBrokerPort(null);
		assertThat(sut.getBrokerPort(),equalTo(defaultValue));
	}

	@Test
	public void nonNullVirtualHostAreMemoized() throws Exception {
		final CollectorConfiguration sut = new CollectorConfiguration();
		sut.setVirtualHost("/myhost");
		assertThat(sut.getVirtualHost(),equalTo("/myhost"));
	}

	@Test
	public void nullVirtualHostsAreMemoizedAsDefaultVirtualHostValue() throws Exception {
		final CollectorConfiguration sut = new CollectorConfiguration();
		final String defaultValue = sut.getVirtualHost();
		sut.setVirtualHost("/myhost");
		assumeThat("Could not change virtual host",sut.getVirtualHost(),equalTo("/myhost"));
		sut.setVirtualHost(null);
		assertThat(sut.getVirtualHost(),equalTo(defaultValue));
	}

}
