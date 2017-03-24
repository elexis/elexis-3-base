/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.ui.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.emediplan.core.EMediplanService;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.ui.medication.handlers.PrintTakingsListHandler.SorterAdapter;
import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.core.ui.medication.views.MedicationView;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;

public class CreateAndOpenHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient == null) {
			return null;
		}
		Mandant mandant = ElexisEventDispatcher.getSelectedMandator();
		if(mandant == null) {
			return null;
		}
		
		String medicationType =
			event.getParameter("ch.elexis.core.ui.medication.commandParameter.medication"); //$NON-NLS-1$
		// if not set use all
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "all";
		}
		
		List<Prescription> prescriptions = getPrescriptions(patient, medicationType, event);
		if (prescriptions != null && !prescriptions.isEmpty()) {
			prescriptions = sortPrescriptions(prescriptions, event);
			
			BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
			ServiceReference<EMediplanService> eMediplanServiceRef =
				bundleContext.getServiceReference(EMediplanService.class);
			if (eMediplanServiceRef != null) {
				EMediplanService eMediplanService = bundleContext.getService(eMediplanServiceRef);
				ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
				eMediplanService.exportEMediplanPdf(mandant, patient, prescriptions, pdfOutput);
				// save as Brief
				SaveEMediplanUtil.saveEMediplan(patient, mandant, pdfOutput.toByteArray());
				// open with system viewer
				try {
					Program.launch(SaveEMediplanUtil.writeTempPdf(pdfOutput));
				} catch (IOException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Das Rezept konnte nicht angezeigt werden.");
				}
				bundleContext.ungetService(eMediplanServiceRef);
			} else {
				LoggerFactory.getLogger(getClass()).error("No EMediplanService available");
				MessageDialog.openError(HandlerUtil.getActiveShell(event), "Fehler",
					"Kein eMediplan Service gefunden.");
			}
		}
		return null;
	}
	
	private List<Prescription> sortPrescriptions(List<Prescription> prescriptions,
		ExecutionEvent event){
		SorterAdapter sorter = new SorterAdapter(event);
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof MedicationView) {
			return sorter.getSorted(prescriptions);
		}
		return prescriptions;
	}
	
	@SuppressWarnings("unchecked")
	private List<Prescription> getPrescriptions(Patient patient, String medicationType,
		ExecutionEvent event){
		if ("selection".equals(medicationType)) {
			ISelection selection =
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection != null && !selection.isEmpty()) {
				List<Prescription> ret = new ArrayList<Prescription>();
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				if (strucSelection.getFirstElement() instanceof MedicationTableViewerItem) {
					List<MedicationTableViewerItem> mtvItems =
						(List<MedicationTableViewerItem>) strucSelection.toList();
					for (MedicationTableViewerItem mtvItem : mtvItems) {
						Prescription p = mtvItem.getPrescription();
						if (p != null) {
							ret.add(p);
						}
					}
				} else if (strucSelection.getFirstElement() instanceof Prescription) {
					ret.addAll(strucSelection.toList());
				}
				return ret;
			}
		} else if ("all".equals(medicationType)) {
			List<Prescription> ret = new ArrayList<Prescription>();
			ret.addAll(patient.getMedication(EntryType.FIXED_MEDICATION));
			ret.addAll(patient.getMedication(EntryType.RESERVE_MEDICATION));
			return ret;
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(EntryType.FIXED_MEDICATION);
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(EntryType.RESERVE_MEDICATION);
		}
		return Collections.emptyList();
	}
}
