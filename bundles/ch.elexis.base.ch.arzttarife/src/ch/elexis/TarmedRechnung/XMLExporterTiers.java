package ch.elexis.TarmedRechnung;

import org.eclipse.jface.dialogs.MessageDialog;
import org.jdom.Element;

import ch.elexis.data.Fall;
import ch.elexis.data.Fall.Tiers;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Rechnung;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class XMLExporterTiers {
	private Element tiersElement;
	
	private String tiers;

	private XMLExporterTiers(Element tiers){
		this.tiersElement = tiers;
	}
	
	public Element getElement(){
		return tiersElement;
	}
	
	public String getTiers(){
		return tiers;
	}

	public static XMLExporterTiers buildTiers(Rechnung rechnung, XMLExporter xmlExporter){
		TarmedACL ta = TarmedACL.getInstance();
		
		Fall fall = rechnung.getFall();
		Patient patient = fall.getPatient();
		Mandant mandant = rechnung.getMandant();
		Kontakt kostentraeger = fall.getCostBearer();
		
		String tiers = XMLExporter.TIERS_PAYANT;
		Tiers tiersType = fall.getTiersType();
		if(Tiers.GARANT == tiersType) {
			tiers = XMLExporter.TIERS_GARANT;
			kostentraeger = fall.getGarant();
		}
		
		if (kostentraeger == null) {
			kostentraeger = patient;
		}
		String kEAN = TarmedRequirements.getEAN(kostentraeger);
		
		Element element = null;
		if (tiers.equals(XMLExporter.TIERS_GARANT)) {
			element = new Element(XMLExporter.ELEMENT_TIERS_GARANT, XMLExporter.nsinvoice); //$NON-NLS-1$
			String paymentPeriode = mandant.getRechnungssteller().getInfoString("rnfrist"); //$NON-NLS-1$
			if (StringTool.isNothing(paymentPeriode)) {
				paymentPeriode = "30"; //$NON-NLS-1$
			}
			element.setAttribute("payment_period", "P" + paymentPeriode + "D"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			element = new Element(XMLExporter.ELEMENT_TIERS_PAYANT, XMLExporter.nsinvoice); //$NON-NLS-1$
		}
		
		XMLExporterTiers ret = new XMLExporterTiers(element);
		
		Element biller = new Element("biller", XMLExporter.nsinvoice); //$NON-NLS-1$
		biller.setAttribute(XMLExporter.ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(mandant.getRechnungssteller()));
		XMLExporterUtil.setAttributeIfNotEmpty(biller, "zsr",
			TarmedRequirements.getKSK(mandant.getRechnungssteller()));
		String spec = mandant.getRechnungssteller().getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			biller.setAttribute("specialty", spec); //$NON-NLS-1$
		}
		biller.addContent(XMLExporterUtil.buildRechnungsstellerAdressElement(mandant
			.getRechnungssteller())); // 11600-11680
		ret.tiersElement.addContent(biller);
		
		Element provider = new Element("provider", XMLExporter.nsinvoice); //$NON-NLS-1$
		provider.setAttribute(XMLExporter.ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(mandant));
		provider.setAttribute(
			"zsr", TarmedRequirements.getKSK(mandant)); //$NON-NLS-1$
		spec = mandant.getInfoString(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			provider.setAttribute("specialty", spec); //$NON-NLS-1$
		}
		provider.addContent(XMLExporterUtil.buildRechnungsstellerAdressElement(mandant));
		ret.tiersElement.addContent(provider);
		
		Element onlineElement = null; // tschaller: see comments in
		// buildOnlineElement
		
		Element insurance = new Element("insurance", XMLExporter.nsinvoice); //$NON-NLS-1$
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
			Element company = new Element("company", XMLExporter.nsinvoice); //$NON-NLS-1$
			Element companyname = new Element("companyname", XMLExporter.nsinvoice); //$NON-NLS-1$
			companyname.setText(StringTool.limitLength(kostentraeger.get(Kontakt.FLD_NAME1), 35));
			company.addContent(companyname);
			company.addContent(XMLExporterUtil.buildPostalElement(kostentraeger));
			Element telcom = XMLExporterUtil.buildTelekomElement(kostentraeger);
			if (telcom != null && !telcom.getChildren().isEmpty()) {
				company.addContent(telcom);
			}
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
		
		Element patientElement = new Element("patient", XMLExporter.nsinvoice); //$NON-NLS-1$
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
		
		Element guarantor = xmlExporter.buildGuarantor(getGuarantor(tiers, patient, fall), patient);
		ret.tiersElement.addContent(guarantor);
		
		Element referrer = new Element("referrer", XMLExporter.nsinvoice); //$NON-NLS-1$
		Kontakt auftraggeber = fall.getRequiredContact("Zuweiser");
		if (auftraggeber != null) {
			String ean = TarmedRequirements.getEAN(auftraggeber);
			if (ean != null && !ean.isEmpty()) {
				referrer.setAttribute(XMLExporter.ATTR_EAN_PARTY,
					TarmedRequirements.getEAN(auftraggeber)); // auftraggeber.
			}
			String zsr = TarmedRequirements.getKSK(auftraggeber);
			if (zsr != null && !zsr.isEmpty()) {
				referrer.setAttribute("zsr", zsr); // auftraggeber. //$NON-NLS-1$
			}
			referrer.addContent(XMLExporterUtil.buildAdressElement(auftraggeber));
			ret.tiersElement.addContent(referrer);
		}
		ret.tiers = tiers;

		return ret;
	}
	
	/**
	 * Get the {@link Kontakt} of the guarantor for a bill using the paymentMode, patient and fall. 
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
	 * @param fall
	 * @return
	 */
	public static Kontakt getGuarantor(String paymentMode, Patient patient, Fall fall){
		Kontakt ret;
		if (paymentMode.equals(XMLExporter.TIERS_PAYANT)) {
			// TP
			Kontakt legalGuardian = patient.getLegalGuardian();
			if(legalGuardian != null) {
				return legalGuardian;
			} else {
				return patient;
			}
		} else if (paymentMode.equals(XMLExporter.TIERS_GARANT)) {
			// TG
			Kontakt invoiceReceiver = fall.getGarant();
			if (invoiceReceiver.equals(patient)) {
				Kontakt legalGuardian = patient.getLegalGuardian();
				if (legalGuardian != null) {
					ret = legalGuardian;
				} else {
					ret = patient;
				}
			} else {
				ret = invoiceReceiver;
			}
		} else {
			ret = fall.getGarant();
		}
		ret.getPostAnschrift(true);
		return ret;
	}
}
