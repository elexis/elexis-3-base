package ch.elexis.TarmedRechnung;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Element;

import ch.elexis.TarmedRechnung.XMLExporter.VatRateSum;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Person;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Verrechnet;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class XMLExporterUtil {
	private static final String ELEMENT_EMAIL = "email"; //$NON-NLS-1$
	private static final String ELEMENT_ONLINE = "online"; //$NON-NLS-1$
	
	public static Element buildRechnungsstellerAdressElement(final Kontakt k){
		return buildAdressElement(k, false, true);
	}
	
	public static Element buildAdressElement(final Kontakt k){
		return buildAdressElement(k, false, false);
	}
	
	public static Element buildAdressElement(final Kontakt k, final boolean useAnschrift){
		return buildAdressElement(k, useAnschrift, false);
	}
	
	public static Element buildAdressElement(final Kontakt k, final boolean useAnschrift,
		boolean checkAnrede){
		Element ret;
		boolean anredeOrganization = false;
		if (checkAnrede) {
			String anrede = (String) k.getInfoElement("Anrede");
			anredeOrganization = (anrede == null || anrede.isEmpty());
		}
		if (k.istPerson() == false || anredeOrganization) {
			ret = new Element("company", XMLExporter.ns); //$NON-NLS-1$
			Element companyname = new Element("companyname", XMLExporter.ns); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(k.get(Kontakt.FLD_NAME1), 35));
			ret.addContent(companyname);
			ret.addContent(buildPostalElement(k));
			ret.addContent(buildTelekomElement(k));
			Element onlineElement = buildOnlineElement(k);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		} else {
			ret = new Element("person", XMLExporter.ns); //$NON-NLS-1$
			Element familyname = new Element("familyname", XMLExporter.ns); //$NON-NLS-1$
			Element givenname = new Element("givenname", XMLExporter.ns); //$NON-NLS-1$
			
			String anschrift = k.get(Kontakt.FLD_ANSCHRIFT);
			if (!useAnschrift || StringTool.isNothing(anschrift)
				|| anschrift.equals(k.createStdAnschrift())) {
				setAttributeIfNotEmptyWithLimit(ret, "salutation", k.getInfoString("Anrede"), 35); //$NON-NLS-1$ //$NON-NLS-2$
				setAttributeIfNotEmptyWithLimit(ret, "title", k.get(Person.TITLE), 35); //$NON-NLS-1$
				familyname.setText(StringTool.limitLength(k.get(Kontakt.FLD_NAME1), 35));
				String gn = k.get(StringTool.limitLength(Kontakt.FLD_NAME2, 35));
				if (StringTool.isNothing(gn)) {
					gn = "Unbekannt"; // make validator happy //$NON-NLS-1$
				}
				givenname.setText(gn);
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(k));
			} else {
				Postanschrift postAnschrift = new Postanschrift(k);
				familyname.setText(StringTool.limitLength(postAnschrift.name, 35));
				givenname.setText(StringTool.limitLength(postAnschrift.vorname, 35));
				
				setAttributeIfNotEmptyWithLimit(ret, "salutation", postAnschrift.anrede, 35); //$NON-NLS-1$
				ret.addContent(familyname);
				ret.addContent(givenname);
				ret.addContent(buildPostalElement(postAnschrift));
			}
			
			ret.addContent(buildTelekomElement(k));
			Element onlineElement = buildOnlineElement(k);
			if (onlineElement != null) {
				ret.addContent(onlineElement);
			}
		}
		return ret;
	}
	
	public static Element buildPostalElement(final Kontakt k){
		Element ret = new Element("postal", XMLExporter.ns); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, StringTool.limitLength(k.getInfoString("Postfach"), //$NON-NLS-1$ //$NON-NLS-2$
			35), null);
		addElementIfExists(ret,
			"street", null, StringTool.limitLength(k.get(Kontakt.FLD_STREET), 35), null); //$NON-NLS-1$
		Element zip =
			addElementIfExists(ret,
				"zip", null, StringTool.limitLength(k.get(Kontakt.FLD_ZIP), 9), "0000"); //$NON-NLS-1$ //$NON-NLS-2$
		setAttributeIfNotEmpty(zip,
			"countrycode", StringTool.limitLength(k.get(Kontakt.FLD_COUNTRY), 3)); //$NON-NLS-1$
		addElementIfExists(
			ret,
			"city", null, StringTool.limitLength(k.get(Kontakt.FLD_PLACE), 35), Messages.XMLExporter_Unknown); //$NON-NLS-1$
		return ret;
	}
	
	public static Element buildPostalElement(final Postanschrift postanschrift){
		Element ret = new Element("postal", XMLExporter.ns); //$NON-NLS-1$
		addElementIfExists(ret, "pobox", null, StringTool.limitLength(postanschrift.adresse2, 35), //$NON-NLS-1$
			null);
		addElementIfExists(ret, "street", null, StringTool.limitLength(postanschrift.adresse1, 35), //$NON-NLS-1$
			null);
		Element zip =
			addElementIfExists(ret, "zip", null, StringTool.limitLength(postanschrift.plz, 9), //$NON-NLS-1$
				"0000"); //$NON-NLS-1$
		setAttributeIfNotEmpty(zip, "countrycode", StringTool.limitLength(postanschrift.land, 3)); //$NON-NLS-1$
		addElementIfExists(ret, "city", null, StringTool.limitLength(postanschrift.ort, 35), //$NON-NLS-1$
			Messages.XMLExporter_unknown);
		return ret;
	}
	
	public static Element buildOnlineElement(final Kontakt k){
		// Element ret = new Element("online", XMLExporter.ns);
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
		String value = StringTool.limitLength(k.get(Kontakt.FLD_E_MAIL), 70);
		if (!value.equals(StringConstants.EMPTY)) {
			if (!value.matches(".+@.+")) { //$NON-NLS-1$
				value = "mail@invalid.invalid"; //$NON-NLS-1$
			}
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, XMLExporter.ns);
			}
			addElementIfExists(ret, ELEMENT_EMAIL, null, value, null);
		}
		
		// webseite
		value = StringTool.limitLength(k.get(Kontakt.FLD_WEBSITE), 100);
		if (!value.equals(StringConstants.EMPTY)) {
			if (ret == null) {
				ret = new Element(ELEMENT_ONLINE, XMLExporter.ns);
				addElementIfExists(ret, ELEMENT_EMAIL, null, "mail@invalid.invalid", null); //$NON-NLS-1$
			}
			addElementIfExists(ret, "url", null, value, null); //$NON-NLS-1$
		}
		return ret;
	}
	
	public static Element buildTelekomElement(final Kontakt k){
		Element ret = new Element("telecom", XMLExporter.ns); //$NON-NLS-1$
		addElementIfExists(ret,
			"phone", null, StringTool.limitLength(k.get(Kontakt.FLD_PHONE1), 25), //$NON-NLS-1$
			"555-555 55 55"); //$NON-NLS-1$
		addElementIfExists(ret,
			"fax", null, StringTool.limitLength(k.get(Kontakt.FLD_FAX), 25), null); //$NON-NLS-1$
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
	
	public static TimeTool getFirstKonsDate(Rechnung rechnung){
		TimeTool ret = new TimeTool(TimeTool.END_OF_UNIX_EPOCH);
		List<Konsultation> konsultationen = rechnung.getKonsultationen();
		for (Konsultation konsultation : konsultationen) {
			TimeTool tt = new TimeTool(konsultation.getDatum());
			if (tt.isBefore(ret)) {
				ret.set(tt);
			}
		}
		return ret;
	}
	
	public static TimeTool getLastKonsDate(Rechnung rechnung){
		TimeTool ret = new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
		List<Konsultation> konsultationen = rechnung.getKonsultationen();
		for (Konsultation konsultation : konsultationen) {
			TimeTool tt = new TimeTool(konsultation.getDatum());
			if (tt.isAfter(ret)) {
				ret.set(tt);
			}
		}
		return ret;
	}
	
	private static Element addElementIfExists(final Element parent, final String name,
		final String attr, String val, final String defValue){
		if (StringTool.isNothing(val)) {
			val = defValue;
		}
		if (!StringTool.isNothing(val)) {
			Element ret = new Element(name, XMLExporter.ns);
			if (attr == null) {
				ret.setText(val);
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
	public static void setVatAttribute(Verrechnet verrechnet, Money amount, Element el,
		VatRateSum vatsum){
		double value = 0.0;
		
		String vatScale = verrechnet.getDetail(Verrechnet.VATSCALE);
		if (vatScale != null && vatScale.length() > 0)
			value = Double.parseDouble(vatScale);
		
		el.setAttribute(XMLExporter.ATTR_VAT_RATE, Double.toString(value)); //$NON-NLS-1$
		
		vatsum.add(value, amount.doubleValue());
	}
	
	/**
	 * Determine the EAN of the responsible Kontakt for a Konsultation. The search for thee right
	 * contact is in the following order.<br\>
	 * 1. configured via ResponsibleComposite on RechnungsPref preference page for the Mandant of
	 * the consultation<br\>
	 * 2. Rechnungssteller of the Mandant of the consultation if not an organization<br\>
	 * 3. the Mandant of the consultation<\br>
	 * 
	 * @param kons
	 * @return
	 */
	public static String getResponsibleEAN(Konsultation kons){
		Kontakt responsibleKontakt = null;
		
		String responsibleId =
			(String) kons.getMandant().getInfoElement(TarmedRequirements.RESPONSIBLE_INFO_KEY);
		if (responsibleId != null && !responsibleId.isEmpty()) {
			responsibleKontakt = Mandant.load(responsibleId);
		} else {
			Rechnungssteller rechnungssteller = kons.getMandant().getRechnungssteller();
			String anrede = rechnungssteller.getInfoString("Anrede");
			// only way to determine if rechnungssteller is a organization is testing empty anrede
			if (anrede != null && !anrede.isEmpty()) {
				responsibleKontakt = rechnungssteller;
			} else {
				responsibleKontakt = kons.getMandant();
			}
		}
		return TarmedRequirements.getEAN(responsibleKontakt);
	}
	
	private static class Postanschrift {
		private String anrede = StringConstants.EMPTY;
		private String name = StringConstants.EMPTY;
		private String vorname = StringConstants.EMPTY;
		private String adresse1 = StringConstants.EMPTY;
		private String adresse2 = StringConstants.EMPTY;
		private String plz = StringConstants.EMPTY;
		private String ort = StringConstants.EMPTY;
		private String land = StringConstants.EMPTY;
		
		public Postanschrift(final Kontakt k){
			super();
			init(k);
		}
		
		private void init(final Kontakt k){
			String postAnschrift = k.getPostAnschrift(true);
			
			// Zeilen lesen
			StringTokenizer tokenizer = new StringTokenizer(postAnschrift, StringConstants.LF);
			List<String> zeileList = new Vector<String>();
			while (tokenizer.hasMoreElements()) {
				zeileList.add(tokenizer.nextToken());
			}
			// Zeilen interpretieren (so gut es geht)
			String plzOrt = ""; //$NON-NLS-1$
			String nameVorname = ""; //$NON-NLS-1$
			final int len = zeileList.size();
			switch (len) {
			case 0: // Kann gar nicht sein, aber man weiss ja nie!
				throw new IllegalArgumentException(Messages.XMLExporter_NoPostal);
			case 1: // Nur Name vorname
				nameVorname = zeileList.get(0);
				break;
			case 2: // NameVorname, Ortsangaben
				nameVorname = zeileList.get(0);
				plzOrt = zeileList.get(1);
				break;
			case 3: // NameVorname, Adr1, Ortsangaben ODER Anrede, NameVorname,
				// Ortsangaben
				if (zeileList.get(0).indexOf(" ") < 0) { //$NON-NLS-1$
					// Erste Zeile Anrede
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					plzOrt = zeileList.get(2);
				} else {
					// Erste Zeile NameVorname
					nameVorname = zeileList.get(0);
					adresse1 = zeileList.get(1);
					plzOrt = zeileList.get(2);
				}
				break;
			case 4: // NameVorname, Adr1, Adr2, Ortsangaben ODER Anrede,
				// NameVorname, Adr1,
				// Ortsangaben
				if (zeileList.get(0).indexOf(" ") < 0) { //$NON-NLS-1$
					// Erste Zeile Anrede
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					adresse1 = zeileList.get(2);
					plzOrt = zeileList.get(3);
				} else {
					// Erste Zeile NameVorname
					nameVorname = zeileList.get(0);
					adresse1 = zeileList.get(1);
					adresse2 = zeileList.get(2);
					plzOrt = zeileList.get(3);
				}
				break;
			default:
				if (len > 4) { // Anrede, NameVorname, Adr1, Adr2, Ortsangaben
					anrede = zeileList.get(0);
					nameVorname = zeileList.get(1);
					adresse1 = zeileList.get(2);
					adresse2 = zeileList.get(3);
					plzOrt = zeileList.get(4);
				}
				break;
			}
			
			// NameVorname aufteilen. Z.B. Von Allmen Christoph
			if (!StringTool.isNothing(nameVorname)) {
				nameVorname = nameVorname.trim();
				int index = nameVorname.lastIndexOf(" "); // Z.B. Von Allmen Christoph //$NON-NLS-1$
				if (index > 0) {
					name = nameVorname.substring(0, index);
					vorname = nameVorname.substring(index + 1);
				} else {
					name = nameVorname;
				}
			}
			
			// plzOrt parsen. Z.B. CH-3600 Lenzburg
			land = k.get(Kontakt.FLD_COUNTRY);
			if (plzOrt.length() > 3 && plzOrt.substring(0, 3).indexOf("-") > 0) { //$NON-NLS-1$
				// Land exists
				int index = plzOrt.indexOf("-"); //$NON-NLS-1$
				land = plzOrt.substring(0, index);
				plzOrt = plzOrt.substring(index + 1);
			}
			plz = k.get(Kontakt.FLD_ZIP);
			if (plzOrt.indexOf(" ") > 0) { //$NON-NLS-1$
				// Read zip code
				int index = plzOrt.indexOf(StringConstants.SPACE);
				plz = plzOrt.substring(0, index);
				plzOrt = plzOrt.substring(index + 1);
			}
			ort = plzOrt;
		}
	}
	
}
