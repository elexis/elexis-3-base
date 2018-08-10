package at.medevit.elexis.agenda.ui.function;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.series.ui.SerienTerminDialog;
import ch.elexis.core.model.IPersistentObject;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.dialogs.TerminDialog;
import ch.elexis.dialogs.TerminDialog.CollisionErrorLevel;

public class DoubleClickFunction extends BrowserFunction {
	
	public DoubleClickFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 1) {
			Termin termin = Termin.load((String) arguments[0]);
			AcquireLockBlockingUi.aquireAndRun((IPersistentObject) termin, new ILockHandler() {
				@Override
				public void lockFailed(){
					// do nothing
				}
				
				@Override
				public void lockAcquired(){
					TerminDialog.setActResource(termin.getBereich());
					if (termin.isRecurringDate()) {
						SerienTerminDialog dlg =
							new SerienTerminDialog(getBrowser().getShell(),
								new SerienTermin(termin));
						dlg.open();
					} else {
						TerminDialog dlg = new TerminDialog(termin);
						dlg.setCollisionErrorLevel(CollisionErrorLevel.WARNING);
						dlg.open();
					}
					
				}
			});
		}
		return null;
	}
}
