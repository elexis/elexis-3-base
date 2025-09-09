package ch.elexis.agenda.series.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.elexis.agenda.externalaccess.AgendaStatus;
import ch.elexis.core.model.IAppointmentSeries;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.dialogs.RecurringAppointmentDialog;

public class AddReccuringDateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IAppointmentSeries series = AppointmentServiceHolder.get().createAppointmentSeries();
		series.setSchedule(AgendaStatus.getSelectedBereich());
		RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(
				series);
		dlg.open();
		return null;
	}
}
