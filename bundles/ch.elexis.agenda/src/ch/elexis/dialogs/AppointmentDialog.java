package ch.elexis.dialogs;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.function.Supplier;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IImage;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
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