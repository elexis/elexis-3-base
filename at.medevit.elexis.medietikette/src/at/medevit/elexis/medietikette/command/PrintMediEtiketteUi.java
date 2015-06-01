/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.elexis.medietikette.command;

import static ch.elexis.core.ui.text.TextTemplateRequirement.TT_MEDI_LABEL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import at.medevit.elexis.medietikette.Activator;
import at.medevit.elexis.medietikette.Messages;
import at.medevit.elexis.medietikette.data.DataAccessor;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.ui.dialogs.EtiketteDruckenDialog;
import ch.elexis.data.Artikel;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Verrechnet;

public class PrintMediEtiketteUi extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		synchronized (PrintMediEtiketteUi.class) {
			boolean valid = false;
			// init the selection
			ISelection selection =
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				Object selected = strucSelection.getFirstElement();
				if (selected instanceof Prescription) {
					Prescription prescription = (Prescription) selected;
					DataAccessor.setSelectedPrescription(prescription);
					DataAccessor.setSelectedArticel(prescription.getArtikel());
					valid = true;
				} else if (selected instanceof Verrechnet) {
					Verrechnet verrechnet = (Verrechnet) selected;
					IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
					if (verrechenbar instanceof Artikel) {
						Artikel articel = (Artikel) verrechenbar;
						DataAccessor.setSelectedArticel(articel);
						valid = true;
					} else {
						return null;
					}
				}
			}
			
			if (valid) {
				// start printing the etikette
				Kontakt kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Patient.class);
				EtiketteDruckenDialog dlg =
					new EtiketteDruckenDialog(HandlerUtil.getActiveShell(event), kontakt,
						TT_MEDI_LABEL);
				dlg.setTitle(Messages.PrintMediEtiketteUi_DialogTitel);
				dlg.setMessage(Messages.PrintMediEtiketteUi_DialogMessage);
				if (!CoreHub.localCfg.get("Drucker/Etiketten/Choose", true)) { //$NON-NLS-1$
					dlg.setBlockOnOpen(false);
					dlg.open();
					if (dlg.doPrint()) {
						dlg.close();
					} else {
						StatusManager.getManager().handle(
							new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID,
								ElexisStatus.CODE_NOFEEDBACK,
								Messages.PrintMediEtiketteUi_PrintError, ElexisStatus.LOG_ERRORS),
							StatusManager.BLOCK);
						return null;
					}
				} else {
					dlg.setBlockOnOpen(true);
					dlg.open();
				}
			}
			// clear the selection
			DataAccessor.setSelectedPrescription(null);
			DataAccessor.setSelectedArticel(null);
			
			return null;
		}
	}
}
