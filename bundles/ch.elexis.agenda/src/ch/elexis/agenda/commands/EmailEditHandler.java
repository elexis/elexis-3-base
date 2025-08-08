package ch.elexis.agenda.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.mail.AttachmentsUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class EmailEditHandler {

	private static final String MAIL_COMMAND_ID = "ch.elexis.core.mail.ui.sendMail";
	private static final String MAIL_TO_PARAM = "ch.elexis.core.mail.ui.sendMail.to";
	private static final String MAIL_ACCOUNTID_PARAM = "ch.elexis.core.mail.ui.sendMail.accountid";
	private static final String MAIL_TEXT_PARAM = "ch.elexis.core.mail.ui.sendMail.text";
	private static final String MAIL_SUBJECT_PARAM = "ch.elexis.core.mail.ui.sendMail.subject";
	private static final String MAIL_DO_SEND_PARAM = "ch.elexis.core.mail.ui.sendMail.doSend";
	private static final String MAIL_DOCUMENTS_PARAM = "ch.elexis.core.mail.ui.sendMail.documents";
	private static final String MAIL_PATIENT_ID = "ch.elexis.core.mail.ui.sendMail.patId";

	public void openSendMailDialogWithContent(IAppointment appointment, IContact pat, String preparedMessageText,
			String subject) {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command sendMailCommand = commandService.getCommand(MAIL_COMMAND_ID);

		HashMap<String, String> params = new HashMap<>();
		if (pat instanceof IContact && StringUtils.isNotBlank(pat.getEmail())) {
			params.put(MAIL_TO_PARAM, ((IContact) pat).getEmail());
		} else if (appointment.getContact() != null && StringUtils.isNotBlank(appointment.getContact().getEmail())) {
			params.put(MAIL_TO_PARAM, appointment.getContact().getEmail());
		}
		if (preparedMessageText != null) {
			params.put(MAIL_TEXT_PARAM, preparedMessageText);
		}
		if (subject != null) {
			params.put(MAIL_SUBJECT_PARAM, subject);
		}

		if (pat instanceof IContact && StringUtils.isNotBlank(pat.getId())) {

			params.put(MAIL_PATIENT_ID, ((IContact) pat).getId());
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
		Optional<IDocumentLetter> selectedDocument = ContextServiceHolder.get().getTyped(IDocumentLetter.class);
		if (selectedDocument.isPresent()) {
			List<?> iDocuments = Collections.singletonList(selectedDocument.get());
			@SuppressWarnings("unchecked")
			String documentsString = AttachmentsUtil.getDocumentsString((List<IDocument>) (List<?>) iDocuments);
			params.put(MAIL_DOCUMENTS_PARAM, documentsString); // $NON-NLS-1$
		}

		try {
			ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(sendMailCommand, params);
			PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand, null);
		} catch (Exception ex) {
			throw new RuntimeException(MAIL_COMMAND_ID + " not found", ex);
		}
	}
}