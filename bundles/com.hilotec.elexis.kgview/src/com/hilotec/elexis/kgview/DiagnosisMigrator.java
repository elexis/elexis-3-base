package com.hilotec.elexis.kgview;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;

import com.hilotec.elexis.kgview.diagnoseliste.DiagnoselisteItem;

import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.ICondition.ConditionCategory;
import ch.elexis.core.findings.ICondition.ConditionStatus;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.IObservation.ObservationCategory;
import ch.elexis.core.findings.IObservation.ObservationCode;
import ch.elexis.core.findings.migration.IMigratorService;
import ch.elexis.core.findings.util.model.TransientCoding;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class DiagnosisMigrator {

	@Inject
	private IFindingsService findingsService;

	public DiagnosisMigrator() {
		CoreUiUtil.injectServices(this);
	}

	public void migrate(IProgressMonitor monitor) {

		List<Patient> allPatients = new Query<Patient>(Patient.class).execute();
		monitor.beginTask("Hilotec Diagnosen/Anamnese Migration", allPatients.size());
		ConfigServiceHolder.get().set(IMigratorService.DIAGNOSE_SETTINGS_USE_STRUCTURED, true);
		ConfigServiceHolder.get().set(IMigratorService.PERSANAM_SETTINGS_USE_STRUCTURED, true);
		for (Patient patient : allPatients) {
			DiagnoselisteItem rootSysAnamnesis = DiagnoselisteItem.getRoot(patient, DiagnoselisteItem.TYP_SYSANAMNESE);
			LocalDate dateRecorded = new TimeTool(rootSysAnamnesis.getDatum()).toLocalDate();
			for (DiagnoselisteItem di : getSortedChildren(rootSysAnamnesis)) {
				LocalDate itemDate = new TimeTool(di.getDatum()).toLocalDate();
				ICondition condition = findingsService.create(ICondition.class);
				condition.setPatientId(patient.getId());
				condition.setCategory(ConditionCategory.PROBLEMLISTITEM);
				condition.setStart(
						di.getText() + " (" + itemDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")");
				condition.setStatus(ConditionStatus.ACTIVE);
				dateRecorded = dateRecorded.minusDays(1);
				condition.setDateRecorded(dateRecorded);
				StringJoiner textJoiner = new StringJoiner("\n");
				addSubItems(textJoiner, getSortedChildren(di), 0, true);
				condition.setText(textJoiner.toString());
				findingsService.saveFinding(condition);
			}

			DiagnoselisteItem rootPersAnamnesis = DiagnoselisteItem.getRoot(patient,
					DiagnoselisteItem.TYP_PERSANAMNESE);
			for (DiagnoselisteItem di : getSortedChildren(rootPersAnamnesis)) {
				LocalDate itemDate = new TimeTool(di.getDatum()).toLocalDate();
				IObservation observation = getOrCreatePersonalAnamnesis(patient.getId());
				StringJoiner textJoiner = new StringJoiner("\n");
				// append to existing text
				observation.getText().ifPresent(text -> textJoiner.add(text));
				textJoiner.add(di.getText() + " (" + itemDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")");
				addSubItems(textJoiner, getSortedChildren(di), 2, true);
				observation.setText(textJoiner.toString());
				findingsService.saveFinding(observation);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	private IObservation getOrCreatePersonalAnamnesis(String patientId) {
		List<IObservation> observations = findingsService.getPatientsFindings(patientId, IObservation.class);
		observations = observations.parallelStream().filter(iFinding -> isPersAnamnese(iFinding))
				.collect(Collectors.toList());
		if (observations.isEmpty()) {
			IObservation observation = findingsService.create(IObservation.class);
			observation.setPatientId(patientId);
			observation.setCategory(ObservationCategory.SOCIALHISTORY);
			observation.setCoding(Collections.singletonList(new TransientCoding(ObservationCode.ANAM_PERSONAL)));
			findingsService.saveFinding(observation);
			return observation;
		} else {
			return observations.get(0);
		}
	}

	private boolean isPersAnamnese(IObservation iFinding) {
		if (iFinding instanceof IObservation && iFinding.getCategory() == ObservationCategory.SOCIALHISTORY) {
			for (ICoding code : iFinding.getCoding()) {
				if (ObservationCode.ANAM_PERSONAL.isSame(code)) {
					return true;
				}
			}
		}
		return false;
	}

	private void addSubItems(StringJoiner textJoiner, List<DiagnoselisteItem> sortedChildren, int depth,
			boolean addDate) {
		String postfix = " ".repeat(depth);
		for (DiagnoselisteItem sub : sortedChildren) {
			LocalDate dateRecorded = new TimeTool(sub.getDatum()).toLocalDate();
			if (addDate) {
				textJoiner.add(postfix + sub.getText() + " ("
						+ dateRecorded.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")");
			} else {
				textJoiner.add(postfix + sub.getText());
			}
			if (getSortedChildren(sub).size() > 0) {
				addSubItems(textJoiner, getSortedChildren(sub), depth + 2, addDate);
			}
		}
	}

	private List<DiagnoselisteItem> getSortedChildren(DiagnoselisteItem item) {
		List<DiagnoselisteItem> sortedChildren = item.getChildren();
		Collections.sort(sortedChildren, new DiagnoselisteItemComparator());
		return sortedChildren;
	}

	private class DiagnoselisteItemComparator implements Comparator<DiagnoselisteItem> {
		@Override
		public int compare(DiagnoselisteItem o1, DiagnoselisteItem o2) {
			return Integer.compare(o1.getPosition(), o2.getPosition());
		}
	}
}
