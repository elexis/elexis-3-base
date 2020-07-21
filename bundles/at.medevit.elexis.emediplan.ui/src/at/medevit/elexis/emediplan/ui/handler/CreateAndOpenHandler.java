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
import java.util.Arrays;
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
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.medication.handlers.PrintTakingsListHandler.SorterAdapter;
import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.core.ui.medication.views.MedicationView;

public class CreateAndOpenHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		IPatient patient = ContextServiceHolder.get().getActivePatient().orElse(null);
		if (patient == null) {
			return null;
		}
		IMandator mandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
		if (mandant == null) {
			return null;
		}
		
		String medicationType =
			event.getParameter("ch.elexis.core.ui.medication.commandParameter.medication"); //$NON-NLS-1$
		// if not set use all
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "all";
		}
		
		List<IPrescription> prescriptions = getPrescriptions(patient, medicationType, event);
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
				IDocument letter =
					SaveEMediplanUtil.saveEMediplan(patient, mandant, pdfOutput.toByteArray());
				// open with system viewer
				try {
					Program.launch(SaveEMediplanUtil.writeTempPdf(pdfOutput));
					ContextServiceHolder.get()
						.postEvent(ElexisEventTopics.BASE + "emediplan/ui/create", letter);
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
	
	private List<IPrescription> sortPrescriptions(List<IPrescription> prescriptions,
		ExecutionEvent event){
		SorterAdapter sorter = new SorterAdapter(event);
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof MedicationView) {
			return sorter.getSorted(prescriptions);
		}
		return prescriptions;
	}
	
	@SuppressWarnings("unchecked")
	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType,
		ExecutionEvent event){
		if ("selection".equals(medicationType)) {
			ISelection selection =
				HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection != null && !selection.isEmpty()) {
				List<IPrescription> ret = new ArrayList<>();
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				if (strucSelection.getFirstElement() instanceof MedicationTableViewerItem) {
					List<MedicationTableViewerItem> mtvItems =
						(List<MedicationTableViewerItem>) strucSelection.toList();
					for (MedicationTableViewerItem mtvItem : mtvItems) {
						IPrescription p = mtvItem.getPrescription();
						if (p != null) {
							ret.add(p);
						}
					}
				} else if (strucSelection.getFirstElement() instanceof IPrescription) {
					ret.addAll(strucSelection.toList());
				}
				return ret;
			}
		} else if ("all".equals(medicationType)) {
			List<IPrescription> ret = new ArrayList<>();
			ret.addAll(patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION,
				EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION)));
			return ret;
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(Collections.singletonList(EntryType.FIXED_MEDICATION));
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(Collections.singletonList(EntryType.RESERVE_MEDICATION));
		} else if ("symptomatic".equals(medicationType)) {
			return patient
				.getMedication(Collections.singletonList(EntryType.SYMPTOMATIC_MEDICATION));
		}
		return Collections.emptyList();
	}
}
