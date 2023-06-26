/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.xml.elements;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import at.medevit.elexis.barcode.tools.BarcodeCreator;
import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.data.CaseData;
import ch.itmed.fop.printing.data.PatientData;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;

public class BarCodeElement {

	public static Document barCodegenerateConfiguration(IPatient pat, boolean isBarcodeFormat1)
			throws ParserConfigurationException {

		CaseData pat2 = new CaseData();
		PatientData pat3 = new PatientData(isBarcodeFormat1);
		pat3.load(pat);

		String docName = PreferenceConstants.BAR_CODE_LABEL;
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

		Document doc = docBuilder.newDocument();

		Element etikette = doc.createElement("Etikette");
		etikette.setAttribute("pageheight", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 5)) + "mm"); //$NON-NLS-1$
		etikette.setAttribute("pagewidth", //$NON-NLS-1$
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 6)) + "mm"); //$NON-NLS-1$
		etikette.setAttribute("textOrientation",
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 7)));
		etikette.setAttribute("marginTop",
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 8)) + "mm"); //$NON-NLS-1$
		etikette.setAttribute("marginBottom",
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 9)) + "mm"); //$NON-NLS-1$
		etikette.setAttribute("marginLeft",
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 10)) + "mm"); //$NON-NLS-1$
		etikette.setAttribute("marginRight",
				Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 11)) + "mm"); //$NON-NLS-1$
		if (isBarcodeFormat1) {
			etikette.setAttribute("barcodeKennung", BarcodeCreator.createInternalCode128FromKontaktPatNr(pat));

		} else {
			etikette.setAttribute("barcodeKennung", BarcodeCreator.createInternalCode128fromKontakt(pat));
		}

		etikette.setAttribute("label", pat.getLabel());
		if (pat2.getCostBearer() != null) {
			etikette.setAttribute("CostBearer", pat2.getCostBearer());

		}
		if (pat2.getInsurancePolicyNumber() != null && !pat2.getInsurancePolicyNumber().isEmpty()) {

			etikette.setAttribute("InsurancePolicyNumber", "Ver-Nr: " + pat2.getInsurancePolicyNumber());
		}

		etikette.setAttribute("FirstName", pat.getFirstName());
		etikette.setAttribute("LastName", pat.getLastName());
		etikette.setAttribute("Birthdate", pat3.getBirthdate());
		etikette.setAttribute("Sex", pat3.getSex());
		etikette.setAttribute("PID", pat.getPatientNr());
		etikette.setAttribute("Title", pat.getTitel());
		etikette.setAttribute("Country", pat3.getCountry());
		etikette.setAttribute("PostalCode", pat.getZip());
		etikette.setAttribute("City", pat.getCity());
		etikette.setAttribute("Street", pat.getStreet());
		etikette.setAttribute("Phone1", pat.getPhone1());
		etikette.setAttribute("Phone2", pat.getPhone2());
		etikette.setAttribute("MobilePhone", pat.getMobile());
		etikette.setAttribute("Email", pat.getEmail());
		etikette.setAttribute("OrderNumber", pat3.getOrderNumber());
		etikette.setAttribute("currentDate", getCurrentDate());
		etikette.setAttribute("currentTime", getCurrentTime());
		doc.appendChild(etikette);

		return doc;
	}

	public static Element create(Document doc, boolean loadFromAgenda) throws Exception {
		return create(doc, loadFromAgenda, false, null);
	}

	/**
	 *
	 * @param doc
	 * @param loadFromAgenda
	 * @param useLegalGuardian
	 * @param patient          use the given patient, instead of loading from
	 *                         context (only considered if loadFromAgenda is false)
	 * @return
	 * @throws Exception
	 */
	public static Element create(Document doc, boolean loadFromAgenda, boolean useLegalGuardian, IPatient patient)
			throws Exception {
		PatientData pd = new PatientData(useLegalGuardian);

		if (loadFromAgenda) {
			pd.loadFromAgenda();
		} else {
			pd.load(patient);
		}

		Element p = doc.createElement("Etikette"); //$NON-NLS-1$
		Element c = doc.createElement("FirstName"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getFirstName()));
		p.appendChild(c);

		c = doc.createElement("LastName"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getLastName()));
		p.appendChild(c);

		c = doc.createElement("Birthdate"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getBirthdate()));
		p.appendChild(c);

		c = doc.createElement("Sex"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getSex()));
		p.appendChild(c);

		c = doc.createElement("PID"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPid()));
		p.appendChild(c);

		c = doc.createElement("Title"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getTitle()));
		p.appendChild(c);

		c = doc.createElement("PostalCode"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPostalCode()));
		p.appendChild(c);

		c = doc.createElement("City"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getCity()));
		p.appendChild(c);

		c = doc.createElement("Street"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getStreet()));
		p.appendChild(c);

		c = doc.createElement("Phone1"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPhone1()));
		p.appendChild(c);

		c = doc.createElement("Phone2"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getPhone2()));
		p.appendChild(c);

		c = doc.createElement("MobilePhone"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getMobilePhone()));
		p.appendChild(c);

		c = doc.createElement("Email"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getEmail()));
		p.appendChild(c);
		c = doc.createElement("OrderNumber"); //$NON-NLS-1$
		c.appendChild(doc.createTextNode(pd.getOrderNumber()));
		p.appendChild(c);
		c = doc.createElement("currentTime");
		c.appendChild(doc.createTextNode(getCurrentTime()));
		p.appendChild(c);
		return p;
	}

	public static String getCurrentDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");
		return localDate.format(dateFormatter);
	}

	public static String getCurrentTime() {
		LocalTime localTime = LocalTime.now();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		return localTime.format(timeFormatter);
	}
}
