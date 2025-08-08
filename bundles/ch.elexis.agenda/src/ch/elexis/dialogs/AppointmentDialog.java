package ch.elexis.dialogs;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import ch.elexis.agenda.commands.EmailSender;
import ch.elexis.agenda.composite.AppointmentDetailComposite;
import ch.elexis.agenda.composite.EmailComposite.EmailDetails;
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
	private boolean expanded;
	public AppointmentDialog(IAppointment appointment) {
		super(Display.getDefault().getActiveShell());
		CoreUiUtil.injectServicesWithContext(this);
		this.appointment = appointment;
		this.emailSender = new EmailSender(textReplacementService, contextService);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		initializeAppointmentIfNecessary();

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		detailComposite = new AppointmentDetailComposite(container, SWT.NONE, appointment);
		detailComposite.setExpanded(expanded);
		detailComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ContextServiceHolder.get().getRootContext().setNamed("sendMailDialog.taskDescriptor", null);

		return container;
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
		ContextServiceHolder.get().getRootContext().setNamed("mail.alreadySent", null);
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
		eventBroker.post(ElexisEventTopics.EVENT_UPDATE, appointment);
	}

	public void setExpanded(boolean expand) {
		expanded = expand;
	}

  private void sendEmailIfConfirmationChecked() {
		Boolean alreadySent = ContextServiceHolder.get().getNamed("mail.alreadySent").map(Boolean.class::cast)
				.orElse(false);
		if (detailComposite.getEmailCheckboxStatus() && !alreadySent) {
			EmailDetails emailDetails = detailComposite.getEmailDeteils();
			emailSender.sendEmail(emailDetails, appointment);
		}
		ContextServiceHolder.get().getRootContext().setNamed("mail.alreadySent", null);
	}
}