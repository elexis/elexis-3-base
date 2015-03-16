/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.core.elements;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.data.LabResult;

public class LabResultCreateListener extends ElexisEventListenerImpl {
	private Executor executor = Executors.newCachedThreadPool();

	public LabResultCreateListener(){
		super(LabResult.class, ElexisEvent.EVENT_CREATE);
	}
	
	@Override
	public void catchElexisEvent(ElexisEvent ev){
		LabResult result = (LabResult) ev.getObject();
		if (result != null) {
			// check if we should add an EAL code to the active Konsultation
			executor.execute(new AddLabInboxElement(result));
		}
	}
}
