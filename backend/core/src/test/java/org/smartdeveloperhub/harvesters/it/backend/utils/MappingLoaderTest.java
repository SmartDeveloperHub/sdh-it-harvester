package org.smartdeveloperhub.harvesters.it.backend.utils;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Unity Test for the mapping loader util class.
 * @author imolina
 *
 */
public class MappingLoaderTest {
	
	private static final String FILE_TEST = "/utils/mappingTest.properties";

	MappingLoader mappingLoader;
	@Before
	public void setup() {

		mappingLoader = new MappingLoader();
	}

	@Test(expected=NullPointerException.class)
	public void shouldThrowExceptionWhenNoFileFound() throws IOException {
		
		mappingLoader.load("fake", HelperEnum.class);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionWhenNotEnumClass() throws IOException {
		
		mappingLoader.load(FILE_TEST, NotEnum.class);
	}

	@Test
	public void shouldLoadEmptyMapWhenNoConstants() throws IOException {
		
		Map<String, EmptyEnum> map = mappingLoader.load(FILE_TEST, EmptyEnum.class);
		then(map).isEmpty();
	}

	@Test
	public void shouldReturnThreeEntries() throws IOException {

		Map<String, HelperEnum> map = mappingLoader.load(FILE_TEST, HelperEnum.class);
		then(map).hasSize(3);

		then(map.get("v1")).isEqualTo(HelperEnum.VALUE_1);
		then(map.get("v2")).isEqualTo(HelperEnum.VALUE_2);
		then(map.get("v3")).isEqualTo(HelperEnum.VALUE_2);
	}

	private enum HelperEnum {

		VALUE_1, VALUE_2;
	}

	private enum EmptyEnum {
		
	}

	private class NotEnum {

	}
}
