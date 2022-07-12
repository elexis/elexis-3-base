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

import org.apache.commons.lang3.StringUtils;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.agenda.AreaType;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.PersistentObject;
import ch.itmed.fop.printing.resources.Messages;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public final class AppointmentData {
	private Termin appointment;

	public AppointmentData() {
	}

	public AppointmentData(Termin termin) {
		appointment = termin;
	}

	public void load() throws NullPointerException {
		appointment = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
		if (appointment == null) {
			ContextServiceHolder.get().getTyped(IAppointment.class).ifPresent(iAppointment -> {
				PersistentObject po = NoPoUtil.loadAsPersistentObject(iAppointment);
				if (po instanceof Termin) {
					appointment = (Termin) po;
				}
			});
			if (appointment == null) {
				SWTHelper.showInfo(Messages.Info_NoAppointment_Title, Messages.Info_NoAppointment_Message);
				throw new NullPointerException("No appointment selected"); //$NON-NLS-1$
			}
		}
	}

	public String getAppointmentDetailed() {
		StringBuilder appointmentDate = new StringBuilder();

		TimeSpan timeSpan = appointment.getTimeSpan();

		// the weekday of the appointment
		TimeTool timeTool = new TimeTool();
		timeTool.setDate(appointment.getDay());
		appointmentDate.append(timeTool.toString(TimeTool.WEEKDAY));
		appointmentDate.append(", "); //$NON-NLS-1$

		// the date of the appointment
		appointmentDate.append(timeTool.toString(TimeTool.DATE_GER));
		appointmentDate.append(StringUtils.SPACE);

		// start time of the appointment
		timeTool.setTime(timeSpan.from);
		appointmentDate.append(timeTool.toString(TimeTool.TIME_SMALL));
		appointmentDate.append(" - "); //$NON-NLS-1$

		// end time of the appointment
		timeTool.setTime(timeSpan.until);
		appointmentDate.append(timeTool.toString(TimeTool.TIME_SMALL));

		return appointmentDate.toString();
	}

	public String getAppointmentDetailedNoEnd() {
		StringBuilder appointmentDate = new StringBuilder();

		TimeSpan timeSpan = appointment.getTimeSpan();

		// the weekday of the appointment
		TimeTool timeTool = new TimeTool();
		timeTool.setDate(appointment.getDay());
		appointmentDate.append(timeTool.toString(TimeTool.WEEKDAY));
		appointmentDate.append(", "); //$NON-NLS-1$

		// the date of the appointment
		appointmentDate.append(timeTool.toString(TimeTool.DATE_GER));
		appointmentDate.append(StringUtils.SPACE);

		// start time of the appointment
		timeTool.setTime(timeSpan.from);
		appointmentDate.append(timeTool.toString(TimeTool.TIME_SMALL));

		return appointmentDate.toString();
	}

	public TimeTool getStartTime() {
		return appointment.getStartTime();
	}

	public String getAgendaArea() {
		Kontakt kontakt = null;
		String agendaSection = appointment.getBereich();
		String type = ConfigServiceHolder.getGlobal(
				PreferenceConstants.AG_BEREICH_PREFIX + agendaSection + PreferenceConstants.AG_BEREICH_TYPE_POSTFIX,
				null);
		if (type != null) {
			if (type.startsWith(AreaType.CONTACT.name())) {
				kontakt = Kontakt.load(type.substring(AreaType.CONTACT.name().length() + 1));
			}
		}

		if (kontakt != null) {
			return kontakt.getSalutation();
		} else {
			return agendaSection;
		}
	}

}
