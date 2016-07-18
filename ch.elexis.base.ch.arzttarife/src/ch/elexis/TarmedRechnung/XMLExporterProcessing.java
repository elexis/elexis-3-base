package ch.elexis.TarmedRechnung;

import org.jdom.Element;

import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.XMLTool;

public class XMLExporterProcessing {
	
	private static final String ELEMENT_PROCESSING = "processing"; //$NON-NLS-1$
	private static final String ATTR_INTERMEDIAT_PRINT = "print_at_intermediate"; //$NON-NLS-1$
	private static final String ATTR_PATIENT_COPY_PRINT = "print_patient_copy"; //$NON-NLS-1$

	private static final String ELEMENT_TRANSPORT = "transport"; //$NON-NLS-1$
	private static final String ELEMENT_TRANSPORT_VIA = "via"; //$NON-NLS-1$

	private static final String ATTR_TRANSPORT_FROM = "from"; //$NON-NLS-1$
	private static final String ATTR_TRANSPORT_TO = "to"; //$NON-NLS-1$

	private static final String ATTR_TRANSPORT_VIA_VIA = "via"; //$NON-NLS-1$
	private static final String ATTR_TRANSPORT_VIA_SEQ = "sequence_id"; //$NON-NLS-1$

	private Element processingElement;
	
	private XMLExporterProcessing(Element processing){
		this.processingElement = processing;
	}
	
	public Element getElement(){
		return processingElement;
	}
	
	public static XMLExporterProcessing buildProcessing(Rechnung rechnung, XMLExporter xmlExporter){
		
		Fall actFall = rechnung.getFall();
		Patient actPatient = actFall.getPatient();
		Mandant actMandant = rechnung.getMandant();
		Kontakt kostentraeger = actFall.getRequiredContact(TarmedRequirements.INSURANCE);

		if (kostentraeger == null) {
			kostentraeger = actPatient;
		}

		
		String kEAN = TarmedRequirements.getEAN(kostentraeger); // (String)kostentraeger.
		String rEAN = TarmedRequirements.getRecipientEAN(kostentraeger);
		if (rEAN.equals("unknown")) { //$NON-NLS-1$
			rEAN = kEAN;
		}
		
		String iEAN = xmlExporter.getIntermediateEAN(actFall);
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

		Element element = null;
		element = new Element(ELEMENT_PROCESSING, XMLExporter.nsinvoice);
		element.setAttribute(ATTR_INTERMEDIAT_PRINT, "1");

		if (actFall.getCopyForPatient()) {
			element.setAttribute(ATTR_PATIENT_COPY_PRINT, "1");
		}
		
		Element transport = new Element(ELEMENT_TRANSPORT, XMLExporter.nsinvoice);
		transport.setAttribute(ATTR_TRANSPORT_FROM, xmlExporter.getSenderEAN(actMandant));
		transport.setAttribute(ATTR_TRANSPORT_TO, rEAN);

		Element via = new Element(ELEMENT_TRANSPORT_VIA, XMLExporter.nsinvoice);
		via.setAttribute(ATTR_TRANSPORT_VIA_VIA, iEAN);
		via.setAttribute(ATTR_TRANSPORT_VIA_SEQ, "1");

		transport.addContent(via);
		element.addContent(transport);

		// insert demand if TG and TC contract
		String tiers = XMLExporterTiers.getTiers(actFall.getGarant(), kostentraeger, actFall);
		if (tiers.equals(XMLExporter.TIERS_GARANT)
			&& (TarmedRequirements.hasTCContract(actMandant))) {
			String tcCode = TarmedRequirements.getTCCode(actMandant);
			Element demand = new Element("demand", XMLExporter.nsinvoice); //$NON-NLS-1$
			demand.setAttribute("tc_demand_id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			
			demand
				.setAttribute(
					"tc_token", xmlExporter.getBesr().createCodeline(XMLTool.moneyToXmlDouble(xmlExporter.getDueMoney()) //$NON-NLS-1$
								.replaceFirst("[.,]", ""), tcCode)); //$NON-NLS-1$ //$NON-NLS-2$
			demand.setAttribute(
				"insurance_demand_date", XMLExporterUtil.makeTarmedDatum(rechnung.getDatumRn())); //$NON-NLS-1$
			element.addContent(demand);
		}

		XMLExporterProcessing ret = new XMLExporterProcessing(element);

		return ret;
	}
}
