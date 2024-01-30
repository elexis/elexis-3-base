package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.Objects;

import com.equo.chromium.swt.Browser;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.agenda.RecurringAppointment;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LocalLockServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class EventDropFunction extends AbstractBrowserFunction {

	public EventDropFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(final Object[] arguments) {
		if (arguments.length >= 3) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);

			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed() {
					redraw();
				}

				@Override
				public void lockAcquired() {
					IAppointment current = termin;

					// do copy
					if (arguments.length >= 5 && Boolean.TRUE.equals(arguments[4])) {
						current = AppointmentServiceHolder.get().clone(termin);
						if (termin.isRecurring() && termin.getContact() == null) {
							// take kontakt from root termin
							IContact k = new RecurringAppointment(termin, CoreModelServiceHolder.get())
									.getRootAppoinemtent().getContact();
							if (k != null) {
								current.setSubjectOrPatient(k.getId());
							}
						}
					}

					// moving
					boolean isAllDay = arguments[1].toString().length() == 10;
					LocalDateTime startDate = getDateTimeArg(arguments[1]);
					current.setStartTime(startDate);
					if (isAllDay) {
						current.setEndTime(null);
					} else {
						LocalDateTime endTime = Objects.isNull(arguments[2]) ? startDate.plusMinutes(15)
								: getDateTimeArg(arguments[2]);
						current.setEndTime(endTime);
					}

					if (arguments.length >= 4 && arguments[3] != null) {
						String bereich = (String) arguments[3];
						if (!bereich.isEmpty()) {
							current.setSchedule(bereich);
						}
					}

					// checks if that termin is copied
					if (!current.equals(termin)) {
						if (LocalLockServiceHolder.get().acquireLock(current).isOk()) {
							LocalLockServiceHolder.get().releaseLock(current);
						} else {
							// should not happened - no lock - delete the copied termin
							CoreModelServiceHolder.get().delete(current);
						}
					}
					CoreModelServiceHolder.get().save(current);
					CoreModelServiceHolder.get().save(termin);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, termin);
					redraw();
				}
			});
		} else {
			throw new IllegalArgumentException("Unexpected arguments"); //$NON-NLS-1$
		}
		return null;
	}

}