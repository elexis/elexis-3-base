package ch.elexis.TarmedRechnung;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.jdom.Element;

import ch.elexis.base.ch.ebanking.esr.ESR;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
	
	public static XMLExporterEsr9 buildEsr9(IInvoice invoice, XMLExporterBalance balance,
		XMLExporter xmlExporter){
		
		IMandator actMandant = invoice.getMandator();
		
		String esrmode = (String) actMandant.getBiller().getExtInfo(XMLExporter.ta.ESR5OR9);
		Element element;
		ESR besr = xmlExporter.getBesr();
		
		if (StringUtils.isNotBlank(esrmode) && esrmode.equals("esr9")) {
			element = new Element("esr9", XMLExporter.nsinvoice); //$NON-NLS-1$
			String participantNumber = besr.makeParticipantNumber(true);
			if (StringUtils.isEmpty(participantNumber)) {
				MessageDialog.openError(null, Messages.XMLExporter_MandatorErrorCaption,
					Messages.XMLExporter_MandatorErrorEsr + " [" + actMandant.getLabel() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
			element.setAttribute(ATTR_PARTICIPANT_NUMBER, participantNumber);
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
		String bankid = (String) actMandant.getBiller().getExtInfo(XMLExporter.ta.RNBANK);
		if (StringUtils.isNotBlank(bankid)) { //$NON-NLS-1$
			Optional<IOrganization> bank =
				CoreModelServiceHolder.get().load(bankid, IOrganization.class);
			bank.ifPresent(b -> {
				Element eBank = new Element("bank", XMLExporter.nsinvoice); //$NON-NLS-1$
				Element company = XMLExporterUtil.buildAdressElement(b);
				eBank.addContent(company);
				element.addContent(eBank);
			});
		}

		return new XMLExporterEsr9(element);
	}
}
