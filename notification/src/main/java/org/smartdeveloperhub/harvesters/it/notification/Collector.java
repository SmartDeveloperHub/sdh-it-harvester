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

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	Collector.INSTANCE,
	Collector.BROKER_HOST,
	Collector.BROKER_PORT,
	Collector.VIRTUAL_HOST,
	Collector.EXCHANGE_NAME
})
public class Collector {

	static final String INSTANCE = "instance";
	static final String BROKER_HOST = "brokerHost";
	static final String BROKER_PORT = "brokerPort";
	static final String VIRTUAL_HOST = "virtualHost";
	static final String EXCHANGE_NAME = "exchangeName";

	@JsonProperty(INSTANCE)
	private String instance;

	@JsonProperty(BROKER_HOST)
	private String brokerHost;

	@JsonProperty(BROKER_PORT)
	private Integer brokerPort = 5672;

	@JsonProperty(VIRTUAL_HOST)
	private String virtualHost = "/";

	@JsonProperty(EXCHANGE_NAME)
	private String exchangeName;

	/**
	 * Get the collector's API base endpoint
	 *
	 * @return the instance
	 */
	@JsonProperty(INSTANCE)
	public String getInstance() {
		return this.instance;
	}

	/**
	 * Set the collector's API base endpoint
	 *
	 * @param instance
	 *            the collector's API base endpoint
	 */
	@JsonProperty(INSTANCE)
	public void setInstance(final String instance) {
		this.instance = instance;
	}

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
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
