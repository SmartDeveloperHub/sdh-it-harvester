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
