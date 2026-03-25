package ch.elexis.base.ch.arzttarife.ui.adjuster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher.PatientClassificationSystemService;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillableAdjuster;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.dialogs.DiagnoseSelektor;

@Component
public class AmbulatoryAllowanceUiAdjuster implements IBillableAdjuster {

	@Override
	public IBillable adjust(IBillable billable, IEncounter encounter) {
		if (billable instanceof IAmbulatoryAllowance
				&& ((IAmbulatoryAllowance) billable).getTyp() == AmbulantePauschalenTyp.TRIGGER) {
			if (getIcd10Diagnosis(encounter).isEmpty()) {
				Optional<PatientClassificationSystemService> classificationSystem = OsgiServiceUtil
						.getService(PatientClassificationSystemService.class);
				if (classificationSystem.isPresent()) {
					Map<String, List<String>> diagnosisInfo = classificationSystem.get()
							.getIcdDiagnosisInfo(billable.getCode());
					if (!diagnosisInfo.isEmpty()) {
						DiagnoseSelektor diagnoseSelektor = new DiagnoseSelektor(Display.getDefault().getActiveShell(),
								"ICD-10");
						diagnoseSelektor.setInfo("Für die Trigger Position " + billable.getCode()
								+ " können folgende ICD10 codes aus Kapitel(n) "
								+ diagnosisInfo.keySet().stream().collect(Collectors.joining(","))
								+ " verwendet werden.");
						List<String> filter = new ArrayList<String>();
						diagnosisInfo.values().forEach(l -> filter.addAll(toIcd10Notation(l)));
						diagnoseSelektor.setFilterCodes(filter);
						if (diagnoseSelektor.open() == DiagnoseSelektor.OK) {
							Object[] sel = diagnoseSelektor.getResult();
							if (sel != null && sel.length > 0) {
								IDiagnosis diagnose = (IDiagnosis) sel[0];
								encounter.addDiagnosis(diagnose);
								CoreModelServiceHolder.get().save(diagnose);
							}
						}
					}
				}
			}
		}
		return billable;
	}

	private List<String> toIcd10Notation(List<String> list) {
		return list.stream().map(s -> {
			if (s.length() > 3) {
				return s.substring(0, 3) + "." + s.substring(3, s.length());
			}
			return s;
		}).toList();
	}

	private List<IDiagnosisReference> getIcd10Diagnosis(IEncounter encounter) {
		return encounter.getDiagnoses().stream().filter(d -> d.getCodeSystemName().toLowerCase().contains("icd-10"))
				.toList();
	}

}
