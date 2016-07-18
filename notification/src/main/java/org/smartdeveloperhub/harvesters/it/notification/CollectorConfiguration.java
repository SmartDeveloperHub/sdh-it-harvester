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

import com.google.common.base.MoreObjects;

public final class CollectorConfiguration {

	private static final String DEFAULT_VIRTUAL_HOST = "/";

	private static final int DEFAULT_PORT = 5672;

	private String instance;

	private String brokerHost;

	private Integer brokerPort = DEFAULT_PORT;

	private String virtualHost = DEFAULT_VIRTUAL_HOST;

	private String exchangeName;

	/**
	 * Get the collector's API base endpoint
	 *
	 * @return the instance
	 */
	public String getInstance() {
		return this.instance;
	}

	/**
	 * Set the collector's API base endpoint
	 *
	 * @param instance
	 *            the collector's API base endpoint
	 */
	public void setInstance(final String instance) {
		this.instance = instance;
	}

	/**
	 * Get the host where the broker is available. The host might be an IP
	 * address or a fully qualified domain name.
	 *
	 * @return the broker host
	 */
	public String getBrokerHost() {
		return this.brokerHost;
	}

	/**
	 * Set the host where the broker is available. The host might be an IP
	 * address or a fully qualified domain name.
	 * @param brokerHost
	 *            the broker host
	 */
	public void setBrokerHost(final String brokerHost) {
		this.brokerHost = brokerHost;
	}

	/**
	 * Get the port where the broker is available.
	 *
	 * @return the broker port
	 */
	public Integer getBrokerPort() {
		return this.brokerPort;
	}

	/**
	 * Set the port where the broker is available.
	 *
	 * @param brokerPort
	 *            the broker port
	 */
	public void setBrokerPort(final Integer brokerPort) {
		this.brokerPort = brokerPort==null?DEFAULT_PORT:brokerPort;
	}

	/**
	 * Get the virtual host used by the collector
	 *
	 * @return the virtual host
	 */
	public String getVirtualHost() {
		return this.virtualHost;
	}

	/**
	 * Set the virtual host used by the collector
	 *
	 * @param virtualHost
	 *            the virtual host
	 */
	public void setVirtualHost(final String virtualHost) {
		this.virtualHost = virtualHost==null?DEFAULT_VIRTUAL_HOST:virtualHost;
	}

	/**
	 * Get the exchange name used by the collector
	 *
	 * @return the exchange name used by the collector
	 */
	public String getExchangeName() {
		return this.exchangeName;
	}

	/**
	 * Set the exchange name used by the collector
	 *
	 * @param exchangeName
	 *            the exchange name
	 */
	public void setExchangeName(final String exchangeName) {
		this.exchangeName = exchangeName;
	}

	@Override
	public String toString() {
		return
			MoreObjects.
				toStringHelper(getClass()).
					omitNullValues().
					add("instance",this.instance).
					add("brokerHost",this.brokerHost).
					add("brokerPort",this.brokerPort).
					add("virtualHost",this.virtualHost).
					add("exchangeName",this.exchangeName).
					toString();
	}

}
