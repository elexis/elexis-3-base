package com.hilotec.elexis.kgview;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.hilotec.elexis.kgview.data.KonsData;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.text.model.Samdas.Record;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

public class ArchivKGMigrator {

	public static final String DOMAIN_HILOTEC_MIGRATION_ARCHIVKG = "www.hilotec.com/migration/archivkg/xid";

	public ArchivKGMigrator() {
		XidServiceHolder.get().localRegisterXIDDomainIfNotExists(DOMAIN_HILOTEC_MIGRATION_ARCHIVKG,
				"hilotec archivkg migration", XidConstants.ASSIGNMENT_LOCAL);
	}

	public void migrate(IProgressMonitor monitor) {

		List<Patient> allPatients = new Query<Patient>(Patient.class).execute();
		monitor.beginTask("Hilotec ArchivKG Migration", allPatients.size());
		for (Patient patient : allPatients) {
			for (Fall fall : patient.getFaelle()) {
				for (Konsultation kons : fall.getBehandlungen(false)) {
					IEncounter encounter = CoreModelServiceHolder.get().load(kons.getId(), IEncounter.class)
							.orElse(null);
					if (encounter != null) {
						IXid migrated = XidServiceHolder.get().getXid(encounter, DOMAIN_HILOTEC_MIGRATION_ARCHIVKG);
						if (migrated == null) {
							KonsData archivKg = KonsData.load(kons);
							if (archivKg.exists()) {
								StringBuilder sb = new StringBuilder();
								addParagraph("JetzigesLeiden", archivKg.getJetzigesLeiden(),
										archivKg.getJetzigesLeidenICPC(), sb);
								addParagraph("Status", archivKg.getLokalstatus(), sb);
								addParagraph("Röntgen", archivKg.getRoentgen(), sb);
								addParagraph("EKG", archivKg.getEKG(), sb);
								addParagraph("Diagnose", archivKg.getDiagnose(), archivKg.getDiagnoseICPC(), sb);
								addParagraph("Therapie", archivKg.getTherapie(), sb);
								addParagraph("Verlauf", archivKg.getVerlauf(), sb);
								addParagraph("Procedere", archivKg.getProzedere(), archivKg.getProzedereICPC(), sb);

								if (StringUtils.isNotBlank(sb.toString())) {
									appendKonsText(encounter, sb);
								}
							}
						}
					}
				}
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	private void appendKonsText(IEncounter encounter, StringBuilder sb) {
		Samdas samdas = new Samdas(encounter.getVersionedEntry().getHead());
		Record rec = samdas.getRecord();
		String recText = rec.getText();
		if (StringUtils.isNotBlank(recText)) {
			recText += "\n\n" + sb.toString();
		} else {
			recText += sb.toString();
		}
		rec.setText(recText);
		EncounterServiceHolder.get().updateVersionedEntry(encounter, samdas);
		XidServiceHolder.get().addXid(encounter, DOMAIN_HILOTEC_MIGRATION_ARCHIVKG, encounter.getId(), true);
	}

	private void addParagraph(String titel, String text, StringBuilder sb) {
		addParagraph(titel, text, null, sb);
	}

	private void addParagraph(String titel, String text, String icpc, StringBuilder sb) {
		if ((text == null || text.isEmpty()) && (icpc == null || icpc.isEmpty()))
			return;

		sb.append(titel + ":\n");
		if (icpc != null && !icpc.isEmpty())
			sb.append("ICPC: " + icpc.replace(",", ", ") + "\n");
		sb.append(text);
		sb.append("\n\n");
	}
}
