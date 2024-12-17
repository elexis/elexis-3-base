package at.medevit.elexis.agenda.ui.handler;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog;
import at.medevit.elexis.agenda.ui.dialog.AppointmentLinkOptionsDialog.DeleteActionType;
import ch.elexis.agenda.util.AppointmentExtensionHandler;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
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
			if (p instanceof IAppointment) {
				handleAppointmentDeletion((IAppointment) p, shell);
			} else {
				handleNonAppointmentDeletion(p, shell);
			}
		});
		return null;
	}

	private void handleAppointmentDeletion(IAppointment appointment, Shell shell) {
		if (AppointmentExtensionHandler.isMainAppointment(appointment)) {
			handleMainAppointmentDeletion(appointment, shell);
		} else {
			handleLinkedAppointmentDeletion(appointment, shell);
		}
	}

	private void handleMainAppointmentDeletion(IAppointment appointment, Shell shell) {
		List<IAppointment> linkedAppointments = AppointmentExtensionHandler.getLinkedAppointments(appointment);


		if (!linkedAppointments.isEmpty()) {
			DeleteActionType action = AppointmentLinkOptionsDialog.showDeleteDialog(shell, linkedAppointments);
			processDeleteAction(appointment, linkedAppointments, action);
		} else {
			boolean userConfirmed = showConfirmationDialog(shell, appointment);
			if (userConfirmed) {
				deleteMainAppointmentOnly(appointment);
			}
		}
	}

	private void handleLinkedAppointmentDeletion(IAppointment appointment, Shell shell) {
		String mainAppointmentId = AppointmentExtensionHandler.getMainAppointmentId(appointment);
		if (mainAppointmentId != null) {
			Optional<IAppointment> mainAppointment = CoreModelServiceHolder.get().load(mainAppointmentId,
					IAppointment.class);
			if (mainAppointment.isPresent()) {
				boolean shouldProceed = showMainAppointmentWarning(shell, mainAppointment.get(), appointment);
				if (!shouldProceed) {
					return;
				}
			}
		}

		boolean userConfirmed = showConfirmationDialog(shell, appointment);
		if (userConfirmed) {
			performAppointmentDeletion(appointment, shell);
		}
	}

	private void handleNonAppointmentDeletion(IPeriod period, Shell shell) {
		boolean userConfirmed = MessageDialog.openConfirm(shell, Messages.AgendaUI_Delete_delete,
				NLS.bind(Messages.AgendaUI_Delete_ask_really_delete, period.getLabel()));
		if (userConfirmed) {
			AcquireLockBlockingUi.aquireAndRun(period, new ILockHandler() {
				@Override
				public void lockFailed() {
					// do nothing
				}
				@Override
				public void lockAcquired() {
					IAppointment appointment = (IAppointment) period;
					performAppointmentDeletion(appointment, shell);
				}
			});
		}
	}

	private void performAppointmentDeletion(IAppointment appointment, Shell shell) {
		AcquireLockBlockingUi.aquireAndRun(appointment, new ILockHandler() {
			@Override
			public void lockFailed() {
				// do nothing
			}
			@Override
			public void lockAcquired() {
				if (appointment.isRecurring()) {
					boolean deleteSeries = MessageDialog.openQuestion(shell, Messages.AgendaUI_Delete__delete,
							Messages.AgendaUI_Delete_ask_delete_whole_series);
					appointmentService.delete(appointment, deleteSeries);
				} else {
					appointmentService.delete(appointment, false);
				}
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
			}
		});
	}

	private boolean showConfirmationDialog(Shell shell, IPeriod period) {
		return MessageDialog.openConfirm(shell, Messages.AgendaUI_Delete_delete,
				NLS.bind(Messages.AgendaUI_Delete_ask_really_delete, period.getLabel()));
	}

	private boolean showMainAppointmentWarning(Shell shell, IAppointment mainAppointment, IPeriod period) {
		String message = Messages.DeleteHandlerLinkedAppointmentWarning + mainAppointment.getLabel();
		return MessageDialog.openConfirm(shell, Messages.AgendaUI_Delete_delete,
				NLS.bind(Messages.AgendaUI_Delete_ask_really_delete, period.getLabel()) + message);
	}

	private void processDeleteAction(IAppointment appointment, List<IAppointment> linkedAppointments,
			DeleteActionType action) {
		if (action == DeleteActionType.KEEP_LINKS) {
			deleteMainAppointmentOnly(appointment);
		} else if (action == DeleteActionType.DELETE_ALL) {
			deleteLinkedAppointments(appointment, linkedAppointments);
		}
	}

	private void deleteMainAppointmentOnly(IAppointment appointment) {
		AcquireLockBlockingUi.aquireAndRun(appointment, new ILockHandler() {
			@Override
			public void lockFailed() {
				// do nothing
			}
			@Override
			public void lockAcquired() {
				appointmentService.delete(appointment, false);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
			}
		});
	}

	private void deleteLinkedAppointments(IAppointment mainAppointment, List<IAppointment> linkedAppointments) {
		AcquireLockBlockingUi.aquireAndRun(mainAppointment, new ILockHandler() {
			@Override
			public void lockFailed() {
				// do nothing
			}
			@Override
			public void lockAcquired() {
				appointmentService.delete(mainAppointment, false);
				linkedAppointments.forEach(appt -> {
					appointmentService.delete(appt, false);
				});
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
			}
		});
	}

	private Optional<IPeriod> getSelectedPeriod() {
		Object activeSelection = selectionService.getSelection();
		if (activeSelection instanceof StructuredSelection && !((StructuredSelection) activeSelection).isEmpty()) {
			Object element = ((StructuredSelection) activeSelection).getFirstElement();
			if (element instanceof IPeriod) {
				return Optional.of((IPeriod) element);
			}
		}
		return Optional.empty();
	}
}
