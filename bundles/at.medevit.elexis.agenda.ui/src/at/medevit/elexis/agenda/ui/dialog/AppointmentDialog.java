package at.medevit.elexis.agenda.ui.dialog;

import java.time.LocalDateTime;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.composite.AppointmentDetailComposite;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class AppointmentDialog extends Dialog {
	
	private AppointmentDetailComposite detailComposite;
	
	private IAppointment appointment;
	
	public AppointmentDialog(IAppointment appointment){
		super(Display.getDefault().getActiveShell());
		this.appointment = appointment;
	}
	
	@Override
	protected Control createContents(Composite parent){
		if (appointment == null) {
			appointment = CoreModelServiceHolder.get().create(IAppointment.class);
			appointment.setStartTime(LocalDateTime.now());
		}
		detailComposite = new AppointmentDetailComposite(parent, SWT.NONE, appointment);
		return super.createContents(parent);
		
	}
	
	@Override
	protected void okPressed(){
		if (appointment != null) {
			// save appointment
			CoreModelServiceHolder.get().save(detailComposite.setToModel());
		}
		super.okPressed();
	}
	
}
