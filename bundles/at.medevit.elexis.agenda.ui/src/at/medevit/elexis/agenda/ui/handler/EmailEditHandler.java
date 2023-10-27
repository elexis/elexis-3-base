package at.medevit.elexis.agenda.ui.handler;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class EmailEditHandler {

	private static final String MAIL_COMMAND_ID = "ch.elexis.core.mail.ui.sendMail";
	private static final String MAIL_TO_PARAM = "ch.elexis.core.mail.ui.sendMail.to";
	private static final String MAIL_ACCOUNTID_PARAM = "ch.elexis.core.mail.ui.sendMail.accountid";
	private static final String MAIL_TEXT_PARAM = "ch.elexis.core.mail.ui.sendMail.text";
	private static final String MAIL_SUBJECT_PARAM = "ch.elexis.core.mail.ui.sendMail.subject";
	private static final String MAIL_DO_SEND_PARAM = "ch.elexis.core.mail.ui.sendMail.doSend";


	public void openSendMailDialogWithContent(IAppointment appointment, Object pat, String preparedMessageText,
			String subject) {

		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command sendMailCommand = commandService.getCommand(MAIL_COMMAND_ID);

		HashMap<String, String> params = new HashMap<>();
		if (appointment.getContact().getEmail() != null) {
			params.put(MAIL_TO_PARAM, appointment.getContact().getEmail());
		}
		if (preparedMessageText != null) {
			params.put(MAIL_TEXT_PARAM, preparedMessageText);
		}
		if (subject != null) {
			params.put(MAIL_SUBJECT_PARAM, subject);
		}
		params.put(MAIL_DO_SEND_PARAM, Boolean.TRUE.toString());
		String savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT_APPOINTMENT,
				null);
		if (StringUtils.isEmpty(savedAccount)) {
			savedAccount = ConfigServiceHolder.get().get(PreferenceConstants.PREF_DEFAULT_MAIL_ACCOUNT, null);
		}
		if (savedAccount != null) {
			params.put(MAIL_ACCOUNTID_PARAM, savedAccount);
		}
		try {
			ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand, params);
			PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand, null);
		} catch (Exception ex) {
			throw new RuntimeException(MAIL_COMMAND_ID + " not found", ex);
		}
	}
}