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
