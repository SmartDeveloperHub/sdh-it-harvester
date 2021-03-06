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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Notifications;
import org.smartdeveloperhub.harvesters.it.frontend.BackendController;
import org.smartdeveloperhub.harvesters.it.notification.CollectorConfiguration;

import com.google.common.collect.ImmutableList;

public final class PublisherFactory {

	private static final Logger LOGGER=LoggerFactory.getLogger(PublisherFactory.class);

	private PublisherFactory() {
	}

	public static Publisher createPublisher(final BackendController controller) throws IOException {
		return
			new DynamicPublisher(
				controller,
				toConfiguration(
					controller.getTarget(),
					controller.
						getCollector().
							getNotifications()));
	}

	private static List<CollectorConfiguration> toConfiguration(final URI instance, final Notifications notifications) {
		final ImmutableList.Builder<CollectorConfiguration> builder=ImmutableList.builder();
		if(notifications!=null) {
			final CollectorConfiguration result = new CollectorConfiguration();
			result.setInstance(instance.toString());
			result.setBrokerHost(notifications.getBrokerHost());
			result.setBrokerPort(notifications.getBrokerPort());
			result.setVirtualHost(notifications.getVirtualHost());
			result.setExchangeName(notifications.getExchangeName());
			LOGGER.debug("Using collector configuration {}",result);
			builder.add(result);
		}
		return builder.build();
	}

}
