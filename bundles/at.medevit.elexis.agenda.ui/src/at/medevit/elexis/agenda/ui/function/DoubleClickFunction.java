package at.medevit.elexis.agenda.ui.function;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import at.medevit.elexis.agenda.ui.dialog.AppointmentDialog;
import at.medevit.elexis.agenda.ui.dialog.RecurringAppointmentDialog;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;

public class DoubleClickFunction extends BrowserFunction {
	
	public DoubleClickFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			IAppointment termin = CoreModelServiceHolder.get()
				.load((String) arguments[0], IAppointment.class).orElse(null);
			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed(){
					// do nothing
				}
				
				@Override
				public void lockAcquired(){
					// TerminDialog.setActResource(termin.getBereich());
					if (termin.isRecurring()) {
						RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(termin);
						dlg.open();
					} else {
						AppointmentDialog dlg = new AppointmentDialog(termin);
						dlg.open();
					}
				}
			});
		}
		return null;
	}
}
