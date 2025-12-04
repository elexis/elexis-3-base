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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.handler.AppointmentExtensionHandler;
import ch.itmed.fop.printing.data.AppointmentData;
import ch.itmed.fop.printing.data.AppointmentsData;

public class AppointmentsInformationElement {

	public static Element create(Document doc, final boolean singleAppoinment) throws Exception {
		return create(doc, singleAppoinment, Collections.emptyList(), null);
	}

	public static Element create(Document doc, final boolean singleAppoinment, List<IAppointment> appointments,
			IPatient patient) throws Exception {
		List<AppointmentData> al; // AppointmentsList

		if (singleAppoinment) {
			AppointmentData ad = new AppointmentData();
			ad.load(appointments);
			al = new ArrayList<>();
			al.add(ad);
		} else {
			AppointmentsData ad = new AppointmentsData();
			al = ad.load(appointments, patient);
		}

		Element p = doc.createElement("AppointmentsInformation"); //$NON-NLS-1$

		if (!al.isEmpty()) {
			Element l = doc.createElement("AppointmentsList"); //$NON-NLS-1$

			if (!al.isEmpty()) {
				Collections.sort(al, new Comparator<AppointmentData>() {
					@Override
					public int compare(AppointmentData o1, AppointmentData o2) {
						return o1.getStartTime().compareTo(o2.getStartTime());
					}
				});
				for (AppointmentData ad : al) {
					Element appointment = doc.createElement("ListAppointment"); //$NON-NLS-1$
					appointment.setAttribute("area", ad.getAgendaArea()); //$NON-NLS-1$
					appointment.appendChild(doc.createTextNode(ad.getAppointmentDetailed()));
					l.appendChild(appointment);
					Element appointmentNoEnd = doc.createElement("ListAppointmentNoEnd"); //$NON-NLS-1$
					appointmentNoEnd.setAttribute("area", ad.getAgendaArea()); //$NON-NLS-1$
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

				Element c = doc.createElement("AgendaArea"); //$NON-NLS-1$
				c.appendChild(doc.createTextNode(area.getKey()));
				p.appendChild(c);

				c = doc.createElement("Appointments"); //$NON-NLS-1$
				for (AppointmentData ad : areaAppointments) {
					List<IAppointment> allRelatedAppointments = AppointmentExtensionHandler
							.getAllRelatedAppointments(ad.getAppointment());
					String appointmentDetails = ad.getEarliestAndLatestAppointmentTimes(allRelatedAppointments);
					Element appointment = doc.createElement("Appointment"); //$NON-NLS-1$
					appointment.appendChild(doc.createTextNode(appointmentDetails));
					c.appendChild(appointment);
					Element appointmentNoEnd = doc.createElement("AppointmentNoEnd"); //$NON-NLS-1$
					appointmentNoEnd.appendChild(doc.createTextNode(ad.getAppointmentDetailedNoEnd()));
					c.appendChild(appointmentNoEnd);
				}
				p.appendChild(c);
			}
		}

		return p;
	}
}