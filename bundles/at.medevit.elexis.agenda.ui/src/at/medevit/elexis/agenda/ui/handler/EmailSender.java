package at.medevit.elexis.agenda.ui.handler;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import at.medevit.elexis.agenda.ui.composite.EmailComposite.EmailDetails;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.IContext;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class EmailSender {

    private ITextReplacementService textReplacementService;
    private IContextService contextService;
    public EmailSender(ITextReplacementService textReplacementService, IContextService contextService) {
        this.textReplacementService = textReplacementService;
        this.contextService = contextService;
    }

	public void sendEmail(EmailDetails emailDetails, IAppointment appointment) {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command sendMailCommand = commandService.getCommand("ch.elexis.core.mail.ui.sendMailNoUi");
		HashMap<String, String> params = new HashMap<String, String>();
		String recipientEmail = appointment.getContact().getEmail();
		if (recipientEmail != null) {
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.to", recipientEmail);
		}
		String emailTemplate = emailDetails.getTemplateContent();
		if (emailTemplate != null) {
			IContext context = contextService.createNamedContext("appointment_reminder_context");
			context.setTyped(appointment);
			context.setTyped(emailDetails.patient());
			String preparedMessageText = textReplacementService.performReplacement(context, emailTemplate);
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.text", preparedMessageText);
		}
		String subject = emailDetails.getTemplateName();
		if (subject != null) {
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.subject", subject);
		}
		String savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null);
		if (StringUtils.isEmpty(savedAccount)) {
			savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, null);
		}
		if (savedAccount != null) {
			params.put("ch.elexis.core.mail.ui.sendMailNoUi.accountid", savedAccount);
		}
		try {
			ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand, params);
			PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand, null);
		} catch (Exception ex) {
			throw new RuntimeException("ch.elexis.core.mail.ui.sendMailNoUi not found", ex);
		}
	}

}
