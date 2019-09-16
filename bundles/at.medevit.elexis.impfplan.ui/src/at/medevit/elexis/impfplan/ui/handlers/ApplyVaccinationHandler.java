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

import java.util.Date;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.elexis.impfplan.ui.billing.AddVaccinationToKons;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ApplyVaccinationHandler extends AbstractHandler {
	private static Logger logger = LoggerFactory.getLogger(ApplyVaccinationHandler.class);
	
	private static boolean inProgress = false;
	private static PersistentObjectDropTarget dropTarget;
	private static TimeTool doa;
	private static Konsultation kons;
	private LeistungenView leistungenView;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		if (dropTarget == null) {
			dropTarget =
				new PersistentObjectDropTarget("Impfplan", UiDesk.getTopShell(), new DropReceiver());
		}
		
		// open the LeistungenView
		try {
			if (StringTool.isNothing(LeistungenView.ID)) {
				SWTHelper.alert("Fehler", "LeistungenView.ID");
			}
			
			leistungenView =
				(LeistungenView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(LeistungenView.ID);
			CodeSelectorHandler csHandler = CodeSelectorHandler.getInstance();
			csHandler.setCodeSelectorTarget(dropTarget);
			csHandler.getCodeSelectorTarget().registered(false);
			
			for (CTabItem cti : leistungenView.ctab.getItems()) {
				if (cti.getText().equalsIgnoreCase(ArtikelstammConstants.CODESYSTEM_NAME)) {
					leistungenView.setSelected(cti);
					leistungenView.setFocus();
					leistungenView.ctab.setSelection(cti);
				}
			}
		} catch (Exception e) {
			logger.error("Error trying to open LeistungenView", e);
		}
		return null;
	}
	
	public static Date getKonsDate(){
		doa = new TimeTool(kons.getDatum());
		return doa.getTime();
	}
	
	public static boolean inProgress(){
		return inProgress;
	}
	
	/**
	 * waits for dropps/double-clicks on vaccinations
	 *
	 */
	private final class DropReceiver implements PersistentObjectDropTarget.IReceiver {
		public void dropped(PersistentObject o, DropTargetEvent ev){
			if (o instanceof Artikel) {
				Artikel artikel = (Artikel) o;
				
				// only accept vaccinations
				if (artikel.isVaccination()) {
					AddVaccinationToKons addVacToKons = new AddVaccinationToKons(
						ElexisEventDispatcher.getSelectedPatient(), artikel, artikel.getEAN());
					
					kons = addVacToKons.findOrCreateKons();
					if (kons == null) {
						logger.warn(
							"Could not insert vaccination as no consultation was found for this patient");
						SWTHelper.showError("Nicht erstellbar",
							"Konnte Impfung nich eintragen, da keine Konsultation vorhanden ist.");
					}
					inProgress = true;
				}
			}
		}
		
		public boolean accept(PersistentObject o){
			if (ElexisEventDispatcher.getSelectedPatient() != null) {
				if (o instanceof Artikel) {
					return true;
				}
			}
			return false;
		}
	}
}
