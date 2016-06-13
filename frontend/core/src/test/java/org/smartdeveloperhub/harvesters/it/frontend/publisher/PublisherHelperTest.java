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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-core:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-core-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.publisher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ldp4j.application.session.AttachmentSnapshot;
import org.ldp4j.application.session.ContainerSnapshot;
import org.ldp4j.application.session.ResourceSnapshot;
import org.ldp4j.application.session.SessionTerminationException;
import org.ldp4j.application.session.WriteSession;
import org.ldp4j.commons.testing.Utils;
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
import org.smartdeveloperhub.harvesters.it.notification.event.Modification;
import org.smartdeveloperhub.harvesters.it.notification.event.ProjectUpdatedEvent;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PublisherHelperTest {

	@Test
	public void verifyIsUtilityClass() {
		assertThat(Utils.isUtilityClass(PublisherHelper.class),equalTo(true));
	}

	@Test
	public void testCloseQuietly$nullSession() throws SessionTerminationException {
		PublisherHelper.closeGracefully(null);
	}

	@Test
	public void testCloseQuietly$happyPath(@Mocked final WriteSession session) throws SessionTerminationException {
		new Expectations() {{
			session.close();
		}};
		PublisherHelper.closeGracefully(session);
	}

	@Test
	public void testCloseQuietly$withFailure(@Mocked final WriteSession session) throws SessionTerminationException {
		new Expectations() {{
			session.close();this.result=new SessionTerminationException("Failure");
		}};
		PublisherHelper.closeGracefully(session);
	}

	@Test
	public void testPublishHarvester(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource, @Mocked final ContainerSnapshot container) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.collectorName(target),HarvesterHandler.class);this.result=resource;
			resource.createAttachedResource(ContainerSnapshot.class, HarvesterHandler.HARVESTER_COMMITS, IdentityUtil.collectorName(target), CommitContainerHandler.class);this.result=container;
			resource.createAttachedResource(ContainerSnapshot.class, HarvesterHandler.HARVESTER_CONTRIBUTORS, IdentityUtil.collectorName(target), ContributorContainerHandler.class);this.result=container;
			resource.createAttachedResource(ContainerSnapshot.class, HarvesterHandler.HARVESTER_PROJECTS, IdentityUtil.collectorName(target), ProjectContainerHandler.class);this.result=container;
			session.find(ContainerSnapshot.class,IdentityUtil.collectorName(target),ProjectContainerHandler.class);this.result=container;
			session.find(ResourceSnapshot.class,IdentityUtil.projectName("1"),ProjectHandler.class);this.result=null;
			container.addMember(IdentityUtil.projectName("1"));
			session.find(ResourceSnapshot.class,IdentityUtil.projectName("2"),ProjectHandler.class);this.result=null;
			container.addMember(IdentityUtil.projectName("2"));
		}};
		PublisherHelper.
			publishHarvester(session,target,Arrays.asList("1","2"));
	}

	@Test
	public void testPublishProject$notExists(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final Project project=new Project();
		project.setId("1");
		new Expectations() {{
			session.find(ResourceSnapshot.class,IdentityUtil.projectName("1"),ProjectHandler.class);this.result=null;
			session.find(ContainerSnapshot.class,IdentityUtil.collectorName(target),ProjectContainerHandler.class);this.result=container;
			container.addMember(IdentityUtil.projectName("1"));this.result=resource;
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_COMPONENTS, IdentityUtil.projectName("1"), ComponentContainerHandler.class);
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_VERSIONS, IdentityUtil.projectName("1"), VersionContainerHandler.class);
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_ISSUES, IdentityUtil.projectName("1"), IssueContainerHandler.class);
		}};
		PublisherHelper.
			publishProject(session, target, project);
	}

	@Test
	public void testPublishProject$exists(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final Project project=new Project();
		project.setId("1");
		new Expectations() {{
			session.find(ResourceSnapshot.class,IdentityUtil.projectName("1"),ProjectHandler.class);this.result=resource;
		}};
		PublisherHelper.
			publishProject(session, target, project);
	}

	@Test
	public void testPublishContributors(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.collectorName(target), ContributorContainerHandler.class);this.result=container;
			container.addMember(IdentityUtil.contributorName("contributor1"));
			container.addMember(IdentityUtil.contributorName("contributor2"));
		}};
		PublisherHelper.
			publishContributors(session,target,Arrays.asList("contributor1","contributor2"));
	}

	@Test
	public void testUnpublishContributors(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) {
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.contributorName("contributor1"), ContributorHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.contributorName("contributor2"), ContributorHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			unpublishContributors(session, Arrays.asList("contributor1","contributor2"));
	}

	@Test
	public void testPublishCommits(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.collectorName(target), CommitContainerHandler.class);this.result=container;
			container.addMember(IdentityUtil.commitName("commit1"));
			container.addMember(IdentityUtil.commitName("commit2"));
		}};
		PublisherHelper.
			publishCommits(session,target,Arrays.asList("commit1","commit2"));
	}

	@Test
	public void testUnpublishCommits(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) {
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.commitName("commit1"), CommitHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.commitName("commit2"), CommitHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			unpublishCommits(session, Arrays.asList("commit1","commit2"));
	}

	@Test
	public void testPublishProjects(@Mocked final WriteSession session, @Mocked final ContainerSnapshot container, @Mocked final ResourceSnapshot resource) {
		final URI target = URI.create("target");
		new Expectations() {{
			session.find(ContainerSnapshot.class, IdentityUtil.collectorName(target), ProjectContainerHandler.class);this.result=container;
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("2"), ProjectHandler.class);this.result=null;
			container.addMember(IdentityUtil.projectName("2"));this.result=resource;
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_COMPONENTS, IdentityUtil.projectName("2"), ComponentContainerHandler.class);
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_VERSIONS, IdentityUtil.projectName("2"), VersionContainerHandler.class);
			resource.createAttachedResource(ContainerSnapshot.class, ProjectHandler.PROJECT_ISSUES, IdentityUtil.projectName("2"), IssueContainerHandler.class);
		}};
		PublisherHelper.
			publishProjects(session, target, Arrays.asList("1","2"));
	}

	@Test
	public void testUnpublishRepositories(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) {
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("2"), ProjectHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			unpublishProjects(session, Arrays.asList("1","2"));
	}

	@Test
	public void testUpdateProjects$projectNotFound(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().component("component"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=null;
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if the project does not exist");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Project 1 does not exist"));
		}
	}

	@Test
	public void testUpdateProjects$newComponents(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().component("component1"));
		event.append(Modification.create().component("component2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_COMPONENTS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.componentName(new ComponentKey("1","component1")));
			container.addMember(IdentityUtil.componentName(new ComponentKey("1","component2")));
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProjects$newComponentsWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().component("component1"));
		event.append(Modification.create().component("component2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_COMPONENTS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.componentName(new ComponentKey("1","component1")));this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot add component");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not publish components of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProjects$newVersions(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().version("version1"));
		event.append(Modification.create().version("version2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_VERSIONS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.versionName(new VersionKey("1","version1")));
			container.addMember(IdentityUtil.versionName(new VersionKey("1","version2")));
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProjects$newVersionsWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().version("version1"));
		event.append(Modification.create().version("version2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_VERSIONS);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.versionName(new VersionKey("1","version1")));this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot add versions");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not publish versions of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProjects$newIssues(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().issue("issue1"));
		event.append(Modification.create().issue("issue2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_ISSUES);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.issueName(new IssueKey("1","issue1")));
			container.addMember(IdentityUtil.issueName(new IssueKey("1","issue2")));
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProjects$newIssuesWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.create().issue("issue1"));
		event.append(Modification.create().issue("issue2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_ISSUES);this.result=attachment;
			attachment.resource();this.result=container;
			container.addMember(IdentityUtil.issueName(new IssueKey("1","issue1")));this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot add issues");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not publish issues of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProject$deletedComponents(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().component("component1"));
		event.append(Modification.delete().component("component2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.componentName(new ComponentKey("1","component1")), ComponentHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.componentName(new ComponentKey("1","component2")), ComponentHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProject$deletedComponentWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().component("component1"));
		event.append(Modification.delete().component("component2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.componentName(new ComponentKey("1","component1")), ComponentHandler.class);this.result=resource;
			session.delete(resource);this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot remove components");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not unpublish components of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProject$deletedVersions(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().version("version1"));
		event.append(Modification.delete().version("version2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.versionName(new VersionKey("1","version1")), VersionHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.versionName(new VersionKey("1","version2")), VersionHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProject$deletedVersionWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().version("version1"));
		event.append(Modification.delete().version("version2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.versionName(new VersionKey("1","version2")), VersionHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.versionName(new VersionKey("1","version1")), VersionHandler.class);this.result=null;
			session.delete(resource);this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot remove versions");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not unpublish versions of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProject$deletedIssues(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().issue("issue1"));
		event.append(Modification.delete().issue("issue2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.issueName(new IssueKey("1","issue1")), IssueHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.issueName(new IssueKey("1","issue2")), IssueHandler.class);this.result=null;
			session.delete(resource);this.times=1;
		}};
		PublisherHelper.
			updateProject(session, event);
	}

	@Test
	public void testUpdateProject$deletedIssueWithFailure(@Mocked final WriteSession session, @Mocked final ResourceSnapshot resource) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.delete().issue("issue1"));
		event.append(Modification.delete().issue("issue2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.issueName(new IssueKey("1","issue2")), IssueHandler.class);this.result=resource;
			session.find(ResourceSnapshot.class, IdentityUtil.issueName(new IssueKey("1","issue1")), IssueHandler.class);this.result=null;
			session.delete(resource);this.result=new IOException("Failure");
		}};
		try {
			PublisherHelper.
				updateProject(session, event);
			fail("Should fail if cannot remove issues");
		} catch (final IOException e) {
			assertThat(e.getMessage(),equalTo("Could not unpublish issues of project 1"));
			assertThat(e.getCause(),instanceOf(IOException.class));
			assertThat(e.getCause().getMessage(),equalTo("Failure"));
		}
	}

	@Test
	public void testUpdateProjects$updateComponents(@Mocked final WriteSession session, @Mocked final ResourceSnapshot project, @Mocked final AttachmentSnapshot attachment, @Mocked final ContainerSnapshot container) throws IOException {
		final URI target = URI.create("target");
		final ProjectUpdatedEvent event=new ProjectUpdatedEvent();
		event.setInstance(target.toString());
		event.setProject("1");
		event.append(Modification.update().component("component1"));
		event.append(Modification.update().component("component2"));
		new Expectations() {{
			session.find(ResourceSnapshot.class, IdentityUtil.projectName("1"), ProjectHandler.class);this.result=project;
			session.modify(project);
			project.attachmentById(ProjectHandler.PROJECT_COMPONENTS);this.maxTimes=0;
		}};
		PublisherHelper.
			updateProject(session, event);
	}

}
