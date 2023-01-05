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
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.xml.elements.AppointmentsInformationElement;
import ch.itmed.fop.printing.xml.elements.MandatorElement;
import ch.itmed.fop.printing.xml.elements.PatientElement;

public final class AppointmentCard {

	public static InputStream create() throws Exception {
		return create(null, null, null);
	}

	public static InputStream create(IAppointment appointment, IPatient patient, IMandator mandator) throws Exception {
		Document doc = DomDocument.newDocument();

		Element page = PageProperties.setProperties(doc, PreferenceConstants.APPOINTMENT_CARD);
		PageProperties.setCurrentDate(page);
		doc.appendChild(page);
		// requires Context Set for Appointment, Patient and Mandator
		Element _appointment;
		if (appointment != null) {
			_appointment = AppointmentsInformationElement.create(doc, true, Collections.singletonList(appointment),
					patient);
		} else {
			// load all future appointments - developer is too lazy to clean this up ...
			_appointment = AppointmentsInformationElement.create(doc, true, null, patient);
		}
		page.appendChild(_appointment);
		Element patientElement = PatientElement.create(doc, patient == null, false, patient);
		page.appendChild(patientElement);
		Element mandatorElement = MandatorElement.create(doc, mandator);
		if (mandatorElement != null) {
			page.appendChild(mandatorElement);
		}
		return DomDocument.toInputStream(doc);
	}

}
