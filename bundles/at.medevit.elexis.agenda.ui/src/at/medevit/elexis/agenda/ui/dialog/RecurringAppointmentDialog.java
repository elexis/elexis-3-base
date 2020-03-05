package at.medevit.elexis.agenda.ui.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.model.IAppointment;

public class RecurringAppointmentDialog extends TitleAreaDialog {
	
	private IAppointment appointment;
	
	public RecurringAppointmentDialog(IAppointment appointment){
		super(Display.getDefault().getActiveShell());
		
		this.appointment = appointment;
	}
}
