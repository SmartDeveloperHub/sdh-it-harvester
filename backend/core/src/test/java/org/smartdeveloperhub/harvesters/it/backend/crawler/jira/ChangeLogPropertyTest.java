package org.smartdeveloperhub.harvesters.it.backend.crawler.jira;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unity test paramatrized for ChangeLogProperty Enum.
 * @author imolina
 *
 */
@RunWith(Parameterized.class)
public class ChangeLogPropertyTest {

	@Parameter(0) public ChangeLogProperty property;
	@Parameter(1) public String propertyName;
	
	@Parameters(name="property={0}, propertyName={1}")
	public static Object[][] data() {
		return new Object[][] {{ChangeLogProperty.STATUS, "status"},
								{ChangeLogProperty.ASSIGNEE,  "assignee"},
								{ChangeLogProperty.TITLE, "summary"},
								{ChangeLogProperty.DESCRIPTION, "description"},
								{ChangeLogProperty.ISSUE_TYPE, "issuetype"},
								{ChangeLogProperty.DUE_DATE, "duedate"},
								{ChangeLogProperty.COMPONENT, "Component"},
								{ChangeLogProperty.PRIORITY, "priority"},
								{ChangeLogProperty.SEVERITY, "severity"},
								{ChangeLogProperty.ESTIMATED_TIME, "timeestimate"},
								{ChangeLogProperty.VERSION, "Fix Version"},
								{ChangeLogProperty.BLOCKERS, "Link"}};
	}

	@Test
	public void shouldAcceptPropertyName() {

		then(property.is(propertyName));
	}
}
