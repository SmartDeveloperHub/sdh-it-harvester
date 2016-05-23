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

import static com.google.common.base.Preconditions.checkState;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NotificationPump {

	private static final class PumpExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			LOGGER.error("Notification pump thread died unexpectedly. Full stacktrace follows",e);
		}

	}

	private static final class PumpWorker implements Runnable {

		private final BlockingQueue<SuspendedNotification> notifications;
		private final NotificationListener listener;
		private volatile boolean stopped;

		PumpWorker(final BlockingQueue<SuspendedNotification> notifications, final NotificationListener listener) {
			this.notifications = notifications;
			this.listener = listener;
		}

		@Override
		public void run() {
			LOGGER.info("Started pumping notifications...");
			while(!this.stopped) {
				try {
					final SuspendedNotification notification=this.notifications.poll(QUEUE_POLL_TIMEOUT,QUEUE_POLL_TIMEUNIT);
					if(notification!=null) {
						LOGGER.trace("Resuming {}...",notification);
						notification.resume(this.listener);
						// Stop if interrupted while resuming a notification,
						// and the notification propagates the interruption.
						this.stopped|=Thread.currentThread().isInterrupted();
					}
				} catch (final InterruptedException e) {
					// Stop if interrupted on the queue
					this.stopped=true;
					LOGGER.info("Notification pumping interrupted.");
				}
			}
			LOGGER.info("Notification pumping finished");
			this.stopped=false;
		}

		/**
		 * Signal external shutdown request
		 */
		private void stop() {
			this.stopped=true;
		}

	}

	private static final Logger LOGGER=LoggerFactory.getLogger(NotificationPump.class);

	private static final TimeUnit QUEUE_POLL_TIMEUNIT = TimeUnit.MILLISECONDS;
	private static final int QUEUE_POLL_TIMEOUT       = 1000;

	private static final TimeUnit SHUTDOWN_TIMEUNIT = TimeUnit.MILLISECONDS;
	private static final int SHUTDOWN_TIMEOUT       = 5000;

	private final Lock lock;
	private final BlockingQueue<SuspendedNotification> notifications;

	private final NotificationListener listener;

	private Thread thread;

	private final PumpWorker worker;

	NotificationPump(final BlockingQueue<SuspendedNotification> notifications, final NotificationListener listener) {
		this.notifications=notifications;
		this.listener=listener;
		this.worker=new PumpWorker(this.notifications,this.listener);
		this.lock=new ReentrantLock();
	}

	void start() {
		final Thread workerThread = createWorkerThread();
		this.lock.lock();
		try {
			checkState(this.thread==null,"Pump already started");
			this.thread=workerThread;
			this.thread.start();
		} finally {
			this.lock.unlock();
		}
	}

	void stop() {
		this.lock.lock();
		try {
			checkState(this.thread!=null,"Pump not started");
			stopWorkerThreadGracefully();
			this.thread=null;
		} finally {
			this.lock.unlock();
		}
	}

	private void stopWorkerThreadGracefully() {
		if(!this.thread.isAlive()) {
			return;
		}
		// Request shutdown
		this.worker.stop();
		// Force shutdown
		this.thread.interrupt();
		try {
			this.thread.join(SHUTDOWN_TIMEUNIT.toMillis(SHUTDOWN_TIMEOUT));
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.warn("Interrupted while awaiting the termination of the worker thread",e);
		}
	}

	private Thread createWorkerThread() {
		final Thread result=new Thread(this.worker,"NotificationPump");
		result.setUncaughtExceptionHandler(new PumpExceptionHandler());
		result.setDaemon(true);
		result.setPriority(Thread.MAX_PRIORITY);
		return result;
	}

}
