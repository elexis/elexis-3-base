/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.TarmedRechnung;

import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import ch.elexis.core.data.Rechnung;
import ch.elexis.core.data.RnStatus.REJECTCODE;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

/**
 * Class to deal with mdinvoiceresponses
 * (http://www.forum-datenaustausch.ch/mdinvoiceresponse_xml4.00_v1.1_d.pdf)
 * 
 * @author Gerry
 * 
 */
public class ResponseAnalyzer {
	final static Namespace ns = Namespace.getNamespace(
		"invoice", "http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$ //$NON-NLS-2$
	final static Namespace xsi = Namespace.getNamespace(
		"xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$ //$NON-NLS-2$
	final static Namespace nsSchema = Namespace.getNamespace(
		"schemaLocation", "http://www.xmlData.ch/xmlInvoice/XSD"); //$NON-NLS-1$ //$NON-NLS-2$
	
	Document responseDoc;
	Element eRoot;
	private String rnNr;
	private String status;
	private ch.rgw.tools.Result<String> resume;
	
	Rechnung rn;
	
	public Document load(final InputStream xmlResponse){
		try {
			SAXBuilder builder = new SAXBuilder();
			responseDoc = builder.build(xmlResponse);
			eRoot = responseDoc.getRootElement();
			analyze();
			return responseDoc;
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		return null;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getRnNr(){
		return rnNr;
	}
	
	public ch.rgw.tools.Result<String> getResume(){
		return resume;
	}
	
	private boolean analyze(){
		resume = new Result<String>();
		if (eRoot == null) {
			return false;
		}
		StringBuilder ret = new StringBuilder();
		Element eHeader = eRoot.getChild("header", ns); //$NON-NLS-1$
		Element eSender = eHeader.getChild("sender", ns); //$NON-NLS-1$
		Element eIntermediate = eHeader.getChild("intermediate", ns); //$NON-NLS-1$
		Element eRecipient = eHeader.getChild("recipient", ns); //$NON-NLS-1$
		ret.append("Sender: ").append(eSender.getAttributeValue("ean_party")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ret.append(Messages.ResponseAnalyzer_Intermediate)
			.append(eIntermediate.getAttributeValue("ean_party")).append( //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$
				"\n"); //$NON-NLS-1$
		ret.append(Messages.ResponseAnalyzer_Receiver)
			.append(eRecipient.getAttributeValue("ean_party")).append("\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		Element eInvoice = eRoot.getChild("invoice", ns); //$NON-NLS-1$
		int tr = -1;
		if (eInvoice != null) {
			String rnId = eInvoice.getAttributeValue("invoice_id"); //$NON-NLS-1$
			tr = rnId.lastIndexOf('0');
			if (tr == -1) {
				rnNr = rnId;
			} else {
				String patNr = Integer.toString(Integer.parseInt(rnId.substring(0, tr))); // eliminate
				// leading
				// zeroes
				rnNr = rnId.substring(tr + 1);
			}
		} else {
			rnNr = "0"; //$NON-NLS-1$
		}
		
		rn = Rechnung.getFromNr(rnNr);
		if (rn == null) {
			ret.append(Messages.ResponseAnalyzer_BillIsNotKnown);
		} else {
			ret.append(Messages.ResponseAnalyzer_BillNumber).append(rnNr).append("\n"); //$NON-NLS-1$
			ret.append(Messages.ResponseAnalyzer_Patient)
				.append(rn.getFall().getPatient().getLabel()).append("\n"); //$NON-NLS-1$
			ret.append(Messages.ResponseAnalyzer_Date).append(rn.getDatumRn())
				.append("\n----------------------\n"); //$NON-NLS-1$
		}
		ret.append(Messages.ResponseAnalyzer_State);
		Element eStatus = eRoot.getChild(Messages.ResponseAnalyzer_State2, ns);
		List<Element> lStatus = eStatus.getChildren();
		if (lStatus.size() != 1) {
			ret.append(Messages.ResponseAnalyzer_NotDeclaredCorrectly);
		} else {
			Element eStatusType = lStatus.get(0);
			Element eError = eStatusType.getChild("error", ns); //$NON-NLS-1$
			Element eExpl = eStatusType.getChild("explanation", ns); //$NON-NLS-1$
			String explanation = "Keine Erläuterung angegeben"; //$NON-NLS-1$
			if (eExpl != null) {
				explanation = eExpl.getText();
			}
			status = eStatusType.getName().toLowerCase();
			if (status.equals("rejected")) { //$NON-NLS-1$
				ret.append(Messages.ResponseAnalyzer_StateRejected).append(explanation)
					.append("\n"); //$NON-NLS-1$
				if (eError != null) {
					ret.append(Messages.ResponseAnalyzer_ErrorCode);
					ret.append(eError.getAttributeValue("major")).append("."); //$NON-NLS-1$ //$NON-NLS-2$
					ret.append(eError.getAttributeValue("minor")).append("->"); //$NON-NLS-1$ //$NON-NLS-2$
					ret.append(eError.getAttributeValue("error")).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				resume.add(new Result<String>(Result.SEVERITY.ERROR, 1,
					"Rejected", ret.toString(), true)); //$NON-NLS-1$
				rn.reject(REJECTCODE.REJECTED_BY_PEER, explanation);
				
			} else if (status.equals("calledin")) { //$NON-NLS-1$
				ret.append(Messages.ResponseAnalyzer_MoreInformationsRequested).append(explanation)
					.append("\n"); //$NON-NLS-1$
				if (eError != null) {
					ret.append(Messages.ResponseAnalyzer_Code).append(
						eError.getAttributeValue("major")); //$NON-NLS-1$
				}
			} else if (status.equals("pending")) { //$NON-NLS-1$
				ret.append(Messages.ResponseAnalyzer_Pending).append(explanation).append("\n"); //$NON-NLS-1$
			} else if (status.equals("resend")) { //$NON-NLS-1$
				ret.append(Messages.ResponseAnalyzer_PleaseResend).append(explanation)
					.append(Messages.ResponseAnalyzer_56);
			} else if (status.equals(Messages.ResponseAnalyzer_57)) {
				ret.append(Messages.ResponseAnalyzer_58).append(explanation)
					.append(Messages.ResponseAnalyzer_59);
				if (eError != null) {
					ret.append(Messages.ResponseAnalyzer_60);
					ret.append(eError.getAttributeValue(Messages.ResponseAnalyzer_61))
						.append(Messages.ResponseAnalyzer_62)
						.append(eError.getAttributeValue(Messages.ResponseAnalyzer_63))
						.append(Messages.ResponseAnalyzer_64)
						.append(eError.getAttributeValue(Messages.ResponseAnalyzer_65))
						.append(Messages.ResponseAnalyzer_66);
				}
			} else if (status.equals(Messages.ResponseAnalyzer_67)) {
				ret.append(Messages.ResponseAnalyzer_68).append(explanation)
					.append(Messages.ResponseAnalyzer_69);
				List<Element> reasons = eStatusType.getChildren();
				Element eReason = reasons.get(0);
				ret.append(eReason.getName()).append(Messages.ResponseAnalyzer_70);
			} else if (status.equals(Messages.ResponseAnalyzer_71)) {
				ret.append(Messages.ResponseAnalyzer_72).append(explanation)
					.append(Messages.ResponseAnalyzer_73);
				Element eAnswer = (Element) eStatusType.getChildren().get(0);
				ret.append(eAnswer.getName()).append(Messages.ResponseAnalyzer_74);
			} else {
				ret.append(Messages.ResponseAnalyzer_75);
			}
		}
		resume = new Result<String>(ret.toString());
		return true;
	}
}
