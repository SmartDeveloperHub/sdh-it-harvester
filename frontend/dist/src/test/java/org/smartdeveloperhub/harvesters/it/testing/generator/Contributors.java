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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.1.0
 *   Bundle      : it-frontend-dist-0.1.0.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.util.List;

import org.smartdeveloperhub.harvesters.it.backend.Contributor;

import com.google.common.collect.Lists;

public class Contributors {

	static List<Contributor> all() {
		return
			Lists.
				newArrayList(
					alexFernandez(),
					alejandroVera(),
					andresGarciaSilva(),
					carlosBlanco(),
					fernandoSerena(),
					miguelEstebanGutierrez(),
					ignacioMolina(),
					mariaPoveda(),
					oscarCorcho(),
					asunGomezPerez(),
					javierSoriano(),
					mariaJoseGonzalez(),
					rubenDeDios(),
					julianGarcia(),
					cesarRubio());
	}

	static Contributor alexFernandez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1007");
		contributor.setName("Alejandro F. Carrera");
		contributor.getEmails().add("alej4fc@gmail.com");
		return contributor;
	}

	static Contributor alejandroVera() {
		final Contributor contributor = new Contributor();
		contributor.setId("1001");
		contributor.setName("Alejandro Vera");
		contributor.getEmails().add("xafilox@gmail.com");
		return contributor;
	}

	static Contributor andresGarciaSilva() {
		final Contributor contributor = new Contributor();
		contributor.setId("1008");
		contributor.setName("Andres Garcia Silva");
		contributor.getEmails().add("andresgs77@hotmail.com");
		return contributor;
	}

	static Contributor carlosBlanco() {
		final Contributor contributor = new Contributor();
		contributor.setId("1003");
		contributor.setName("Carlos Blanco");
		contributor.getEmails().add("cblanco@conwet.com");
		return contributor;
	}

	static Contributor fernandoSerena() {
		final Contributor contributor = new Contributor();
		contributor.setId("1002");
		contributor.setName("Fernando Serena");
		contributor.getEmails().add("kudhmud@gmail.com");
		return contributor;
	}

	static Contributor miguelEstebanGutierrez() {
		final Contributor contributor = new Contributor();
		contributor.setId("1009");
		contributor.setName("Miguel Esteban Gutierrez");
		contributor.getEmails().add("m.esteban.gutierrez@gmail.com");
		return contributor;
	}

	static Contributor ignacioMolina() {
		final Contributor contributor = new Contributor();
		contributor.setId("1015");
		contributor.setName("Ignacio Molina Cuquerella");
		contributor.getEmails().add("imolina@centeropenmiddleware.com");
		return contributor;
	}

	static Contributor mariaPoveda() {
		final Contributor contributor = new Contributor();
		contributor.setId("1016");
		contributor.setName("Maria Poveda Villalon");
		contributor.getEmails().add("mpoveda@fi.upm.es");
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

}
