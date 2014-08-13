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

import at.medevit.elexis.inbox.core.elements.service.ServiceComponent;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabResult;

public class LabResultCreateListener extends ElexisEventListenerImpl {
	public LabResultCreateListener(){
		super(LabResult.class, ElexisEvent.EVENT_CREATE);
	}
	
	@Override
	public void catchElexisEvent(ElexisEvent ev){
		LabResult result = (LabResult) ev.getObject();
		if (result != null && result.getPatient() != null) {
			ServiceComponent.getService().createInboxElement(result.getPatient(),
				CoreHub.actMandant, result);
			Kontakt doctor = result.getPatient().getStammarzt();
			if (doctor != null && doctor.exists() && !doctor.equals(CoreHub.actMandant)) {
				ServiceComponent.getService().createInboxElement(
					ElexisEventDispatcher.getSelectedPatient(), doctor, result);
			}
		}
	}
}
