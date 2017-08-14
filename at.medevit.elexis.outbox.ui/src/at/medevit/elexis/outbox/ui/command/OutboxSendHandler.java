package at.medevit.elexis.outbox.ui.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.outbox.model.OutboxElement;
import at.medevit.elexis.outbox.ui.OutboxServiceComponent;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;

public class OutboxSendHandler extends AbstractHandler implements IHandler {
	
	private File attachmentsFolder;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection
			&& !((StructuredSelection) selection).isEmpty()) {
			List<?> iOutboxElements = ((StructuredSelection) selection).toList();
			Set<String> patientIds = new HashSet<>();
			for (Object iOutboxElement : iOutboxElements) {
				
				if (iOutboxElement instanceof OutboxElement) {
					Patient p = ((OutboxElement) iOutboxElement).getPatient();
					if (p != null && p.exists()) {
						patientIds.add(p.getId());
					}
				}
			}
			// precondition
			if (patientIds.size() > 1) {
				MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warnung",
					"Es dürfen nur Outbox Elemente eines Patienten zum Versenden ausgewählt werden.");
				return null;
			}
			String patientId = patientIds.stream().findFirst().orElse(null);
			sendOutboxElements(event, patientId != null ? Patient.load(patientId) : null,
				iOutboxElements);
		}
		return null;
	}

	private void sendOutboxElements(ExecutionEvent event, Patient patient, List<?> iOutboxElements){
		List<File> attachments = new ArrayList<>();
		for (Object iOutboxElement : iOutboxElements) {
			if (iOutboxElement instanceof OutboxElement)
			{
				Optional<File> tmpFile = getTempFile((OutboxElement) iOutboxElement);
				if (tmpFile.isPresent()) {
					attachments.add(tmpFile.get());
				}
			}
		}
		if (!attachments.isEmpty()) {
			ICommandService commandService = (ICommandService) HandlerUtil
				.getActiveWorkbenchWindow(event).getService(ICommandService.class);
			try {
				String attachmentsString = getAttachmentsString(attachments);
				Command sendMailCommand =
					commandService.getCommand("ch.elexis.core.mail.ui.sendMail");
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("ch.elexis.core.mail.ui.sendMail.attachments", attachmentsString);
				if (patient != null) {
					params.put("ch.elexis.core.mail.ui.sendMail.subject",
						"Patient: " + patient.getLabel());
				}
				
				ParameterizedCommand parametrizedCommmand =
					ParameterizedCommand.generateCommand(sendMailCommand, params);
				parametrizedCommmand.executeWithChecks(null, null);
			} catch (Exception ex) {
				throw new RuntimeException("ch.elexis.core.mail.ui.sendMail not found", ex);
			}
		}
		removeTempAttachments();
	}
	
	private Optional<File> getTempFile(OutboxElement outboxElement){
		try {
			File tmpDir = CoreHub.getTempDir();
			attachmentsFolder = new File(tmpDir, "_outbox" + System.currentTimeMillis() + "_");
			attachmentsFolder.mkdir();
			
			return OutboxServiceComponent.getService().getTempFileWithContents(attachmentsFolder,
				outboxElement);
		} catch (IOException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Fehler",
				"Fehler beim versenden der Dokumente.");
			LoggerFactory.getLogger(getClass()).error("Could not export Outbox.", e);
		}
		return Optional.empty();
	}
	
	private void removeTempAttachments(){
		if (attachmentsFolder != null) {
			try {
				FileUtils.deleteDirectory(attachmentsFolder);
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Cannot delete attachments folder at {}.",
					attachmentsFolder.getAbsolutePath(), e);
			}
		}
	}
	
	private String getAttachmentsString(List<File> attachments){
		StringBuilder sb = new StringBuilder();
		for (File file : attachments) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(file.getAbsolutePath());
		}
		return sb.toString();
	}
}