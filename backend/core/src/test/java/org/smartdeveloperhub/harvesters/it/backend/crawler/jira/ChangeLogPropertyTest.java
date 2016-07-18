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
