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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0
 *   Bundle      : it-frontend-core-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ldp4j.application.data.Name;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.SessionTerminationException;
import org.ldp4j.application.session.WriteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.commit.CommitHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentHandler;
import org.smartdeveloperhub.harvesters.it.frontend.component.ComponentKey;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.contributor.ContributorHandler;
import org.smartdeveloperhub.harvesters.it.frontend.harvester.HarvesterHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueHandler;
import org.smartdeveloperhub.harvesters.it.frontend.issue.IssueKey;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.project.ProjectHandler;
import org.smartdeveloperhub.harvesters.it.frontend.util.IdentityUtil;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionContainerHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionHandler;
import org.smartdeveloperhub.harvesters.it.frontend.version.VersionKey;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Change;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent.Entity;

import com.google.common.collect.Sets;

final class PublisherHelper {

	private static final Logger LOGGER=LoggerFactory.getLogger(PublisherHelper.class);

	private PublisherHelper() {
	}

	static void closeGracefully(final WriteSession session) {
		if(session!=null) {
			try {
				session.close();
			} catch (final SessionTerminationException e) {
				LOGGER.warn("Could not terminate session",e);
			}
		}
	}

	static void publishHarvester(final WriteSession session, final URI target, final List<String> projects) {
		final Name<URI> harvesterName = IdentityUtil.collectorName(target);

		final ResourceSnapshot harvesterSnapshot=
			session.
				find(
					ResourceSnapshot.class,
					harvesterName,
					HarvesterHandler.class);

		harvesterSnapshot.
			createAttachedResource(
				ContainerSnapshot.class,
				HarvesterHandler.HARVESTER_CONTRIBUTORS,
				harvesterName,
				ContributorContainerHandler.class);

		harvesterSnapshot.
			createAttachedResource(
				ContainerSnapshot.class,
				HarvesterHandler.HARVESTER_COMMITS,
				harvesterName,
				CommitContainerHandler.class);

		harvesterSnapshot.
			createAttachedResource(
				ContainerSnapshot.class,
				HarvesterHandler.HARVESTER_PROJECTS,
				harvesterName,
				ProjectContainerHandler.class);

		publishProjects(session,target,projects);
	}

	static void publishProject(final WriteSession session, final URI target, final Project project) throws IOException {
		final ResourceSnapshot repositorySnapshot = findProjectResource(session, target, project.getId());
		publishProjectComponents(
			project.getId(),
			project.getComponents(),
			repositorySnapshot);
		publishProjectVersions(
			project.getId(),
			project.getVersions(),
			repositorySnapshot);
		publishProjectIssues(
			project.getId(),
			project.getIssues(),
			repositorySnapshot);
	}

	static void publishContributors(final WriteSession session, final URI target, final List<String> contributors) {
		final ContainerSnapshot userContainer=
			session.find(
				ContainerSnapshot.class,
				IdentityUtil.collectorName(target),
				ContributorContainerHandler.class);
		for(final String contributorId:contributors){
			final Name<String> contributorName = IdentityUtil.contributorName(contributorId);
			userContainer.addMember(contributorName);
			LOGGER.debug("Published resource for contributor {}",contributorId);
		}
	}

	static void unpublishContributors(final WriteSession session, final List<String> contributorIds) {
		for(final String contributorId:contributorIds){
			final Name<String> contributorName = IdentityUtil.contributorName(contributorId);
			final ResourceSnapshot contributorResource = session.find(ResourceSnapshot.class, contributorName, ContributorHandler.class);
			if(contributorResource!=null) {
				session.delete(contributorResource);
			}
		}
	}

	static void publishCommits(final WriteSession session, final URI target, final List<String> commits) {
		final ContainerSnapshot userContainer=
				session.find(
					ContainerSnapshot.class,
					IdentityUtil.collectorName(target),
					CommitContainerHandler.class);
			for(final String commitId:commits){
				final Name<String> commitName = IdentityUtil.commitName(commitId);
				userContainer.addMember(commitName);
				LOGGER.debug("Published resource for commit {}",commitId);
			}
	}

	static void unpublishCommits(final WriteSession session, final List<String> commitIds) {
		for(final String contributorId:commitIds){
			final Name<String> commitName = IdentityUtil.commitName(contributorId);
			final ResourceSnapshot commitResource = session.find(ResourceSnapshot.class, commitName, CommitHandler.class);
			if(commitResource!=null) {
				session.delete(commitResource);
			}
		}
	}

