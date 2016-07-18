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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.backend:it-backend-core:0.1.0
 *   Bundle      : it-backend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class to load mappings values.
 * @author imolina
 *
 */
public class MappingLoader {

	/**
	 * Loads the mapping values from properties file.
	 * 
	 * @param filename of the properties file with the mapping values.
	 * @param enumType Enumerate class.
	 * @return map with enumType relation.
	 * @throws IOException when properties file not found.
	 */
	public <T> Map<String, T> load(String filename, Class<T> enumType) throws IOException {

		Map<String, T> mapping = new HashMap<>();
		Properties properties = new Properties();

		if (!enumType.isEnum()) {

			throw new IllegalArgumentException("Loader requires an Enum class.");
		}

		properties.load(this.getClass().getResourceAsStream(filename));

		for (T entry : enumType.getEnumConstants()) {

			String values = properties.getProperty(((Enum<?>) entry).name());

			for (String value : values.split("\\|")) {

				mapping.put(value, entry);
			}
		}

		return mapping;
	}
}
