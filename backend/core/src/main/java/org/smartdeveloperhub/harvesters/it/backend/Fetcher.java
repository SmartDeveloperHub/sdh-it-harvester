package org.smartdeveloperhub.harvesters.it.backend;

import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;

public class Fetcher implements Runnable {

	private Crawler collector;
	private long lastUpdate;
	
	public Fetcher(Crawler collector) {

		this.collector = collector;
		this.lastUpdate = 0L;
	}

	public void run() {

		collector.collect(lastUpdate);
		lastUpdate = System.currentTimeMillis();
	}
}
