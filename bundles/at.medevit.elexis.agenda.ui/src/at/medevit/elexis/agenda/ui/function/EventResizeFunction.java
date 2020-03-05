package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import org.eclipse.swt.browser.Browser;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;

public class EventResizeFunction extends AbstractBrowserFunction {
	
	public EventResizeFunction(Browser browser, String name){
		super(browser, name);
	}
	
	public Object function(Object[] arguments){
		if (arguments.length == 3) {
			IAppointment termin = CoreModelServiceHolder.get()
				.load((String) arguments[0], IAppointment.class).orElse(null);
			final LocalDateTime startDate = getDateTimeArg(arguments[1]);
			final LocalDateTime endDate = getDateTimeArg(arguments[2]);
			
			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed(){
					redraw();
				}
				
				@Override
				public void lockAcquired(){
					termin.setStartTime(startDate);
					termin.setEndTime(endDate);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD,
						IAppointment.class);
					redraw();
				}
			});
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
		return null;
	}
}