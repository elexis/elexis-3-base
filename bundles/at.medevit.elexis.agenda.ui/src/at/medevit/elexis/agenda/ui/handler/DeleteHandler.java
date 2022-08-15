package at.medevit.elexis.agenda.ui.handler;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class DeleteHandler {

	@Inject
	private ESelectionService selectionService;

	@Inject
	private IAppointmentService appointmentService;

	@Execute
	public Object execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) throws ExecutionException {
		Optional<IPeriod> period = getSelectedPeriod();

		period.ifPresent(p -> {
			if (MessageDialog.openConfirm(shell, Messages.AgendaUI_Delete_delete,
					NLS.bind(Messages.AgendaUI_Delete_ask_really_delete, period.get().getLabel()))) {
				AcquireLockBlockingUi.aquireAndRun(p, new ILockHandler() {
					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						IAppointment appointment = (IAppointment) p;
						if (appointment.isRecurring()) {
							if (MessageDialog.openQuestion(shell, Messages.AgendaUI_Delete__delete,
									Messages.AgendaUI_Delete_ask_delete_whole_series)) {
								appointmentService.delete(appointment, true);
							} else {
								appointmentService.delete(appointment, false);
							}
						} else {
							appointmentService.delete(appointment, false);
						}
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
					}
				});
			}
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
