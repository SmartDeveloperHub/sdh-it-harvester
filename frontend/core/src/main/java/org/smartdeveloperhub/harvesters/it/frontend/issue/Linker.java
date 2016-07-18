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
package org.smartdeveloperhub.harvesters.it.frontend.issue;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.ldp4j.application.data.IndividualHelper;
import org.ldp4j.application.data.Name;
import org.ldp4j.application.data.PropertyHelper;
import org.smartdeveloperhub.harvesters.it.frontend.vocabulary.RDF;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public abstract class Linker<V extends Serializable,K> {

	private final Set<String> inferredTypes;
	private final IndividualHelper helper;

	public Linker(final IndividualHelper helper) {
		this.helper = helper;
		this.inferredTypes=Sets.newLinkedHashSet();
	}

	public Linker<V,K> infer(final String type) {
		this.inferredTypes.add(type);
		return this;
	}

	public void link(final String relation, final Collection<? extends K> elements) {
		if(elements.isEmpty()) {
			return;
		}
		addInferredTypes();
		addRelations(relation, elements);
	}

	public void link(final String relation, final K element, @SuppressWarnings("unchecked") final K... moreElements) {
		if(element==null) {
			return;
		}
		link(
			relation,
			ImmutableList.
				<K>builder().
					add(element).
					add(moreElements).
					build());
	}

	private void addRelations(final String relation, final Collection<? extends K> elements) {
		final String managerId=managerId();
		final PropertyHelper property=this.helper.property(relation);
		for(final K key:elements){
			property.withIndividual(createName(key),managerId);
		}
	}

	private void addInferredTypes() {
		final PropertyHelper property=this.helper.property(RDF.TYPE);
		for(final String type:this.inferredTypes) {
			property.withIndividual(type);
		}
	}

	protected abstract String managerId();
	protected abstract Name<V> createName(K key);

}