package at.medevit.elexis.agenda.ui.dialog;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.composite.AppointmentDetailComposite;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class AppointmentDialog extends Dialog {
	
	private AppointmentDetailComposite detailComposite;
	
	private IAppointment appointment;
	
	@Inject
	private IEventBroker eventBroker;
	
	public AppointmentDialog(IAppointment appointment){
		super(Display.getDefault().getActiveShell());
		CoreUiUtil.injectServicesWithContext(this);
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
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
		super.okPressed();
	}
	
}
