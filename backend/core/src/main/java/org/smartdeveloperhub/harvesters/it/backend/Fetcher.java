package org.smartdeveloperhub.harvesters.it.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;

/**
 * Class that runs collector with the last update date.
 * @author imolina
 *
 */
public class Fetcher implements Runnable {

	private static final Logger logger =
										LoggerFactory.getLogger(Fetcher.class);

	private Crawler collector;
	private long lastUpdate;
	
	public Fetcher(Crawler collector) {

		this.collector = collector;
		this.lastUpdate = 0L;
	}

	public void run() {

		try {
			collector.collect(lastUpdate);
			lastUpdate = System.currentTimeMillis();
		} catch (Exception e) {
			logger.error("Exception while running Fetcher. {}", e);
		}
	}
}
