package ch.elexis.TarmedRechnung;

import org.jdom.Element;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.ICoverageService.Tiers;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;

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

	public static XMLExporterTiers buildTiers(IInvoice invoice, XMLExporter xmlExporter){
		TarmedACL ta = TarmedACL.getInstance();
		
		ICoverage coverage = invoice.getCoverage();
		IPatient patient = coverage.getPatient();
		IMandator mandant = invoice.getMandator();
	
		String tiers = XMLExporter.TIERS_PAYANT;
		Tiers tiersType = CoverageServiceHolder.get().getTiersType(coverage);
		if(Tiers.GARANT == tiersType) {
			tiers = XMLExporter.TIERS_GARANT;
		}
		
		IContact costBearer = coverage.getCostBearer();
		if (costBearer == null) {
			costBearer = patient;
		}
		String kEAN = TarmedRequirements.getEAN(costBearer);
		
		Element element = null;
		if (tiers.equals(XMLExporter.TIERS_GARANT)) {
			element = new Element(XMLExporter.ELEMENT_TIERS_GARANT, XMLExporter.nsinvoice); //$NON-NLS-1$
			String paymentPeriode = (String) mandant.getBiller().getExtInfo("rnfrist"); //$NON-NLS-1$
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
			TarmedRequirements.getEAN(mandant.getBiller()));
		XMLExporterUtil.setAttributeIfNotEmpty(biller, "zsr",
			TarmedRequirements.getKSK(mandant.getBiller()));
		String spec = (String) mandant.getBiller().getExtInfo(ta.SPEC);
		if (!spec.equals("")) { //$NON-NLS-1$
			biller.setAttribute("specialty", spec); //$NON-NLS-1$
		}
		biller.addContent(XMLExporterUtil.buildRechnungsstellerAdressElement(mandant
			.getBiller())); // 11600-11680
		ret.tiersElement.addContent(biller);
		
		Element provider = new Element("provider", XMLExporter.nsinvoice); //$NON-NLS-1$
		provider.setAttribute(XMLExporter.ATTR_EAN_PARTY,
			TarmedRequirements.getEAN(mandant));
		provider.setAttribute(
			"zsr", TarmedRequirements.getKSK(mandant)); //$NON-NLS-1$
		spec = (String) mandant.getExtInfo(ta.SPEC);
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
			if (costBearer.isOrganization()) {
				if (kEAN.matches("[0-9]{13,13}")) { //$NON-NLS-1$
					insurance.setAttribute(XMLExporter.ATTR_EAN_PARTY, kEAN);
					insurance.addContent(XMLExporterUtil.buildAdressElement(costBearer));
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
			companyname.setText(StringTool.limitLength(costBearer.getDescription1(), 35));
			company.addContent(companyname);
			company.addContent(XMLExporterUtil.buildPostalElement(costBearer));
			Element telcom = XMLExporterUtil.buildTelekomElement(costBearer);
			if (telcom != null && !telcom.getChildren().isEmpty()) {
				company.addContent(telcom);
			}
			// company.addContent(buildOnlineElement(kostentraeger)); //
			// tschaller: see comments in
			// buildOnlineElement
			onlineElement = XMLExporterUtil.buildOnlineElement(costBearer);
			if (onlineElement != null) {
				company.addContent(onlineElement);
			}
			
			insurance.addContent(company);
			ret.tiersElement.addContent(insurance);
			// note this may lead to a person mistreated as organization. So
			// these faults should be
			// caught when generating bills
			
		}
		
		Element patientElement = xmlExporter.buildPatient(patient);
		ret.tiersElement.addContent(patientElement);
		
		Element guarantor =
			xmlExporter.buildGuarantor(getGuarantor(tiers, patient, coverage), patient);
		ret.tiersElement.addContent(guarantor);
		
		Element referrer = new Element("referrer", XMLExporter.nsinvoice); //$NON-NLS-1$
		IContact auftraggeber =
			CoverageServiceHolder.get().getRequiredContact(coverage, "Zuweiser");
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
			if(legalGuardian != null) {
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
	
}
