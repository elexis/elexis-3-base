package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import org.eclipse.swt.browser.Browser;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
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
					LocalDateTime startDate = getDateTimeArg(arguments[1]);
					termin.setStartTime(new TimeTool(startDate));
					LocalDateTime endDate = getDateTimeArg(arguments[2]);
					termin.setEndTime(new TimeTool(endDate));
					if (arguments.length >= 4) {
						String bereich = (String) arguments[3];
						if (bereich != null && !bereich.isEmpty()) {
							termin.setBereich(bereich);
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