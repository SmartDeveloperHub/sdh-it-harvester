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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-backend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.apache.http.ExceptionLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;

import java.util.List;

/**
 * Unity test for Fetcher class.
 * @author imolina
 *
 */
public class FetcherTest {

	@Mock Crawler crawler;
	@Captor private ArgumentCaptor<Long> captor;

	private Fetcher fetcher;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);
		fetcher = new Fetcher(crawler);
	}

	@Test
	public void shouldCallCrawlerForFirstTimeWithZeroDate() {

		fetcher.run();

		verify(crawler).collect(captor.capture());
		then(captor.getValue()).isEqualTo(0L);
	}

	@Test
	public void shouldCallCrawlerForASecondTimeWithCurrentDateInMiliseconds() {

		long before = System.currentTimeMillis();
		fetcher.run();
		long after = System.currentTimeMillis();

		fetcher.run();

		verify(crawler, times(2)).collect(captor.capture());
		then(captor.getValue()).isBetween(before, after);
	}

	@Test
	public void shouldCaptureUncheckedExceptionsFromCrawler() {

		doThrow(new IllegalArgumentException()).when(crawler).collect(0L);

		fetcher.run();
	}
}
