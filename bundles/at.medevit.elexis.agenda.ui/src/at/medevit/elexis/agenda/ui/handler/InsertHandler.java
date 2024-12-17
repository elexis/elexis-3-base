package at.medevit.elexis.agenda.ui.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog.CopyActionType;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog.MoveActionType;
import at.medevit.elexis.agenda.ui.function.AbstractBrowserFunction;
import ch.elexis.agenda.composite.AppointmentDetailComposite;
import ch.elexis.agenda.util.AppointmentExtensionHandler;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.AppointmentHistoryServiceHolder;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;


public class InsertHandler {
	@Inject
	private IEventBroker eventBroker;

	@Execute
	public Object execute(MPart part) {

		Optional<SideBarComposite> activeSideBar = AbstractBrowserFunction.getActiveSideBar(part);
		activeSideBar.ifPresent(sideBar -> {
			Optional<IAppointment> copiedAppointment = CopyHandler.getCopiedAppointment();
			if (copiedAppointment.isPresent()) {
				handleCopiedAppointment(sideBar, copiedAppointment.get());
			} else {
				sideBar.getMoveInformation().ifPresent(this::handleMoveInformation);
			}
		});
		return null;
	}

	private void handleCopiedAppointment(SideBarComposite sideBar, IAppointment copiedAppointment) {
		Optional<SideBarComposite.MoveInformation> moveInfoOpt = sideBar.getMoveInformation();
		if (moveInfoOpt.isPresent()) {
			SideBarComposite.MoveInformation moveInfo = moveInfoOpt.get();
			LocalDateTime targetTime = moveInfo.getDateTime();
			String targetResource = moveInfo.getResource();
			List<IAppointment> linkedAppointments = AppointmentExtensionHandler
					.getLinkedAppointments(copiedAppointment);

			if (linkedAppointments.isEmpty() || !AppointmentExtensionHandler.isMainAppointment(copiedAppointment)) {
				cloneAndModifyAppointment(copiedAppointment, targetTime, targetResource,
						sideBar);
				sideBar.removeMovePeriod(copiedAppointment);
				CopyHandler.clearCopiedAppointment();
				return;
			}

			CopyActionType copyAction = AppointmentLinkOptionsDialog
					.showCopyDialog(Display.getDefault().getActiveShell(), linkedAppointments);
			switch (copyAction) {
			case KEEP_MAIN_ONLY:
				IAppointment newMainAppointment = cloneAndModifyAppointment(copiedAppointment, targetTime,
						targetResource, sideBar);
				AppointmentExtensionHandler.setMainAppointmentId(newMainAppointment, newMainAppointment.getId());
				CoreModelServiceHolder.get().save(newMainAppointment);
				break;

			case COPY_ALL:
				IAppointment newMainAppointmentWithLinks = cloneAndModifyAppointment(copiedAppointment, targetTime,
						targetResource, sideBar);
				LocalDateTime oldMainTime = copiedAppointment.getStartTime();
				long minutesDifference = java.time.Duration.between(oldMainTime, targetTime).toMinutes();
				AppointmentExtensionHandler.setMainAppointmentId(newMainAppointmentWithLinks,
						newMainAppointmentWithLinks.getId());
				List<String> newLinkedAppointmentIds = new ArrayList<>();
				for (IAppointment linkedAppointment : linkedAppointments) {
					LocalDateTime oldLinkedTime = linkedAppointment.getStartTime();
					LocalDateTime newLinkedTime = oldLinkedTime.plusMinutes(minutesDifference);
					IAppointment newLinkedAppointment = cloneAndModifyAppointment(linkedAppointment, newLinkedTime,
							linkedAppointment.getSchedule(), sideBar);
					AppointmentExtensionHandler.setMainAppointmentId(newLinkedAppointment,
							newMainAppointmentWithLinks.getId());
					AppointmentExtensionHandler.addLinkedAppointmentId(newLinkedAppointment,
							newLinkedAppointment.getId());
					newLinkedAppointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
					CoreModelServiceHolder.get().save(newLinkedAppointment);
					newLinkedAppointmentIds.add(newLinkedAppointment.getId());
				}
				AppointmentExtensionHandler.addMultipleLinkedAppointments(newMainAppointmentWithLinks,
						newLinkedAppointmentIds);
				newMainAppointmentWithLinks.setLastEdit(AppointmentDetailComposite.createTimeStamp());
				CoreModelServiceHolder.get().save(newMainAppointmentWithLinks);
				// loggen
				break;

			case CANCEL:
			default:
				break;
			}
			sideBar.removeMovePeriod(copiedAppointment);
			CopyHandler.clearCopiedAppointment();
		} else {
			LoggerFactory.getLogger(getClass()).info("Fehler: Keine MoveInformation vorhanden");
		}
	}

