package ch.elexis.TarmedRechnung;

import org.eclipse.jface.dialogs.MessageDialog;
import org.jdom.Element;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.data.Mandant;
import ch.elexis.data.Organisation;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.XMLTool;

public class XMLExporterEsr9 {
	
	private static final String ATTR_PARTICIPANT_NUMBER = "participant_number"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	private Element esr9Element;
	
	private XMLExporterEsr9(Element esr9){
		this.esr9Element = esr9;
	}
	
	public Element getElement(){
		return esr9Element;
	}
	
	public static XMLExporterEsr9 buildEsr9(Rechnung rechnung, XMLExporterBalance balance,
		XMLExporter xmlExporter){
		
		Mandant actMandant = rechnung.getMandant();
		
		String esrmode = actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.ESR5OR9);
		Element element;
		String userdata = rechnung.getRnId();
		ESR besr =
			new ESR(actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.ESRNUMBER),
				actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.ESRSUB), userdata,
				ESR.ESR27);
		
		if (esrmode.equals("esr9")) {
			element = new Element("esr9", XMLExporter.nsinvoice); //$NON-NLS-1$
			element.setAttribute(ATTR_PARTICIPANT_NUMBER, besr.makeParticipantNumber(true));
			element.setAttribute(ATTR_TYPE, "16or27"); //$NON-NLS-1$
			String refnr = besr.makeRefNr(true);
			String codingline =
				besr.createCodeline(
					XMLTool.moneyToXmlDouble(balance.getDue()).replaceFirst("[.,]", ""), null); //$NON-NLS-1$ //$NON-NLS-2$
			element.setAttribute("reference_number", refnr); //$NON-NLS-1$
			element.setAttribute("coding_line", codingline); //$NON-NLS-1$
		} else {
			MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
				Messages.XMLExporter_MandatorErrorText);
			return null;
		}
		String bankid = actMandant.getRechnungssteller().getInfoString(XMLExporter.ta.RNBANK);
		if (!bankid.equals("")) { //$NON-NLS-1$
			Organisation bank = Organisation.load(bankid);
			Element eBank = new Element("bank", XMLExporter.nsinvoice); //$NON-NLS-1$
			Element company = XMLExporterUtil.buildAdressElement(bank);
			eBank.addContent(company);
			element.addContent(eBank);
		}

		return new XMLExporterEsr9(element);
	}
}
