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
package org.smartdeveloperhub.harvesters.it.notification;

public class NotificationTestHelper {

	protected static final String CONTENT_TYPE = "Content-Type";

	public NotificationTestHelper() {
		super();
	}

	protected CollectorConfiguration defaultCollector() {
		final String instance = "http://russell.dia.fi.upm.es:5000/api";
		final CollectorConfiguration collector = instanceCollector(instance);
		return collector;
	}

	protected CollectorConfiguration instanceCollector(final String instance) {
		final String virtualHost = "/";
		final String exchangeName = "sdh";
		final CollectorConfiguration collector = customCollector(instance, virtualHost,exchangeName);
		return collector;
	}

	protected CollectorConfiguration customCollector(final String instance, final String virtualHost, final String exchangeName) {
		final CollectorConfiguration collector=new CollectorConfiguration();
		collector.setInstance(instance);
		collector.setBrokerHost("localhost");
		collector.setBrokerPort(5672);
		collector.setVirtualHost(virtualHost);
		collector.setExchangeName(exchangeName);
		return collector;
	}

}