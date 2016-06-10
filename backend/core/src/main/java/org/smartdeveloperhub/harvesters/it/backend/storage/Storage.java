package org.smartdeveloperhub.harvesters.it.backend.storage;

import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Version;

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
	 */
	public void storeIssues(Set<Issue> issues);

	/**
	 * Method for store list of {@link Project}s.
	 * @param projects set of projects
	 */
	public void storeProjects(Set<Project> projects);

	/**
	 * Method for store list of {@link Version}s.
	 * @param versions set of versions
	 */
	public void storeVersions(Set<Version> versions);

	/**
	 * Method for store list of {@link Component}s.
	 * @param components set of components
	 */
	public void storeComponents(Set<Component> components);
}
