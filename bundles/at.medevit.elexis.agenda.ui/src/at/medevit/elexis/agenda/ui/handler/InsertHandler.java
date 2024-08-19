package at.medevit.elexis.agenda.ui.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.SideBarComposite;
import at.medevit.elexis.agenda.ui.function.AbstractBrowserFunction;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.ui.e4.dialog.GenericSelectionDialog;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class InsertHandler {
	@Inject
	private IEventBroker eventBroker;

	@Execute
	public Object execute(MPart part) {
		Optional<SideBarComposite> activeSideBar = AbstractBrowserFunction.getActiveSideBar(part);
		activeSideBar.ifPresent(sideBar -> {
			Optional<IAppointment> copyAppointment = CopyHandler.getCopiedAppointment();
			if (copyAppointment.isPresent()) {
				Optional<SideBarComposite.MoveInformation> moveInfoOpt = sideBar.getMoveInformation();
				if (moveInfoOpt.isPresent()) {
					SideBarComposite.MoveInformation moveInfo = moveInfoOpt.get();
					LocalDateTime targetTime = moveInfo.getDateTime();
					String targetResource = moveInfo.getResource();
					cloneAndModifyAppointment(copyAppointment.get(), targetTime,
							targetResource, sideBar);
					sideBar.removeMovePeriod(copyAppointment.get());
					CopyHandler.clearCopiedAppointment();
				} else {
					LoggerFactory.getLogger(getClass()).info("Fehler: Keine MoveInformation vorhanden");
				}
			} else {
				sideBar.getMoveInformation().ifPresent(moveInformation -> {
					List<IPeriod> moveablePeriods = moveInformation.getMoveablePeriods();
					if (moveablePeriods.size() == 1) {
						moveInformation.movePeriod(moveablePeriods.get(0));
					} else if (moveablePeriods.size() > 1) {
						GenericSelectionDialog dialog = new GenericSelectionDialog(
								Display.getDefault().getActiveShell(), moveablePeriods);
						if (dialog.open() == GenericSelectionDialog.OK) {
							IStructuredSelection selection = dialog.getSelection();
							if (selection != null && !selection.isEmpty()) {
								@SuppressWarnings("unchecked")
								List<IPeriod> selected = (List<IPeriod>) (List<?>) selection.toList();
								for (IPeriod iPeriod : selected) {
									moveInformation.movePeriod(iPeriod);
								}
							}
						}
					}
				});
			}
		});
		return null;
	}

	private IAppointment cloneAndModifyAppointment(IAppointment originalAppointment, LocalDateTime newStartTime,
			String newResource, SideBarComposite sideBar) {
		IAppointmentService appointmentService = AppointmentServiceHolder.get();
		IAppointment clonedAppointment = appointmentService.clone(originalAppointment);
		clonedAppointment.setStartTime(newStartTime);
		clonedAppointment.setEndTime(newStartTime.plusMinutes(originalAppointment.getDurationMinutes()));
		clonedAppointment.setSchedule(newResource);
		CoreModelServiceHolder.get().save(clonedAppointment);
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
		eventBroker.post(ElexisEventTopics.EVENT_UPDATE, clonedAppointment);
		return clonedAppointment;
	}
}
