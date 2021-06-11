package ch.elexis.TarmedRechnung;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.jdom.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.base.ch.arzttarife.importer.TrustCenters;
import ch.elexis.base.ch.arzttarife.rfe.IReasonForEncounter;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.model.format.PostalAddress;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class XMLExporterUtil {
	
	private static Logger logger = LoggerFactory.getLogger(XMLExporterUtil.class);
	
	private static final String ELEMENT_EMAIL = "email"; //$NON-NLS-1$
	private static final String ELEMENT_ONLINE = "online"; //$NON-NLS-1$
	
	public static Element buildRechnungsstellerAdressElement(final IContact k){
		return buildAdressElement(k, false, true);
	}
	
	public static Element buildAdressElement(final IContact contact){
		return buildAdressElement(contact, false, false);
	}
	
	public static Element buildAdressElement(final IContact contact, final boolean useAnschrift){
		return buildAdressElement(contact, useAnschrift, false);
	}
	
	public static Element buildAdressElement(final IContact contact, final boolean useAnschrift,
		boolean checkAnrede){
		Element ret;
		boolean anredeOrganization = false;
		if (checkAnrede) {
			String anrede = (String) contact.getExtInfo("Anrede");
			anredeOrganization = (anrede == null || anrede.isEmpty());
		}
		if (contact.isPerson() == false || anredeOrganization) {
			ret = new Element("company", XMLExporter.nsinvoice); //$NON-NLS-1$
			Element companyname = new Element("companyname", XMLExporter.nsinvoice); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(contact.getDescription1(), 35));
			ret.addContent(companyname);
			ret.addContent(buildPostalElement(contact));
			Element telcom = buildTelekomElement(contact);
			if (telcom != null && !telcom.getChildren().isEmpty()) {
				ret.addContent(telcom);
			}
			Element onlineElement = buildOnlineElement(contact);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		} else {
			ret = new Element("person", XMLExporter.nsinvoice); //$NON-NLS-1$
			Element familyname = new Element("familyname", XMLExporter.nsinvoice); //$NON-NLS-1$
			Element givenname = new Element("givenname", XMLExporter.nsinvoice); //$NON-NLS-1$
			
			if (!useAnschrift) {
				setAttributeIfNotEmptyWithLimit(ret, "salutation", //$NON-NLS-1$
					(String) contact.getExtInfo("Anrede"), //$NON-NLS-1$
					35);
				if (contact.isPerson()) {
					IPerson person =
						CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
					setAttributeIfNotEmptyWithLimit(ret, "title", person.getTitel(), 35); //$NON-NLS-1$
					if (StringUtils.isEmpty((String) contact.getExtInfo("Anrede"))) {
						setAttributeIfNotEmptyWithLimit(ret, "salutation", //$NON-NLS-1$
							PersonFormatUtil.getSalutation(person), //$NON-NLS-1$
							35);
					}
				}
				familyname.setText(StringTool.limitLength(contact.getDescription1(), 35));
				String gn = StringTool.limitLength(contact.getDescription2(), 35);
				if (StringTool.isNothing(gn)) {
					gn = "Unbekannt"; // make validator happy //$NON-NLS-1$
				}
				givenname.setText(gn);
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(contact));
			} else {
				PostalAddress postAnschrift = PostalAddress.ofText(contact.getPostalAddress());
				familyname.setText(StringTool.limitLength(postAnschrift.getLastName(), 35));
				givenname.setText(StringTool.limitLength(postAnschrift.getFirstName(), 35));
				
				setAttributeIfNotEmptyWithLimit(ret, "salutation", postAnschrift.getSalutation(), //$NON-NLS-1$
					35);
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(postAnschrift));
			}
			Element telcom = buildTelekomElement(contact);
			if (telcom != null && !telcom.getChildren().isEmpty()) {
				ret.addContent(telcom);
			}
			Element onlineElement = buildOnlineElement(contact);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		}
		return ret;
	}
	
	public static Element buildPostalElement(final IContact contact){
		Element ret = new Element("postal", XMLExporter.nsinvoice); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, //$NON-NLS-1$
			StringTool.limitLength((String) contact.getExtInfo("Postfach"), //$NON-NLS-1$
				35),
			null);
		addElementIfExists(ret, "street", null, StringTool.limitLength(contact.getStreet(), 35), //$NON-NLS-1$
			null);
		Element zip =
			addElementIfExists(ret, "zip", null, StringTool.limitLength(contact.getZip(), 9), //$NON-NLS-1$
				"0000"); //$NON-NLS-1$
		Country country = contact.getCountry();
		if (Country.NDF == country) {
			logger.info("IContact [] Country not set, defaulting to CH", contact.getId());
			country = Country.CH;
		}
		setAttributeIfNotEmpty(zip, "countrycode", //$NON-NLS-1$
			StringTool.limitLength(country.toString(), 3));
		addElementIfExists(ret, "city", null, StringTool.limitLength(contact.getCity(), 35), //$NON-NLS-1$
			Messages.XMLExporter_Unknown);
		return ret;
	}
	
	public static Element buildPostalElement(final PostalAddress postalAddress){
		Element ret = new Element("postal", XMLExporter.nsinvoice); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, //$NON-NLS-1$
			StringTool.limitLength(postalAddress.getAddress2(), 35), null);
		addElementIfExists(ret, "street", null, //$NON-NLS-1$
			StringTool.limitLength(postalAddress.getAddress1(), 35), null);
		Element zip =
			addElementIfExists(ret, "zip", null, StringTool.limitLength(postalAddress.getZip(), 9), //$NON-NLS-1$
				"0000"); //$NON-NLS-1$
		setAttributeIfNotEmpty(zip, "countrycode", //$NON-NLS-1$
			StringTool.limitLength(postalAddress.getCountry(), 3));
		addElementIfExists(ret, "city", null, StringTool.limitLength(postalAddress.getCity(), 35), //$NON-NLS-1$
			Messages.XMLExporter_unknown);
		return ret;
	}
	
	public static Element buildOnlineElement(final IContact k){
		// Element ret = new Element("online", XMLExporter.nsinvoice);
		// String email = StringTool.limitLength(k.get("E-Mail"), 70);
		// if (!email.matches(".+@.+")) {
		// email = "mail@invalid.invalid";
		// }
		// addElementIfExists(ret, "email", null, k.get("E-Mail"),
		// "mail@invalid.invalid");
		// addElementIfExists(ret, "url", null,
		// StringTool.limitLength(k.get("Website"), 100), null);
		// return ret;
		
		// Tony Schaller, 28.12.2008:
		// optimized code: online element is created when it contains real
		// content, only
		Element ret = null;
		
		// mail adresse
		String value = getValidXMLString(StringTool.limitLength(k.getEmail(), 70));
		if (!value.equals(StringConstants.EMPTY)) {
			if (!value.matches(".+@.+")) { //$NON-NLS-1$
				value = "mail@invalid.invalid"; //$NON-NLS-1$
			}
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, XMLExporter.nsinvoice);
			}
			addElementIfExists(ret, ELEMENT_EMAIL, null, value, null);
		}
		
		// webseite
		value = getValidXMLString(StringTool.limitLength(k.getWebsite(), 100));
		if (!value.equals(StringConstants.EMPTY)) {
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, XMLExporter.nsinvoice);
				addElementIfExists(ret, ELEMENT_EMAIL, null, "mail@invalid.invalid", null); //$NON-NLS-1$
			}
			addElementIfExists(ret, "url", null, value, null); //$NON-NLS-1$
		}
		return ret;
	}
	
	public static Element buildTelekomElement(final IContact k){
		Element ret = new Element("telecom", XMLExporter.nsinvoice); //$NON-NLS-1$
		Element phoneElement = addElementIfExists(ret, "phone", null, //$NON-NLS-1$
			StringTool.limitLength(k.getPhone1(), 25), null); //$NON-NLS-1$
		// only add the fax element if there is a phone, telcom without phone is not allowed by xsd
		if (phoneElement != null) {
			addElementIfExists(ret, "fax", null, StringTool.limitLength(k.getFax(), 25), //$NON-NLS-1$
				null);
		}
		return ret;
	}
	
	public static boolean setAttributeIfNotEmpty(final Element element, final String name,
		final String value){
		if (element == null) {
			return false;
		}
		if (StringTool.isNothing(name)) {
			return false;
		}
		if (StringTool.isNothing(value)) {
			return false;
		}
		element.setAttribute(name, value);
		return true;
	}
	
	public static boolean setAttributeIfNotEmptyWithLimit(final Element element, final String name,
		String value, final int len){
		if (value != null && value.length() >= len) {
			value = value.substring(0, len - 1);
		}
		return setAttributeIfNotEmpty(element, name, value);
	}
	
	public static String makeTarmedDatum(final String datum){
		return new TimeTool(datum).toString(TimeTool.DATE_MYSQL) + "T00:00:00"; //$NON-NLS-1$
	}
	
	public static String makeTarmedDatum(final LocalDate datum){
		return datum.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T00:00:00"; //$NON-NLS-1$
	}
	
	private static Element addElementIfExists(final Element parent, final String name,
		final String attr, String val, final String defValue){
		if (StringTool.isNothing(val)) {
			val = defValue;
		}
		if (!StringTool.isNothing(val)) {
			Element ret = new Element(name, XMLExporter.nsinvoice);
			if (attr == null) {
				// the replaceAll is an ugly fix problems in a database from Bruno Büchel in Yverdon
				ret.setText(val.replaceAll("\u001f", "")); // Unit-Separator
			} else {
				ret.setAttribute(attr, val);
			}
			parent.addContent(ret);
			return ret;
		}
		return null;
	}
	
	public static void setAttributeWithDefault(final Element element, final String name,
		String value, final String def){
		if (element != null) {
			if (!StringTool.isNothing(name)) {
				if (StringTool.isNothing(value)) {
					value = def;
				}
				element.setAttribute(name, value);
			}
		}
	}
	
	/**
	 * Set the correct VAT Attribute based on the Verrechent and the info if the Rechnungssteller
	 * has to pay VAT.
	 * 
	 * @param verrechnet
	 * @param amount
	 * @param el
	 */
	public static void setVatAttribute(IBilled billed, Money amount, Element el, VatRateSum vatsum){
		double value = 0.0;
		
		String vatScale = (String) billed.getExtInfo(Constants.VAT_SCALE);
		if (vatScale != null && vatScale.length() > 0)
			value = Double.parseDouble(vatScale);
		
		el.setAttribute(XMLExporter.ATTR_VAT_RATE, Double.toString(value)); //$NON-NLS-1$
		
		vatsum.add(value, amount.doubleValue());
	}
	
	/**
	 * Determine the EAN of the responsible Kontakt for a Konsultation. The search for thee right
	 * contact is in the following order.<br\> 1. configured via ResponsibleComposite on
	 * RechnungsPref preference page for the Mandant of the consultation<br\> 2. Rechnungssteller of
	 * the Mandant of the consultation if not an organization<br\> 3. the Mandant of the
	 * consultation<\br>
	 * 
	 * @param encounter
	 * @return
	 */
	public static String getResponsibleEAN(IEncounter encounter){
		IContact responsibleKontakt = null;
		
		String responsibleId =
			(String) encounter.getMandator().getExtInfo(TarmedRequirements.RESPONSIBLE_INFO_KEY);
		if (responsibleId != null && !responsibleId.isEmpty()) {
			responsibleKontakt =
				CoreModelServiceHolder.get().load(responsibleId, IMandator.class).orElse(null);
		} else {
			IContact rechnungssteller = encounter.getMandator().getBiller();
			String anrede = (String) rechnungssteller.getExtInfo("Anrede");
			// only way to determine if rechnungssteller is a organization is testing empty anrede
			if (anrede != null && !anrede.isEmpty()) {
				responsibleKontakt = rechnungssteller;
			} else {
				responsibleKontakt = encounter.getMandator();
			}
		}
		return TarmedRequirements.getEAN(responsibleKontakt);
	}
	
	public static void negate(Element el, String attr){
		String v = el.getAttributeValue(attr);
		if (!StringTool.isNothing(v)) {
			if (!v.equals(StringConstants.DOUBLE_ZERO)) {
				if (v.startsWith(StringConstants.DASH)) {
					v = v.substring(1);
				} else {
					v = StringConstants.DASH + v;
				}
				el.setAttribute(attr, v);
			}
		}
	}
	
	public static String getValidXMLString(String source){
		StringBuilder ret = new StringBuilder();
		for (int i = 0, len = source.length(); i < len; i++) {
			// skip non valid XML characters
			if (Verifier.isXMLCharacter(source.charAt(i))) {
				ret.append(source.charAt(i));
			}
		}
		return ret.toString();
	}
	
	public static String getIntermediateEAN(IInvoice invoice){
		String kEAN = getCostBearerEAN(invoice);
		String rEAN = getRecipientEAN(invoice);
		
		// Try to find the intermediate EAN. If we have explicitely set
		// an intermediate EAN, we'll use this one. Otherweise, we'll
		// check whether the mandator has a TC contract. if so, we try to
		// find the TC's EAN.
		// If nothing appropriate is found, we'll try to use the receiver EAN
		// or at least the guarantor EAN.
		// If everything fails we use a pseudo EAN to make the Validators happy
		String iEAN = TarmedRequirements.getIntermediateEAN(invoice.getCoverage());
		if (iEAN.length() == 0) {
			if (TarmedRequirements.hasTCContract(invoice.getMandator())) {
				String trustCenter = TarmedRequirements.getTCName(invoice.getMandator());
				if (trustCenter.length() > 0) {
					iEAN = TrustCenters.getTCEAN(trustCenter);
				}
			}
		}
		logger.info("Intermediate EAN [" + iEAN + "]");
		if (StringTool.isNothing(iEAN)) {
			// make validator happy
			if (!rEAN.matches("(20[0-9]{11}|76[0-9]{11})")) { //$NON-NLS-1$
				if (kEAN.matches("(20[0-9]{11}|76[0-9]{11})")) { //$NON-NLS-1$
					iEAN = kEAN;
				} else {
					iEAN = TarmedRequirements.EAN_PSEUDO;
				}
			} else {
				iEAN = rEAN;
			}
		}
		return iEAN;
	}
	
	private static IContact getCostBearer(ICoverage invoiceCoverage){
		IContact kostentraeger = invoiceCoverage.getCostBearer();
		if (kostentraeger == null) {
			kostentraeger = invoiceCoverage.getPatient();
		}
		return kostentraeger;
	}
	
	public static String getRecipientEAN(IInvoice invoice){
		String rEAN = TarmedRequirements.getRecipientEAN(getCostBearer(invoice.getCoverage()));
		logger.info("Recipient EAN [" + rEAN + "]");
		if (rEAN.equals("unknown")) { //$NON-NLS-1$
			rEAN = getCostBearerEAN(invoice);
		}
		return rEAN;
	}
	
	public static String getCostBearerEAN(IInvoice invoice){
		String kEAN = TarmedRequirements.getEAN(getCostBearer(invoice.getCoverage()));
		logger.info("Costbearer EAN [" + kEAN + "]");
		return kEAN;
	}
	
	public static XMLGregorianCalendar makeXMLDate(LocalDate date)
		throws DatatypeConfigurationException{
		ZonedDateTime zonedDateTime = date.atStartOfDay().atZone(ZoneId.systemDefault());
		GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}
	
	public static XMLGregorianCalendar makeXMLDateTime(LocalDateTime dateTime)
		throws DatatypeConfigurationException{
		ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
		GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}
	
	public static XMLGregorianCalendar makeTarmedDate(String dateString)
		throws DatatypeConfigurationException{
		TimeTool timetool = new TimeTool(dateString);
		GregorianCalendar gregorianCalendar = GregorianCalendar.from(timetool.toZonedDateTime());
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}
	
	public static Optional<Double> getALScalingFactor(IBilled billed){
		String scalingFactor = (String) billed.getExtInfo("AL_SCALINGFACTOR");
		if (scalingFactor != null && !scalingFactor.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(scalingFactor));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}
	
	public static Optional<Double> getALNotScaled(IBilled billed){
		String notScaled = (String) billed.getExtInfo("AL_NOTSCALED");
		if (notScaled != null && !notScaled.isEmpty()) {
			try {
				return Optional.of(Double.parseDouble(notScaled));
			} catch (NumberFormatException ne) {
				// return empty if not parseable
			}
		}
		return Optional.empty();
	}
	
	public static List<IReasonForEncounter> getReasonsForEncounter(IEncounter encounter){
		IQuery<IReasonForEncounter> query =
			ArzttarifeModelServiceHolder.get().getQuery(IReasonForEncounter.class);
		query.and("konsID", COMPARATOR.EQUALS, encounter.getId());
		return query.execute();
	}
	
	/**
	 * Get the {@link IContact} of the guarantor for a bill using the paymentMode, patient and fall.
	 * 
	 * <ul>
	 * <li>Fall TP, Guardian defined -> return guardian
	 * <li>Fall TP, No Guardian defined -> return patient
	 * <li>Fall TG, Guarantor equals Patient, Guardian defined -> return guardian
	 * <li>Fall TG, Guarantor equals Patient, No Guardian defined -> return patient
	 * <li>Fall TG, Guarantor not equals Patient -> return guarantor
	 * </ul>
	 * 
	 * @param paymentMode
	 * @param patient
	 * @param coverage
	 * @return
	 */
	public static IContact getGuarantor(String paymentMode, IPatient patient, ICoverage coverage){
		IContact ret;
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			// TP
			IContact legalGuardian = patient.getLegalGuardian();
			if (legalGuardian != null) {
				return legalGuardian;
			} else {
				return patient;
			}
		} else if (paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			// TG
			IContact invoiceReceiver = coverage.getGuarantor();
			if (invoiceReceiver.equals(patient)) {
				IContact legalGuardian = patient.getLegalGuardian();
				if (legalGuardian != null) {
					ret = legalGuardian;
				} else {
					ret = patient;
				}
			} else {
				ret = invoiceReceiver;
			}
		} else {
			ret = coverage.getGuarantor();
		}
		ret.getPostalAddress();
		return ret;
	}
	
	public static void addSSNAttribute(Element element, IPatient actPatient, ICoverage coverage,
		IInvoice invoice, boolean isOptional){
		String ahv =
			TarmedRequirements.getAHV(actPatient).replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		if (ahv.length() == 0) {
			ahv = CoverageServiceHolder.get().getRequiredString(coverage, TarmedRequirements.SSN)
				.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		}
		boolean ahvValid = ahv.matches("[0-9]{4,10}|[1-9][0-9]{10}|756[0-9]{10}|438[0-9]{10}"); //$NON-NLS-1$
		if (!isOptional
			&& ((ConfigServiceHolder.getUser(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
				&& !ahvValid))) {
			invoice.reject(InvoiceState.REJECTCODE.VALIDATION_ERROR,
				Messages.XMLExporter_AHVInvalid);
			CoreModelServiceHolder.get().save(invoice);
		} else if (ahvValid) {
			element.setAttribute("ssn", ahv); //$NON-NLS-1$
		}
	}
}
