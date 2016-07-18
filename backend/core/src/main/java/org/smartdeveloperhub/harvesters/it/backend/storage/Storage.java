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
package org.smartdeveloperhub.harvesters.it.backend.storage;

import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.State;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.backend.crawler.Crawler;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Interface for object storage.
 * @author imolina
 *
 */
public interface Storage {

	/**
	 * Method for store list of {@link Project}s.
	 * @param projects collection of projects
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeProjects(Collection<Project> projects) throws IOException;

	/**
	 * Method to load previously stored {@link Project}s information.
	 * @return {@link Project} project entities.
	 * @throws IOException when a load error occurs.
	 */
	public Map<String, Project> loadProjects() throws IOException;

	/**
	 * Method for store list of {@link Issue}s.
	 * @param projectId Project identifier.
	 * @param issues collection of issues
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeIssues(String projectId, Collection<Issue> issues)
															throws IOException;

	/**
	 * Method to load previously stored {@link Issue}s.
	 * @param projectId Project identifier.
	 * @return {@link Issue}s entities.
	 * @throws IOException when a load error occurs.
	 */
	public Map<String, Issue> loadIssues(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Version}s.
	 * @param projectId Project identifier.
	 * @param versions collection of versions
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeVersions(String projectId, Collection<Version> versions)
															throws IOException;

	/**
	 * Method to load previously stored {@link Version}s.
	 * @param projectId Project identifier.
	 * @return {@link Version} entities.
	 * @throws IOException when a load error occurs.
	 */
	public Map<String, Version> loadVersions(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Component}s.
	 * @param projectId Project identifier.
	 * @param components collection of components.
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeComponents(String projectId, Collection<Component> components)
															throws IOException;

	/**
	 * Method to load previously stored {@link Component}s.
	 * @param projectId Project identifier.
	 * @return {@link Component}s entities.
	 * @throws IOException when a load error occurs.
	 */
	public Map<String, Component> loadComponents(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Contributor}s.
	 * @param contributors collection of global Jira {@link Contributor}s.
	 * @throws IOException when a storage error occurs.
	 */
	public void storeContriburos(Map<String, Contributor> contributors)
															throws IOException;

	/**
	 * Method to load previously stored {@link Contributor}s.
	 * @return {@link Contributor} entities.
	 * @throws IOException when a load error occurs.
	 */
	public Map<String, Contributor> loadContributors() throws IOException;

	/**
	 * Method that store information relative to the {@link Crawler} activity.
	 * @param state current state of the crawler.
	 * @throws IOException when a storage error occurs.
	 */
	public void storeState(State state) throws IOException;

	/**
	 * Method that loads the last stored state of the {@link Crawler} activity.
	 * @return last state of the {@link Crawler}.
	 * @throws IOException when a load error occurs.
	 */
	public State loadState() throws IOException;
}
