package ch.elexis.agenda.series.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.dialogs.RecurringAppointmentDialog;

public class AddReccuringDateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(
				AppointmentServiceHolder.get().createAppointmentSeries());
		dlg.open();
		return null;
	}
}
