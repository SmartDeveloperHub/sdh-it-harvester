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
 *   Artifact    : org.smartdeveloperhub.harvesters.it.frontend:it-frontend-dist:0.2.0-SNAPSHOT
 *   Bundle      : it-frontend-dist-0.2.0-SNAPSHOT.war
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.it.testing.generator;

import java.util.Random;

final class ChangeDecissionPoint implements PolicyDecissionPoint<ChangeInformationPoint, ChangeManager> {

	private static final class Add implements Decission<ChangeManager> {
		@Override
		public void apply(final ChangeManager pep) {
			pep.add();
		}
	}

	private static final class Remove implements Decission<ChangeManager> {
		@Override
		public void apply(final ChangeManager pep) {
			pep.remove();
		}
	}

	private static final class NoOp implements Decission<ChangeManager> {
		@Override
		public void apply(final ChangeManager pep) {
			// Do nothing
		}
	}

	private final Random random;

	ChangeDecissionPoint(final Random random) {
		this.random = random;
	}

	@Override
	public Decission<ChangeManager> decide(final ChangeInformationPoint point) {
		Decission<ChangeManager> result=new NoOp();
		if(point.canModify()) {
			if(point.canAdd() && point.canRemove()) {
				if(this.random.nextBoolean()) {
					result=new Add();
				} else {
					result=new Remove();
				}
			} else if(point.canAdd()) {
				result=new Add();
			} else {
				result=new Remove();
			}
		}
		return result;
	}

}