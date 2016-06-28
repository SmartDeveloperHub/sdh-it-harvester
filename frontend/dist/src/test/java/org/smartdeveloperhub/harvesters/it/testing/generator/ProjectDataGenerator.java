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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.AssigneeChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.ClosedDateChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.Item;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.PriorityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.SeverityChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.ChangeLog.Entry.StatusChangeItem;
import org.smartdeveloperhub.harvesters.it.backend.Component;
import org.smartdeveloperhub.harvesters.it.backend.Contributor;
import org.smartdeveloperhub.harvesters.it.backend.Entities;
import org.smartdeveloperhub.harvesters.it.backend.Issue;
import org.smartdeveloperhub.harvesters.it.backend.Priority;
import org.smartdeveloperhub.harvesters.it.backend.Project;
import org.smartdeveloperhub.harvesters.it.backend.Severity;
import org.smartdeveloperhub.harvesters.it.backend.Status;
import org.smartdeveloperhub.harvesters.it.backend.Version;
import org.smartdeveloperhub.harvesters.it.frontend.controller.LocalData;
import org.smartdeveloperhub.harvesters.it.frontend.testing.util.StateUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
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
	private WorkDay workDay;

	private SemVer version;

	private List<Contributor> contributors;


	private ProjectDataGenerator() {
		this.random = new Random();
	}

	public static void main(final String... args) {
		try {
			final LocalData data=new LocalData();
			data.getContributors().addAll(Contributors.all());
			final ProjectDataGenerator generator = new ProjectDataGenerator();
			generator.
				generateProjectData(
					data,
					Contributors.developers());
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

	private void generateProjectData(final LocalData data, final List<Contributor> participants) {
		final String id = generateProjectId(data);
		bootstrapProject(id);
		populateProject(participants);
		combineProjectData(data);
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
		this.workDay=new WorkDay(this.random);

		LOGGER.info("Bootstrapping project {} ({})",this.project.getName(),this.project.getId());
		LOGGER.info("- Project started on {} ({} days of ongoing work)",this.projectStart,this.projectDuration.getDays());
		LOGGER.info("- {}",this.workDay);

		final int initialComponents=1+this.random.nextInt(3);
		for(int i=0;i<initialComponents;i++) {
			createComponent();
		}
		this.version=SemVer.create();
		if(this.random.nextBoolean()) {
			createVersion();
		}
	}

	private void populateProject(final List<Contributor> contributors) {
		this.contributors=Lists.newArrayList(contributors);
		for(int day=0;day<this.projectDuration.getDays();day++) {
			final LocalDate today = this.projectStart.plusDays(day);
			if(Utils.isWorkingDay(today) || this.random.nextInt(1000)%25==0) {
				labour(today);
			}
		}
		this.contributors=null;
	}

	private void labour(final LocalDate today) {
		LOGGER.info("- Labour on {}working day {} in project {} ({}):",Utils.isWorkingDay(today)?"":"non-",today,this.project.getName(),this.project.getId());
		createNewComponents();
		createNewVersions();
		createNewIssues(today);
		evaluateIssues(today);
		workOnIssues(today);
		reopenIssues(today);
	}

	private void createNewComponents() {
		if(this.random.nextInt(1000)%5==0) {
			createComponent();
		}
	}

	private void createNewVersions() {
		if(this.random.nextInt(10000)%5==0) {
			createVersion();
		}
	}

	private void createNewIssues(final LocalDate today) {
		final int inProgressIssues = findIssuesByStatus(Status.IN_PROGRESS).size();
		final int openIssues = findIssuesByStatus(Status.OPEN).size();

		int newIssues=0;
		if(openIssues==0) {
			newIssues=
				this.random.nextInt(this.contributors.size())+
				this.random.nextInt(this.contributors.size()*2)/3;
		} else if(openIssues*3<inProgressIssues) {
			newIssues=this.random.nextInt(this.contributors.size()*2)/3;
		} else {
			newIssues=this.random.nextBoolean()?1:0;
		}

		final Iterator<LocalTime> time=this.workDay.workingTimes();
		int start=this.issues.size();
		for(int i=0;i<newIssues;i++) {
			createIssue(Integer.toString(++start),today.toLocalDateTime(time.next()));
		}
	}

	private void createIssue(final String issueId, final LocalDateTime creationDate) {
		final Issue issue = new Issue();
		issue.setProjectId(this.project.getId());
		issue.setId(issueId);
		issue.setName(StateUtil.generateSentence());
		issue.setDescription(StateUtil.generateSentences(1,2+this.random.nextInt(8)));
		issue.setStatus(Status.OPEN);
		issue.setCreationDate(creationDate.toDateTime());
		issue.setOpened(issue.getCreationDate());
		Contributor assignee=null;
		final Contributor reporter = selectContributor();
		issue.setReporter(reporter.getId());

		LOGGER.debug("   * Created issue {} at {}, reported by {}",issue.getId(),creationDate,reporter.getName());

		if(this.random.nextBoolean()) {
			issue.setSeverity(selectSeverity());
			LOGGER.debug("     + Severity: {}",issue.getSeverity());
		}

		if(this.random.nextBoolean()) {
			issue.setPriority(selectPriority());
			LOGGER.debug("     + Priority: {}",issue.getPriority());
		}

		if(this.random.nextBoolean()) {
			assignee=selectContributor();
			issue.setAssignee(assignee.getId());
			LOGGER.debug("     + Assigned to {}",assignee.getName());
		}

		if(this.random.nextBoolean()) {
			final LocalDateTime dueTo=createDueTo(creationDate);
			issue.setDueTo(dueTo.toDateTime());
			LOGGER.debug("     + Scheduled for {}",dueTo);
			if(this.random.nextBoolean()) {
				issue.
					setEstimatedTime(
						estimateEffort(creationDate, dueTo));
				LOGGER.debug("     + Estimated {} hours ",issue.getEstimatedTime().getStandardHours());
			}
		}

		if(!this.components.isEmpty() && this.random.nextBoolean()) {
			final int affectedComponents=1+this.random.nextInt(2);
			final List<String> names=Lists.newArrayList();
			for(int i=0;i<affectedComponents;i++) {
				final Component component=selectComponent();
				if(issue.getComponents().add(component.getId())) {
					names.add(component.getName());
				}
			}
			LOGGER.debug("     + Related to components: {}",Joiner.on(", ").join(names));
		}

		if(!this.versions.isEmpty() && this.random.nextBoolean()) {
			final int affectedVersions=1+this.random.nextInt(2);
			final List<String> names=Lists.newArrayList();
			for(int i=0;i<affectedVersions;i++) {
				final Version version=selectVersion();
				if(issue.getVersions().add(version.getId())) {
					names.add(version.getName());
				}
			}
			LOGGER.debug("     + Affects versions: {}",Joiner.on(", ").join(names));
		}

		this.issues.put(issueId,issue);
	}

	private Duration estimateEffort(final LocalDateTime start, final LocalDateTime dueTo) {
		final Days daysBetween=Days.daysBetween(start,dueTo);
		int workingDays=0;
		for(int i=0;i<daysBetween.getDays();i++) {
			if(Utils.isWorkingDay(start.toLocalDate().plusDays(i))) {
				workingDays++;
			}
		}
		final int maxMinutes = workingDays*this.workDay.effortPerDay();
		return
			Duration.
				standardMinutes(
					33*maxMinutes/100+
					67*maxMinutes/100*(this.random.nextInt(100)/100));
	}

	private LocalDateTime createDueTo(final LocalDateTime dateTime) {
		LocalDate localDate = dateTime.toLocalDate().plusDays(1+this.random.nextInt(15));
		while(Utils.isWorkingDay(localDate)) {
			localDate=localDate.plusDays(1);
		}
		return localDate.toLocalDateTime(this.workDay.workingHour());
	}

	private void evaluateIssues(final LocalDate today) {
		for(final Issue issue:findIssuesByStatus(Status.OPEN)) {
			if(!isInFlight(issue,today) && canEvaluate(issue)) {
				evaluate(issue,today);
			}
		}
	}

	private void workOnIssues(final LocalDate today) {
		for(final Issue issue:findIssuesByStatus(Status.IN_PROGRESS)) {
			if(!isInFlight(issue,today) && canWorkOn(issue)) {
				workOn(issue,today);
			}
		}
	}

	private void reopenIssues(final LocalDate today) {
		for(final Issue issue:findIssuesByStatus(Status.CLOSED)) {
			if(!isInFlight(issue,today) && canReopen(issue)) {
				reopen(issue);
			}
		}
	}

	private void evaluate(final Issue issue, final LocalDate today) {
		final Set<Item> changes=Sets.newLinkedHashSet();
		final LocalDateTime now = today.toLocalDateTime(this.workDay.workingTime());
		LOGGER.debug("   * Evaluated issue {} at {}",issue.getId(),now);
		if(issue.getAssignee()==null) {
			final Contributor assignee = selectContributor();
			issue.setAssignee(assignee.getId());

			final AssigneeChangeItem item = new AssigneeChangeItem();
			item.setOldValue(null);
			item.setNewValue(issue.getAssignee());
			changes.add(item);
			LOGGER.debug("     + Assigned to {}",assignee.getName());
		}

		if(issue.getSeverity()==null) {
			issue.setSeverity(selectSeverity());

			final SeverityChangeItem item = new SeverityChangeItem();
			item.setOldValue(null);
			item.setNewValue(issue.getSeverity());
			changes.add(item);
			LOGGER.debug("     + Severity: {}",issue.getSeverity());
		}

		if(issue.getPriority()==null) {
			issue.setPriority(selectPriority());

			final PriorityChangeItem item = new PriorityChangeItem();
			item.setOldValue(null);
			item.setNewValue(issue.getPriority());
			changes.add(item);
			LOGGER.debug("     + Priority: {}",issue.getPriority());
		}

		if(this.random.nextInt(100)<10) {
			issue.setStatus(Status.CLOSED);
			issue.setClosed(now.toDateTime());

			final ClosedDateChangeItem item = new ClosedDateChangeItem();
			item.setOldValue(null);
			item.setNewValue(issue.getClosed());
			changes.add(item);
			LOGGER.debug("     + Action: close");
		} else {
			issue.setStatus(Status.IN_PROGRESS);
			LOGGER.debug("     + Action: start work");
		}

		final StatusChangeItem item = new StatusChangeItem();
		item.setOldValue(Status.OPEN);
		item.setNewValue(issue.getStatus());
		changes.add(item);

		final Entry entry=new Entry();
		entry.setTimeStamp(now.toDateTime());
		entry.setAuthor(issue.getAssignee());
		entry.getItems().addAll(changes);

		final ChangeLog changeLog = new ChangeLog();
		changeLog.getEntries().add(entry);

		issue.setChanges(changeLog);
	}

	private void workOn(final Issue issue, final LocalDate today) {
		final Set<Item> changes=Sets.newLinkedHashSet();
		final LocalDateTime now = today.toLocalDateTime(this.workDay.workingTime());
		LOGGER.debug("   * Worked on issue {} at {}",issue.getId(),now);

		/**
		 * TODO: Implement better way for determining if we can close the issue
		 * depending on the deadline and remaining effort.
		 */
		if(this.random.nextInt(100)<80) {
			issue.setStatus(Status.CLOSED);
			issue.setClosed(now.toDateTime());

			final ClosedDateChangeItem item = new ClosedDateChangeItem();
			item.setOldValue(null);
			item.setNewValue(issue.getClosed());
			changes.add(item);
			LOGGER.debug("     + Action: close");
		}

		/**
		 * TODO: Implement mechanism to update other fields.
		 */

		final StatusChangeItem item = new StatusChangeItem();
		item.setOldValue(Status.IN_PROGRESS);
		item.setNewValue(Status.CLOSED);
		changes.add(item);

		final Entry entry=new Entry();
		entry.setTimeStamp(now.toDateTime());
		entry.setAuthor(issue.getAssignee());
		entry.getItems().addAll(changes);

		final ChangeLog changeLog = issue.getChanges();
		changeLog.getEntries().add(entry);
	}

	/**
	 * TODO: Implement issue reopening logic
	 */
	private void reopen(final Issue issue) {
		LOGGER.debug("Should reopen closed issue {}",issue.getId());
	}

	private boolean isInFlight(final Issue issue, final LocalDate today) {
		if(issue.getCreationDate().toLocalDate().equals(today)) {
			return true;
		}
		if(issue.getChanges()==null) {
			return false;
		}
		final Entry changeSet= Iterables.getLast(issue.getChanges().getEntries());
		if(changeSet==null) {
			return false;
		}
		return !changeSet.getTimeStamp().toLocalDate().equals(today);
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

	private Severity selectSeverity() {
		final List<Severity> values = Lists.newArrayList(Severity.values());
		return values.get(this.random.nextInt(values.size()));
	}

	private Priority selectPriority() {
		final List<Priority> values = Lists.newArrayList(Priority.values());
		return values.get(this.random.nextInt(values.size()));
	}

	private Contributor selectContributor() {
		return this.contributors.get(this.random.nextInt(this.contributors.size()*4)%this.contributors.size());
	}

	private Component selectComponent() {
		final List<Component> currentComponents = Lists.newArrayList(this.components.values());
		return currentComponents.get(this.random.nextInt(currentComponents.size()*4)%currentComponents.size());
	}

	private Version selectVersion() {
		final List<Version> currentVersions = Lists.newArrayList(this.versions.values());
		return currentVersions.get(this.random.nextInt(currentVersions.size()*4)%currentVersions.size());
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
			LOGGER.debug("- Skipped component creation for project {} ({}): all predefined components have been created",this.project.getName(),this.project.getId());
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
		this.componentNames.add(component.getName());
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
