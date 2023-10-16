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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public final class AppointmentsData {
	private IContact kontakt;
	private List<AppointmentData> appointmentsData;

	public List<AppointmentData> load(List<IAppointment> appointments) throws NullPointerException {
		return load(appointments, null);
	}

	public List<AppointmentData> load(List<IAppointment> appointments, IPatient patient) throws NullPointerException {
		if (appointments != null && !appointments.isEmpty()) {
			appointmentsData = appointments.stream().map(a -> new AppointmentData(a)).collect(Collectors.toList());
		} else {
			if (patient != null) {
				kontakt = patient;
			} else {
				kontakt = ContextServiceHolder.get().getTyped(IPatient.class).orElse(null);
			}
			if (kontakt == null) {
				throw new NullPointerException("No patient selected"); //$NON-NLS-1$
			}

			appointmentsData = new ArrayList<>();
			querryAppointments(kontakt.getId());
		}
		return appointmentsData;
	}

	/**
	 * Searches all future appointments for a given Kontakt. The future is defined
	 * as > Instant.now();
	 *
	 * @param contactId
	 */
	private void querryAppointments(String contactId) {
		IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
		query.and(ModelPackage.Literals.IAPPOINTMENT__SUBJECT_OR_PATIENT, COMPARATOR.EQUALS, contactId);
		query.and("tag", COMPARATOR.GREATER, LocalDate.now());
		List<IAppointment> appointments = query.execute();
		for (IAppointment appointment : appointments) {
			appointmentsData.add(new AppointmentData(appointment));
		}
	}

	public String getAgendaArea() {
		return appointmentsData.get(0).getAgendaArea();
	}
}
