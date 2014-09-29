package ch.elexis.TarmedRechnung;

import org.eclipse.jface.dialogs.MessageDialog;
import org.jdom.Element;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Rechnung;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

public class XMLExporterTiers {
	private Element tiersElement;
	
	private XMLExporterTiers(Element tiers){
		this.tiersElement = tiers;
	}
	
	public Element getElement(){
		return tiersElement;
	}
	
	public static XMLExporterTiers buildTiers(Rechnung rechnung, ESR besr, Money mDue){
		TarmedACL ta = TarmedACL.getInstance();
		
		Fall fall = rechnung.getFall();
		Patient patient = fall.getPatient();
		Mandant mandant = rechnung.getMandant();
		Kontakt kostentraeger = fall.getRequiredContact(TarmedRequirements.INSURANCE);
		// We try to figure out whether we should use Tiers Payant or Tiers
		// Garant.
		// if unsure, we make it TG
		String tiers = XMLExporter.TIERS_GARANT;
		Kontakt rnAdressat = fall.getGarant();
		
		if ((kostentraeger != null) && (kostentraeger.isValid())) {
			if (rnAdressat.equals(kostentraeger)) {
				tiers = XMLExporter.TIERS_PAYANT;
			} else {
				tiers = XMLExporter.TIERS_GARANT;
			}
		} else {
			kostentraeger = rnAdressat;
			tiers = XMLExporter.TIERS_GARANT;
		}
		String tcCode = TarmedRequirements.getTCCode(mandant);
		
		if (kostentraeger == null) {
			kostentraeger = patient;
		}
		String kEAN = TarmedRequirements.getEAN(kostentraeger);
		
		Element element = null;
		if (tiers.equals(XMLExporter.TIERS_GARANT)) {
			element = new Element(XMLExporter.ELEMENT_TIERS_GARANT, XMLExporter.ns); // 11020 //$NON-NLS-1$
			String paymentPeriode = mandant.getRechnungssteller().getInfoString("rnfrist"); //$NON-NLS-1$
			if (StringTool.isNothing(paymentPeriode)) {
				paymentPeriode = "30"; //$NON-NLS-1$
			}
			element.setAttribute("payment_periode", "P" + paymentPeriode + "D"); // 11021 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			element = new Element(XMLExporter.ELEMENT_TIERS_PAYANT, XMLExporter.ns); // 11260 //$NON-NLS-1$
			// to simplify things for now we do no accept modifications
			element.setAttribute("invoice_modification", "false"); // 11262 //$NON-NLS-1$ //$NON-NLS-2$
			element.setAttribute("purpose", XMLExporter.ELEMENT_INVOICE); // 11265 //$NON-NLS-1$
		}
		
		XMLExporterTiers ret = new XMLExporterTiers(element);
		
		Element biller = new Element("biller", XMLExporter.ns); // 11070 -> 11400 //$NON-NLS-1$
		// biller.setAttribute("ean_party",actMandant.getInfoString("EAN")); //
		// 11402
		biller.setAttribute(XMLExporter.ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(mandant.getRechnungssteller())); // 11402
		biller.setAttribute("zsr", TarmedRequirements.getKSK(mandant.getRechnungssteller())); // actMandant.getInfoString //$NON-NLS-1$
		// ("KSK"));
		// // 11403
		String spec = mandant.getRechnungssteller().getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			biller.setAttribute("specialty", spec); // 11404 //$NON-NLS-1$
		}
		biller.addContent(XMLExporterUtil.buildRechnungsstellerAdressElement(mandant
			.getRechnungssteller())); // 11600-11680
		ret.tiersElement.addContent(biller);
		
