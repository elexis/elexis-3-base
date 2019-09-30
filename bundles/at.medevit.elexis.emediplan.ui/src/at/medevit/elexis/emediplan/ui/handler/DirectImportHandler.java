package at.medevit.elexis.emediplan.ui.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.core.model.chmed16a.Posology;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;

public class DirectImportHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String emediplan =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.emediplan");
		String patientid =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.patientid");
		String stopreason =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.stopreason");
		String medicationType =
			event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.medication"); //$NON-NLS-1$
		// if not set use all
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "all";
		}
		
		if (StringUtils.isNotEmpty(patientid) && StringUtils.isNotEmpty(emediplan)) {
			Medication medication =
				EMediplanServiceHolder.getService().createModelFromChunk(emediplan);
			
			EMediplanServiceHolder.getService().addExistingArticlesToMedication(medication);
			
			Patient patient = Patient.load(patientid);
			if (patient.exists()) {
				ElexisEventDispatcher.fireSelectionEvent(patient);
				
				List<Prescription> currentMedication = getPrescriptions(patient, medicationType);
				for (Prescription prescription : currentMedication) {
					prescription.stop(null);
					prescription.setStopReason(stopreason != null ? stopreason : "Direct Import");
					ElexisEventDispatcher.getInstance().fire(new ElexisEvent(prescription,
						Prescription.class, ElexisEvent.EVENT_UPDATE));
				}
				List<Medicament> notFoundMedicament = new ArrayList<>();
				for (Medicament medicament : medication.Medicaments) {
					if (medicament.artikelstammItem != null) {
						createPrescription(medicament, patient);
					} else {
						notFoundMedicament.add(medicament);
					}
				}
				if (!notFoundMedicament.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					sb.append(
						"Folgende Medikamente konnte im Artikelstamm nicht gefunden werden\n\n");
					notFoundMedicament
						.forEach(m -> sb.append(" - " + m.Id + " " + m.AppInstr + " " + m.TkgRsn));
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warnung",
						sb.toString());
				}
			}
		}
		return null;
	}
	
	private List<Prescription> getPrescriptions(Patient patient, String medicationType){
		if ("all".equals(medicationType)) {
			List<Prescription> ret = new ArrayList<Prescription>();
			ret.addAll(patient.getMedication(EntryType.FIXED_MEDICATION));
			ret.addAll(patient.getMedication(EntryType.RESERVE_MEDICATION));
			ret.addAll(patient.getMedication(EntryType.SYMPTOMATIC_MEDICATION));
			return ret;
		} else if ("fix".equals(medicationType)) {
			return patient.getMedication(EntryType.FIXED_MEDICATION);
		} else if ("reserve".equals(medicationType)) {
			return patient.getMedication(EntryType.RESERVE_MEDICATION);
		} else if ("symptomatic".equals(medicationType)) {
			return patient.getMedication(EntryType.SYMPTOMATIC_MEDICATION);
		}
		return Collections.emptyList();
	}
	
	private Prescription createPrescription(Medicament medicament, Patient patient){
		medicament.entryType = EntryType.FIXED_MEDICATION;
		if (medicament.Pos != null && !medicament.Pos.isEmpty()) {
			for (Posology pos : medicament.Pos) {
				if (pos.InRes == 1) {
					medicament.entryType = EntryType.RESERVE_MEDICATION;
				}
			}
		}
		Prescription prescription = new Prescription(medicament.artikelstammItem, patient,
			medicament.dosis, medicament.AppInstr);
		prescription.set(new String[] {
			Prescription.FLD_PRESC_TYPE, Prescription.FLD_DATE_FROM, Prescription.FLD_DATE_UNTIL
		}, String.valueOf(medicament.entryType.numericValue()), medicament.dateFrom,
			medicament.dateTo);
		prescription.setDisposalComment(medicament.TkgRsn);
		CoreHub.getLocalLockService().acquireLock(prescription);
		CoreHub.getLocalLockService().releaseLock(prescription);
		return prescription;
	}
}
