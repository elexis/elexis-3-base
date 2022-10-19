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

package ch.itmed.fop.printing.xml.documents;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.AppointmentsInformationElement;
import ch.itmed.fop.printing.xml.elements.MandatorElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public final class RecurringAppointmentsCard {

	public static InputStream create() throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.RECURRING_APPOINTMENTS_CARD);
		PageProperties.setCurrentDate(page);
		doc.appendChild(page);
		Element appointment = AppointmentsInformationElement.create(doc, false);
		page.appendChild(appointment);
		Element patient = PatientElement.create(doc, false);
		page.appendChild(patient);
		Element mandator = MandatorElement.create(doc, null);
		if (mandator != null) {
			page.appendChild(mandator);
		}
		return DomDocument.toInputStream(doc);
	}

}
