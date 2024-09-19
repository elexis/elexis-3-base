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

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.agenda.AreaType;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.TimeTool;

public final class AppointmentData {
	private IAppointment appointment;

	public void load(List<IAppointment> appointments) throws NullPointerException {
		if (appointments == null || appointments.isEmpty()) {
			appointment = ContextServiceHolder.get().getTyped(IAppointment.class).orElse(null);
			if (appointment == null) {
				throw new NullPointerException("No appointment selected"); //$NON-NLS-1$
			}
		} else {
			appointment = appointments.get(0);
		}
	}

	public AppointmentData() {
	}

	public AppointmentData(IAppointment appointment) {
		this.appointment = appointment;
	}

	public String getAppointmentDetailed() {
		StringBuilder appointmentDate = new StringBuilder();

		// the weekday of the appointment
		TimeTool startTime = getStartTime();
		appointmentDate.append(startTime.toString(TimeTool.WEEKDAY));
		appointmentDate.append(", "); //$NON-NLS-1$

		// the date of the appointment
		appointmentDate.append(startTime.toString(TimeTool.DATE_GER));
		appointmentDate.append(StringUtils.SPACE);

		// start time of the appointment
		appointmentDate.append(startTime.toString(TimeTool.TIME_SMALL));

		if (!appointment.isAllDay()) {
			appointmentDate.append(" - "); //$NON-NLS-1$

			// end time of the appointment
			TimeTool endTime = new TimeTool(appointment.getEndTime());
			appointmentDate.append(endTime.toString(TimeTool.TIME_SMALL));
		}

		return appointmentDate.toString();
	}

	public String getAppointmentDetailedNoEnd() {
		StringBuilder appointmentDate = new StringBuilder();

		// the weekday of the appointment
		TimeTool startTime = getStartTime();
		appointmentDate.append(startTime.toString(TimeTool.WEEKDAY));
		appointmentDate.append(", "); //$NON-NLS-1$

		// the date of the appointment
		appointmentDate.append(startTime.toString(TimeTool.DATE_GER));
		appointmentDate.append(StringUtils.SPACE);

		// start time of the appointment
		appointmentDate.append(startTime.toString(TimeTool.TIME_SMALL));

		return appointmentDate.toString();
	}

	public TimeTool getEndTime() {
		return new TimeTool(appointment.getEndTime());
	}

	public TimeTool getStartTime() {
		return new TimeTool(appointment.getStartTime());
	}

	public String getEarliestAndLatestAppointmentTimes(List<IAppointment> allRelatedAppointments) {
		if (allRelatedAppointments == null || allRelatedAppointments.isEmpty()) {
			return getAppointmentDetailed();
		}
		IAppointment earliestAppointment = allRelatedAppointments.stream()
				.min(Comparator.comparing(appointment -> new TimeTool(appointment.getStartTime()))).orElse(appointment);
		IAppointment latestAppointment = allRelatedAppointments.stream()
				.max(Comparator.comparing(appointment -> new TimeTool(appointment.getEndTime()))).orElse(appointment);
		TimeTool earliestStartTime = new TimeTool(earliestAppointment.getStartTime());
		TimeTool latestEndTime = new TimeTool(latestAppointment.getEndTime());
		String formattedWeekday = earliestStartTime.toString(TimeTool.WEEKDAY);
		String formattedEarliestDate = earliestStartTime.toString(TimeTool.DATE_GER);
		String formattedEarliestTime = earliestStartTime.toString(TimeTool.TIME_SMALL);
		String formattedLatestTime = latestEndTime.toString(TimeTool.TIME_SMALL);
		if (earliestStartTime.isSameDay(latestEndTime)) {
			return formattedWeekday + ", " + formattedEarliestDate + StringUtils.SPACE + formattedEarliestTime + " - "
					+ formattedLatestTime;
		} else {
			String formattedLatestDate = latestEndTime.toString(TimeTool.DATE_GER);
			return formattedWeekday + ", " + formattedEarliestDate + StringUtils.SPACE + formattedEarliestTime + " - "
					+ formattedLatestDate + StringUtils.SPACE + formattedLatestTime;
		}
	}

	public String getAgendaArea() {
		IContact contact = null;
		String agendaSection = appointment.getSchedule();
		String type = ConfigServiceHolder
				.getGlobal(Preferences.AG_BEREICH_PREFIX + agendaSection + Preferences.AG_BEREICH_TYPE_POSTFIX, null);
		if (type != null) {
			if (type.startsWith(AreaType.CONTACT.name())) {
				contact = CoreModelServiceHolder.get()
						.load(type.substring(AreaType.CONTACT.name().length() + 1), IContact.class).orElse(null);
			}
		}

		if (contact != null) {
			return Salutation.getSalutation(contact);
		} else {
			return agendaSection;
		}
	}

	public IAppointment getAppointment() {
		return appointment;
	}

}
