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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-test:0.1.0
 *   Bundle      : it-frontend-test-0.1.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.frontend.testing.util;

import java.util.Random;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import me.atrox.haikunator.Haikunator;
import me.atrox.haikunator.HaikunatorBuilder;

public final class StateUtil {

	private static final Random RANDOM=new Random(System.nanoTime());

	private static final String[] SENTENCES= {
		"Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
		"Maecenas rutrum erat in sem accumsan, sit amet volutpat magna consectetur.",
		"Nulla rutrum eros eget lectus tempor, quis bibendum quam euismod.",
		"Quisque iaculis est eu mauris porttitor aliquet.",
		"Etiam id libero fringilla, mattis nunc a, lobortis odio.",
		"Ut efficitur urna vitae enim accumsan vestibulum.",
		"Etiam sagittis eros pulvinar purus scelerisque malesuada nec sed ante.",
		"Praesent id nunc eget nisl dapibus lobortis.",
		"Nunc feugiat augue vitae aliquet lobortis.",
		"Suspendisse iaculis quam non bibendum maximus.",
		"Aenean eleifend urna convallis iaculis hendrerit.",
		"Proin congue ex ut tellus efficitur, at maximus massa porta.",
		"Morbi consequat augue in efficitur semper.",
		"Donec molestie massa convallis, vestibulum justo ac, sodales turpis.",
		"Donec eu mauris vel mi consequat auctor vitae in augue.",
		"Vestibulum porttitor eros eu ante accumsan ullamcorper.",
		"Duis posuere est laoreet, venenatis dui sit amet, ullamcorper nulla.",
		"Suspendisse euismod dolor nec orci elementum sodales.",
		"Proin euismod lacus quis rutrum aliquam.",
		"Duis faucibus lectus quis mi finibus volutpat.",
		"Proin vel nulla ac nibh auctor scelerisque.",
		"Aliquam porttitor ex sit amet sapien ultricies, quis vehicula velit tincidunt.",
		"Etiam quis quam quis dolor sodales hendrerit.",
		"Donec ut orci vehicula, accumsan arcu a, tempus lorem.",
		"Vivamus feugiat odio in massa rhoncus faucibus.",
		"Pellentesque ullamcorper neque quis ante imperdiet dictum.",
		"Praesent sollicitudin metus a turpis dictum porttitor.",
		"Duis quis orci ut dolor mattis eleifend.",
		"Donec tempus nisi eget arcu dictum porta.",
		"Cras nec felis tristique, luctus ligula sit amet, pharetra nunc.",
		"Cras finibus sapien cursus lectus mollis congue.",
		"Vestibulum hendrerit leo ac imperdiet pretium.",
		"Quisque vel odio molestie, sodales dolor sed, porttitor massa.",
		"Donec sollicitudin augue nec ultrices rutrum.",
		"Duis dapibus odio a velit tempor, vitae dignissim mi egestas.",
		"Aenean dictum justo sed interdum malesuada.",
		"Duis condimentum velit non tempus suscipit.",
		"Sed iaculis eros sed sodales vehicula.",
		"Curabitur accumsan lacus et mollis molestie.",
		"Integer eget diam eu arcu sollicitudin ornare.",
		"Suspendisse vitae odio ullamcorper, blandit massa id, convallis risus.",
		"Mauris commodo magna nec odio molestie, vel viverra justo elementum.",
		"Fusce feugiat ipsum id justo vehicula tristique.",
		"Suspendisse tempor ligula in vehicula faucibus.",
		"Mauris quis massa quis nulla tristique lacinia ut nec sapien.",
		"Aliquam non lorem finibus, blandit tellus id, dignissim sem.",
		"Suspendisse interdum nunc at purus euismod pharetra.",
		"Duis varius sem et leo placerat, ut porttitor ex finibus.",
		"Nulla cursus nunc tincidunt ex scelerisque consectetur.",
		"Aenean quis erat at lectus dapibus condimentum.",
		"Vestibulum eu ligula et ligula laoreet faucibus tristique at quam.",
		"Phasellus ut massa pulvinar, auctor velit in, bibendum purus.",
		"Morbi bibendum purus bibendum porttitor rutrum.",
		"Quisque malesuada justo eu nunc venenatis mollis.",
		"Donec mollis ipsum et nisl accumsan, mattis euismod magna interdum.",
		"Mauris a orci lacinia, molestie elit vel, auctor ante.",
		"Curabitur vitae enim volutpat, egestas nunc a, congue ligula.",
		"Phasellus accumsan neque at aliquam dignissim.",
		"Sed fermentum velit at augue pellentesque posuere in quis nisl.",
		"Duis quis risus ultrices, cursus diam id, bibendum nisl.",
		"Ut tincidunt mauris in purus suscipit, in venenatis neque porta.",
		"Integer eu leo venenatis, dictum odio in, placerat est.",
		"Sed vitae lacus vitae felis interdum sodales non eu ante.",
		"Etiam volutpat dolor eget sagittis tristique.",
		"Maecenas in mauris ut ex sollicitudin vehicula sed feugiat nibh.",
		"Donec laoreet ligula et augue viverra iaculis.",
		"Duis mollis nisl sit amet justo molestie, bibendum tempor eros imperdiet.",
		"Sed mollis sapien sed semper tempus.",
		"Cras consequat magna a cursus venenatis.",
		"Aliquam et erat malesuada, lacinia purus vel, tincidunt erat.",
		"Fusce porttitor augue bibendum egestas posuere.",
		"Integer ultricies nulla et augue blandit, sit amet finibus libero elementum.",
		"Morbi et urna ut sapien eleifend scelerisque.",
		"Aliquam elementum lectus in vulputate semper.",
		"Ut aliquam eros id dui finibus, ut semper dui interdum.",
		"Nullam interdum odio ac odio accumsan vestibulum.",
		"Donec ac est imperdiet metus fringilla mollis.",
		"Praesent et est ut eros pharetra pellentesque.",
		"Quisque sed felis et eros aliquet dignissim et ac leo.",
		"Donec sollicitudin ipsum eu elit auctor facilisis quis vel diam.",
		"Morbi fermentum massa vel nisl pharetra pharetra.",
		"Proin condimentum odio ac mattis laoreet.",
		"Mauris sit amet tortor sodales, condimentum sapien sit amet, imperdiet sem.",
		"Donec ac nisl iaculis, lacinia sapien at, egestas diam.",
		"Aliquam laoreet quam at ante lacinia feugiat.",
		"Mauris laoreet odio ultrices arcu molestie accumsan.",
		"Pellentesque accumsan tortor in ultricies finibus.",
		"Nullam quis lorem sed dolor ultrices posuere.",
		"Suspendisse placerat erat sit amet elementum aliquet.",
		"Suspendisse eu justo venenatis, bibendum metus vitae, accumsan ante.",
		"Pellentesque lacinia dolor id risus ultrices, id efficitur massa aliquet.",
		"Vivamus eu lacus id odio molestie commodo.",
		"Donec mattis leo et placerat fermentum.",
		"Vivamus consequat ex id aliquet molestie.",
		"Nullam rutrum metus sit amet ultricies tempor.",
		"Phasellus pellentesque justo eu sem vulputate efficitur.",
		"Curabitur eu neque vel purus vulputate maximus.",
		"Nam eget augue at urna molestie viverra.",
		"Donec ut lacus vel felis mollis pulvinar eget at ex.",
		"Aliquam eleifend lacus nec lacus porttitor, non ultrices tortor egestas.",
		"Nunc id risus et dui molestie facilisis id at mauris.",
		"Curabitur vitae ligula sit amet est bibendum convallis.",
		"Vivamus nec erat vitae nibh sagittis posuere nec non purus.",
		"Nulla eget turpis nec velit condimentum pulvinar sed nec erat.",
		"Pellentesque vitae mi vitae nisl sodales accumsan.",
		"Mauris rhoncus lectus vitae ligula faucibus, id ultrices odio semper.",
		"Morbi placerat purus a elementum tempor.",
		"Pellentesque vel nisi vitae erat sollicitudin interdum.",
		"Aenean placerat turpis ac elementum vehicula.",
		"Donec ornare lacus at ipsum placerat, faucibus pretium mauris bibendum.",
		"Mauris interdum ligula id ornare tempus.",
		"Duis bibendum massa et aliquet feugiat.",
		"Morbi vel augue et nisi maximus suscipit.",
		"Vivamus nec mauris finibus, iaculis sem id, dictum ante.",
		"Suspendisse tincidunt est sit amet quam tincidunt, a facilisis dolor cursus.",
		"Donec eleifend augue ac velit porttitor tempus.",
		"Cras in nisi tristique, mollis ex id, gravida nibh.",
		"Nullam sagittis enim nec turpis rhoncus, id faucibus ante molestie.",
		"Etiam vehicula massa vitae leo commodo sagittis.",
		"Nullam nec tortor eu dui mattis dictum eget sodales felis.",
		"Aliquam et nulla lobortis, gravida ex ac, interdum sapien.",
		"Nam id ligula dapibus, bibendum ipsum eget, interdum libero.",
		"Suspendisse ultricies massa quis lacus tempor faucibus.",
		"Aenean vel felis et augue volutpat ultrices.",
		"Nullam vel dui sed ex eleifend hendrerit in iaculis libero.",
		"Cras at dolor a diam pellentesque viverra at eget diam.",
		"Donec vehicula nisi vitae feugiat viverra.",
		"Aliquam ut enim malesuada, consequat ipsum feugiat, accumsan turpis.",
		"Nunc tincidunt quam a sem vulputate sollicitudin.",
		"Pellentesque sagittis erat eget leo commodo pulvinar sed et ligula.",
		"Aliquam dictum leo vel volutpat ornare.",
		"Suspendisse eget erat volutpat, pharetra magna eget, tincidunt nisl.",
		"Suspendisse consequat nulla ac purus rutrum porta.",
		"Pellentesque volutpat urna convallis, pulvinar quam et, aliquet ipsum.",
		"Praesent sed mi quis urna rhoncus eleifend sed eu purus.",
		"Nullam auctor metus sit amet lorem consequat blandit.",
		"Nullam ac ipsum eu augue dapibus viverra id nec erat.",
		"Donec efficitur lacus ut nisl ultrices interdum.",
		"Nunc nec ipsum commodo, fermentum massa et, aliquam mauris.",
		"Vivamus molestie ligula vel ultrices pulvinar.",
		"Praesent eget nibh eu libero consectetur faucibus nec sed ipsum.",
		"Sed at nibh accumsan, condimentum leo ut, pellentesque enim.",
		"Nunc vestibulum tellus condimentum, laoreet dui eget, ultricies purus.",
		"Suspendisse vitae neque eu metus pretium auctor.",
		"Mauris blandit massa eu nisi varius lacinia.",
		"Praesent cursus magna et feugiat semper.",
		"Etiam ultrices lacus vestibulum odio ultrices tincidunt.",
		"In quis elit vel lacus elementum eleifend.",
		"Aliquam commodo nunc sed mi maximus accumsan.",
		"Nulla eu purus at dui tempus mollis at sed tortor.",
		"Ut fringilla libero sed ipsum semper, eleifend pulvinar nibh egestas.",
		"Vivamus euismod est eget diam egestas fermentum.",
		"Morbi sodales nisi vitae tellus hendrerit convallis.",
		"Aenean tincidunt erat ut lacinia iaculis.",
		"Suspendisse eu neque ut dolor semper accumsan ut a nibh.",
		"Aenean porttitor ex ac bibendum sodales.",
		"Aenean nec ligula pharetra, consectetur enim nec, lacinia ipsum.",
		"Vivamus tempus nunc sit amet blandit porta.",
		"Pellentesque finibus nulla vitae libero congue dapibus.",
		"Praesent at mi commodo, venenatis eros mollis, sagittis leo.",
		"Integer mattis mauris sit amet diam elementum feugiat.",
		"Integer dapibus enim id elit euismod, ut vestibulum nisi ullamcorper.",
		"In fringilla risus eu augue tincidunt posuere.",
		"Nunc tempor est ut ligula fermentum fringilla.",
		"Aenean vitae mi rhoncus, vulputate lectus eget, facilisis nunc."
	};

