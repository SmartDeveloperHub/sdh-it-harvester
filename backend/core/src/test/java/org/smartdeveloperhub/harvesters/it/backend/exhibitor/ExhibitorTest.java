package org.smartdeveloperhub.harvesters.it.backend.exhibitor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartdeveloperhub.harvesters.it.backend.Notifications;
import org.smartdeveloperhub.harvesters.it.backend.storage.Storage;

public class ExhibitorTest {

	private static final String VERSION = "0.0.0";

	private Exhibitor exhibitor;

	@Mock private Storage storage;
	@Mock private Notifications notifications;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);
		exhibitor = new Exhibitor(VERSION, notifications, storage);
	}

	@Test(expected=NullPointerException.class)
	public void shouldNotAcceptNullVersion() {

		exhibitor = new Exhibitor(null, notifications, storage);
	}

	@Test(expected=NullPointerException.class)
	public void shouldNotAcceptNullNotifications() {

		exhibitor = new Exhibitor(VERSION, null, storage);
	}

	@Test(expected=NullPointerException.class)
	public void shouldNotAcceptNullStorage() {

		exhibitor = new Exhibitor(VERSION, notifications, null);
	}
}
