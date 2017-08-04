package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import org.eclipse.swt.browser.Browser;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.rgw.tools.TimeTool;

public class EventResizeFunction extends AbstractBrowserFunction {
	
	public EventResizeFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			final Termin termin = Termin.load((String) arguments[0]);
			final LocalDateTime startDate = getDateTimeArg(arguments[1]);
			final LocalDateTime endDate = getDateTimeArg(arguments[2]);
			
			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed(){
					redraw();
				}
				
				@Override
				public void lockAcquired(){
					termin.setStartTime(new TimeTool(startDate));
					termin.setEndTime(new TimeTool(endDate));
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