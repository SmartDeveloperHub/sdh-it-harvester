package org.smartdeveloperhub.harvesters.it.backend;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.Test;
import org.smartdeveloperhub.harvesters.it.backend.crawler.jira.ChangeLogProperty;

/**
 * This test is only for achieve 100% coverage on Enum due to compiler generated
 * methods.  
 * 
 * @author imolina
 *
 */
public class EnumSyntheticTest {

	@Test
	public void shouldCoverValueOfAndValue() {

		for (ChangeLogProperty change : ChangeLogProperty.values()) {
			
			then(ChangeLogProperty.valueOf(change.name())).isSameAs(change);
		}
	}
}
