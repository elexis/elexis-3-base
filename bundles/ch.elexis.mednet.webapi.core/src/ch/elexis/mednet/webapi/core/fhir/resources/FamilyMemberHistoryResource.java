package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.findings.IFamilyMemberHistory;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.fhir.resources.util.FhirResourceFactory;

public class FamilyMemberHistoryResource {

	public static List<FamilyMemberHistory> createFamilyMemberHistories(Reference patientReference,
			IPatient sourcePatient,
			IFindingsService findingsService, FhirResourceFactory resourceFactory) {
		List<FamilyMemberHistory> familyHistories = new ArrayList<>();

		IFamilyMemberHistory localFamilyHistory = findingsService.create(IFamilyMemberHistory.class);
		localFamilyHistory.setPatientId(patientReference.getReferenceElement().getIdPart());
		localFamilyHistory.setText(sourcePatient.getFamilyAnamnese());

		FamilyMemberHistory familyHistory = resourceFactory.getResource(localFamilyHistory, IFamilyMemberHistory.class,
				FamilyMemberHistory.class);

		familyHistories.add(familyHistory);
		return familyHistories;
	}
}

