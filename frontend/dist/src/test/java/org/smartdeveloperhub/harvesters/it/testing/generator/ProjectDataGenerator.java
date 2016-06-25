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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.1.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.controller.LocalData;
import org.smartdeveloperhub.harvesters.it.frontend.testing.util.StateUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class ProjectDataGenerator {

	private static final String[] COMPONENT_NAMES={
		"Frontend",
		"Backend",
		"Harvester",
		"Publisher",
		"Orchestrator",
		"Engine",
		"Reasoner",
		"Collector",
		"Crawler",
		"Database",
		"Store",
		"Repository",
		"Adapter",
		"Bridge",
		"Mediator",
		"Planner",
		"Scheduler",
		"Client",
		"Proxy",
		"CLI",
		"GUI",
		"Generator",
		"Installer",
		"Translator",
		"Compiler",
		"Transpiler"
	};

	private static final Logger LOGGER=LoggerFactory.getLogger(ProjectDataGenerator.class);

	private final Random random;

	private Project project;
	private Map<String,Component> components;
	private Set<String> componentNames;

	private Map<String,Version> versions;

	private Map<String,Issue> issues;

	private LocalDate projectStart;
	private Days projectDuration;

	private LocalTime workDayStartTime;
	private Minutes workDayDuration;

	private SemVer version;

	private LocalTime workDayEndTime;

	private List<Contributor> contributors;

	private ProjectDataGenerator() {
		this.random = new Random();
	}

	public static void main(final String... args) {
		try {
			final LocalData data = localData();
			final ProjectDataGenerator generator = new ProjectDataGenerator();
			generator.generateProjectData(data);
			final Path path = Paths.get(args[0]);
			Files.
				write(
					Entities.marshallEntity(data),
					path.toFile(),
					StandardCharsets.UTF_8);
		} catch (final IOException e) {
			LOGGER.error("Project generation failed",e);
			System.exit(-1);
		}
	}

	private static LocalData localData() {
		final LocalData data=new LocalData();
		data.getContributors().add(alexFernandez());
		data.getContributors().add(alejandroVera());
		data.getContributors().add(andresGarciaSilva());
		data.getContributors().add(carlosBlanco());
		data.getContributors().add(fernandoSerena());
		data.getContributors().add(miguelEstebanGutierrez());
		data.getContributors().add(oscarCorcho());
		data.getContributors().add(asunGomezPerez());
		data.getContributors().add(javierSoriano());
		data.getContributors().add(mariaJoseGonzalez());
		data.getContributors().add(rubenDeDios());
		data.getContributors().add(julianGarcia());
		data.getContributors().add(cesarRubio());
		return data;
	}

	private static Contributor alexFernandez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1007");
		contributor.setName("Alejandro F. Carrera");
		contributor.getEmails().add("alej4fc@gmail.com");
		return contributor;
	}

	private static Contributor alejandroVera() {
		final Contributor contributor = new Contributor();
		contributor.setId("1001");
		contributor.setName("Alejandro Vera");
		contributor.getEmails().add("xafilox@gmail.com");
		return contributor;
	}

	private static Contributor andresGarciaSilva() {
		final Contributor contributor = new Contributor();
		contributor.setId("1008");
		contributor.setName("Andres Garcia Silva");
		contributor.getEmails().add("andresgs77@hotmail.com");
		return contributor;
	}

	private static Contributor carlosBlanco() {
		final Contributor contributor = new Contributor();
		contributor.setId("1003");
		contributor.setName("Carlos Blanco");
		contributor.getEmails().add("cblanco@conwet.com");
		return contributor;
	}

	private static Contributor fernandoSerena() {
		final Contributor contributor = new Contributor();
		contributor.setId("1002");
		contributor.setName("Fernando Serena");
		contributor.getEmails().add("kudhmud@gmail.com");
		return contributor;
	}

	private static Contributor miguelEstebanGutierrez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1009");
		contributor.setName("Miguel Esteban Gutierrez");
		contributor.getEmails().add("m.esteban.gutierrez@gmail.com");
		return contributor;
	}

	private static Contributor oscarCorcho() {
		final Contributor contributor = new Contributor();
		contributor.setId("1005");
		contributor.setName("Oscar Corcho");
		contributor.getEmails().add("ocorcho@fi.upm.es");
		return contributor;
	}

	private static Contributor asunGomezPerez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1006");
		contributor.setName("Asuncion Gomez Perez");
		contributor.getEmails().add("asun@fi.upm.es");
		return contributor;
	}

	private static Contributor javierSoriano() {
		final Contributor contributor = new Contributor();
		contributor.setId("1004");
		contributor.setName("Francisco Javier Soriano");
		contributor.getEmails().add("jsoriano@fi.upm.es");
		return contributor;
	}

	private static Contributor mariaJoseGonzalez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1011");
		contributor.setName("Maria Jose Gonzalez");
		contributor.getEmails().add("mgonzper@isban.es");
		return contributor;
	}

	private static Contributor rubenDeDios() {
		final Contributor contributor = new Contributor();
		contributor.setId("1012");
		contributor.setName("Ruben de Dios Barbero");
		contributor.getEmails().add("rdediosb@servexternos.isban.es");
		return contributor;
	}

	private static Contributor julianGarcia() {
		final Contributor contributor = new Contributor();
		contributor.setId("1013");
		contributor.setName("Julian Garcia");
		contributor.getEmails().add("juliangarcia@gmail.com");
		return contributor;
	}

	private static Contributor cesarRubio() {
		final Contributor contributor = new Contributor();
		contributor.setId("1014");
		contributor.setName("Cesar Rubio");
		contributor.getEmails().add("crubio@gmail.com");
		return contributor;
	}

	private void generateProjectData(final LocalData data) {
		final String id = generateProjectId(data);
		bootstrapProject(id);
		populateProject(data.getContributors());
		combineProjectData(data);
	}

	private void populateProject(final List<Contributor> contributors) {
		this.contributors=Lists.newArrayList(contributors);
		for(int day=0;day<this.projectDuration.getDays();day++) {
			final LocalDate today = this.projectStart.plusDays(day);
			if(isWorkingDay(today) || this.random.nextInt(1000)%25==0) {
				labour(today);
			}
		}
		this.contributors=null;
	}

	private boolean isWorkingDay(final LocalDate today) {
		final int dayOfWeek = today.getDayOfWeek();
		final boolean isWorkingDay=DateTimeConstants.SUNDAY!=dayOfWeek && DateTimeConstants.SATURDAY!=dayOfWeek;
		return isWorkingDay;
	}

	private void labour(final LocalDate today) {
		LOGGER.info("- Labour on {}working day {} in project {} ({}):",isWorkingDay(today)?"":"non-",today,this.project.getName(),this.project.getId());
		manageComponents();
		manageVersions();
		createNewIssues(today);
		evaluateIssues();
		workOnIssues();
		reopenIssues();
	}

	/**
	 * TODO: Implement version management logic
	 */
	private void manageComponents() {
		LOGGER.debug("Should manage components...");
	}

	/**
	 * TODO: Implement version management logic
	 */
	private void manageVersions() {
		LOGGER.debug("Should manage versions...");
	}

	private void createNewIssues(final LocalDate today) {
		final int newIssues=this.random.nextInt(this.contributors.size());
		LocalTime time=this.workDayStartTime;
		int start=this.issues.size();
		for(int i=0;i<newIssues;i++) {
			start++;
			time=time.plusMinutes(this.random.nextInt(15)*3+i);
			createIssue(Integer.toString(start),today.toLocalDateTime(time));
		}
	}

	private void createIssue(final String issueId, final LocalDateTime dateTime) {
		final Issue issue = new Issue();
		issue.setId(issueId);
		issue.setCreationDate(dateTime.toDateTime());
		issue.setOpened(issue.getCreationDate());
		final Contributor reporter = selectContributor();
		issue.setReporter(reporter.getId());
		this.issues.put(issueId,issue);
		LOGGER.debug("Created issue {} at {}, reported by {}",issue.getId(),dateTime,reporter.getName());
	}

	private Contributor selectContributor() {
		return this.contributors.get(this.random.nextInt(this.contributors.size()*4)%this.contributors.size());
	}

	private void evaluateIssues() {
		for(final Issue issue:findIssuesByStatus(Status.OPEN)) {
			if(canEvaluate(issue)) {
				evaluate(issue);
			}
		}
	}

	private void workOnIssues() {
		for(final Issue issue:findIssuesByStatus(Status.IN_PROGRESS)) {
			if(canWorkOn(issue)) {
				workOn(issue);
			}
		}
	}

	private void reopenIssues() {
		for(final Issue issue:findIssuesByStatus(Status.CLOSED)) {
			if(canReopen(issue)) {
				reopen(issue);
			}
		}
	}

	/**
	 * TODO: Implement issue evaluation logic
	 */
	private void evaluate(final Issue issue) {
		LOGGER.debug("Should evaluate open issue {}",issue.getId());
	}

	/**
	 * TODO: Implement issue progress logic
	 */
	private void workOn(final Issue issue) {
		LOGGER.debug("Should work on in progress issue {}",issue.getId());
	}

	/**
	 * TODO: Implement issue reopening logic
	 */
	private void reopen(final Issue issue) {
		LOGGER.debug("Should reopen closed issue {}",issue.getId());
	}

	private boolean canEvaluate(final Issue issue) {
		return this.random.nextInt(1000)%25==0;
	}

	private boolean canWorkOn(final Issue issue) {
		return this.random.nextInt(1000)%50==0;
	}

	private boolean canReopen(final Issue issue) {
		return this.random.nextInt(1000)%100==0;
	}

	private ArrayList<Issue> findIssuesByStatus(final Status status) {
		return Lists.
			newArrayList(
				Iterables.
					filter(
						this.issues.values(),
						new Predicate<Issue>(){
							@Override
							public boolean apply(final Issue input) {
								return status.equals(input.getStatus());
							}
						}
					)
				);
	}

	private void bootstrapProject(final String id) {
		this.project=new Project();
		this.project.setId(id);
		this.project.setName(StateUtil.generateProjectName());

		this.components=Maps.newLinkedHashMap();
		this.componentNames=Sets.newLinkedHashSet();
		this.versions=Maps.newLinkedHashMap();
		this.issues=Maps.newLinkedHashMap();

		this.projectDuration=Days.days(180+this.random.nextInt(360*4));
		this.projectStart=new DateTime().minus(this.projectDuration).toLocalDate();
		this.workDayStartTime=new LocalTime(8,0);
		this.workDayEndTime=new LocalTime(20,0);
		this.workDayDuration=Minutes.minutes(60*6+this.random.nextInt(30*6));

		LOGGER.info("Bootstrapping project {} ({})",this.project.getName(),this.project.getId());
		LOGGER.info("- Project started on {} ({} days of ongoing work)",this.projectStart,this.projectDuration.getDays());
		LOGGER.info("- Regular work day starts at {} and ends at {}",this.workDayStartTime,this.workDayEndTime);
		LOGGER.info("- A contributor must work ~{} hours per working day",this.workDayDuration.getMinutes()/60);

		final int initialComponents=1+this.random.nextInt(3);
		for(int i=0;i<initialComponents;i++) {
			createComponent();
		}
		this.version=SemVer.create();
		if(this.random.nextBoolean()) {
			createVersion();
		}
	}

	private Version createVersion() {
		final int versionUpdate=this.random.nextInt(1000);
		if(versionUpdate % 100 == 0) {
			this.version=this.version.nextMajor();
		} else if(versionUpdate % 25 == 0) {
			this.version=this.version.nextMinor();
		} else {
			this.version=this.version.nextMinor();
		}

		final Version version=new Version();
		version.setId(Integer.toString(this.versions.size()+1));
		version.setName(this.version.toString());
		version.setProjectId(this.project.getId());
		this.versions.put(version.getId(),version);
		this.project.getVersions().add(version.getId());
		LOGGER.info("- Created version {} ({}) for project {} ({}) ",version.getName(),version.getId(),this.project.getName(),this.project.getId());
		return version;
	}

	private Component createComponent() {
		if(this.componentNames.size()>=COMPONENT_NAMES.length) {
			LOGGER.debug("- Skipped project {} ({}) component creation: all predefined components have been created",this.project.getName(),this.project.getId());
			return Iterables.getLast(this.components.values());
		}
		final Component component=new Component();
		component.setId(Integer.toString(this.components.size()+1));
		int index = this.random.nextInt(COMPONENT_NAMES.length);
		while(this.componentNames.contains(COMPONENT_NAMES[index])) {
			index=(index+1)%COMPONENT_NAMES.length;
		}
		component.setName(COMPONENT_NAMES[index]);
		component.setProjectId(this.project.getId());
		this.components.put(component.getId(),component);
		this.project.getComponents().add(component.getId());
		LOGGER.info("- Created component {} ({}) for project {} ({}) ",component.getName(),component.getId(),this.project.getName(),this.project.getId());
		return component;
	}

	private void combineProjectData(final LocalData data) {
		data.getProjects().add(this.project);
		data.getProjectComponents().put(this.project.getId(),Lists.newArrayList(this.components.values()));
		data.getProjectVersions().put(this.project.getId(),Lists.newArrayList(this.versions.values()));
		data.getProjectIssues().put(this.project.getId(),Lists.newArrayList(this.issues.values()));
	}

	private String generateProjectId(final LocalData data) {
		final Set<String> projectIds=
			Sets.
				newHashSet(
					Iterables.
						transform(
							data.getProjects(),
							new Function<Project,String>() {
								@Override
								public String apply(final Project input) {
									return input.getId();
								}
							}
						)
					);
		int starting = projectIds.size();
		String id;
		do {
			starting++;
			id=Integer.toString(starting);
		} while(projectIds.contains(id));
		return id;
	}

}
