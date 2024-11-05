package at.medevit.elexis.agenda.ui.function;

import javax.inject.Inject;

import org.osgi.service.component.annotations.Reference;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import at.medevit.elexis.agenda.ui.dialog.AppointmentDialog;
import at.medevit.elexis.agenda.ui.dialog.RecurringAppointmentDialog;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.IAppointmentHistoryManagerService;
import ch.elexis.core.services.holder.AppointmentHistoryServiceHolder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class DoubleClickFunction extends BrowserFunction {

	private IAppointmentHistoryManagerService appointmentHistoryManagerService;

	public DoubleClickFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		appointmentHistoryManagerService = AppointmentHistoryServiceHolder.get();
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			if (termin != null) {
				AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						boolean isEditConfirmed = false;
						if (termin.isRecurring()) {
							RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(
									AppointmentServiceHolder.get().getAppointmentSeries(termin).get());
							isEditConfirmed = dlg.openAndWaitForOk();
						} else {
							AppointmentDialog dlg = new AppointmentDialog(termin);
							isEditConfirmed = dlg.openAndWaitForOk();
						}

						if (isEditConfirmed) {
							appointmentHistoryManagerService.logAppointmentEdit(termin);
						}
					}
				});
			} else {
				new ScriptingHelper(getBrowser()).refetchEvents();
			}
		}
		return null;
	}

}
