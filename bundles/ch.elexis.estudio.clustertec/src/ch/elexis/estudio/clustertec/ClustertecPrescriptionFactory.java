package ch.elexis.estudio.clustertec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.clustertec.estudio.schemas.prescription.AddressComplexType;
import ch.clustertec.estudio.schemas.prescription.DeliveryAddress;
import ch.clustertec.estudio.schemas.prescription.Insurance;
import ch.clustertec.estudio.schemas.prescription.ObjectFactory;
import ch.clustertec.estudio.schemas.prescription.PatientAddress;
import ch.clustertec.estudio.schemas.prescription.Posology;
import ch.clustertec.estudio.schemas.prescription.Prescription;
import ch.clustertec.estudio.schemas.prescription.PrescriptorAddress;
import ch.clustertec.estudio.schemas.prescription.Product;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IXid;
import ch.elexis.core.types.Country;
import ch.elexis.core.types.Gender;

public class ClustertecPrescriptionFactory {

	private static final String DOMAIN_COVERCARD = "www.covercard.ch/xid";

	public static Prescription createPrescription(String user, String password) {
		Prescription ret = new ObjectFactory().createPrescription();
		ret.setUser(user);
		ret.setPassword(password);
		return ret;
	}

	public static PrescriptorAddress createPrescriptorAddress(String clientNrClustertec, IMandator mandator) {
		PrescriptorAddress ret = new ObjectFactory().createPrescriptorAddress();
		setAddress(ret, mandator);
		ret.setClientNrClustertec(clientNrClustertec);
		ret.setZsrId(getZsrId(mandator));
		ret.setEanId(getEanId(mandator));
		ret.setLangCode(1);
		return ret;
	}

	public static PatientAddress createPatientAddress(IPatient patient) {
		PatientAddress ret = new ObjectFactory().createPatientAddress();
		setAddress(ret, patient);
		if (patient.getDateOfBirth() != null) {
			ret.setBirthday(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(patient.getDateOfBirth()));
		}
		ret.setLangCode(1);
		ret.setSex(patient.getGender() == Gender.MALE ? 1 : 2);
		ret.setPatientNr(patient.getCode());
		ret.setCoverCardId(getCovercardId(patient));
		return ret;
	}

	public static DeliveryAddress createDeliveryAddress(IContact contact) {
		DeliveryAddress ret = new ObjectFactory().createDeliveryAddress();
		setAddress(ret, contact);
		return ret;
	}

	public static Product createProduct(IPrescription prescription, Integer quantity, Integer numberOfRepetitions,
			LocalDate validityRepetition, IContact insurance, int billingType, String insuranceNumber) {
		Product ret = new ObjectFactory().createProduct();
		ret.setPharmacode(getPharmaCode(prescription.getArticle()));
		ret.setEanId(getEan(prescription.getArticle()));
		ret.setDescription(StringUtils.abbreviate(prescription.getArticle().getName(), "", 49));

		String dosage = prescription.getDosageInstruction();
		Posology posology = new ObjectFactory().createPosology();
		posology.setLabel(false);
		if (StringUtils.isNotBlank(dosage)) {
			String[] dos = dosage.split("\\s*-\\s*");
			if (dos.length > 1) {
				if (getInt(dos[0]).isPresent()) {
					posology.setQtyMorning(getInt(dos[0]).get());
				} else {
					posology.setQtyMorningString(dos[0]);
				}
				if (getInt(dos[1]).isPresent()) {
					posology.setQtyMidday(getInt(dos[1]).get());
				} else {
					posology.setQtyMiddayString(dos[1]);
				}
				if (dos.length > 2) {
					if (getInt(dos[2]).isPresent()) {
						posology.setQtyEvening(getInt(dos[2]).get());
					} else {
						posology.setQtyEveningString(dos[2]);
					}
				}
				if (dos.length > 3) {
					if (getInt(dos[3]).isPresent()) {
						posology.setQtyNight(getInt(dos[3]).get());
					} else {
						posology.setQtyNightString(dos[3]);
					}
				}
			} else {
				posology.setPosologyText(dosage);
			}
		}
		if (StringUtils.isNotBlank(prescription.getRemark())) {
			posology.setPosologyText(StringUtils.abbreviate(prescription.getRemark(), "", 79));
			posology.setLabel(true);
		}
		if (quantity != null) {
			ret.setQuantity(quantity);
		}
		ret.setRepetition(false);
		if (numberOfRepetitions != null) {
			ret.setNrOfRepetitions(numberOfRepetitions);
			ret.setRepetition(numberOfRepetitions > 0);
		}
		if (validityRepetition != null) {
			ret.setValidityRepetition(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(validityRepetition));
			ret.setRepetition(true);
		}
		Insurance clustertecInsurance = new ObjectFactory().createInsurance();
		if (insurance != null) {
			clustertecInsurance.setEanId(getEanId(insurance));
			clustertecInsurance.setInsuranceName(StringUtils.abbreviate(insurance.getLabel(), "", 30));
			clustertecInsurance.setBillingType(billingType);
			clustertecInsurance.setInsureeNr(insuranceNumber);
		} else {
			clustertecInsurance.setBillingType(2);
		}
		ret.setInsurance(clustertecInsurance);

		return ret;
	}

