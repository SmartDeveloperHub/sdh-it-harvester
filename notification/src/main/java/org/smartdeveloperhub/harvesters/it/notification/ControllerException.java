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


final class ControllerException extends Exception {

	private static final long serialVersionUID = 7249473927408663886L;

	private final String brokerHost;
	private final int brokerPort;
	private final String virtualHost;

	ControllerException(final String brokerHost, final int brokerPort, final String virtualHost, final String message, final Throwable cause) {
		super(message, cause);
		this.brokerHost = brokerHost;
		this.brokerPort = brokerPort;
		this.virtualHost = virtualHost;
	}

	/**
	 * Get the host IP or name of the broker that caused the failure.
	 *
	 * @return the broker host
	 */
	String getBrokerHost() {
		return this.brokerHost;
	}

	/**
	 * Get the port of the broker that caused the failure.
	 *
	 * @return the broker port
	 */
	int getBrokerPort() {
		return this.brokerPort;
	}

	/**
	 * Get the virtual host of the broker that caused the failure.
	 *
	 * @return the virtual host
	 */
	String getVirtualHost() {
		return this.virtualHost;
	}

}