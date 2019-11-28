package ch.elexis.TarmedRechnung;

import org.jdom.Element;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.services.holder.CoverageServiceHolder;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;

public class XMLExporterInsurance {
	private static final String ATTR_CASE_ID = "case_id"; //$NON-NLS-1$
	
	private Element insuranceElement;
	
	private XMLExporterInsurance(Element insuranceElement){
		this.insuranceElement = insuranceElement;
	}
	
	public Element getElement(){
		return insuranceElement;
	}
	
	public static XMLExporterInsurance buildInsurance(IInvoice invoice, XMLExporter xmlExporter){
		
		ICoverage coverage = invoice.getCoverage();
		IPatient patient = coverage.getPatient();
		IMandator mandator = invoice.getMandator();
		String gesetz = TarmedRequirements.getGesetz(coverage);
		
		Element element = new Element(gesetz.toLowerCase(), XMLExporter.nsinvoice);
		if (gesetz.equalsIgnoreCase("ivg")) { //$NON-NLS-1$
			String caseNumber = CoverageServiceHolder.get().getRequiredString(coverage,
				TarmedRequirements.CASE_NUMBER);
			caseNumber = caseNumber.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			
			if ((!caseNumber.matches("[0-9]{14}")) && // seit 1.1.2000 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{10}")) && // bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{9}")) && // auch bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{6}"))) { // Nummer für Abklärungsmassnahmen //$NON-NLS-1$
				/* die spinnen, die Bürokraten */
				if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)) {
					invoice.reject(InvoiceState.REJECTCODE.VALIDATION_ERROR,
						Messages.XMLExporter_IVCaseNumberInvalid);
				} else {
					caseNumber = "123456"; // sometimes it's better to cheat than to fight //$NON-NLS-1$
					// bureaucrazy
				}
			}
			element.setAttribute(ATTR_CASE_ID, caseNumber);
			addSSNAttribute(element, patient, coverage, invoice, false);
			String nif = TarmedRequirements.getNIF(mandator.getBiller()).replaceAll("[^0-9]", //$NON-NLS-1$
				StringConstants.EMPTY);
			if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
				&& (!nif.matches("[0-9]{1,7}"))) { //$NON-NLS-1$
				invoice.reject(InvoiceState.REJECTCODE.VALIDATION_ERROR,
					Messages.XMLExporter_NIFInvalid);
			} else {
				element.setAttribute("nif", nif); //$NON-NLS-1$
			}
		} else if (gesetz.equalsIgnoreCase("mvg")) { //$NON-NLS-1$
			addSSNAttribute(element, patient, coverage, invoice, false);
			addInsuredId(element, patient, coverage);
		} else if (gesetz.equalsIgnoreCase("uvg")) { //$NON-NLS-1$
			String casenumber = CoverageServiceHolder.get().getRequiredString(coverage,
				TarmedRequirements.CASE_NUMBER);
			if (StringTool.isNothing(casenumber)) {
				casenumber = CoverageServiceHolder.get().getRequiredString(coverage,
					TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (!StringTool.isNothing(casenumber)) {
				element.setAttribute(ATTR_CASE_ID, casenumber);
			}
			addSSNAttribute(element, patient, coverage, invoice, true);
			addInsuredId(element, patient, coverage);
		} else {
			addInsuredId(element, patient, coverage);
		}
		String casedate = (String) coverage.getExtInfo("Unfalldatum"); //$NON-NLS-1$
		if (StringTool.isNothing(casedate)) {
			element.setAttribute("case_date", //$NON-NLS-1$
				XMLExporterUtil.makeTarmedDatum(invoice.getDateFrom()));
		} else {
			element.setAttribute("case_date", XMLExporterUtil.makeTarmedDatum(casedate)); //$NON-NLS-1$			
		}
		XMLExporterUtil.setAttributeIfNotEmpty(element, "contract_number", //$NON-NLS-1$
			(String) coverage.getExtInfo("Vertragsnummer")); //$NON-NLS-1$
		
		return new XMLExporterInsurance(element);
	}
	
	private static void addInsuredId(Element element, IPatient patient, ICoverage coverage){
		String vnummer = CoverageServiceHolder.get().getRequiredString(coverage,
			TarmedRequirements.INSURANCE_NUMBER);
		if (StringTool.isNothing(vnummer)) {
			vnummer = CoverageServiceHolder.get().getRequiredString(coverage,
				TarmedRequirements.CASE_NUMBER);
		}
		if (StringTool.isNothing(vnummer)) {
			vnummer = patient.getId();
		}
		element.setAttribute("insured_id", vnummer); //$NON-NLS-1$
	}
	
	private static void addSSNAttribute(Element element, IPatient actPatient, ICoverage coverage,
		IInvoice invoice, boolean isOptional){
		String ahv =
			TarmedRequirements.getAHV(actPatient).replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		if (ahv.length() == 0) {
			ahv = CoverageServiceHolder.get().getRequiredString(coverage, TarmedRequirements.SSN)
				.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
		}
		boolean ahvValid = ahv.matches("[0-9]{11}") || ahv.matches("[0-9]{13}"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!isOptional && ((CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
			&& !ahvValid))) {
			invoice.reject(InvoiceState.REJECTCODE.VALIDATION_ERROR,
				Messages.XMLExporter_AHVInvalid);
		} else if (ahvValid) {
			element.setAttribute("ssn", ahv); //$NON-NLS-1$
		}
	}
}
