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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.barcode.BarcodeCreator;
import ch.itmed.fop.printing.data.PatientData;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;

public class BarCodeElement {

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

		String docName = PreferenceConstants.BAR_CODE_LABEL;
		int barcodeFormat = Integer
				.parseInt(Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 14)));

		boolean isBarcodeFormat1 = barcodeFormat == 14;

		Element p = doc.createElement("Etikette");
		if (isBarcodeFormat1) {
			p.setAttribute("barcodeLabel", BarcodeCreator.createInternalCode128FromKontaktPatNr(pd.getPatient()));

		} else {
			p.setAttribute("barcodeLabel", BarcodeCreator.createInternalCode128fromKontakt(pd.getPatient()));
		}

		return p;
	}
}
