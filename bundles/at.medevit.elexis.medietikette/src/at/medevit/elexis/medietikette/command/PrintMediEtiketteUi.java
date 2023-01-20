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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.medietikette.Activator;
import at.medevit.elexis.medietikette.Messages;
import at.medevit.elexis.medietikette.data.DataAccessor;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.status.ElexisStatus;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.ui.dialogs.EtiketteDruckenDialog;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;

public class PrintMediEtiketteUi extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(PrintMediEtiketteUi.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		synchronized (PrintMediEtiketteUi.class) {
			// init the selection
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				List<IPrescription> prescriptions = getPrescriptionsFromSelection(strucSelection);
				List<IArticle> artikels = getArtikelsFromSelection(strucSelection);

				for (IPrescription prescription : prescriptions) {
					DataAccessor.setSelectedPrescription(prescription);
					DataAccessor.setSelectedArticel(prescription.getArticle());
					print(event);
					// clear the selection
					DataAccessor.setSelectedPrescription(null);
					DataAccessor.setSelectedArticel(null);
				}

				for (IArticle artikel : artikels) {
					DataAccessor.setSelectedArticel(artikel);
					print(event);
					// clear the selection
					DataAccessor.setSelectedPrescription(null);
					DataAccessor.setSelectedArticel(null);
				}
			}
			return null;
		}
	}

	private void print(ExecutionEvent event) {
		// start printing the etikette
		Kontakt kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Patient.class);
		EtiketteDruckenDialog dlg = new EtiketteDruckenDialog(HandlerUtil.getActiveShell(event), kontakt,
				TT_MEDI_LABEL);
		dlg.setTitle(Messages.PrintMediEtiketteUi_DialogTitel);
		dlg.setMessage(Messages.PrintMediEtiketteUi_DialogMessage);
		if (!CoreHub.localCfg.get("Drucker/Etiketten/Choose", true)) { //$NON-NLS-1$
			dlg.setBlockOnOpen(false);
			dlg.open();
			if (dlg.doPrint()) {
				dlg.close();
			} else {
				StatusManager
						.getManager().handle(
								new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID, ElexisStatus.CODE_NOFEEDBACK,
										Messages.PrintMediEtiketteUi_PrintError, ElexisStatus.LOG_ERRORS),
								StatusManager.BLOCK);
				return;
			}
		} else {
			dlg.setBlockOnOpen(true);
			dlg.open();
		}
	}

	private List<IArticle> getArtikelsFromSelection(IStructuredSelection strucSelection) {
		List<?> selection = strucSelection.toList();
		List<IArticle> ret = new ArrayList<>();
		for (Object object : selection) {
			if (object instanceof IBilled) {
				IBilled verrechnet = (IBilled) object;
				IBillable verrechenbar = verrechnet.getBillable();
				if (verrechenbar instanceof IArticle) {
					ret.add((IArticle) verrechenbar);
				}
			}
		}
		return ret;
	}

	private List<IPrescription> getPrescriptionsFromSelection(IStructuredSelection strucSelection) {
		List<?> selection = strucSelection.toList();
		List<IPrescription> ret = new ArrayList<>();
		for (Object object : selection) {
			if (object instanceof IPrescription) {
				ret.add((IPrescription) object);
			} else if (object.getClass().getName()
					.equals("ch.elexis.core.ui.medication.views.MedicationTableViewerItem")) { //$NON-NLS-1$
				try {
					Method method = object.getClass().getMethod("getPrescription", new Class[0]); //$NON-NLS-1$
					if (method != null) {
						IPrescription pres = (IPrescription) method.invoke(object, new Object[0]);
						if (pres != null) {
							ret.add(pres);
						}
					}
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					logger.warn("Could not get selected prescription.", e); //$NON-NLS-1$
				}
			}
		}
		return ret;
	}
}