		Element provider = new Element("provider", XMLExporter.ns); // 11080 -> 11800 //$NON-NLS-1$
		// 11802
		provider.setAttribute(XMLExporter.ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(mandant.getRechnungssteller())); // 11802
		provider.setAttribute("zsr", TarmedRequirements.getKSK(mandant.getRechnungssteller())); // actMandant.getInfoString //$NON-NLS-1$
		// ("KSK"));
		// // 11803
		spec = mandant.getRechnungssteller().getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			provider.setAttribute("specialty", spec); // 11804 //$NON-NLS-1$
		}
		provider.addContent(XMLExporterUtil.buildRechnungsstellerAdressElement(mandant
			.getRechnungssteller())); // 11830-11680
		ret.tiersElement.addContent(provider);
		
		Element onlineElement = null; // tschaller: see comments in
		// buildOnlineElement
		
		Element insurance = new Element("insurance", XMLExporter.ns); // 11090 //$NON-NLS-1$
		// The 'insurance' element is optional in Tiers Garant so in TG we only
		// insert this Element,
		// if we have all data absolutely correct.
		// In Tiers Payant, the insurance element is mandatory, and,
		// furthermore, MUST be an
		// Organization. So in TP, we insert an insurance element in any case,
		// and, if the guarantor
		// is a person, we "convert" it to an organization to make the validator
		// happy
		if (tiers.equals(XMLExporter.TIERS_GARANT)) {
			if (kostentraeger.istOrganisation()) {
				if (kEAN.matches("[0-9]{13,13}")) { //$NON-NLS-1$
					insurance.setAttribute(XMLExporter.ATTR_EAN_PARTY, kEAN);
					insurance.addContent(XMLExporterUtil.buildAdressElement(kostentraeger));
					ret.tiersElement.addContent(insurance);
				}
			}
		} else {
			// insurance.addContent(buildAdressElement(kostentraeger)); // must
			// be an organization,
			// so we fake one
			/*
			 * if(!kEAN.matches("[0-9]{13,13}")){ kEAN="2000000000000"; }
			 */
			insurance.setAttribute(XMLExporter.ATTR_EAN_PARTY, kEAN);
			Element company = new Element("company", XMLExporter.ns); //$NON-NLS-1$
			Element companyname = new Element("companyname", XMLExporter.ns); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(kostentraeger.get(Kontakt.FLD_NAME1), 35));
			company.addContent(companyname);
			company.addContent(XMLExporterUtil.buildPostalElement(kostentraeger));
			company.addContent(XMLExporterUtil.buildTelekomElement(kostentraeger));
			// company.addContent(buildOnlineElement(kostentraeger)); //
			// tschaller: see comments in
			// buildOnlineElement
			onlineElement = XMLExporterUtil.buildOnlineElement(kostentraeger);
			if (onlineElement != null) {
				company.addContent(onlineElement);
			}
			
			insurance.addContent(company);
			ret.tiersElement.addContent(insurance);
			// note this may lead to a person mistreated as organization. So
			// these faults should be
			// caught when generating bills
			
		}
		
		Element patientElement = new Element("patient", XMLExporter.ns); // 11100 //$NON-NLS-1$
		// patient.setAttribute("unique_id",rn.getFall().getId()); // this is
		// optional and should be
		// ssn13 type. leave it out for now
		String gender = "male"; //$NON-NLS-1$
		if (patient == null) {
			MessageDialog.openError(null, Messages.XMLExporter_ErrorCaption,
				Messages.XMLExporter_NoPatientText);
			return null;
		}
		if (StringTool.isNothing(patient.getGeschlecht())) { // we fall back to
			// female. why not?
			patient.set(Person.SEX, Person.FEMALE);
		}
		if (patient.getGeschlecht().equals(Person.FEMALE)) {
			gender = "female"; //$NON-NLS-1$
		}
		patientElement.setAttribute("gender", gender); //$NON-NLS-1$
		String gebDat = patient.getGeburtsdatum();
		if (StringTool.isNothing(gebDat)) { // make validator happy if we don't
			// know the birthdate
			patientElement.setAttribute(XMLExporter.ATTR_BIRTHDATE, "0001-00-00T00:00:00"); //$NON-NLS-1$
		} else {
			patientElement
				.setAttribute(XMLExporter.ATTR_BIRTHDATE,
					new TimeTool(patient.getGeburtsdatum()).toString(TimeTool.DATE_MYSQL)
						+ "T00:00:00"); //$NON-NLS-1$
		}
		patientElement.addContent(XMLExporterUtil.buildAdressElement(patient));
		ret.tiersElement.addContent(patientElement);
		
		Element guarantor = XMLExporterUtil.buildGuarantor(rnAdressat, patient); // 11110
		ret.tiersElement.addContent(guarantor);
		
		Element referrer = new Element("referrer", XMLExporter.ns); // 11120 //$NON-NLS-1$
		Kontakt auftraggeber = fall.getRequiredContact("Zuweiser");
		if (auftraggeber != null) {
			referrer.setAttribute(XMLExporter.ATTR_EAN_PARTY,
				TarmedRequirements.getEAN(auftraggeber)); // auftraggeber.
			
			referrer.setAttribute("zsr", TarmedRequirements.getKSK(auftraggeber)); // auftraggeber. //$NON-NLS-1$
			
			referrer.addContent(XMLExporterUtil.buildAdressElement(auftraggeber));
			ret.tiersElement.addContent(referrer);
		}
		
		if (tiers.equals(XMLExporter.TIERS_GARANT) && (TarmedRequirements.hasTCContract(mandant))) {
			Element demand = new Element("demand", XMLExporter.ns); //$NON-NLS-1$
			demand.setAttribute("tc_demand_id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			
			demand.setAttribute("tc_token", besr.createCodeline(XMLTool.moneyToXmlDouble(mDue) //$NON-NLS-1$
				.replaceFirst("[.,]", ""), tcCode)); //$NON-NLS-1$ //$NON-NLS-2$
			demand.setAttribute(
				"insurance_demand_date", XMLExporterUtil.makeTarmedDatum(rechnung.getDatumRn())); //$NON-NLS-1$
			ret.tiersElement.addContent(demand);
		}
		
		return ret;
	}
}