	/**
	 * Apellidos con frecuencia mayor o igual que 100 en el primer apellido,
	 * ordenadores por frecuencia de mayor a menor. Estadística del Padrón
	 * Continuo a fecha 01/01/2014
	 */
	private static final String[] SURNAMES={
		"GARCIA",
		"GONZALEZ",
		"RODRIGUEZ",
		"FERNANDEZ",
		"LOPEZ",
		"MARTINEZ",
		"SANCHEZ",
		"PEREZ",
		"GOMEZ",
		"MARTIN",
		"JIMENEZ",
		"RUIZ",
		"HERNANDEZ",
		"DIAZ",
		"MORENO",
		"ALVAREZ",
		"MUÑOZ",
		"ROMERO",
		"ALONSO",
		"GUTIERREZ",
		"NAVARRO",
		"TORRES",
		"DOMINGUEZ",
		"VAZQUEZ",
		"RAMOS",
		"GIL",
		"RAMIREZ",
		"SERRANO",
		"BLANCO",
		"MOLINA",
		"SUAREZ",
		"MORALES",
		"ORTEGA",
		"DELGADO",
		"CASTRO",
		"ORTIZ",
		"RUBIO",
		"MARIN",
		"SANZ",
		"NUÑEZ",
		"IGLESIAS",
		"MEDINA",
		"GARRIDO",
		"CORTES",
		"SANTOS",
		"CASTILLO",
		"LOZANO",
		"GUERRERO",
		"CANO",
		"PRIETO",
		"MENDEZ",
		"CALVO",
		"CRUZ",
		"GALLEGO",
		"VIDAL",
		"LEON",
		"MARQUEZ",
		"HERRERA",
		"PEÑA",
		"FLORES",
		"CABRERA",
		"CAMPOS",
		"VEGA",
		"FUENTES",
		"DIEZ",
		"CARRASCO",
		"CABALLERO",
		"NIETO",
		"REYES",
		"AGUILAR",
		"PASCUAL",
		"HERRERO",
		"SANTANA",
		"LORENZO",
		"HIDALGO",
		"MONTERO",
		"GIMENEZ",
		"IBAÑEZ",
		"FERRER",
		"DURAN",
		"SANTIAGO",
		"VICENTE",
		"BENITEZ",
		"MORA",
		"ARIAS",
		"VARGAS",
		"CARMONA",
		"CRESPO",
		"ROMAN",
		"PASTOR",
		"SOTO",
		"SAEZ",
		"VELASCO",
		"MOYA",
		"SOLER",
		"ESTEBAN",
		"PARRA",
		"BRAVO",
		"GALLARDO",
		"ROJAS"
	};

