package at.medevit.elexis.agenda.ui.function;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import at.medevit.elexis.agenda.ui.dialog.AppointmentDialog;
import at.medevit.elexis.agenda.ui.dialog.RecurringAppointmentDialog;
import at.medevit.elexis.agenda.ui.handler.AppointmentHistoryManager;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class DoubleClickFunction extends BrowserFunction {

	public DoubleClickFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			if (termin != null) {
				AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
					@Override
					public void lockFailed() {
						// do nothing
					}

					AppointmentHistoryManager historyManager = new AppointmentHistoryManager(termin);
					@Override
					public void lockAcquired() {
						// TerminDialog.setActResource(termin.getBereich());
						if (termin.isRecurring()) {
							RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(
									AppointmentServiceHolder.get().getAppointmentSeries(termin).get());
							dlg.open();
							historyManager.logAppointmentEdit();
						} else {
							AppointmentDialog dlg = new AppointmentDialog(termin);
							dlg.open();

							historyManager.logAppointmentEdit();
						}
					}
				});
			} else {
				// the event could not be loaded, trigger refetch
				new ScriptingHelper(getBrowser()).refetchEvents();
			}
		}
		return null;
	}
}
