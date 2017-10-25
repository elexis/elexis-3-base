package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import org.eclipse.swt.browser.Browser;

import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.data.Kontakt;
import ch.rgw.tools.TimeTool;

public class EventDropFunction extends AbstractBrowserFunction {
	
	public EventDropFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(final Object[] arguments){
		if (arguments.length >= 3) {
			final Termin termin = Termin.load((String) arguments[0]);
			
			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed(){
					redraw();
				}
				
				@Override
				public void lockAcquired(){
					Termin current = termin;
					
					// do copy
					if (arguments.length >= 5 && Boolean.TRUE.equals(arguments[4])) {
						current = (Termin) termin.clone();
						if (termin.isRecurringDate() && termin.getKontakt() == null) {
							// take kontakt from root termin
							Kontakt k = new SerienTermin(termin).getRootTermin().getKontakt();
							if (k != null) {
								current.setKontakt(k);
							}
						}
					}
					
					// moving
					LocalDateTime startDate = getDateTimeArg(arguments[1]);
					current.setStartTime(new TimeTool(startDate));
					LocalDateTime endDate = getDateTimeArg(arguments[2]);
					current.setEndTime(new TimeTool(endDate));
					if (arguments.length >= 4 && arguments[3] != null) {
						String bereich = (String) arguments[3];
						if (!bereich.isEmpty()) {
							current.setBereich(bereich);
						}
					}
					
					// checks if that termin is copied
					if (!current.equals(termin)) {
						if (CoreHub.getLocalLockService().acquireLock(current).isOk()) {
							CoreHub.getLocalLockService().releaseLock(current);
						} else {
							// should not happened - no lock - delete the copied termin
							current.delete();
						}
					}
					
					ElexisEventDispatcher.reload(Termin.class);
					redraw();
				}
			});
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
		return null;
	}
	
}