	private void handleMoveInformation(SideBarComposite.MoveInformation moveInformation) {
		List<IPeriod> moveablePeriods = moveInformation.getMoveablePeriods();
		if (moveablePeriods.isEmpty()) {
			LoggerFactory.getLogger(getClass()).info("Fehler: Keine verschiebbaren Perioden vorhanden");
			return;
		}
		IAppointment mainAppointment = extractMainAppointment(moveablePeriods.get(0));
		if (mainAppointment == null) {
			LoggerFactory.getLogger(getClass()).info("Fehler: Kein Haupt-Termin gefunden");
			return;
		}
		String oldArea = mainAppointment.getSchedule();
		List<IAppointment> linkedAppointments = AppointmentExtensionHandler.getLinkedAppointments(mainAppointment);
		if (linkedAppointments.isEmpty() || !AppointmentExtensionHandler.isMainAppointment(mainAppointment)) {
			LocalDateTime oldTime = mainAppointment.getStartTime();
			moveInformation.movePeriod(mainAppointment);
			LocalDateTime newTime = moveInformation.getDateTime();
			String newArea = mainAppointment.getSchedule();
			AppointmentHistoryServiceHolder.get().logAppointmentMove(mainAppointment, oldTime, newTime, oldArea,
					newArea);
		} else {
			MoveActionType moveAction = AppointmentLinkOptionsDialog
					.showMoveDialog(Display.getDefault().getActiveShell(), linkedAppointments);
			switch (moveAction) {
			case KEEP_MAIN_ONLY:
				LocalDateTime oldTime = mainAppointment.getStartTime();
				moveInformation.movePeriod(mainAppointment);
				LocalDateTime newTime = moveInformation.getDateTime();
				String newArea = mainAppointment.getSchedule();
				AppointmentHistoryServiceHolder.get().logAppointmentMove(mainAppointment, oldTime, newTime, oldArea,
						newArea);
				break;

			case MOVE_ALL:
				LocalDateTime oldMainTime = mainAppointment.getStartTime();
				moveInformation.movePeriod(mainAppointment);
				LocalDateTime newMainTime = moveInformation.getDateTime();
				moveLinkedAppointments(linkedAppointments, oldMainTime, newMainTime);
				String newMainArea = mainAppointment.getSchedule();
				AppointmentHistoryServiceHolder.get().logAppointmentMove(mainAppointment, oldMainTime, newMainTime,
						oldArea,
						newMainArea);
				break;

			case CANCEL:
			default:
				break;
			}
		}
	}

	private void moveLinkedAppointments(List<IAppointment> linkedAppointments, LocalDateTime oldMainTime,
			LocalDateTime newMainAppointmentTime) {
		long minutesDifference = java.time.Duration.between(oldMainTime, newMainAppointmentTime).toMinutes();
		for (IAppointment linkedAppointment : linkedAppointments) {
			LocalDateTime oldLinkedTime = linkedAppointment.getStartTime();
			LocalDateTime newLinkedTime = oldLinkedTime.plusMinutes(minutesDifference);
			moveLinkedAppointment(linkedAppointment, newLinkedTime);
			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, linkedAppointment);
		}
		eventBroker.post(ElexisEventTopics.EVENT_UPDATE, linkedAppointments.get(0));
	}

	private void moveLinkedAppointment(IAppointment appointment, LocalDateTime newStartTime) {
		LocalDateTime oldStartTime = appointment.getStartTime();
		String oldArea = appointment.getSchedule();
		appointment.setStartTime(newStartTime);
		appointment.setEndTime(newStartTime.plusMinutes(appointment.getDurationMinutes()));
		appointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
		CoreModelServiceHolder.get().save(appointment);
		String newArea = appointment.getSchedule();
		ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, appointment);
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
		AppointmentHistoryServiceHolder.get().logAppointmentMove(appointment, oldStartTime, newStartTime, oldArea,
				newArea);
	}

	private IAppointment extractMainAppointment(IPeriod period) {
		if (period instanceof IAppointment) {
			return (IAppointment) period;
		}
		return null;
	}

	private IAppointment cloneAndModifyAppointment(IAppointment originalAppointment, LocalDateTime newStartTime,
			String newResource, SideBarComposite sideBar) {
		IAppointmentService appointmentService = AppointmentServiceHolder.get();
		IAppointment clonedAppointment = appointmentService.clone(originalAppointment);
		clonedAppointment.setCreatedBy(originalAppointment.getCreatedBy());
		clonedAppointment.setCreated(originalAppointment.getCreated());
		clonedAppointment.setLastEdit(AppointmentDetailComposite.createTimeStamp());
		clonedAppointment.setStartTime(newStartTime);
		clonedAppointment.setEndTime(newStartTime.plusMinutes(originalAppointment.getDurationMinutes()));
		clonedAppointment.setSchedule(newResource);
		clonedAppointment.setReason(originalAppointment.getReason());
		CoreModelServiceHolder.get().save(clonedAppointment);
		ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, clonedAppointment);
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
		AppointmentHistoryServiceHolder.get().logAppointmentCopy(originalAppointment, originalAppointment.getId());
		return clonedAppointment;
	}
}
