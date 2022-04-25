package ch.elexis.TarmedRechnung;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IInvoice;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.StringTool;

public class XMLExporterProcessing {

	private static Logger logger = LoggerFactory.getLogger(XMLExporterProcessing.class);

	private static final String ELEMENT_PROCESSING = "processing"; //$NON-NLS-1$
	public static final String ATTR_INTERMEDIAT_PRINT = "print_at_intermediate"; //$NON-NLS-1$
	public static final String ATTR_PATIENT_COPY_PRINT = "print_patient_copy"; //$NON-NLS-1$

	public static final String ELEMENT_TRANSPORT = "transport"; //$NON-NLS-1$
	public static final String ELEMENT_TRANSPORT_VIA = "via"; //$NON-NLS-1$

	private static final String ATTR_TRANSPORT_FROM = "from"; //$NON-NLS-1$
	private static final String ATTR_TRANSPORT_TO = "to"; //$NON-NLS-1$

	public static final String ATTR_TRANSPORT_VIA_VIA = "via"; //$NON-NLS-1$
	private static final String ATTR_TRANSPORT_VIA_SEQ = "sequence_id"; //$NON-NLS-1$

	private Element processingElement;

	private XMLExporterProcessing(Element processing) {
		this.processingElement = processing;
	}

	public Element getElement() {
		return processingElement;
	}

	// public static XMLExporterProcessing buildProcessing(IInvoice invoice,
	// XMLExporter xmlExporter){
	//
	// ICoverage coverage = invoice.getCoverage();
	// IMandator actMandant = invoice.getMandator();
	//
	// Element element = null;
	// element = new Element(ELEMENT_PROCESSING, XMLExporter.nsinvoice);
	// element.setAttribute(ATTR_INTERMEDIAT_PRINT,
	// xmlExporter.isPrintAtIntermediate() ? "1" : "0");
	//
	// if (CoverageServiceHolder.get().getCopyForPatient(coverage)) {
	// element.setAttribute(ATTR_PATIENT_COPY_PRINT, "1");
	// }
	//
	// Element transport = new Element(ELEMENT_TRANSPORT, XMLExporter.nsinvoice);
	// transport.setAttribute(ATTR_TRANSPORT_FROM,
	// xmlExporter.getSenderEAN(actMandant));
	// transport.setAttribute(ATTR_TRANSPORT_TO, getRecipientEAN(invoice,
	// xmlExporter));
	//
	// logger.debug("Using intermediate EAN [" + getIntermediateEAN(invoice,
	// xmlExporter) + "]");
	// Element via = new Element(ELEMENT_TRANSPORT_VIA, XMLExporter.nsinvoice);
	// via.setAttribute(ATTR_TRANSPORT_VIA_VIA, getIntermediateEAN(invoice,
	// xmlExporter));
	// via.setAttribute(ATTR_TRANSPORT_VIA_SEQ, "1");
	//
	// transport.addContent(via);
	// element.addContent(transport);
	//
	// // insert demand if TG and TC contract
	// Tiers tiersType = CoverageServiceHolder.get().getTiersType(coverage);
	// if (Tiers.GARANT == tiersType &&
	// (TarmedRequirements.hasTCContract(actMandant))) {
	// String tcCode = TarmedRequirements.getTCCode(actMandant);
	// Element demand = new Element("demand", XMLExporter.nsinvoice); //$NON-NLS-1$
	// demand.setAttribute("tc_demand_id", "0"); //$NON-NLS-1$ //$NON-NLS-2$
	//
	// demand.setAttribute("tc_token", //$NON-NLS-1$
	// xmlExporter.getBesr().createCodeline(
	// XMLTool.moneyToXmlDouble(xmlExporter.getDueMoney()).replaceFirst("[.,]", ""),
	// //$NON-NLS-1$//$NON-NLS-2$
	// tcCode));
	// demand.setAttribute("insurance_demand_date", //$NON-NLS-1$
	// XMLExporterUtil.makeTarmedDatum(invoice.getDate()));
	// element.addContent(demand);
	// }
	//
	// XMLExporterProcessing ret = new XMLExporterProcessing(element);
	//
	// return ret;
	// }

	public static String getIntermediateEAN(IInvoice existingInvoice, XMLExporter xmlExporter) {
		String kEAN = getKostentraegerEAN(existingInvoice, xmlExporter);
		String rEAN = getRecipientEAN(existingInvoice, xmlExporter);

		String iEAN = xmlExporter.getIntermediateEAN(existingInvoice.getCoverage());
		logger.debug("Intermediate EAN [" + iEAN + "]");
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

	private static IContact getKostentTraeger(ICoverage invoiceCoverage) {
		IContact kostentraeger = invoiceCoverage.getCostBearer();
		if (kostentraeger == null) {
			kostentraeger = invoiceCoverage.getPatient();
		}
		return kostentraeger;
	}

	public static String getRecipientEAN(IInvoice invoice, XMLExporter xmlExporter) {
		String rEAN = TarmedRequirements.getRecipientEAN(getKostentTraeger(invoice.getCoverage()));
		logger.debug("Recipient EAN [" + rEAN + "]");
		if (rEAN.equals("unknown")) { //$NON-NLS-1$
			rEAN = getKostentraegerEAN(invoice, xmlExporter);
		}
		return rEAN;
	}

	public static String getKostentraegerEAN(IInvoice invoice, XMLExporter xmlExporter) {
		String kEAN = TarmedRequirements.getEAN(getKostentTraeger(invoice.getCoverage()));
		logger.debug("Kostentraeger EAN [" + kEAN + "]");
		return kEAN;
	}
}
