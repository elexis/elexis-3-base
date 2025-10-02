package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.CoreUtil;
import ch.oaat_otma.Diagnosis;
import ch.oaat_otma.Service;
import ch.oaat_otma.Side;
import ch.oaat_otma.Tarpo;
import ch.oaat_otma.casemaster.Casemaster;
import ch.oaat_otma.casemaster.CasemasterResult;
import ch.oaat_otma.casemaster.Patient;
import ch.oaat_otma.casemaster.Session;

@Component(service = CasemasterService.class)
public class CasemasterService {

	private static final String CAP_ASSIGNMENT_FILENAME = "system_ambP_11c_cap_assignment.json";
	
	private Casemaster caseMaster;

	@Activate
	public void activate() {
		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File capAssignmentFile = new File(tarifmatcherdir, CAP_ASSIGNMENT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, CAP_ASSIGNMENT_FILENAME))) {
			IOUtils.copy(CasemasterService.class.getResourceAsStream("/rsc/casemaster/" + CAP_ASSIGNMENT_FILENAME),
					out);
			caseMaster = new Casemaster(capAssignmentFile);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Casemaster", e);
		}
	}

	public CasemasterResult getResult(IBilled billed, IEncounter encounter) {
		int sessionIdx = 1;
		List<Session> sessions = new ArrayList<>();
		List<IEncounter> encounters = encounter.getCoverage().getEncounters().stream()
				.filter(e -> e.getInvoice() == null || e.getInvoiceState() == InvoiceState.CANCELLED).toList();
		for (IEncounter iEncounter : encounters) {
			Session session = new Session(sessionIdx++, iEncounter.getDate());
			for (IDiagnosisReference diagnose : iEncounter.getDiagnoses()) {
				if (diagnose.getCodeSystemName().toLowerCase().contains("icd")) {
					session.setDiagnosis(new Diagnosis(diagnose.getCode()));
					break;
				}
				if (diagnose.getCodeSystemName().toLowerCase().contains("ti-code")) {
					session.setDiagnosis(new Diagnosis(diagnose.getCode()));
				}
			}
			if (iEncounter.equals(encounter)) {
				addBilled(billed, session);
			}
			for (IBilled encounterBilled : iEncounter.getBilled()) {
				addBilled(encounterBilled, session);
			}
			sessions.add(session);
		}
		Patient patient = new Patient();
		patient.setBirthDate(encounters.get(0).getPatient().getDateOfBirth().toLocalDate());
		patient.setSex(encounters.get(0).getPatient().getGender() == Gender.FEMALE ? "W" : "M");
		patient.setSessions(sessions);
		return caseMaster.apply(patient);
	}

	private void addBilled(IBilled billed, Session session) {
		IBillable billable = billed.getBillable();
		if (billable.getCodeSystemName().toLowerCase().contains("tardoc")
				|| billable.getCodeSystemName().toLowerCase().contains("ambulantepauschalen")) {
			session.addService(new Service(billable.getCode(), getSide(billed),
					Double.valueOf(billed.getAmount()).intValue(), billed.getEncounter().getDate(), session.number));
		} else if (!(billable instanceof IArticle) || billable.getCodeSystemCode().equals("402")) {
			session.addTarpo(new Tarpo(billable.getCode(), billable.getCodeSystemCode(), billed.getAmount(),
					billed.getEncounter().getDate(), billed.getAmount(), (billed.getPrice().getCents() / 100),
					Side.NONE));
		}
	}

	private Side getSide(IBilled billed) {
		if (StringUtils.isNotBlank((String) billed.getExtInfo(Constants.FLD_EXT_SIDE))) {
			String side = (String) billed.getExtInfo(Constants.FLD_EXT_SIDE);
			if (Constants.SIDE_L.equals(side)) {
				return Side.L;
			} else if (Constants.SIDE_R.equals(side)) {
				return Side.R;
			}
		}
		return Side.NONE;
	}
}
