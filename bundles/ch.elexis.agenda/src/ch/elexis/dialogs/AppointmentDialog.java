package ch.elexis.dialogs;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.agenda.commands.EmailSender;
import ch.elexis.agenda.composite.AppointmentDetailComposite;
import ch.elexis.agenda.composite.EmailComposite.EmailDetails;
import ch.elexis.agenda.ui.Messages;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.agenda.CollisionErrorLevel;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.utils.CoreUtil;

public class AppointmentDialog extends Dialog {

	private IAppointment appointment;

	@Inject
	@Service(filterExpression = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Inject
	private IContextService contextService;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private ITextReplacementService textReplacementService;

	private AppointmentDetailComposite detailComposite;
	private EmailSender emailSender;
	private boolean expanded;

	private CollisionErrorLevel collisionErrorLevel;

	private boolean initColliding = false;
	private boolean showAllDay = false;
	private boolean scheduleChangeMode = false;


	public AppointmentDialog(IAppointment appointment) {
		super(Display.getDefault().getActiveShell());
		CoreUiUtil.injectServicesWithContext(this);
		coreModelService.refresh(appointment);
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
		detailComposite.setShowAllDay(showAllDay);
		detailComposite.setExpanded(expanded);
		detailComposite.setScheduleChangeMode(scheduleChangeMode);
		detailComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (collisionErrorLevel != null) {
			detailComposite.setCollisionErrorLevel(collisionErrorLevel, (colliding) -> {
				if (collisionErrorLevel == CollisionErrorLevel.ERROR) {
					Button okButton = getButton(IDialogConstants.OK_ID);
					if (okButton != null) {
						if (colliding) {
							okButton.setEnabled(false);
							okButton.setText(Messages.AgendaUI_DayOverView_date_collision);
						} else {
							okButton.setEnabled(true);
							okButton.setText(IDialogConstants.OK_LABEL);
						}
					} else {
						initColliding = colliding;
					}
				}
			});
		}

		contextService.getRootContext().setNamed("sendMailDialog.taskDescriptor", null);

		return container;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control ret = super.createButtonBar(parent);
		if (initColliding) {
			Button okButton = getButton(IDialogConstants.OK_ID);
			if (okButton != null) {
				okButton.setEnabled(false);
				okButton.setText(Messages.AgendaUI_DayOverView_date_collision);
			}
		}
		return ret;
	}

	@Override
	protected Point getInitialSize() {
		if (detailComposite != null && expanded) {
			if (CoreUtil.isLinux()) {
				return new Point(1000, 900);
			} else {
				return new Point(1000, 800);
			}
		} else {
			if (CoreUtil.isLinux()) {
				return new Point(800, 560);
			} else {
				return new Point(700, 490);
			}
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (CoreUtil.isLinux()) {
			newShell.setMinimumSize(800, 560);
		} else {
			newShell.setMinimumSize(700, 500);
		}
		newShell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Point size = newShell.getSize();
				if (size.x >= 880 && size.y >= 750) {
					if (!expanded) {
						expanded = true;
						detailComposite.setExpanded(true);
						newShell.layout(true, true);
					}
				} else {
					if (expanded) {
						expanded = false;
						detailComposite.setExpanded(false);
						newShell.layout(true, true);
					}
				}
			}
		});
	}

	@Override
	protected void okPressed() {
		saveAndReloadAppointment();
		sendEmailIfConfirmationChecked();
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		coreModelService.refresh(appointment, false, true);
		contextService.getRootContext().setNamed("sendMailDialog.taskDescriptor", null);
		contextService.getRootContext().setNamed("mail.alreadySent", null);
		super.cancelPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	public void setCollisionErrorLevel(final CollisionErrorLevel level) {
		this.collisionErrorLevel = level;
	}

	private void initializeAppointmentIfNecessary() {
		if (appointment == null) {
			appointment = coreModelService.create(IAppointment.class);
			appointment.setStartTime(LocalDateTime.now());
		}
	}

	private void saveAndReloadAppointment() {
		if (appointment != null) {
			coreModelService.save(detailComposite.setToModel());
		}
		eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
		eventBroker.post(ElexisEventTopics.EVENT_UPDATE, appointment);
	}

	public void setExpanded(boolean expand) {
		expanded = expand;
	}

	public void setScheduleChangeMode(boolean mode) {
		this.scheduleChangeMode = mode;
	}

	public void setShowAllDay(boolean showAllDay) {
		this.showAllDay = showAllDay;
	}

	private void sendEmailIfConfirmationChecked() {
		Boolean alreadySent = contextService.getNamed("mail.alreadySent").map(Boolean.class::cast)
				.orElse(false);
		if (detailComposite.getEmailCheckboxStatus() && !alreadySent) {
			EmailDetails emailDetails = detailComposite.getEmailDeteils();
			emailSender.sendEmail(emailDetails, appointment);
		}
		contextService.getRootContext().setNamed("mail.alreadySent", null);
	}
}