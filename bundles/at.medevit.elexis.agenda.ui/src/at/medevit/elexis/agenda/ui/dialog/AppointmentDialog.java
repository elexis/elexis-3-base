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
import at.medevit.elexis.agenda.ui.composite.EmailComposite.EmailDetails;
import at.medevit.elexis.agenda.ui.handler.EmailSender;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class AppointmentDialog extends Dialog {

	private IAppointment appointment;

	@Inject
	IContextService contextService;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private ITextReplacementService textReplacementService;

	private AppointmentDetailComposite detailComposite;
	private EmailSender emailSender;

	public AppointmentDialog(IAppointment appointment) {
		super(Display.getDefault().getActiveShell());
		CoreUiUtil.injectServicesWithContext(this);
		this.appointment = appointment;
		this.emailSender = new EmailSender(textReplacementService, contextService);
	}
	@Override
	protected Control createContents(Composite parent) {
		initializeAppointmentIfNecessary();
		detailComposite = new AppointmentDetailComposite(parent, SWT.NONE, appointment);
		return super.createContents(parent);
	}
	@Override
	protected void okPressed() {
		saveAndReloadAppointment();
		sendEmailIfConfirmationChecked();
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		ContextServiceHolder.get().getRootContext().setNamed("sendMailDialog.taskDescriptor", null);
		super.cancelPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void initializeAppointmentIfNecessary() {
		if (appointment == null) {
			appointment = CoreModelServiceHolder.get().create(IAppointment.class);
			appointment.setStartTime(LocalDateTime.now());
		}
	}

	private void saveAndReloadAppointment() {
		if (appointment != null) {
			CoreModelServiceHolder.get().save(detailComposite.setToModel());
		}
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
	}

	private void sendEmailIfConfirmationChecked() {
		if (detailComposite.getEmailCheckboxStatus()) {
			EmailDetails emailDetails = detailComposite.getEmailDeteils();
			emailSender.sendEmail(emailDetails, appointment);
		}
	}
}