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
package org.smartdeveloperhub.harvesters.it.testing;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.RiotException;

import com.google.common.io.BaseEncoding;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.jayway.restassured.response.Response;

public final class TestingUtil {

	public static String loadResource(final String resourceName) {
		try {
			final InputStream resourceAsStream =
				Thread.
					currentThread().
						getContextClassLoader().
							getResourceAsStream(resourceName);
			if(resourceAsStream==null) {
				throw new AssertionError("Could not find resource '"+resourceName+"'");
			}
			return IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new AssertionError("Could not load resource '"+resourceName+"'");
		}
	}

	public static String resolve(final URL base, final String path) {
		return base.toString()+path;
	}

	public static String relativize(final URL base, final String path) {
		try {
			return base.toURI().relativize(URI.create(path)).toString();
		} catch (final URISyntaxException e) {
			fail(String.format("Could not relativize %s from %s (%s)",path,base,e.getMessage()));
			return null;
		}
	}

	public static Model asModel(final Response response, final String base) {
		final String rawData = new String(response.asByteArray(),StandardCharsets.UTF_8);
		try {
			return
				ModelFactory.
					createDefaultModel().
						read(
							new StringReader(rawData),
							base,
							"TURTLE");
		} catch (final RiotException e) {
			final String encode = BaseEncoding.base64().encode(rawData.getBytes());
			System.err.printf("> Base: %s%n> Failure: %s%n> Response body:%n%s%n> Base64 encoded body:%n%s%n",base,e.getMessage(),rawData,encode);
			fail("Could not parse response for "+base+" as Turtle RDF data");
			return null; // Should not get to here...
		}
	}

	public static Model asModel(final Response response, final URL base, final String path) {
		return asModel(response,resolve(base,path));
	}

	public static String interpolate(final String input, final String parameter, final String value) {
		return input.replaceAll("\\$\\{"+parameter+"\\}", value);
	}

}
