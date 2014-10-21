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
package at.medevit.elexis.impfplan.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.ui.PlatformUI;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.VaccinationView;
import at.medevit.elexis.impfplan.ui.billing.AddVaccinationToKons;
import at.medevit.elexis.impfplan.ui.dialogs.ApplyVaccinationDialog;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObjectFactory;
import ch.rgw.tools.TimeTool;

public class ApplyVaccinationHandler extends AbstractHandler {
	private static boolean inProgress = false;
	private static Patient patient;
	private static String administratorString;
	private static TimeTool doa;
	private static String lotNo;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ApplyVaccinationDialog avd = new ApplyVaccinationDialog(UiDesk.getTopShell());
		int retVal = avd.open();
		if (retVal == TitleAreaDialog.OK) {
			patient = ElexisEventDispatcher.getSelectedPatient();
			administratorString = avd.getAdministratorString();
			lotNo = avd.getLotNo();
			doa = avd.getDateOfAdministration();
			
			String ean = avd.getEAN();
			String articleString = avd.getArticleString();
			Artikel art = (Artikel) new PersistentObjectFactory().createFromString(articleString);
			
			if (avd.isSupplement()) {
				if (art != null) {
					new Vaccination(patient.getId(), art, doa.getTime(), lotNo, administratorString);
				} else {
					String articleAtcCode = avd.getAtcCode();
					new Vaccination(patient.getId(), null, articleString, ean, articleAtcCode,
						doa.getTime(), lotNo, administratorString);
				}
				updateVaccinationView();
			} else {
				inProgress = true;
				AddVaccinationToKons addVacToKons = new AddVaccinationToKons(patient, art, ean);
				
				if (!addVacToKons.findOrCreateKons()) {
					SWTHelper.showError("Nicht erstellbar",
						"Konnte Impfung nich eintragen, da keine Konsultation vorhanden ist.");
				}
			}
			
		}
		return null;
	}
	
	public static void createVaccination(Artikel art){
		inProgress = false;
		new Vaccination(patient.getId(), art, doa.getTime(), lotNo, administratorString);
		updateVaccinationView();
		patient = null;
		lotNo = null;
		administratorString = null;
	}
	
	public static boolean inProgress(){
		return inProgress;
	}
	
	private static void updateVaccinationView(){
		VaccinationView vaccView =
			(VaccinationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(VaccinationView.PART_ID);
		vaccView.updateUi(true);
	}
}
