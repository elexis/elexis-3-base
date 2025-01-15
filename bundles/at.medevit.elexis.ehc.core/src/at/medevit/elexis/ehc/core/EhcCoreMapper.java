/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.core;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.projecthusky.common.basetypes.NameBaseType;
import org.projecthusky.common.basetypes.OrganizationBaseType;
import org.projecthusky.common.enums.AdministrativeGender;
import org.projecthusky.common.enums.CodeSystems;
import org.projecthusky.common.enums.TelecomAddressUse;
import org.projecthusky.common.model.Address;
import org.projecthusky.common.model.Author;
import org.projecthusky.common.model.Identificator;
import org.projecthusky.common.model.Name;
import org.projecthusky.common.model.Organization;
import org.projecthusky.common.model.Patient;
import org.projecthusky.common.model.Telecom;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.IPersistentObject;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Xid;
import ch.rgw.tools.TimeTool;

/**
 * Utility class for mapping objects between the Elexis domain and the
 * ehealthconnector domain.
 *
 * @see <a href=
 *      "https://sourceforge.net/projects/ehealthconnector/">ehealthconnector</a>
 * @author thomas
 *
 */
public class EhcCoreMapper {

	private final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)[a-z]?$"); //$NON-NLS-1$

	public static Name getEhcName(String name) {
		NameBaseType baseType = new NameBaseType();

		String[] parts = name.split(StringUtils.SPACE);
		if (parts.length == 1) {
			baseType.setFamily(parts[0]);
		} else if (parts.length == 2) {
			baseType.setGiven(parts[0]);
			baseType.setFamily(parts[1]);
		} else if (parts.length >= 3) {
			baseType.setPrefix(parts[0]);
			baseType.setGiven(parts[1]);
			baseType.setFamily(parts[2]);
		}
		return new org.projecthusky.common.model.Name(baseType);
	}

	public static org.projecthusky.common.model.Patient getEhcPatient(ch.elexis.data.Patient elexisPatient) {
		Patient ret = new Patient(getEhcPersonName(elexisPatient), getEhcGenderCode(elexisPatient),
				new TimeTool(elexisPatient.getGeburtsdatum()));
		System.out.println();
		// PHONE
		String value = elexisPatient.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.setTelecoms(Collections.singletonList(
					new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.PRIVATE).build())));
		}
		value = elexisPatient.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.setTelecoms(Collections.singletonList(
					new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.MOBILE).build())));
		}

		// ADDRESS
		Anschrift elexisAddress = elexisPatient.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}

		String socialSecurityNumber = elexisPatient.getXid(DOMAIN_AHV);
		if (socialSecurityNumber != null) {
			socialSecurityNumber = socialSecurityNumber.trim();
			socialSecurityNumber = socialSecurityNumber.replaceAll("\\.", StringUtils.EMPTY); //$NON-NLS-1$
			if (socialSecurityNumber.length() == 11) {
				ret.addId(new Identificator(CodeSystems.SWISS_SSN_DEPRECATED.getCodeSystemId(), socialSecurityNumber));
			} else if (socialSecurityNumber.length() == 13) {
				ret.addId(new Identificator(CodeSystems.SWISS_SSN.getCodeSystemId(), socialSecurityNumber));
			} else {
				LoggerFactory.getLogger(EhcCoreMapper.class).warn("Ignoring SSN [" + socialSecurityNumber + "] length " //$NON-NLS-1$ //$NON-NLS-2$
						+ socialSecurityNumber.length() + " not vaild."); //$NON-NLS-1$
			}
		}

		return ret;
	}

	public static Address getEhcAddress(Anschrift elexisAddress) {
		String elexisStreet = elexisAddress.getStrasse();
		String houseNumber = StringUtils.EMPTY;
		// try to get the house number
		Matcher matcher = lastIntPattern.matcher(elexisStreet);
		if (matcher.find()) {
			houseNumber = matcher.group(1);
			elexisStreet = elexisStreet.substring(0, matcher.start(1));
		}

		Address ehcAddress = new Address(
				Address.builder().withStreetAddressLine1(elexisAddress.getStrasse()).withStreetName(elexisStreet.trim())
						.withBuildingNumber(houseNumber).withPostalCode(elexisAddress.getPlz())
						.withCity(elexisAddress.getOrt()).build());
		return ehcAddress;
	}

	public static Author getEhcAuthor(Mandant elexisMandant) {
		String gln = elexisMandant.getXid(DOMAIN_EAN);
		Author ret = new Author(getEhcPersonName(elexisMandant), gln);
		// add old EAN oid
		if (gln != null) {
			ret.addId(new Identificator("1.3.88", gln)); //$NON-NLS-1$
		}
		// PHONE
		String value = elexisMandant.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.setTelecoms(Collections.singletonList(
					new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.PRIVATE).build())));
		}
		value = elexisMandant.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.setTelecoms(Collections.singletonList(
					new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.MOBILE).build())));
		}

		// ADDRESS
		Anschrift elexisAddress = elexisMandant.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}

		return ret;
	}

	public static Organization getEhcOrganization(Mandant elexisMandant) {
		Rechnungssteller rechnungssteller = elexisMandant.getRechnungssteller();
		String gln = rechnungssteller.getXid(DOMAIN_EAN);
		Organization ret = new Organization(OrganizationBaseType.builder()
				.withPrimaryName(NameBaseType.builder().withName(rechnungssteller.getLabel()).build()).build());

		if (StringUtils.isNotBlank(gln)) {
			ret.addIdentificator(
					new Identificator(CodeSystems.GLN.getCodeSystemId(), gln));
		}

		// PHONE
		String value = elexisMandant.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.getTelecomList()
					.add(new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.PRIVATE).build()));
		}
		value = elexisMandant.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) { //$NON-NLS-1$
			ret.getTelecomList()
					.add(new Telecom(Telecom.builder().withValue(value).withUsage(TelecomAddressUse.MOBILE).build()));
		}
		// ADDRESS
		Anschrift elexisAddress = rechnungssteller.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}

		return ret;
	}

	public static org.projecthusky.common.model.Name getEhcPersonName(Person elexisPerson) {
		NameBaseType baseType = new NameBaseType();
		baseType.setFamily(elexisPerson.getName());
		baseType.setGiven(elexisPerson.getVorname());
		baseType.setPrefix(elexisPerson.get(Person.TITLE));

		org.projecthusky.common.model.Name ret = new org.projecthusky.common.model.Name(baseType);
		return ret;
	}

	public static AdministrativeGender getEhcGenderCode(Person elexisPerson) {
		if (elexisPerson.getGeschlecht().equals(Person.FEMALE)) {
			return AdministrativeGender.FEMALE;
		} else if (elexisPerson.getGeschlecht().equals(Person.MALE)) {
			return AdministrativeGender.MALE;
		}
		return AdministrativeGender.UNDIFFERENTIATED;
	}

	public static ch.elexis.data.Patient getElexisPatient(Patient ehcPatient, boolean create) {
		// try to look up via ids
		List<Identificator> ids = ehcPatient.getIds();
		for (Identificator identificator : ids) {
			String idRoot = identificator.getRoot();
			if (idRoot.equals(CodeSystems.SWISS_SSN_DEPRECATED.getCodeSystemId())
					|| idRoot.equals(CodeSystems.SWISS_SSN.getCodeSystemId())) {
				IPersistentObject ret = Xid.findObject(DOMAIN_AHV, identificator.getExtension());
				if (ret instanceof Kontakt) {
					if (((Kontakt) ret).istPatient()) {
						return ch.elexis.data.Patient.load(ret.getId());
					}
				}
				if (ret instanceof ch.elexis.data.Patient) {
					return (ch.elexis.data.Patient) ret;
				}
			}
		}

		Query<ch.elexis.data.Patient> qpa = new Query<ch.elexis.data.Patient>(ch.elexis.data.Patient.class);
		// initialize data
		Name ehcName = ehcPatient.getName();
		Date ehcBirthdate = ehcPatient.getBirthday();
		String gender = ehcPatient.getAdministrativeGenderCode() == AdministrativeGender.FEMALE ? Person.FEMALE
				: Person.MALE;
		TimeTool ttBirthdate = new TimeTool();
		// add data to query
		if (ehcName.getFamily() != null && !ehcName.getFamily().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_NAME, Query.EQUALS, ehcName.getFamily());
		}
		if (ehcName.getGiven() != null && !ehcName.getGiven().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_FIRSTNAME, Query.EQUALS, ehcName.getGiven());
		}
		if (ehcBirthdate != null) {
			ttBirthdate.setTime(ehcBirthdate);
			qpa.add(Person.BIRTHDATE, Query.EQUALS, ttBirthdate.toString(TimeTool.DATE_COMPACT));
		}
		List<ch.elexis.data.Patient> existing = qpa.execute();
		// create or overwrite Patient
		ch.elexis.data.Patient ret = null;
		if (existing.isEmpty() && create) {
			ret = new ch.elexis.data.Patient(ehcName.getFamily(), ehcName.getGiven(),
					ttBirthdate.toString(TimeTool.DATE_COMPACT), gender);
		} else {
			ret = existing.get(0);
		}

		return ret;
	}

	public static ch.elexis.data.Patient getElexisPatient(Bundle bundle, boolean create) {
		List<org.hl7.fhir.r4.model.Patient> fhirPatients = bundle.getEntry().stream()
				.filter(e -> e.getResource() != null && e.getResource() instanceof org.hl7.fhir.r4.model.Patient)
				.map(e -> (org.hl7.fhir.r4.model.Patient) e.getResource()).toList();
		if (!fhirPatients.isEmpty()) {
			for (org.hl7.fhir.r4.model.Patient fhirPatient : fhirPatients) {
				// try to look up via ids
				List<Identifier> ids = fhirPatient.getIdentifier();
				for (Identifier identifier : ids) {
					String idRoot = identifier.getSystem();
					if (idRoot.equals(CodeSystems.SWISS_SSN_DEPRECATED.getCodeSystemId())
							|| idRoot.equals(CodeSystems.SWISS_SSN.getCodeSystemId())) {
						IPersistentObject ret = Xid.findObject(DOMAIN_AHV, identifier.getValue());
						if (ret instanceof Kontakt) {
							if (((Kontakt) ret).istPatient()) {
								return ch.elexis.data.Patient.load(ret.getId());
							}
						}
						if (ret instanceof ch.elexis.data.Patient) {
							return (ch.elexis.data.Patient) ret;
						}
					}
				}

				Query<ch.elexis.data.Patient> qpa = new Query<ch.elexis.data.Patient>(ch.elexis.data.Patient.class);
				// initialize data
				HumanName fhirName = fhirPatient.getNameFirstRep();
				Date fhirBirthdate = fhirPatient.getBirthDate();
				String gender = fhirPatient
						.getGender() == org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.FEMALE ? Person.FEMALE
								: Person.MALE;
				TimeTool ttBirthdate = new TimeTool();
				// add data to query
				if (fhirName.getFamily() != null && !fhirName.getFamily().isEmpty()) {
					qpa.add(ch.elexis.data.Patient.FLD_NAME, Query.EQUALS, fhirName.getFamily());
				}
				if (!fhirName.getGiven().isEmpty() && !fhirName.getGiven().get(0).isEmpty()) {
					qpa.add(ch.elexis.data.Patient.FLD_FIRSTNAME, Query.EQUALS,
							fhirName.getGiven().get(0).asStringValue());
				}
				if (fhirBirthdate != null) {
					ttBirthdate.setTime(fhirBirthdate);
					qpa.add(Person.BIRTHDATE, Query.EQUALS, ttBirthdate.toString(TimeTool.DATE_COMPACT));
				}
				List<ch.elexis.data.Patient> existing = qpa.execute();
				// create or overwrite Patient
				if (existing.isEmpty() && create) {
					return new ch.elexis.data.Patient(fhirName.getFamily(), fhirName.getGiven().get(0).asStringValue(),
							ttBirthdate.toString(TimeTool.DATE_COMPACT), gender);
				} else {
					return existing.get(0);
				}
			}
		}
		return null;
	}

	public static void importEhcAddress(ch.elexis.data.Kontakt kontakt, Address address) {
		Anschrift elexisAddress = kontakt.getAnschrift();
		elexisAddress.setOrt(address.getCity());
		elexisAddress.setPlz(address.getPostalCode());
		elexisAddress.setStrasse(address.getStreetName() + StringUtils.SPACE + address.getBuildingNumber());
		kontakt.setAnschrift(elexisAddress);
	}

	public static void importEhcPhone(ch.elexis.data.Kontakt kontakt, List<Telecom> list) {
		list.forEach(t -> {
			if (StringUtils.isNotBlank(t.getValue())) {
				if (t.getUsage() == TelecomAddressUse.MOBILE) {
					kontakt.set(Kontakt.FLD_PHONE2, t.getValue());
				} else {
					kontakt.set(Kontakt.FLD_PHONE2, t.getValue());
				}
			}
		});
	}
}
