package at.medevit.elexis.agenda.ui.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.composite.AppointmentDetailComposite;
import ch.elexis.core.model.IAppointment;

public class AppointmentDialog extends TitleAreaDialog {
	
	private AppointmentDetailComposite detailComposite;
	
	private IAppointment appointment;
	
	public AppointmentDialog(IAppointment appointment){
		super(Display.getDefault().getActiveShell());
		
		this.appointment = appointment;
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite ret = (Composite) super.createContents(parent);
		detailComposite = new AppointmentDetailComposite(ret, SWT.NONE);
		if (appointment != null) {
			detailComposite.setAppointment(appointment);
		}
		return ret;
	}
	
}
