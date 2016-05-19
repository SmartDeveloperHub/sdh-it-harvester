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
 *   Artifact    : org.smartdeveloperhub.harvesters.it:it-harvester-backend:0.1.0-SNAPSHOT
 *   Bundle      : it-harvester-backend-0.1.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.backend;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class for the backend of the Issue Tracker Harvester.
 * @author imolina
 *
 */
public class Orchestrator {

    private static final Logger logger =
            LoggerFactory.getLogger(Orchestrator.class);

    private Tomcat tomcat;
    private Collector collector;
    private Exhibitor exhibitor;
    private RedisStorer database;
    private ExecutorService executorService;

    public Orchestrator(ExecutorService executor) {

        this.executorService = Objects.requireNonNull(executor,
                "ExecutorService can't be null.");
    }

    public void start() {

        // TODO: move config parameters to config file
        // Read config
        String name = "ITHarvester-Backend";
        String version = "1.0.0";
        String url = "instance_url";
        String username = "user";
        String password = "password";
        int servletPort = 8080;
        String servletPath = "/*";
        int collectorTime = 60;

        try {

            database = new RedisStorer();
            collector = new Collector(url, username, password, database);
            exhibitor = new Exhibitor(database);

            // Deploying tomcat to listen incoming CAPs
            tomcat = new Tomcat();
            tomcat.setPort(servletPort);

            File base = new File(System.getProperty("java.io.tmpdir"));
            Context rootCtx = tomcat.addContext("", base.getAbsolutePath());

            ServletContainer servlet = new ServletContainer(
                     new ResourceConfig().register(
                             new ExhibitorResources(name, version, exhibitor)));

            Tomcat.addServlet(rootCtx, "ITHarvester", servlet);
            rootCtx.addServletMapping(servletPath, "ITHarvester");
            tomcat.start();

            ((ScheduledThreadPoolExecutor) executorService).scheduleAtFixedRate(
                                 collector, 0, collectorTime, TimeUnit.MINUTES);
        } catch (LifecycleException e) {

            logger.error("Error when trying to deploy endpoints.", e);
        } catch (URISyntaxException e) {

            logger.error("Error due to wrong URL", e);
        }
    }

    public static void main(String[] args) {

        Orchestrator orchestrator = new Orchestrator(
                Executors.newScheduledThreadPool(1));

        orchestrator.start();
    }
}