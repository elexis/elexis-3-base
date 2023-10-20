package at.medevit.elexis.agenda.ui.handler;

import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.composite.EmailComposite.EmailDetails;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.mail.TaskUtil;
import ch.elexis.core.mail.ui.handlers.EncounterUtil;
import ch.elexis.core.mail.ui.handlers.OutboxUtil;
import ch.elexis.core.mail.ui.handlers.SendMailHandler;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.tasks.TaskException;
import ch.elexis.core.services.IContext;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.ITextReplacementService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.tasks.model.ITask;
import ch.elexis.core.tasks.model.ITaskDescriptor;

public class EmailSender {

	private static final String SEND_MAIL_COMMAND_NOUI = "ch.elexis.core.mail.ui.sendMailNoUi";

    private ITextReplacementService textReplacementService;
    private IContextService contextService;

    public EmailSender(ITextReplacementService textReplacementService, IContextService contextService) {
        this.textReplacementService = textReplacementService;
        this.contextService = contextService;
    }

	public void sendEmail(EmailDetails emailDetails, IAppointment appointment) {
		Optional<ITaskDescriptor> descriptor = SendMailHandler.taskDescriptor;

		if (descriptor != null && descriptor.isPresent()) {
			processExistingDescriptor(descriptor.get());
		} else {
			sendEmailUsingCommand(emailDetails, appointment);
		}
	}

	private void processExistingDescriptor(ITaskDescriptor descriptor) {
		try {
			ITask task = TaskUtil.executeTaskSync(descriptor, new NullProgressMonitor());
			if (task.isSucceeded()) {
				OutboxUtil.getOrCreateElement(descriptor, true);
				EncounterUtil.addMailToEncounter(descriptor);
			}
		} catch (TaskException e) {
			LoggerFactory.getLogger(TaskUtil.class).error("Error executing mail task", e);
		} finally {
			SendMailHandler.taskDescriptor = Optional.empty();
		}
	}

	private void sendEmailUsingCommand(EmailDetails emailDetails, IAppointment appointment) {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command sendMailCommand = commandService.getCommand(SEND_MAIL_COMMAND_NOUI);
		HashMap<String, String> params = new HashMap<>();

		populateParams(params, emailDetails, appointment);

		try {
			ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand, params);
			PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand, null);
		} catch (Exception ex) {
			throw new RuntimeException(SEND_MAIL_COMMAND_NOUI + " not found", ex);
		}
	}

	private void populateParams(HashMap<String, String> params, EmailDetails emailDetails, IAppointment appointment) {
		String recipientEmail = appointment.getContact().getEmail();
		if (recipientEmail != null) {
			params.put(SEND_MAIL_COMMAND_NOUI + ".to", recipientEmail);
		}

		String emailTemplate = emailDetails.getTemplateContent();
		if (emailTemplate != null) {
			IContext context = contextService.createNamedContext("appointment_reminder_context");
			context.setTyped(appointment);
			context.setTyped(emailDetails.patient());
			String preparedMessageText = textReplacementService.performReplacement(context, emailTemplate);
			params.put(SEND_MAIL_COMMAND_NOUI + ".text", preparedMessageText);
		}

		String subject = emailDetails.getTemplateName();
		if (subject != null) {
			params.put(SEND_MAIL_COMMAND_NOUI + ".subject", subject);
		}

		String savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null);
		if (StringUtils.isEmpty(savedAccount)) {
			savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, null);
		}
		if (savedAccount != null) {
			params.put(SEND_MAIL_COMMAND_NOUI + ".accountid", savedAccount);
		}
	}

}
