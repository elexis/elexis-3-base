package at.medevit.elexis.agenda.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.time.TimeUtil;

public class AppointmentLinkOptionsDialog {

	public enum MoveActionType {
		KEEP_MAIN_ONLY, MOVE_ALL, CANCEL
	}

	public enum CopyActionType {
		KEEP_MAIN_ONLY, COPY_ALL, CANCEL
	}

	public enum DeleteActionType {
		KEEP_LINKS, DELETE_ALL
	}

	public static MoveActionType showMoveDialog(Shell shell, List<IAppointment> linkedAppointments) {
		StringBuilder message = new StringBuilder(Messages.AppointmentLinkOptionsDialogDescription);
		message.append(Messages.AppointmentLinkOptionsDialogTitel);
		appendAppointmentDetails(message, linkedAppointments);
		String[] options = { Messages.AppointmentLinkOptionsDialogMainAppointmentButton,
				Messages.AppointmentLinkOptionsDialogAllButton, Messages.AppointmentLinkOptionsDialogAbortButton };
		MessageDialog dialog = new MessageDialog(shell, Messages.AppointmentLinkOptionsDialogMove, null,
				message.toString(), MessageDialog.QUESTION, options, 0);
		int result = dialog.open();

		return getMoveActionType(result);
	}

	public static CopyActionType showCopyDialog(Shell shell, List<IAppointment> linkedAppointments) {
		StringBuilder message = new StringBuilder(Messages.AppointmentLinkOptionsDialogDescription);
		message.append(Messages.AppointmentLinkOptionsDialogTitel);
		appendAppointmentDetails(message, linkedAppointments);
		String[] options = { Messages.AppointmentLinkOptionsDialogMainAppointmentButton,
				Messages.AppointmentLinkOptionsDialogAllButton, Messages.AppointmentLinkOptionsDialogAbortButton };
		MessageDialog dialog = new MessageDialog(shell, Messages.AppointmentLinkOptionsDialogCopy, null,
				message.toString(), MessageDialog.QUESTION, options, 0);
		int result = dialog.open();

		return getCopyActionType(result);
	}

	public static DeleteActionType showDeleteDialog(Shell shell, List<IAppointment> linkedAppointments) {
		StringBuilder message = new StringBuilder(Messages.AppointmentLinkOptionsDialogDescription);
		message.append(Messages.AppointmentLinkOptionsDialogTitel);
		appendAppointmentDetails(message, linkedAppointments);
		String[] options = { Messages.AppointmentLinkOptionsDialogKeepButton,
				Messages.AppointmentLinkOptionsDialogDeleteButton };
		MessageDialog dialog = new MessageDialog(shell, Messages.AppointmentLinkOptionsDialogDelete, null,
				message.toString(), MessageDialog.QUESTION, options, 0);
		int result = dialog.open();

		return getDeleteActionType(result);
	}

	private static void appendAppointmentDetails(StringBuilder message, List<IAppointment> appointments) {
		for (IAppointment appt : appointments) {
			message.append(String.format("- %s, %s, %s\n", appt.getStartTime().format(TimeUtil.FULL_GER),
					appt.getSchedule(), appt.getType()));
		}
	}

	private static MoveActionType getMoveActionType(int result) {
		if (result == SWT.DEFAULT) {
			return MoveActionType.CANCEL;
		}
		return result == 0 ? MoveActionType.KEEP_MAIN_ONLY : MoveActionType.MOVE_ALL;
	}

	private static CopyActionType getCopyActionType(int result) {
		if (result == SWT.DEFAULT) {
			return CopyActionType.CANCEL;
		}
		return result == 0 ? CopyActionType.KEEP_MAIN_ONLY : CopyActionType.COPY_ALL;
	}

	private static DeleteActionType getDeleteActionType(int result) {
		if (result == SWT.DEFAULT) {
			return null;
		}
		return result == 0 ? DeleteActionType.KEEP_LINKS : DeleteActionType.DELETE_ALL;
	}
}