	static void publishProjects(final WriteSession session, final URI target, final List<String> projectIds) {
		final ContainerSnapshot projectContainer=findProjectContainer(session, target);
		for(final String projectId:projectIds) {
			final Name<String> projectName = IdentityUtil.projectName(projectId);
			final ResourceSnapshot projectSnapshot = session.find(ResourceSnapshot.class,projectName,ProjectHandler.class);
			if(projectSnapshot==null) {
				PublisherHelper.
					publishProject(projectContainer,projectId);
			}
		}
	}

	static void unpublishProjects(final WriteSession session, final List<String> projectIds) {
		for(final String projectId:projectIds) {
			final Name<String> projectName = IdentityUtil.projectName(projectId);
			final ResourceSnapshot projectSnapshot = session.find(ResourceSnapshot.class,projectName,ProjectHandler.class);
			if(projectSnapshot!=null) {
				session.delete(projectSnapshot);
			}
		}
	}

	static void updateProject(final WriteSession session, final ProjectUpdatedEvent event) throws IOException {
		final Name<String> projectName = IdentityUtil.projectName(event.getProject());
		final ResourceSnapshot projectSnapshot = session.find(ResourceSnapshot.class,projectName,ProjectHandler.class);
		if(projectSnapshot==null) {
			throw new IOException("Project "+event.getProject()+" does not exist");
		}

		/**
		 * The project resource is always updated to cater for modifications
		 * that do not affect child resources.
		 */
		session.modify(projectSnapshot);

		final Set<String> addedComponents=Sets.newLinkedHashSet();
		final Set<String> deletedComponents=Sets.newLinkedHashSet();
		final Set<String> addedVersions=Sets.newLinkedHashSet();
		final Set<String> deletedVersions=Sets.newLinkedHashSet();
		final Set<String> addedIssues=Sets.newLinkedHashSet();
		final Set<String> deletedIssues=Sets.newLinkedHashSet();

		for(final Entry<Entity, Map<String, List<Change>>> entry:event.getChanges().entrySet()) {
			switch(entry.getKey()) {
			case COMPONENT:
				populateEntityChanges(entry.getValue(), addedComponents, deletedComponents);
				break;
			case VERSION:
				populateEntityChanges(entry.getValue(), addedVersions, deletedVersions);
				break;
			default: // MUST BE ISSUE
				populateEntityChanges(entry.getValue(), addedIssues, deletedIssues);
				break;
			}
		}

		publishProjectComponents(
			event.getProject(),
			addedComponents,
			projectSnapshot);

		unpublishProjectComponents(
			event.getProject(),
			deletedComponents,
			session);

		publishProjectVersions(
			event.getProject(),
			addedVersions,
			projectSnapshot);

		unpublishProjectVersions(
			event.getProject(),
			deletedVersions,
			session);

		publishProjectIssues(
			event.getProject(),
			addedIssues,
			projectSnapshot);

		unpublishProjectIssues(
			event.getProject(),
			deletedIssues,
			session);
	}

	private static void populateEntityChanges(final Map<String, List<Change>> typeChanges, final Set<String> addedEntities, final Set<String> deletedEntities) {
		for(final Entry<String,List<Change>> entityChanges:typeChanges.entrySet()) {
			for(final Change change:entityChanges.getValue()) {
				switch(change.getAction()) {
				case CREATED:
					addedEntities.add(entityChanges.getKey());
					break;
				case DELETED:
					deletedEntities.add(entityChanges.getKey());
					break;
				case UPDATED:
					/**
					 * TODO: Check what do we do when facing update changes
					 */
				default:
					LOGGER.warn("Ignored {} change {}",change.getAction(),change);
					break;
				}
			}
		}
	}

	private static ContainerSnapshot findProjectContainer(final WriteSession session, final URI target) {
		return
			session.
				find(
					ContainerSnapshot.class,
					IdentityUtil.collectorName(target),
					ProjectContainerHandler.class);
	}

	private static ResourceSnapshot findProjectResource(final WriteSession session, final URI target, final String projectId) {
		final Name<String> projectName = IdentityUtil.projectName(projectId);
		ResourceSnapshot projectSnapshot = session.find(ResourceSnapshot.class,projectName,ProjectHandler.class);
		if(projectSnapshot==null) {
			LOGGER.warn("Could not find resource for project {}",projectId);
			projectSnapshot=
				publishProject(
					findProjectContainer(session,target),
					projectId);
		}
		return projectSnapshot;
	}

