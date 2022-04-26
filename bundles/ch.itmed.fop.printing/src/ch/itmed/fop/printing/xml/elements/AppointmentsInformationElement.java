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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.itmed.fop.printing.data.AppointmentData;
import ch.itmed.fop.printing.data.AppointmentsData;

public class AppointmentsInformationElement {

	public static Element create(Document doc, final boolean singleAppoinment) throws Exception {
		ArrayList<AppointmentData> al; // AppointmentsList

		if (singleAppoinment) {
			AppointmentData ad = new AppointmentData();
			ad.load();
			al = new ArrayList<>();
			al.add(ad);
		} else {
			AppointmentsData ad = new AppointmentsData();
			al = ad.load();
		}

		Element p = doc.createElement("AppointmentsInformation");

		if (!al.isEmpty()) {
			Element l = doc.createElement("AppointmentsList");

			if (!al.isEmpty()) {
				for (AppointmentData ad : al) {
					Element appointment = doc.createElement("ListAppointment");
					appointment.setAttribute("area", ad.getAgendaArea());
					appointment.appendChild(doc.createTextNode(ad.getAppointmentDetailed()));
					l.appendChild(appointment);
					Element appointmentNoEnd = doc.createElement("ListAppointmentNoEnd");
					appointmentNoEnd.setAttribute("area", ad.getAgendaArea());
					appointmentNoEnd.appendChild(doc.createTextNode(ad.getAppointmentDetailedNoEnd()));
					l.appendChild(appointmentNoEnd);
				}
			}
			p.appendChild(l);

			Map<String, List<AppointmentData>> appointmentPerAreaMap = al.stream()
					.collect(Collectors.groupingBy(AppointmentData::getAgendaArea));

			List<Entry<String, List<AppointmentData>>> appointmentPerArea = new ArrayList<>(
					appointmentPerAreaMap.entrySet());
			appointmentPerArea.sort(new Comparator<Entry<String, List<AppointmentData>>>() {
				@Override
				public int compare(Entry<String, List<AppointmentData>> e1, Entry<String, List<AppointmentData>> e2) {
					return e1.getValue().get(0).getStartTime().compareTo(e2.getValue().get(0).getStartTime());
				}
			});

			for (Entry<String, List<AppointmentData>> area : appointmentPerArea) {
				List<AppointmentData> areaAppointments = area.getValue();

				Element c = doc.createElement("AgendaArea");
				c.appendChild(doc.createTextNode(area.getKey()));
				p.appendChild(c);

				c = doc.createElement("Appointments");
				for (AppointmentData ad : areaAppointments) {
					Element appointment = doc.createElement("Appointment");
					appointment.appendChild(doc.createTextNode(ad.getAppointmentDetailed()));
					c.appendChild(appointment);
					Element appointmentNoEnd = doc.createElement("AppointmentNoEnd");
					appointmentNoEnd.appendChild(doc.createTextNode(ad.getAppointmentDetailedNoEnd()));
					c.appendChild(appointmentNoEnd);
				}
				p.appendChild(c);
			}
		} else {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Keine Termin Serie",
					"Keine Termin Serie zum selektierten Patienten gefunden");
		}

		return p;
	}
}