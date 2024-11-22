package ch.elexis.mednet.webapi.core.fhir.resources;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IRelatedContact;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IXidService;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;
import ch.elexis.core.findings.util.fhir.transformer.mapper.IPatientPatientAttributeMapper;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.core.types.LocalizeUtil;
import ch.elexis.core.types.RelationshipType;


import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.rest.api.SummaryEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PatientResource {
	@org.osgi.service.component.annotations.Reference(target = "(" + IModelService.SERVICEMODELNAME
			+ "=ch.elexis.core.model)")
	private static IModelService coreModelService;

	@org.osgi.service.component.annotations.Reference
	private static IXidService xidService = OsgiServiceUtil.getService(IXidService.class).get();

	private static IPatientPatientAttributeMapper patientMapper = new IPatientPatientAttributeMapper(coreModelService,
			xidService);

	public static Patient createFhirPatient(IPatient sourcePatient, Bundle bundle) {
	    Patient fhirPatient = new Patient();
	    patientMapper.elexisToFhir(sourcePatient, fhirPatient, SummaryEnum.DATA, null);
		fhirPatient.setId(UUID.nameUUIDFromBytes(sourcePatient.getId().getBytes()).toString());
		fhirPatient.getMeta().addProfile(FHIRConstants.PROFILE_PATIENT);

	    addContactInformation(sourcePatient, fhirPatient, bundle);
	    return fhirPatient;
	}

	private static void addContactInformation(IPatient source, Patient target, Bundle bundle) {
		List<ContactComponent> contacts = new ArrayList<>();
		List<IRelatedContact> relatedContacts = source.getRelatedContacts();
		for (IRelatedContact relatedContact : relatedContacts) {
			IContact otherContact = relatedContact.getOtherContact();

			if (otherContact != null) {

				ContactComponent contactComponent = createContactComponent(relatedContact, otherContact, bundle);

				if (otherContact.isOrganization()) {

					String organizationId = otherContact.getId();
					String organizationFullUrl = FHIRConstants.UUID_PREFIX
							+ UUID.nameUUIDFromBytes(organizationId.getBytes()).toString();
					if (bundle.getEntry().stream().noneMatch(entry -> organizationFullUrl.equals(entry.getFullUrl()))) {
						Organization organization = OrganizationResource.createOrganization(otherContact);
						bundle.addEntry().setFullUrl(organizationFullUrl).setResource(organization);
					}

					contactComponent.setOrganization(new Reference(organizationFullUrl));
				}

				contacts.add(contactComponent);
			}
		}
		target.setContact(contacts);
	}

	private static ContactComponent createContactComponent(IRelatedContact relatedContact, IContact otherContact,
			Bundle bundle) {
	    ContactComponent contactComponent = new ContactComponent();

	    String fhirCode = mapRelationshipToRoleCode(relatedContact.getOtherType());

	    CodeableConcept relationship = new CodeableConcept()
				.addCoding(new Coding().setSystem(FHIRConstants.ROLE_CODE_SYSTEM_V2).setCode(fhirCode)
	                    .setDisplay(relatedContact.getRelationshipDescription()));

	    contactComponent.setRelationship(Collections.singletonList(relationship));

	   if (otherContact.isPerson()) {

	        IPerson person = CoreModelServiceHolder.get().load(otherContact.getId(), IPerson.class).get();
	        HumanName humanName = new HumanName().setFamily(person.getLastName()).addGiven(person.getFirstName())
	                .addPrefix(person.getTitel()).addSuffix(person.getTitelSuffix()).setUse(HumanName.NameUse.USUAL);
	        contactComponent.setName(humanName);

	        AdministrativeGender gender = AdministrativeGender.UNKNOWN;
	        Gender personGender = person.getGender();
	        if (personGender != null) {
	            switch (personGender) {
	                case MALE:
	                    gender = AdministrativeGender.MALE;
	                    break;
	                case FEMALE:
	                    gender = AdministrativeGender.FEMALE;
	                    break;
	                default:
	                    gender = AdministrativeGender.UNKNOWN;
	                    break;
	            }
	        }
	        contactComponent.setGender(gender);

			Address address = new Address().setUse(Address.AddressUse.HOME).addLine(otherContact.getStreet())
					.setCity(otherContact.getCity()).setPostalCode(otherContact.getZip())
					.setCountry(otherContact.getCountry().toString());
			contactComponent.setAddress(address);

			List<ContactPoint> contactPoints = new ArrayList<>();
			if (otherContact.getMobile() != null) {
				ContactPoint phone = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE)
						.setValue(otherContact.getMobile()).setUse(ContactPoint.ContactPointUse.HOME);
				contactPoints.add(phone);
			}
			if (otherContact.getEmail() != null) {
				ContactPoint email = new ContactPoint().setSystem(ContactPoint.ContactPointSystem.EMAIL)
						.setValue(otherContact.getEmail()).setUse(ContactPoint.ContactPointUse.HOME);
				contactPoints.add(email);
			}
			contactComponent.setTelecom(contactPoints);
	    }



	    return contactComponent;
	}


	private static String mapRelationshipToRoleCode(RelationshipType relationshipType) {
		switch (relationshipType) {
		case FAMILY_PARENT:
			return "PRN";
		case FAMILY_CHILD:
			return "CHILD";
		case BUSINESS_EMPLOYER:
			return "E";
		case BUSINESS_EMPLOYEE:
			return "E";
		case FAMILY_ICE:
			return "C";
		default:
			return "U";
		}
	}

	public static String[] getBezugKontaktTypes() {
		RelationshipType[] allRelationshipTypes = RelationshipType.values();
		String[] displayValues = new String[allRelationshipTypes.length];
		int idx = 0;
		for (RelationshipType relationshipType : allRelationshipTypes) {
			displayValues[idx++] = LocalizeUtil.getLocaleText(relationshipType);
		}
		return displayValues;
	}
}
