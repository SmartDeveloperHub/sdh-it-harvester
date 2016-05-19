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

import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
public class ExhibitorResources extends Application {

    private String name;
    private String version;
    private Exhibitor exhibitor;
    
    public ExhibitorResources(String name, String version,
                              Exhibitor exhibitor) {

        this.name = Objects.requireNonNull(name, "Name can't be null.");
        this.version = Objects.requireNonNull(version, "Version can't be null");
        this.exhibitor = Objects.requireNonNull(exhibitor,
                                                "Exhibitor can't be null.");
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCosa() {

        String api = "{" +
                     "\"name\": \"" + name + "\"," +
                     "\"version\": \"" + version + "\"" +
//                     "\"Notification\" : {" +
//                     "  \"brokerHost\" : \"" + brokerHost + "\"" +
//                     "  \"brokerPort\" : \""+ brokerPort +"\"" +
//                     "  \"virtualHost\" : \"" + virtualHost + "\"" +
//                     "  \"exchangeName\" : \"" + exchangeName + "\"" +
//                     "  }" +
                     "}";

        return Response.ok(api, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/issues")
    @Produces(MediaType.TEXT_HTML)
    public String getIssue() {

        return "Resultado Issue";
    }

    @GET
    @Path("/projects")
    @Produces(MediaType.TEXT_HTML)
    public String getProjects() {

        return "Resultado projects";
    }
}
