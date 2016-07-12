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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.collector;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.frontend.testing.TestingService;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.Activity;
import org.smartdeveloperhub.harvesters.it.frontend.testing.collector.ActivityListener;
import org.smartdeveloperhub.harvesters.it.frontend.testing.handlers.MoreHandlers.APIVersion;
import org.smartdeveloperhub.harvesters.it.testing.util.AppAssembler;
import org.smartdeveloperhub.harvesters.it.testing.util.Application;


public final class StandaloneTestingCollector {

	private static final int        DEFAULT_PORT                     = 8080;
	private static final APIVersion DEFAULT_IT_COLLECTOR_API_VERSION = APIVersion.v1;

	public static void main(final String... args) {
		if(args.length!=1) {
			System.err.printf("Invalid argument number: 1 argument required (%d found)%n", args.length);
			showUsage(args);
			System.exit(-1);
		}
		final File localData = new File(args[0]);
		if(!localData.isFile()) {
			System.err.printf("Error: %s is not a file%n",localData);
			showUsage(args);
			System.exit(-2);
		}

		System.out.printf("Standalone Testing IT Collector%s%n",serviceVersion());
		final TestingService service =
			TestingService.
				builder().
					port(port()).
					apiVersion(apiVersion()).
					exchangeName("it.collector.mock").
					collector(new LocalDataBasedCollectorProvider(port(),localData)).
					listener(new ActivityListener() {
						@Override
						public void onActivity(final Activity<?> activity) {
							try {
								System.out.println(Entities.marshallEntity(activity));
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}).
					build();
		try {
			service.start();
			awaitTerminationRequest();
			service.shutdown();
		} catch (final IOException e) {
			System.err.println("Could not start testing collector. Full stacktrace follows:");
			e.printStackTrace(System.err);
		}
	}

	private static void showUsage(final String... args) {
		System.err.printf("USAGE: %s <local-data-file>%n",AppAssembler.applicationName(StandaloneTestingCollector.class));
		System.err.printf(" <output file> : Name of the file where the local data is available.%n");
		Application.logContext(args);
	}

	private static String serviceVersion() {
		final String build=serviceBuild();
		final String version=System.getProperty("service.version","");
		if(version.isEmpty()) {
			return version;
		}
		return " v"+version+build;
	}

	private static String serviceBuild() {
		String build = System.getProperty("service.build","");
		if(!build.isEmpty()) {
			build="-b"+build;
		}
		return build;
	}

	private static int port() {
		final String preference=
			System.
				getProperty(
					"service.port",
					Integer.toString(DEFAULT_PORT));
		int port;
		try {
			port=Integer.parseInt(preference);
		} catch (final Exception e) {
			System.err.printf("Ignored invalid port '%s'%n",preference);
			port=DEFAULT_PORT;
		}
		return port;
	}

	private static APIVersion apiVersion() {
		final String preference=
			System.
				getProperty(
					"service.it.collector.api",
					DEFAULT_IT_COLLECTOR_API_VERSION.name());
		APIVersion version;
		try {
			version = APIVersion.valueOf(preference);
		} catch (final Exception e) {
			System.err.printf("Ignored invalid IT Collector API version '%s'%n",preference);
			version=DEFAULT_IT_COLLECTOR_API_VERSION;
		}
		return version;
	}

	private static void awaitTerminationRequest() {
		System.out.println("Hit <ENTER> to exit...");
		try(final Scanner scanner = new Scanner(System.in)) {
			String readString = scanner.nextLine();
			while(readString != null) {
				if (readString.isEmpty()) {
					break;
				}
				if (scanner.hasNextLine()) {
					readString = scanner.nextLine();
				} else {
					readString = null;
				}
			}
		}
	}

}
