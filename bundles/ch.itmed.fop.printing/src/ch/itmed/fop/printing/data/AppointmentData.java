package ch.itmed.fop.printing.data;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public final class AppointmentData {
	private Termin appointment;
	
	public AppointmentData() {}

	public AppointmentData(Termin termin) {
		appointment = termin;
	}
	
	public void load() throws NullPointerException {
		appointment = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
		if (appointment == null) {
			SWTHelper.showInfo("Kein Mandant ausgewählt", "Bitte wählen Sie vor dem Drucken einen Termin aus.");
			throw new NullPointerException("No appointment selected");
		}		
	}

	public String getAppointmentDetailed() {
		StringBuilder appointmentDate = new StringBuilder();

		TimeSpan timeSpan = appointment.getTimeSpan();

		// the weekday of the appointment
		TimeTool timeTool = new TimeTool();
		timeTool.setDate(appointment.getDay());
		appointmentDate.append(timeTool.toString(TimeTool.WEEKDAY));
		appointmentDate.append(", ");

		// the date of the appointment
		appointmentDate.append(timeTool.toString(TimeTool.DATE_GER));
		appointmentDate.append(" ");

		// start time of the appointment
		timeTool.setTime(timeSpan.from);
		appointmentDate.append(timeTool.toString(TimeTool.TIME_SMALL));
		appointmentDate.append(" - ");

		// end time of the appointment
		timeTool.setTime(timeSpan.until);
		appointmentDate.append(timeTool.toString(TimeTool.TIME_SMALL));

		return appointmentDate.toString();
	}
	
	public String getAgendaArea() {
		return appointment.getBereich();
	}

}
