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

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

public final class Entities {

	private static final class DateTimeSerializer extends JsonSerializer<DateTime> {

		@Override
		public void serialize(final DateTime value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
			jgen.writeString(value.toString());
		}

	}

	private static final class DateTimeDeserializer extends JsonDeserializer<DateTime> {

		@Override
		public DateTime deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
			return new DateTime(jp.getValueAsString());
		}

	}

	private static final class DurationSerializer extends JsonSerializer<Duration> {

		@Override
		public void serialize(final Duration value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
			jgen.writeString(value.toString());
		}

	}

	private static final class DurationDeserializer extends JsonDeserializer<Duration> {

		@Override
		public Duration deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
			return new Duration(jp.getValueAsString());
		}

	}

	private Entities() {
	}

	private static ObjectMapper parsingMapper() {
		final SimpleModule module = new SimpleModule();
		module.addDeserializer(DateTime.class, new DateTimeDeserializer());
		module.addDeserializer(Duration.class, new DurationDeserializer());
		return
			new ObjectMapper().
				enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).
				enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).
				enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS).
				registerModule(module);
	}

	private static ObjectMapper writingMapper() {
		final SimpleModule module = new SimpleModule();
		module.addSerializer(DateTime.class, new DateTimeSerializer());
		module.addSerializer(Duration.class, new DurationSerializer());
		return
			new ObjectMapper().
				enable(SerializationFeature.INDENT_OUTPUT).
				registerModule(module);
	}

	public static <T> List<T> unmarshallList(final String value, final Class<? extends T> clazz) throws IOException {
		final JavaType type=
			TypeFactory.
				defaultInstance().
					constructParametricType(List.class,clazz);
		return
			parsingMapper().
				readValue(
					value,
					type);
	}

	public static <T extends Entity> T unmarshallEntity(final String value, final Class<? extends T> clazz) throws IOException {
		return
			parsingMapper().
				readValue(value,clazz);
	}

	public static <T> String marshallList(final List<T> list) throws IOException {
		return writingMapper().writeValueAsString(list);
	}

	public static String marshallEntity(final Entity entity) throws IOException {
		return writingMapper().writeValueAsString(entity);
	}


}
