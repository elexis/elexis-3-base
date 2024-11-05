package at.medevit.elexis.agenda.ui.handler;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.dialog.AppointmentDialog;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.IAppointmentHistoryManagerService;
import ch.elexis.core.services.holder.AppointmentHistoryServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class EditHandler {

	@Inject
	private ESelectionService selectionService;

	@Reference
	private IAppointmentHistoryManagerService appointmentHistoryManagerService;

	@Execute
	public Object execute() {
		appointmentHistoryManagerService = AppointmentHistoryServiceHolder.get();
		Optional<IPeriod> period = getSelectedPeriod();

		period.ifPresent(p -> {
			AcquireLockBlockingUi.aquireAndRun(p, new ILockHandler() {
				@Override
				public void lockFailed() {
					// do nothing
				}

				@Override
				public void lockAcquired() {
					boolean isEditConfirmed = false;
					IAppointment appointment = (IAppointment) p;
					AppointmentDialog dlg = new AppointmentDialog((IAppointment) p);
					isEditConfirmed = dlg.openAndWaitForOk();
					if (isEditConfirmed) {
						appointmentHistoryManagerService.logAppointmentEdit(appointment);
					}
				}
			});
		});
		return null;
	}

	private Optional<IPeriod> getSelectedPeriod() {
		try {
			ISelection activeSelection = (ISelection) selectionService.getSelection();
			if (activeSelection instanceof StructuredSelection && !((StructuredSelection) activeSelection).isEmpty()) {
				Object element = ((StructuredSelection) activeSelection).getFirstElement();
				if (element instanceof IPeriod) {
					return Optional.of((IPeriod) element);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error setting status", e); //$NON-NLS-1$
		}
		return Optional.empty();
	}
}
