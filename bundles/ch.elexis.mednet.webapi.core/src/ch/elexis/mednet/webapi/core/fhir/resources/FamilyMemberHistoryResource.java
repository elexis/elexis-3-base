package ch.elexis.mednet.webapi.core.fhir.resources;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Reference;

import ch.elexis.core.model.IPatient;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyMemberHistoryResource {

	public static List<FamilyMemberHistory> createFamilyMemberHistories(Reference patientReference,
			IPatient sourcePatient) {
		List<FamilyMemberHistory> familyHistories = new ArrayList<>();

		FamilyMemberHistory familyHistory = new FamilyMemberHistory();
		familyHistory.setId(UUID.randomUUID().toString());
		familyHistory.getMeta().addProfile(FHIRConstants.PROFILE_FAMALY_MEMBER_HISTORY);
		familyHistory.setStatus(FamilyMemberHistory.FamilyHistoryStatus.COMPLETED);

		familyHistory.setPatient(patientReference);

		familyHistory.setRelationship(new CodeableConcept().addCoding(new Coding()
				.setSystem(FHIRConstants.ROLE_CODE_SYSTEM).setCode("FAMMEMB").setDisplay("family member"))
				.setText("Family member of the patient"));

		FamilyMemberHistory.FamilyMemberHistoryConditionComponent condition = new FamilyMemberHistory.FamilyMemberHistoryConditionComponent();
		condition.setCode(new CodeableConcept()
				.addCoding(new Coding().setSystem(FHIRConstants.SNOMED_SYSTEM).setCode("416471007")
						.setDisplay("Family history of clinical finding"))
				.setText(sourcePatient.getFamilyAnamnese()));
		condition.setContributedToDeath(true);
		familyHistory.addCondition(condition);

		familyHistories.add(familyHistory);

		return familyHistories;
	}
}
