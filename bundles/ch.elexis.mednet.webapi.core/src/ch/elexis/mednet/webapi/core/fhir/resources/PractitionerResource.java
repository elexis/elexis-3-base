package ch.elexis.mednet.webapi.core.fhir.resources;

import java.util.UUID;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Practitioner;
import org.osgi.service.component.annotations.Reference;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IXidService;
import ch.elexis.core.types.Gender;

import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

import java.util.Optional;

public class PractitionerResource {


	private IModelService coreModelService;

	@Reference
	private static IXidService xidService = OsgiServiceUtil.getService(IXidService.class).get();

	public PractitionerResource() {
		this.coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();

	}


	public Practitioner createPractitioner(IPatient sourcePatient) {
		String gpId = (String) sourcePatient.getExtInfo(FHIRConstants.FHIRKeys.PRACTITIONER_GP_ID);
		if (gpId == null || gpId.isEmpty()) {
			return null;
		}
		Optional<IContact> gpContactOptional = coreModelService.load(gpId, IContact.class);
		if (!gpContactOptional.isPresent()) {
			return null;
		}
		IContact gpContact = gpContactOptional.get();
		Practitioner practitioner = new Practitioner();
		practitioner.setId(UUID.nameUUIDFromBytes(gpContact.getId().getBytes()).toString());
		practitioner.getMeta().addProfile(FHIRConstants.PROFILE_PRACTITIONER);

		// GLN
		String gln = getIdentifierValue(gpContact, FHIRConstants.GLN_SYSTEM);
		if (gln != null && !gln.isEmpty()) {
			practitioner.addIdentifier().setSystem(FHIRConstants.GLN_IDENTIFIER).setValue(gln);
		}

		// ZSR
		String zsr = getIdentifierValue(gpContact, FHIRConstants.ZSR_SYSTEM);
		if (zsr != null && !zsr.isEmpty()) {
			practitioner.addIdentifier().setSystem(FHIRConstants.ZSR_IDENTIFIER).setValue(zsr);
		}

		// Name
		HumanName name = new HumanName().setUse(HumanName.NameUse.USUAL).setFamily(gpContact.getDescription2())
				.addGiven(gpContact.getDescription1());

		if (gpContact instanceof IPerson) {
			IPerson gpPerson = (IPerson) gpContact;
			if (gpPerson.getTitel() != null && !gpPerson.getTitel().isEmpty()) {
				name.addPrefix(gpPerson.getTitel());
			}
			if (gpPerson.getTitelSuffix() != null && !gpPerson.getTitelSuffix().isEmpty()) {
				name.addSuffix(gpPerson.getTitelSuffix());
			}
		}

		practitioner.addName(name);

		// Telecom (Phone)
		if (gpContact.getPhone1() != null && !gpContact.getPhone1().isEmpty()) {
			practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(gpContact.getPhone1())
					.setUse(ContactPoint.ContactPointUse.WORK);
		}

		// Telecom (Email)
		if (gpContact.getEmail() != null && !gpContact.getEmail().isEmpty()) {
			practitioner.addTelecom().setSystem(ContactPoint.ContactPointSystem.EMAIL).setValue(gpContact.getEmail())
					.setUse(ContactPoint.ContactPointUse.WORK);
		}

		// Address
		Address address = new Address().setUse(Address.AddressUse.WORK).addLine(gpContact.getStreet())
				.setCity(gpContact.getCity()).setPostalCode(gpContact.getZip())
				.setCountry(gpContact.getCountry() != null ? gpContact.getCountry().toString() : null);
		practitioner.addAddress(address);

		// Gender
		AdministrativeGender gender = AdministrativeGender.OTHER;
		if (gpContact instanceof IPerson) {
			IPerson gpPerson = (IPerson) gpContact;
			switch (gpPerson.getGender()) {
			case MALE:
				gender = AdministrativeGender.MALE;
				break;
			case FEMALE:
				gender = AdministrativeGender.FEMALE;
				break;
			default:
				gender = AdministrativeGender.OTHER;
				break;
			}
		}
		practitioner.setGender(gender);

		return practitioner;
	}

	private String getIdentifierValue(IContact contact, String system) {
		IXid xid = xidService.getXid(contact, system);
		return xid.getDomainId();
	}
}
