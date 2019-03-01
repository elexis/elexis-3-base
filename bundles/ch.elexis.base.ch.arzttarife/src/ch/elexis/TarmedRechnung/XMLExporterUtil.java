package ch.elexis.TarmedRechnung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jdom.Element;
import org.jdom.Verifier;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.format.PostalAddress;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class XMLExporterUtil {
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
		setAttributeIfNotEmpty(zip, "countrycode", //$NON-NLS-1$
			StringTool.limitLength(contact.getCountry().toString(), 3));
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
		if (value.length() >= len) {
			value = value.substring(0, len - 1);
		}
		return setAttributeIfNotEmpty(element, name, value);
	}
	
	public static String makeTarmedDatum(final String datum){
		return new TimeTool(datum).toString(TimeTool.DATE_MYSQL) + "T00:00:00"; //$NON-NLS-1$
	}
	
	public static String makeTarmedDatum(final LocalDate datum){
		return datum.format(DateTimeFormatter.ofPattern("yyyyy-MM-dd")) + "T00:00:00"; //$NON-NLS-1$
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
				ret.setText(val.replaceAll("\u001f","")); // Unit-Separator
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
	public static void setVatAttribute(IBilled billed, Money amount, Element el,
		VatRateSum vatsum){
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
}
