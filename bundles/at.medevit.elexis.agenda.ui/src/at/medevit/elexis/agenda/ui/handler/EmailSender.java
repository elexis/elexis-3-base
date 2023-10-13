package at.medevit.elexis.agenda.ui.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.AppointmentDetailComposite.EmailDetails;
import ch.elexis.core.mail.MailMessage;
import ch.elexis.core.mail.PreferenceConstants;
import ch.elexis.core.mail.TaskUtil;
import ch.elexis.core.mail.ui.handlers.SendMailTaskWithProgress;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.tasks.model.ITask;
import ch.elexis.core.tasks.model.ITaskDescriptor;
import ch.elexis.core.tasks.model.TaskState;

public class EmailSender {

    private ITextReplacementService textReplacementService;
    private IContextService contextService;
	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

	private static final String KEY_ACCOUNT_ID = "accountId";
	private static final String KEY_MESSAGE = "message";

    public EmailSender(ITextReplacementService textReplacementService, IContextService contextService) {
        this.textReplacementService = textReplacementService;
        this.contextService = contextService;
    }

	public void sendEmail(EmailDetails emailDetails, IAppointment appointment) {
		String recipientEmail = appointment.getContact().getEmail();
		String emailTemplate = emailDetails.getTemplateContent();
		String subject = emailDetails.getTemplateName();
		String savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null);
		savedAccount = StringUtils.isEmpty(savedAccount)
				? ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, null)
				: savedAccount;
		Map<String, Serializable> runContext = new HashMap<>();
		runContext.put(KEY_ACCOUNT_ID, savedAccount);
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("to", recipientEmail);
		messageMap.put("subject", subject);
		contextService.getTyped(getClass());
		contextService.setTyped(appointment);
		String preparedMessageText = textReplacementService.performReplacement(contextService.getRootContext(),
				emailTemplate);
		messageMap.put("text", preparedMessageText);
		runContext.put(KEY_MESSAGE, (Serializable) messageMap);
		Shell activeShell = Display.getDefault().getActiveShell();
		MailMessage message = MailMessage.fromMap(messageMap);
		Optional<ITaskDescriptor> taskDescriptor = TaskUtil.createSendMailTaskDescriptor(savedAccount, message);
		taskDescriptor.ifPresent(descriptor -> {
			ITask task = new SendMailTaskWithProgress().execute(activeShell, descriptor);
			if (task.getState() == TaskState.COMPLETED) {
				logger.info("Email successfully sent!");
			} else {
				logger.error("Email sending failed!");
			}
		});
	}
}
