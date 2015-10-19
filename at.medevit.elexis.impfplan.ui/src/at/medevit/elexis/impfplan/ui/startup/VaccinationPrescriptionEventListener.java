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

import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.dialogs.ApplicationInputDialog;
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
				Date d = new Date();
				if (ApplyVaccinationHandler.inProgress()) {
					d = ApplyVaccinationHandler.getKonsDate();
				}
				
				Mandant m = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
				ApplicationInputDialog aid = new ApplicationInputDialog(UiDesk.getTopShell(), p);
				aid.open();
				String lotNo = aid.getLotNo();
				String side = aid.getSide();
				
				Vaccination vacc =
					new Vaccination(p.get(Prescription.FLD_PATIENT_ID), p.getArtikel(), d, lotNo, m
						.storeToString());
				
				if (side != null && !side.isEmpty()) {
					vacc.setSide(side);
				}
			}
		});
	}
}
