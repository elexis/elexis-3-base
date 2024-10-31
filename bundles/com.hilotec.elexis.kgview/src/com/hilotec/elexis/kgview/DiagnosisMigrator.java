package com.hilotec.elexis.kgview;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;

import com.hilotec.elexis.kgview.diagnoseliste.DiagnoselisteItem;

import ch.elexis.core.findings.ICondition;
import ch.elexis.core.findings.ICondition.ConditionCategory;
import ch.elexis.core.findings.ICondition.ConditionStatus;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.migration.IMigratorService;
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
		monitor.beginTask("Hilotec Diagnosen Migration", allPatients.size());
		ConfigServiceHolder.get().set(IMigratorService.DIAGNOSE_SETTINGS_USE_STRUCTURED, true);
		for (Patient patient : allPatients) {
			DiagnoselisteItem rootDiagnosis = DiagnoselisteItem.getRoot(patient, DiagnoselisteItem.TYP_DIAGNOSELISTE);
			LocalDate dateRecorded = new TimeTool(rootDiagnosis.getDatum()).toLocalDate();
			for (DiagnoselisteItem di : getSortedChildren(rootDiagnosis)) {
				ICondition condition = findingsService.create(ICondition.class);
				condition.setPatientId(patient.getId());
				condition.setCategory(ConditionCategory.PROBLEMLISTITEM);
				condition.setStart(di.getText());
				condition.setStatus(ConditionStatus.ACTIVE);
				dateRecorded = dateRecorded.minusDays(1);
				condition.setDateRecorded(dateRecorded);
				StringJoiner textJoiner = new StringJoiner("\n");
				addSubConditions(textJoiner, getSortedChildren(di), 0);
				condition.setText(textJoiner.toString());
				findingsService.saveFinding(condition);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	private void addSubConditions(StringJoiner textJoiner, List<DiagnoselisteItem> sortedChildren, int depth) {
		String postfix = " ".repeat(depth);
		for (DiagnoselisteItem sub : sortedChildren) {
			textJoiner.add(postfix + sub.getText());
			if (getSortedChildren(sub).size() > 0) {
				addSubConditions(textJoiner, getSortedChildren(sub), depth + 2);
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
