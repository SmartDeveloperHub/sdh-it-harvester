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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;

final class AcknowledgeableNotification implements Notification {

	private static final Logger LOGGER=LoggerFactory.getLogger(AcknowledgeableNotification.class);

	private final long deliveryTag;
	private final Channel channel;
	private boolean acknowledged;

	AcknowledgeableNotification(final Channel channel, final long deliveryTag) {
		this.deliveryTag = deliveryTag;
		this.channel = channel;
	}

	@Override
	public void consume() {
		acknowledge();
		LOGGER.trace("Consumed message {{}}",this.deliveryTag);
	}

	@Override
	public void discard(final Throwable exception) {
		acknowledge();
		LOGGER.trace("Discarded message {{}}. Full stacktrace follows",this.deliveryTag,exception);
	}

	boolean isAcknowledged() {
		return this.acknowledged;
	}

	void acknowledge() {
		checkState(!this.acknowledged,"Notification for message %s has been already acknowledged",this.deliveryTag);
		try {
			this.channel.basicAck(this.deliveryTag, false);
			this.acknowledged=true;
		} catch (final IOException e) {
			LOGGER.warn("Could not acknowledge message {}. Full stacktrace follows",this.deliveryTag,e);
		}
	}
}