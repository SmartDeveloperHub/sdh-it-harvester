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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-api:0.1.0
 *   Bundle      : it-backend-api-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assume.assumeThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.ldp4j.commons.testing.Utils;

public class DateTimesTest {

	@Test
	public void isValidUtilityClass() {
		assertThat(Utils.isUtilityClass(DateTimes.class),equalTo(true));
	}

	@Test
	public void shouldReturnNullDateTimeForNullDateTime() throws Exception {
		assertThat(DateTimes.toUTC(null),nullValue());
	}

	@Test
	public void shouldReturnUTCDateTimeForNonUTCDateTime() throws Exception {
		final DateTimeZone zone = DateTimeZone.forID("Europe/London");
		final DateTime original = new DateTime(zone);
		final DateTime normalized = DateTimes.toUTC(original);
		assumeThat(original.getZone(),equalTo(zone));
		assertThat(normalized,not(equalTo(original)));
		assertThat(normalized.getZone(),equalTo(DateTimeZone.UTC));
		assertThat(normalized.getMillis(),equalTo(original.getMillis()));
	}

	@Test
	public void shouldReturnSameDateTimeForUTCDateTime() throws Exception {
		final DateTime original = new DateTime(DateTimeZone.UTC);
		final DateTime normalized = DateTimes.toUTC(original);
		assertThat(normalized,equalTo(original));
	}

}
