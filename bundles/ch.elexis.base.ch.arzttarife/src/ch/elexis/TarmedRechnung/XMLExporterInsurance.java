package ch.elexis.TarmedRechnung;

import org.jdom.Element;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Fall;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus.REJECTCODE;
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
	
	public static XMLExporterInsurance buildInsurance(Rechnung rechnung, XMLExporter xmlExporter){
		
		Fall actFall = rechnung.getFall();
		Patient actPatient = actFall.getPatient();
		Mandant actMandant = rechnung.getMandant();
		String gesetz = TarmedRequirements.getGesetz(actFall);
		
		Element element = new Element(gesetz.toLowerCase(), XMLExporter.nsinvoice);
		if (gesetz.equalsIgnoreCase("ivg")) { //$NON-NLS-1$
			String caseNumber = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			caseNumber = caseNumber.replaceAll("[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			
			if ((!caseNumber.matches("[0-9]{14}")) && // seit 1.1.2000 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{10}")) && // bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{9}")) && // auch bis 31.12.1999 gültige Nummer //$NON-NLS-1$
				(!caseNumber.matches("[0-9]{6}"))) { // Nummer für Abklärungsmassnahmen //$NON-NLS-1$
				/* die spinnen, die Bürokraten */
				if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)) {
					rechnung.reject(REJECTCODE.VALIDATION_ERROR,
						Messages.XMLExporter_IVCaseNumberInvalid);
				} else {
					caseNumber = "123456"; // sometimes it's better to cheat than to fight //$NON-NLS-1$
					// bureaucrazy
				}
			}
			element.setAttribute(ATTR_CASE_ID, caseNumber);
			XMLExporterUtil.addSSNAttribute(element, actPatient, actFall, rechnung, false);
			String nif =
				TarmedRequirements.getNIF(actMandant.getRechnungssteller()).replaceAll(
					"[^0-9]", StringConstants.EMPTY); //$NON-NLS-1$
			if (CoreHub.userCfg.get(Preferences.LEISTUNGSCODES_BILLING_STRICT, true)
				&& (!nif.matches("[0-9]{1,7}"))) { //$NON-NLS-1$
				rechnung.reject(REJECTCODE.VALIDATION_ERROR, Messages.XMLExporter_NIFInvalid);
			} else {
				element.setAttribute("nif", nif); //$NON-NLS-1$
			}
		} else if (gesetz.equalsIgnoreCase("mvg")) {
			XMLExporterUtil.addSSNAttribute(element, actPatient, actFall, rechnung, false);
			addInsuredId(element, actPatient, actFall);
		} else if (gesetz.equalsIgnoreCase("uvg")) { //$NON-NLS-1$
			String casenumber = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
			if (StringTool.isNothing(casenumber)) {
				casenumber = actFall.getRequiredString(TarmedRequirements.ACCIDENT_NUMBER);
			}
			if (!StringTool.isNothing(casenumber)) {
				element.setAttribute(ATTR_CASE_ID, casenumber);
			}
			XMLExporterUtil.addSSNAttribute(element, actPatient, actFall, rechnung, true);
			addInsuredId(element, actPatient, actFall);
		} else {
			addInsuredId(element, actPatient, actFall);
		}
		String casedate = actFall.getInfoString("Unfalldatum"); //$NON-NLS-1$
		if (StringTool.isNothing(casedate)) {
			casedate = rechnung.getDatumVon();
		}
		element.setAttribute("case_date", XMLExporterUtil.makeTarmedDatum(casedate)); //$NON-NLS-1$
		XMLExporterUtil.setAttributeIfNotEmpty(element, "contract_number", actFall //$NON-NLS-1$
			.getInfoString("Vertragsnummer")); //$NON-NLS-1$
		
		return new XMLExporterInsurance(element);
	}
	
	private static void addInsuredId(Element element, Patient actPatient, Fall actFall){
		String vnummer = actFall.getRequiredString(TarmedRequirements.INSURANCE_NUMBER);
		if (StringTool.isNothing(vnummer)) {
			vnummer = actFall.getRequiredString(TarmedRequirements.CASE_NUMBER);
		}
		if (StringTool.isNothing(vnummer)) {
			vnummer = actPatient.getId();
		}
		element.setAttribute("insured_id", vnummer); //$NON-NLS-1$
	}
}
