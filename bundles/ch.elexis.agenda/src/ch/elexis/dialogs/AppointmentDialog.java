package ch.elexis.dialogs;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.function.Supplier;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import ch.elexis.agenda.commands.EmailSender;
import ch.elexis.agenda.composite.AppointmentDetailComposite;
import ch.elexis.agenda.composite.EmailComposite.EmailDetails;
import ch.elexis.agenda.ui.Messages;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IImage;
import ch.elexis.core.model.agenda.CollisionErrorLevel;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.utils.CoreUtil;
import jakarta.inject.Inject;

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

	private CollisionErrorLevel collisionErrorLevel;

	private boolean initColliding = false;
	private boolean showAllDay = false;
	private boolean scheduleChangeMode = false;


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

		ContextServiceHolder.get().getRootContext().setNamed("sendMailDialog.taskDescriptor", null);

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
		CoreModelServiceHolder.get().refresh(appointment, false, true);
		ContextServiceHolder.get().getRootContext().setNamed("sendMailDialog.taskDescriptor", null);
		ContextServiceHolder.get().getRootContext().setNamed("mail.alreadySent", null);
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

	public void setScheduleChangeMode(boolean mode) {
		this.scheduleChangeMode = mode;
	}

	public void setShowAllDay(boolean showAllDay) {
		this.showAllDay = showAllDay;
	}

	private void sendEmailIfConfirmationChecked() {
		Boolean alreadySent = ContextServiceHolder.get().getNamed("mail.alreadySent").map(Boolean.class::cast)
				.orElse(false);
		if (detailComposite.getEmailCheckboxStatus() && !alreadySent) {
			ContextServiceHolder.get().setNamed("ch.elexis.core.mail.image.elexismailappointmentqr",
					new AppointmentQrSupplier(appointment));
			EmailDetails emailDetails = detailComposite.getEmailDeteils();
			emailSender.sendEmail(emailDetails, appointment);
			ContextServiceHolder.get().setNamed("ch.elexis.core.mail.image.elexismailappointmentqr", null);
		}
		ContextServiceHolder.get().getRootContext().setNamed("mail.alreadySent", null);
	}

	private class AppointmentQrSupplier implements Supplier<IImage> {

		private IAppointment appointment;

		public AppointmentQrSupplier(IAppointment appointment) {
			this.appointment = appointment;
		}

		@Override
		public IImage get() {
			Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			try {
				BitMatrix bitMatrix = qrCodeWriter.encode(appointment.getId(), BarcodeFormat.QR_CODE, 200, 200,
						hintMap);
				int width = bitMatrix.getWidth();
				int height = bitMatrix.getHeight();

				ImageData data = new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						data.setPixel(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
					}
				}
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { data };
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				loader.save(out, SWT.IMAGE_PNG);
				IImage ret = CoreModelServiceHolder.get().create(IImage.class);
				ret.setTitle("elexismailappointmentqr.png");
				ret.setDate(LocalDate.now());
				ret.setImage(out.toByteArray());
				return ret;
			} catch (WriterException e) {
				LoggerFactory.getLogger(AppointmentDialog.class).error("Error creating QR", e); //$NON-NLS-1$
			}
			return null;
		}
	}
}