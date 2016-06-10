package org.smartdeveloperhub.harvesters.it.backend.crawler;

/**
 * Interface to collect IT information.
 * @author imolina
 *
 */
public interface Crawler {

	/**
	 * This method collects IT information later than lastUpdateTimeStamp.
	 * @param lastUpdateTimestamp maximum age date of issue changes.
	 */
	public void collect(long lastUpdateTimestamp);
}
