package at.medevit.elexis.emediplan.ui.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.emediplan.core.EMediplanService;
import at.medevit.elexis.emediplan.core.EMediplanServiceHolder;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medicament;
import at.medevit.elexis.emediplan.core.model.chmed16a.Medication;
import at.medevit.elexis.emediplan.core.model.chmed16a.Posology;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.builder.IPrescriptionBuilder;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.rgw.tools.TimeTool;

public class DirectImportHandler extends AbstractHandler implements IHandler {

	private EMediplanService mediplanService;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		mediplanService = EMediplanServiceHolder.getService();
		String emediplan = event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.emediplan"); //$NON-NLS-1$
		String patientid = event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.patientid"); //$NON-NLS-1$
		String stopreason = event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.stopreason"); //$NON-NLS-1$
		String medicationType = event.getParameter("at.medevit.elexis.emediplan.ui.directImport.parameter.medication"); //$NON-NLS-1$
		// if not set use all
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "all"; //$NON-NLS-1$
		}

		if (StringUtils.isNotEmpty(patientid) && StringUtils.isNotEmpty(emediplan)) {
			Medication medication = mediplanService.createModelFromChunk(emediplan);

			mediplanService.addExistingArticlesToMedication(medication);

			IPatient patient = CoreModelServiceHolder.get().load(patientid, IPatient.class).orElse(null);
			if (patient != null) {
				ContextServiceHolder.get().getRootContext().setNamed(IContextService.SELECTIONFALLBACK, patient);

				List<IPrescription> currentMedication = getPrescriptions(patient, medicationType);
				LocalDateTime now = LocalDateTime.now();
				for (IPrescription prescription : currentMedication) {
					MedicationServiceHolder.get().stopPrescription(prescription, now,
							stopreason != null ? stopreason : "Direct Import");
					// FIXME (#15795) this is a workaround as eclipselink only picks up the change
					// if done twice
					prescription.setDateTo(now);
					CoreModelServiceHolder.get().save(prescription);
				}
				currentMedication
						.forEach(pr -> ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, pr));
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
					sb.append("Folgende Medikamente konnte im Artikelstamm nicht gefunden werden\n\n");
					notFoundMedicament.forEach(m -> sb
							.append(" - " + getDsc(m) + StringUtils.SPACE + m.AppInstr + StringUtils.SPACE + m.TkgRsn)); //$NON-NLS-1$
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warnung", sb.toString());
				}
			}
		}
		return null;
	}

	private String getDsc(Medicament medicament) {
		String ret = medicament.Id;
		if (StringUtils.isNotBlank(mediplanService.getPFieldValue(medicament, "Dsc"))) { //$NON-NLS-1$
			ret = mediplanService.getPFieldValue(medicament, "Dsc"); //$NON-NLS-1$
		}
		return ret;
	}

	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType) {
		if ("all".equals(medicationType)) { //$NON-NLS-1$
			return patient.getMedication(Collections.emptyList());
		} else if ("fix".equals(medicationType)) { //$NON-NLS-1$
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION));
		} else if ("reserve".equals(medicationType)) { //$NON-NLS-1$
			return patient.getMedication(Arrays.asList(EntryType.RESERVE_MEDICATION));
		} else if ("symptomatic".equals(medicationType)) { //$NON-NLS-1$
			return patient.getMedication(Arrays.asList(EntryType.SYMPTOMATIC_MEDICATION));
		}
		return Collections.emptyList();
	}

	private IPrescription createPrescription(Medicament medicament, IPatient patient) {
		medicament.entryType = EntryType.FIXED_MEDICATION;
		String takingScheme = mediplanService.getPFieldValue(medicament, "TkgSch"); //$NON-NLS-1$
		if (StringUtils.isNotBlank(takingScheme)) {
			if ("Prd".equals(takingScheme)) { //$NON-NLS-1$
				medicament.entryType = EntryType.SYMPTOMATIC_MEDICATION;
			} else if ("Ond".equals(takingScheme)) { //$NON-NLS-1$
				medicament.entryType = EntryType.RESERVE_MEDICATION;
			}
		} else if (medicament.Pos != null && !medicament.Pos.isEmpty()) {
			for (Posology pos : medicament.Pos) {
				if (pos.InRes == 1) {
					medicament.entryType = EntryType.RESERVE_MEDICATION;
				}
			}
		}

		IPrescription prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(), ContextServiceHolder.get(),
				medicament.artikelstammItem, patient, medicament.dosis).build();

		getLocalDateTime(medicament.dateFrom).ifPresent(ldt -> prescription.setDateFrom(ldt));
		getLocalDateTime(medicament.dateTo).ifPresent(ldt -> prescription.setDateTo(ldt));

		prescription.setRemark(medicament.AppInstr);
		prescription.setEntryType(medicament.entryType);
		prescription.setDisposalComment(medicament.TkgRsn);

		CoreModelServiceHolder.get().save(prescription);
		return prescription;
	}

	private java.util.Optional<LocalDateTime> getLocalDateTime(String dateString) {
		if (dateString != null && !dateString.isEmpty()) {
			return java.util.Optional.of(new TimeTool(dateString).toLocalDateTime());
		}
		return java.util.Optional.empty();
	}
}
