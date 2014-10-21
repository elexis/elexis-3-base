/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui.startup;

import java.util.Date;

import org.eclipse.jface.dialogs.InputDialog;

import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.handlers.ApplyVaccinationHandler;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Artikel;
import ch.elexis.data.Mandant;
import ch.elexis.data.Prescription;

public class VaccinationPrescriptionEventListener implements ElexisEventListener {
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, Prescription.class,
		ElexisEvent.EVENT_CREATE);
	
	@Override
	public void catchElexisEvent(ElexisEvent ev){
		Prescription p = (Prescription) ev.getObject();
		Artikel artikel = p.getArtikel();
		if (artikel == null)
			return;
		String atc_code = artikel.getATC_code();
		if (atc_code != null && atc_code.length() > 4) {
			if (atc_code.toUpperCase().startsWith(
				DiseaseDefinitionModel.VACCINATION_ATC_GROUP_TRAILER)) {
				performVaccination(p);
			}
		}
	}
	
	@Override
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
	private void performVaccination(final Prescription p){
		UiDesk.asyncExec(new Runnable() {
			
			@Override
			public void run(){
				if (ApplyVaccinationHandler.inProgress()) {
					ApplyVaccinationHandler.createVaccination(p.getArtikel());
				} else {
					Mandant m = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
					InputDialog lotId =
						new InputDialog(UiDesk.getTopShell(), "Lot-Nummer",
							"Bitte geben Sie die Lot-Nummer des Impfstoffes an", null, null);
					lotId.open();
					String lotNo = lotId.getValue();
					
					new Vaccination(p.get(Prescription.PATIENT_ID), p.getArtikel(), new Date(),
						lotNo, m.storeToString());
				}
			}
		});
	}
}
