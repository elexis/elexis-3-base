package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;

import at.medevit.elexis.agenda.ui.composite.ScriptingHelper;
import ch.elexis.agenda.composite.AppointmentDetailComposite;
import ch.elexis.agenda.util.AppointmentExtensionHandler;
import ch.elexis.agenda.util.AppointmentUtil;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.agenda.RecurringAppointment;
import ch.elexis.core.services.holder.AppointmentHistoryServiceHolder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LocalLockServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;
import ch.elexis.dialogs.AppointmentLinkOptionsDialog;
import ch.elexis.dialogs.AppointmentLinkOptionsDialog.MoveActionType;


public class EventDropFunction extends AbstractBrowserFunction {

	public EventDropFunction(Browser browser, String name) {
		super(browser, name);

	}

	@Override
	public Object function(final Object[] arguments) {
		if (arguments.length >= 3) {
			IAppointment termin = CoreModelServiceHolder.get().load((String) arguments[0], IAppointment.class)
					.orElse(null);

			if (termin == null || AppointmentUtil.checkLocked(termin)) {
				new ScriptingHelper(getBrowser()).refetchEvents();
				return null;
			}

			AcquireLockBlockingUi.aquireAndRun(termin, new ILockHandler() {
				@Override
				public void lockFailed() {
					redraw();
				}

				@Override
				public void lockAcquired() {
					IAppointment current = termin;
					LocalDateTime oldMainTime = current.getStartTime();
					String oldArea = current.getSchedule();

					// do copy
					if (arguments.length >= 5 && Boolean.TRUE.equals(arguments[4])) {
						current = AppointmentServiceHolder.get().clone(termin);
						AppointmentHistoryServiceHolder.get().logAppointmentCopyFromTo(current, termin.getId(),
								current.getId());
						if (termin.isRecurring() && termin.getContact() == null) {
							// take kontakt from root termin
							IContact k = new RecurringAppointment(termin, CoreModelServiceHolder.get())
									.getRootAppoinemtent().getContact();
							if (k != null) {
								current.setSubjectOrPatient(k.getId());
							}
						}
					}

					boolean isMainAppointment = AppointmentExtensionHandler.isMainAppointment(current);
					boolean isAllDay = arguments[1].toString().length() == 10;
					LocalDateTime newMainTime = getDateTimeArg(arguments[1]);
					String newResource = arguments.length >= 4 && arguments[3] != null ? (String) arguments[3]
							: current.getSchedule();

					current.setStartTime(newMainTime);
					if (isAllDay) {
						current.setEndTime(null);
					} else {
						LocalDateTime endTime = Objects.isNull(arguments[2]) ? newMainTime.plusMinutes(15)
								: getDateTimeArg(arguments[2]);
						current.setEndTime(endTime);
					}
					if (!newResource.isEmpty()) {
						current.setSchedule(newResource);
					}

					String newArea = current.getSchedule();

					if (isMainAppointment) {
						List<IAppointment> linkedAppointments = AppointmentExtensionHandler
								.getLinkedAppointments(current);
						MoveActionType moveAction = AppointmentLinkOptionsDialog
								.showMoveDialog(Display.getDefault().getActiveShell(), linkedAppointments);
						switch (moveAction) {
						case KEEP_MAIN_ONLY:
							lockAndSaveAppointment(current);
							AppointmentHistoryServiceHolder.get().logAppointmentMove(current, oldMainTime, newMainTime,
									oldArea, newArea);
							break;
						case MOVE_ALL:
							long minutesDifference = java.time.Duration.between(oldMainTime, newMainTime).toMinutes();
							for (IAppointment linkedAppointment : linkedAppointments) {
								LocalDateTime oldLinkedTime = linkedAppointment.getStartTime();
								LocalDateTime newLinkedTime = oldLinkedTime.plusMinutes(minutesDifference);
								linkedAppointment.setStartTime(newLinkedTime);
								linkedAppointment
										.setEndTime(newLinkedTime.plusMinutes(linkedAppointment.getDurationMinutes()));
								linkedAppointment.setSchedule(linkedAppointment.getSchedule());
								linkedAppointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
								lockAndSaveAppointment(linkedAppointment);
								AppointmentHistoryServiceHolder.get().logAppointmentMove(linkedAppointment,
										oldLinkedTime,
										newLinkedTime, oldArea, newArea); // loggen
							}
							lockAndSaveAppointment(current);
							AppointmentHistoryServiceHolder.get().logAppointmentMove(current, oldMainTime, newMainTime,
									oldArea, newArea);
							break;
						case CANCEL:
						default:
							break;
						}
					} else {
						lockAndSaveAppointment(current);
						AppointmentHistoryServiceHolder.get().logAppointmentMove(current, oldMainTime, newMainTime,
								oldArea,
								newArea);
					}

					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, current);
					redraw();
				}

			});
		} else {
			throw new IllegalArgumentException("Unexpected arguments"); //$NON-NLS-1$
		}
		return null;
	}

	private void lockAndSaveAppointment(IAppointment appointment) {
		if (LocalLockServiceHolder.get().acquireLock(appointment).isOk()) {
			try {
				appointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
				CoreModelServiceHolder.get().save(appointment);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, appointment);
			} finally {
				LocalLockServiceHolder.get().releaseLock(appointment);
			}
		} else {
			LoggerFactory.getLogger(getClass()).warn("Failed to acquire lock for appointment: " + appointment.getId());
		}
	}
}
