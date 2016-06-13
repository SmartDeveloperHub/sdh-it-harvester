package org.smartdeveloperhub.harvesters.it.backend.storage;

import org.smartdeveloperhub.harvesters.it.backend.Component;
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
	 * Method for store list of {@link issue}s.
	 * @param issues set of issues
	 * @throws IOException 
	 */
	public void storeIssues(String projectId, Set<Issue> issues) throws IOException;

	public Set<Issue> loadIssues(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Project}s.
	 * @param projects set of projects
	 */
	public void storeProjects(Set<Project> projects) throws IOException;

	public Set<Project> loadProjects() throws IOException;

	/**
	 * Method for store list of {@link Version}s.
	 * @param versions set of versions
	 */
	public void storeVersions(String projectId, Set<Version> versions) throws IOException;

	public Set<Version> loadVersions(String projectId) throws IOException;

	/**
	 * Method for store list of {@link Component}s.
	 * @param components set of components
	 */
	public void storeComponents(String projectId, Set<Component> components) throws IOException;

	public Set<Component> loadComponents(String projectId) throws IOException;
}
