package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import at.medevit.elexis.agenda.ui.handler.AppointmentHistoryManager;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;

import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class EventResizeFunction extends AbstractBrowserFunction {

	public EventResizeFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length == 3) {

			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);
			final LocalDateTime startDate = getDateTimeArg(arguments[1]);
			final LocalDateTime endDate = getDateTimeArg(arguments[2]);

			if (termin != null) {
				AppointmentHistoryManager historyManager = new AppointmentHistoryManager(termin);
				final LocalDateTime oldEndDate = termin.getEndTime();

				AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
					@Override
					public void lockFailed() {
						redraw();
					}

					@Override
					public void lockAcquired() {
						termin.setStartTime(startDate);
						termin.setEndTime(endDate);
						CoreModelServiceHolder.get().save(termin);
						historyManager.logAppointmentDurationChange(oldEndDate, endDate);
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, termin);
						redraw();
					}
				});
			} else {
				new ScriptingHelper(getBrowser()).refetchEvents();
			}
		} else {
			throw new IllegalArgumentException("Unexpected arguments"); //$NON-NLS-1$
		}
		return null;
	}
}
