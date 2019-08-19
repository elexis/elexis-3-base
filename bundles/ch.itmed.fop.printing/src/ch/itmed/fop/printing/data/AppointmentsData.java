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

package ch.itmed.fop.printing.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.itmed.fop.printing.resources.Messages;

public final class AppointmentsData {
	private Kontakt kontakt;
	private ArrayList<AppointmentData> appointmentsData;

	public ArrayList<AppointmentData> load() throws NullPointerException {
		kontakt = (Kontakt) ElexisEventDispatcher.getSelected(Patient.class);
		if (kontakt == null) {
			SWTHelper.showInfo(Messages.Info_NoPatient_Title, Messages.Info_NoPatient_Message);
			throw new NullPointerException("No patient selected");
		}
		appointmentsData = new ArrayList<>();
		querryAppointments(kontakt.getId());
		return appointmentsData;
	}

	/**
	 * Searches all future appointments for a given Kontakt. The future is defined
	 * as > Instant.now();
	 * 
	 * @param contactId
	 */
	private void querryAppointments(String contactId) {
		Instant instant = Instant.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(instant);

		Query<Termin> query = new Query<>(Termin.class);
		query.add(Termin.FLD_PATIENT, Query.EQUALS, contactId);
		query.add(Termin.FLD_TAG, Query.GREATER, currentDate);
		query.orderBy(false, Termin.FLD_TAG);
		List<Termin> appointments = (List<Termin>) query.execute();

		for (Termin appointment : appointments) {
			appointmentsData.add(new AppointmentData(appointment));
		}
	}

	public String getAgendaArea() {
		return appointmentsData.get(0).getAgendaArea();
	}
}
