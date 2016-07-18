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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects.ToStringHelper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Notifications.BROKER_HOST,
	Notifications.BROKER_PORT,
	Notifications.VIRTUAL_HOST,
	Notifications.EXCHANGE_NAME
})
public class Notifications extends Entity {

	static final String BROKER_HOST = "brokerHost";
	static final String BROKER_PORT = "brokerPort";
	static final String VIRTUAL_HOST = "virtualHost";
	static final String EXCHANGE_NAME = "exchangeName";

	@JsonProperty(BROKER_HOST)
	private String brokerHost;

	@JsonProperty(BROKER_PORT)
	private Integer brokerPort = 5672;

	@JsonProperty(VIRTUAL_HOST)
	private String virtualHost = "/";

	@JsonProperty(EXCHANGE_NAME)
	private String exchangeName;

	/**
	 * Get the host where the broker is available. The host might be an IP
	 * address or a fully qualified domain name.
	 *
	 * @return the broker host
	 */
	@JsonProperty(BROKER_HOST)
	public String getBrokerHost() {
		return this.brokerHost;
	}

	/**
	 * Set the host where the broker is available. The host might be an IP
	 * address or a fully qualified domain name.
	 * @param brokerHost
	 *            the broker host
	 */
	@JsonProperty(BROKER_HOST)
	public void setBrokerHost(final String brokerHost) {
		this.brokerHost = brokerHost;
	}

	/**
	 * Get the port where the broker is available.
	 *
	 * @return the broker port
	 */
	@JsonProperty(BROKER_PORT)
	public Integer getBrokerPort() {
		return this.brokerPort;
	}

	/**
	 * Set the port where the broker is available.
	 *
	 * @param brokerPort
	 *            the broker port
	 */
	@JsonProperty(BROKER_PORT)
	public void setBrokerPort(final Integer brokerPort) {
		this.brokerPort = brokerPort;
	}

	/**
	 * Get the virtual host used by the collector
	 *
	 * @return the virtual host
	 */
	@JsonProperty(VIRTUAL_HOST)
	public String getVirtualHost() {
		return this.virtualHost;
	}

	/**
	 * Set the virtual host used by the collector
	 *
	 * @param virtualHost
	 *            the virtual host
	 */
	@JsonProperty(VIRTUAL_HOST)
	public void setVirtualHost(final String virtualHost) {
		this.virtualHost = virtualHost;
	}

	/**
	 * Get the exchange name used by the collector
	 *
	 * @return the exchange name used by the collector
	 */
	@JsonProperty(EXCHANGE_NAME)
	public String getExchangeName() {
		return this.exchangeName;
	}

	/**
	 * Set the exchange name used by the collector
	 *
	 * @param exchangeName
	 *            the exchange name
	 */
	@JsonProperty(EXCHANGE_NAME)
	public void setExchangeName(final String exchangeName) {
		this.exchangeName = exchangeName;
	}

	@Override
	protected ToStringHelper stringHelper() {
		return
			super.stringHelper().
				add("brokerHost",this.brokerHost).
				add("brokerPort",this.brokerPort).
				add("virtualHost",this.virtualHost).
				add("exchangeName",this.exchangeName);
	}

}
