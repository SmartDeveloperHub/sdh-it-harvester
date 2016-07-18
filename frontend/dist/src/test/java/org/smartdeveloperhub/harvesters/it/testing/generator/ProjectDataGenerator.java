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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0
 *   Bundle      : it-frontend-dist-0.1.0.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.frontend.controller.LocalData;
import org.smartdeveloperhub.harvesters.it.testing.util.AppAssembler;
import org.smartdeveloperhub.harvesters.it.testing.util.Application;

import com.google.common.io.Files;

public final class ProjectDataGenerator {

	private static final Logger LOGGER=LoggerFactory.getLogger(ProjectDataGenerator.class);

	private final LocalData data;

	private ProjectDataGenerator() {
		this.data=new LocalData();
		this.data.getContributors().addAll(Contributors.all());
	}

	private void generateProjectData(final ProjectConfiguration configuration) {
		final ProjectActivityGenerator generator =
			new ProjectActivityGenerator(configuration);
		generator.addData(this.data);
	}

	private LocalData data() {
		return this.data;
	}

	public static void main(final String... args) {
		final Path path = getOutputPath(args);
		try {
			System.out.printf("Project Test Data Generator %s%n",serviceVersion());
			final LocalData localData = generateData();
			Files.
				write(
					Entities.marshallEntity(localData),
					path.toFile(),
					StandardCharsets.UTF_8);
		} catch (final IOException e) {
			LOGGER.error("Project generation failed",e);
			System.exit(-1);
		}
	}

	private static Path getOutputPath(final String... args) {
		if(args.length!=1) {
			System.err.printf("Invalid argument number: 1 argument required (%d found)%n", args.length);
			showUsage(args);
			System.exit(-1);
		}
		final Path path = Paths.get(args[0]);
		if(path.toFile().isDirectory()) {
			System.err.printf("Error: %s is a directory%n",path);
			showUsage(args);
			System.exit(-2);
		}
		return path;
	}

	private static void showUsage(final String... args) {
		System.err.printf("USAGE: %s <output file>%n",AppAssembler.applicationName(ProjectDataGenerator.class));
		System.err.printf(" <output file> : Name of the file where the generated data will be stored.%n");
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

	private static LocalData generateData() {
		final ProjectDataGenerator generator=new ProjectDataGenerator();
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-sdh").
						name("Smart Developer Hub").
						contributors(
							Contributors.alexFernandez(),
							Contributors.alejandroVera(),
							Contributors.carlosBlanco(),
							Contributors.fernandoSerena(),
							Contributors.andresGarciaSilva(),
							Contributors.miguelEstebanGutierrez(),
							Contributors.ignacioMolina(),
							Contributors.mariaPoveda()).
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-sdh-agora").
						name("Graph-based Query System for LDP").
						contributors(
							Contributors.fernandoSerena(),
							Contributors.miguelEstebanGutierrez()).
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-sdh-metrics").
						name("Metric Services").
						contributors(
							Contributors.fernandoSerena()).
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-sdh-web").
						name("Web Framework and Dashboards").
						contributors(
							Contributors.alejandroVera(),
							Contributors.carlosBlanco()).
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-sdh-harvesters").
						name("Harvesters").
						contributors(
							Contributors.alexFernandez(),
							Contributors.andresGarciaSilva(),
							Contributors.miguelEstebanGutierrez(),
							Contributors.ignacioMolina()).
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-ldp4j").
						name("Linked Data Platform for Java").
						contributors(
							Contributors.miguelEstebanGutierrez()).
						startedOn("2014-04-23T08:34:38Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-jenkins").
						name("Jenkins").
						startedOn("2015-05-12T14:41:20Z").
						build());
		generator.
			generateProjectData(
				ProjectConfiguration.
					builder().
						id("project-phoenix").
						name("phoenix").
						startedOn("2015-05-12T14:41:20Z").
						build());
		return generator.data();
	}
}
