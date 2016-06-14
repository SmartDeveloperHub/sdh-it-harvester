package org.smartdeveloperhub.harvesters.it.backend.storage;

import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;

import java.io.IOException;
import java.util.Set;

/**
 * Interface for object storage.
 * @author imolina
 *
 */
public interface Storage {

	/**
	 * Method for store list of {@link Project}s.
	 * @param projects set of projects
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeProjects(Set<Project> projects) throws IOException;

	/**
	 * Method to load previously stored {@link Project}s information.
	 * @return {@link Project} project entities.
	 * @throws IOException when a load error occurs.
	 */
	public Set<Project> loadProjects() throws IOException;

	/**
	 * Method for store list of {@link Issue}s.
	 * @param projectId Project identifier.
	 * @param issues set of issues
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeIssues(String projectId, Set<Issue> issues) throws IOException;

	/**
	 * Method to load previously stored {@link Issue}s.
	 * @param projectId Project identifier.
	 * @return {@link Issue}s entities.
	 * @throws IOException when a load error occurs.
	 */
	public Set<Issue> loadIssues(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Version}s.
	 * @param projectId Project identifier.
	 * @param versions set of versions
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeVersions(String projectId, Set<Version> versions) throws IOException;

	/**
	 * Method to load previously stored {@link Version}s.
	 * @param projectId Project identifier.
	 * @return {@link Version} entities.
	 * @throws IOException when a load error occurs.
	 */
	public Set<Version> loadVersions(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Component}s.
	 * @param projectId Project identifier.
	 * @param components set of components.
	 * @throws IOException when a storage error occurs. 
	 */
	public void storeComponents(String projectId, Set<Component> components) throws IOException;

	/**
	 * Method to load previously stored {@link Component}s.
	 * @param projectId Project identifier.
	 * @return {@link Component}s entities.
	 * @throws IOException when a load error occurs.
	 */
	public Set<Component> loadComponents(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Contributor}s.
	 * @param contributors Set of global Jira {@link Contributor}s.
	 * @throws IOException when a storage error occurs.
	 */
	public void storeContriburos(Set<Contributor> contributors) throws IOException;

	/**
	 * Method to load previously stored {@link Contributor}s.
	 * @return {@link Contributor} entities.
	 * @throws IOException when a load error occurs.
	 */
	public Set<Contributor> loadContributors() throws IOException;
}