	private static Optional<Integer> getInt(String string) {
		try {
			return Optional.of(Integer.valueOf(string));
		} catch (Exception e) {
			// ignore is not an int
		}
		return Optional.empty();
	}

	private static String getCovercardId(IPatient patient) {
		IXid patientCovercard = patient.getXid(DOMAIN_COVERCARD);
		if (patientCovercard != null) {
			if (patientCovercard.getDomainId() != null && !patientCovercard.getDomainId().isEmpty()) {
				return patientCovercard.getDomainId().trim();
			}
		}
		return null;
	}

	private static Long getEanId(IContact contact) {
		IXid mandatorEan = contact.getXid(XidConstants.DOMAIN_EAN);
		if (mandatorEan != null) {
			if (mandatorEan.getDomainId() != null && !mandatorEan.getDomainId().isEmpty()) {
				return Long.valueOf(mandatorEan.getDomainId().trim());
			}
		}
		return null;
	}

	private static String getZsrId(IMandator mandator) {
		IXid mandantZSR = mandator.getXid(XidConstants.DOMAIN_KSK);
		if (mandantZSR != null && mandantZSR.getDomainId() != null) {
			return mandantZSR.getDomainId().replaceAll("[\\s\\.\\-]", "").trim();
		}
		return null;
	}

	private static void setAddress(AddressComplexType ret, IContact contact) {
		if (contact.isPerson()) {
			IPerson person = contact.asIPerson();
			ret.setTitleCode(person.getGender() == Gender.MALE ? 1 : 2);
			ret.setTitle(person.getTitel());
		} else {
			ret.setTitle("Firma");
			ret.setTitleCode(0);
		}
		ret.setLastName(contact.getDescription1());
		ret.setFirstName(contact.getDescription2());

		ret.setStreet(contact.getStreet());
		ret.setZipCode(contact.getZip());
		ret.setCity(contact.getCity());
		ret.setKanton("ch");
		Country country = contact.getCountry();
		if (Country.NDF == country) {
			country = Country.CH;
		}
		ret.setCountry(StringUtils.abbreviate(country.toString(), "", 2));
		ret.setPhoneNrBusiness(contact.getPhone1());
		ret.setPhoneNrHome(contact.getPhone2());
		ret.setFaxNr(contact.getFax());
		ret.setEmail(contact.getEmail());
	}

	private static Long getEan(IArticle article) {
		String ret = article.getGtin();
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("EAN"); //$NON-NLS-1$
			if (value instanceof String && ((String) value).length() > 11) {
				return Long.valueOf((String) value);
			}
		}
		return null;
	}

	private static String getPharmaCode(IArticle article) {
		String ret = StringUtils.EMPTY;
		try {
			Method method = article.getClass().getMethod("getPHAR"); //$NON-NLS-1$
			ret = (String) method.invoke(article);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// ignore no pharmacode available ...
		}
		if (StringUtils.isBlank(ret)) {
			Object value = article.getExtInfo("Pharmacode"); //$NON-NLS-1$
			if (value instanceof String && ((String) value).length() == 7) {
				ret = (String) value;
			}
		}
		return StringUtils.leftPad(StringUtils.defaultString(ret), 7, "0");
	}






//	String pharmacode = ArticleUtil.getPharmaCode(artikel);
//	String eanId = ArticleUtil.getEan(artikel);
//	String description = artikel.getName();
//	int quantity = item.getAmount();
//
//	if (StringTool.isNothing(pharmacode) || StringTool.isNothing(eanId) || StringTool.isNothing(description)
//			|| quantity < 1) {
//
//		StringBuffer msg = new StringBuffer();
//		msg.append("Der Artikel " + PersistentObject.checkNull(description) + " (Pharma-Code "
//				+ PersistentObject.checkNull(pharmacode) + ") ist nicht korrekt konfiguriert: ");
//		if (StringTool.isNothing(pharmacode)) {
//			msg.append("Ungültiger Pharmacode. ");
//		}
//		if (StringTool.isNothing(eanId)) {
//			msg.append("Ungültiger EAN-Code. ");
//		}
//		if (quantity < 1) {
//			msg.append("Ungültige Anzahl. ");
//		}
//		msg.append("Bitte korrigieren Sie diese Fehler.");
//
//		SWTHelper.alert("Fehlerhafter Artikel", msg.toString());
//
//		throw new XChangeException("Fehlerhafter Artikel: Pharamcode: " + pharmacode + ", EAN: " + eanId
//				+ ", Name: " + description + ", Anzahl: " + quantity);
//	}
//
//	sb.append("<product" + " pharmacode=\"" + escapeXmlAttribute(pharmacode) + "\"" + " eanId=\""
//			+ escapeXmlAttribute(eanId) + "\"" + " description=\"" + escapeXmlAttribute(description) + "\""
//			+ " quantity=\"" + quantity + "\"" + " positionType=\"1\"" + "/>" + XML_NEWLINE);

}