	/**
	 * Nacimientos según el nombre del nacido Año 2014. Datos provisionales
	 * (actualizados 22/06/2015)
	 */
	private static final String[] NAMES= {
		"DANIEL",
		"PABLO",
		"HUGO",
		"DIEGO",
		"ALEJANDRO",
		"ALVARO",
		"ADRIAN",
		"MARCOS",
		"NICOLAS",
		"DAVID",
		"LUCIA",
		"SOFIA",
		"PAULA",
		"MARIA",
		"MARTINA",
		"DANIELA",
		"SARA",
		"VALERIA",
		"ALBA",
		"CARLA",
		"MIGUEL",
		"FERNANDO",
		"CARLOS",
		"IGNACIO",
		"ESTHER",
		"ANA",
	};

	private static final Haikunator HAIKUNATOR =
			new HaikunatorBuilder().
				setTokenLength(0).
				setDelimiter("_").
				build();

	private static final Function<String,String> TO_CAMEL=new Function<String,String>() {

		private final Converter<String, String> converter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);

		@Override
		public String apply(final String input) {
			return this.converter.convert(input);
		}

	};

	private StateUtil() {
	}

	private static String select(final String[] target) {
		final String next = target[RANDOM.nextInt(target.length-1)];
		return next.substring(0,1)+ next.substring(1,next.length()).toLowerCase();
	}

	public static String generateContributorName() {
		return select(NAMES)+" "+select(SURNAMES)+" "+select(SURNAMES);
	}

	public static String generateProjectName() {
		return
			Joiner.on(" ").join(
				Iterables.transform(
					Splitter.on("_").split(HAIKUNATOR.haikunate().toLowerCase()),
					TO_CAMEL));
	}

	public static String generateSentence() {
		return SENTENCES[RANDOM.nextInt(SENTENCES.length)];
	}

	public static String generateSentences(final int min, final int max) {
		final Set<Integer> indexes=Sets.newLinkedHashSet();
		final int size = min+RANDOM.nextInt(max-min);
		final StringBuilder builder=new StringBuilder();
		while(indexes.size()<size) {
			final int index = RANDOM.nextInt(SENTENCES.length);
			if(indexes.add(index)) {
				builder.append(SENTENCES[index]);
				if(indexes.size()<size) {
					builder.append(" ");
				}
			}
		}
		return builder.toString();
	}

	public static String generateGitUrl(final String name) {
		return generateWebUrl(name)+".git";
	}

	private static String generateWebUrl(final String name) {
		final String repo = Joiner.on("-").join(Splitter.on(' ').split(name.toLowerCase()));
		return "http://localhost:8080/repository/"+repo;
	}

}