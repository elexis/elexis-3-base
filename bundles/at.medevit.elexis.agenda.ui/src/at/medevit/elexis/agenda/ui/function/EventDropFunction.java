package at.medevit.elexis.agenda.ui.function;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.equo.chromium.swt.Browser;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.LocalLockServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.AppointmentDetailComposite;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog.MoveActionType;

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
					boolean isMainAppointment = AppointmentLoader.isMainAppointment(termin);
					LocalDateTime oldMainTime = termin.getStartTime();
					boolean isAllDay = arguments[1].toString().length() == 10;
					LocalDateTime newMainTime = getDateTimeArg(arguments[1]);
					String newResource = arguments.length >= 4 && arguments[3] != null ? (String) arguments[3]
							: termin.getSchedule();
					termin.setStartTime(newMainTime);
					if (isAllDay) {
						termin.setEndTime(null);
					} else {
						LocalDateTime endTime = Objects.isNull(arguments[2]) ? newMainTime.plusMinutes(15)
								: getDateTimeArg(arguments[2]);
						termin.setEndTime(endTime);
					}
					if (!newResource.isEmpty()) {
						termin.setSchedule(newResource);
					}

					if (isMainAppointment) {
						List<IAppointment> linkedAppointments = AppointmentLoader.findLinkedAppointments(termin);
						MoveActionType moveAction = AppointmentLinkOptionsDialog
								.showMoveDialog(Display.getDefault().getActiveShell(), linkedAppointments);
						switch (moveAction) {
						case KEEP_MAIN_ONLY:
							lockAndSaveAppointment(termin);
							break;
						case MOVE_ALL:
							long minutesDifference = java.time.Duration.between(oldMainTime, newMainTime).toMinutes();
							String newMainExtension = updateExtensionWithMainAndKombi(termin, linkedAppointments,
									minutesDifference);
							termin.setExtension(newMainExtension);
							lockAndSaveAppointment(termin);
							break;
						case CANCEL:
						default:
							break;
						}
					} else {
						lockAndSaveAppointment(termin);
					}

					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, termin);
					redraw();
				}
			});
		} else {
			throw new IllegalArgumentException("Unexpected arguments");
		}
		return null;
	}

	private String updateExtensionWithMainAndKombi(IAppointment termin, List<IAppointment> linkedAppointments,
			long minutesDifference) {
		StringBuilder extensionBuilder = new StringBuilder();
		String currentExtension = termin.getExtension();
		if (currentExtension != null) {

			String[] parts = currentExtension.split("\\|\\|");
			for (String part : parts) {
				if (!part.startsWith("Main:") && !part.startsWith("Kombi:")) {
					extensionBuilder.append(part).append("||");
				}
			}
		}

		extensionBuilder.append("Main:").append(termin.getId());
		for (IAppointment linkedAppointment : linkedAppointments) {
			LocalDateTime oldLinkedTime = linkedAppointment.getStartTime();
			LocalDateTime newLinkedTime = oldLinkedTime.plusMinutes(minutesDifference);
			linkedAppointment.setStartTime(newLinkedTime);
			linkedAppointment.setEndTime(newLinkedTime.plusMinutes(linkedAppointment.getDurationMinutes()));
			linkedAppointment.setSchedule(linkedAppointment.getSchedule());
			linkedAppointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
			extensionBuilder.append(",Kombi:").append(linkedAppointment.getId());
			lockAndSaveAppointment(linkedAppointment);
		}
		return extensionBuilder.toString();
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