	private static ResourceSnapshot publishProject(final ContainerSnapshot projectContainer, final String projectId) {
		final Name<String> projectName=
			IdentityUtil.projectName(projectId);

		final ResourceSnapshot repository =
				projectContainer.addMember(projectName);

		repository.
			createAttachedResource(
				ContainerSnapshot.class,
				ProjectHandler.PROJECT_COMPONENTS,
				projectName,
				ComponentContainerHandler.class);

		repository.
			createAttachedResource(
				ContainerSnapshot.class,
				ProjectHandler.PROJECT_VERSIONS,
				projectName,
				VersionContainerHandler.class);

		repository.
			createAttachedResource(
				ContainerSnapshot.class,
				ProjectHandler.PROJECT_ISSUES,
				projectName,
				IssueContainerHandler.class);

		LOGGER.debug("Published resource for project {}",projectId);
		return repository;
	}

	private static void publishProjectComponents(final String repositoryId, final Set<String> componentIds, final ResourceSnapshot repositorySnapshot) throws IOException {
		if(componentIds.isEmpty()) {
			return;
		}
		final ContainerSnapshot componentContainer=
			getAttachedContainer(
				repositorySnapshot,
				ProjectHandler.PROJECT_COMPONENTS);
		try {
			for(final String componentId:componentIds){
				componentContainer.
					addMember(
						IdentityUtil.
							componentName(
								new ComponentKey(repositoryId,componentId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not publish components of project "+repositoryId,e);
		}
	}

	private static void unpublishProjectComponents(final String repositoryId, final Set<String> componentIds, final WriteSession session) throws IOException {
		if(componentIds.isEmpty()) {
			return;
		}
		try {
			for (final String componentId:componentIds){
				final ResourceSnapshot componentResource =
					session.
						find(
							ResourceSnapshot.class,
							IdentityUtil.
								componentName(new ComponentKey(repositoryId,componentId)),
							ComponentHandler.class);
				if(componentResource!=null) {
					session.delete(componentResource);
				}
			}
		} catch(final Exception e) {
			throw new IOException("Could not unpublish components of project "+repositoryId,e);
		}
	}

	private static void publishProjectVersions(final String projectId, final Set<String> versionIds, final ResourceSnapshot repositorySnapshot) throws IOException {
		if(versionIds.isEmpty()) {
			return;
		}
		final ContainerSnapshot versionContainer=
			getAttachedContainer(
				repositorySnapshot,
				ProjectHandler.PROJECT_VERSIONS);
		try {
			for(final String versionId:versionIds){
				versionContainer.
					addMember(
						IdentityUtil.
							versionName(
								new VersionKey(projectId,versionId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not publish versions of project "+projectId,e);
		}
	}

	private static void unpublishProjectVersions(final String projectId, final Set<String> versionIds, final WriteSession session) throws IOException {
		if(versionIds.isEmpty()) {
			return;
		}
		try {
			for (final String versionId:versionIds){
				final ResourceSnapshot versionResource =
					session.
						find(
							ResourceSnapshot.class,
							IdentityUtil.
								versionName(new VersionKey(projectId,versionId)),
							VersionHandler.class);
				if(versionResource!=null) {
					session.delete(versionResource);
				}
			}
		} catch(final Exception e) {
			throw new IOException("Could not unpublish versions of project "+projectId,e);
		}
	}

	private static void publishProjectIssues(final String projectId, final Set<String> issueIds, final ResourceSnapshot repositorySnapshot) throws IOException {
		if(issueIds.isEmpty()) {
			return;
		}
		final ContainerSnapshot issueContainer=
			getAttachedContainer(
				repositorySnapshot,
				ProjectHandler.PROJECT_ISSUES);
		try {
			for(final String issueId:issueIds){
				issueContainer.
					addMember(
						IdentityUtil.
							issueName(
								new IssueKey(projectId,issueId)));
			}
		} catch(final Exception e) {
			throw new IOException("Could not publish issues of project "+projectId,e);
		}
	}

	private static void unpublishProjectIssues(final String projectId, final Set<String> issueIds, final WriteSession session) throws IOException {
		if(issueIds.isEmpty()) {
			return;
		}
		try {
			for (final String issueId:issueIds){
				final ResourceSnapshot issueResource =
					session.
						find(
							ResourceSnapshot.class,
							IdentityUtil.
								issueName(new IssueKey(projectId,issueId)),
							IssueHandler.class);
				if(issueResource!=null) {
					session.delete(issueResource);
				}
			}
		} catch(final Exception e) {
			throw new IOException("Could not unpublish issues of project "+projectId,e);
		}
	}

	private static ContainerSnapshot getAttachedContainer(final ResourceSnapshot resource, final String attachmentId) {
		return (ContainerSnapshot)resource.attachmentById(attachmentId).resource();
	}

}
