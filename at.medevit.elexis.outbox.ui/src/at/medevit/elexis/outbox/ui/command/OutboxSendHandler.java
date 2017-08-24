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
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.outbox.model.IOutboxElementService.State;
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
					"Es dürfen nur Outbox Elemente eines Patienten ausgewählt werden.");
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
		File tmpDir = CoreHub.getTempDir();
		attachmentsFolder = new File(tmpDir, "_outbox" + System.currentTimeMillis() + "_");
		if (!attachmentsFolder.exists()) {
			attachmentsFolder.mkdir();
		}
		
		for (Object iOutboxElement : iOutboxElements) {
			if (iOutboxElement instanceof OutboxElement) {
				Optional<File> tmpFile = createTempFile((OutboxElement) iOutboxElement);
				if (tmpFile.isPresent()) {
					File file = tmpFile.get();
					attachments.add(file);
				}
			}
		}
		if (!attachments.isEmpty()) {
			ICommandService commandService = (ICommandService) HandlerUtil
				.getActiveWorkbenchWindow(event).getService(ICommandService.class);
			try {
				String attachmentsString = getAttachmentsString(attachments);
				if (event.getCommand().getId().endsWith("sendAsMailXDM")) {
					// send as xdm
					Object obj = createXDM(patient, commandService, attachmentsString);
					if (obj instanceof String) {
						sendMailWithXdm(patient, iOutboxElements, commandService, (String) obj);
					}
					else {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Fehler",
							"Der XDM Container konnte nicht erzeugt werden.\nBitte überprüfen Sie die Log Datei.");
					}
				} else {
					// send as files
					sendMailWithFiles(patient, iOutboxElements, commandService, attachmentsString);
					
				}
			} catch (Exception ex) {
				throw new RuntimeException("ch.elexis.core.mail.ui.sendMail not found", ex);
			}
		}
		// cleanup
		removeTempAttachments();
	}

	private void sendMailWithFiles(Patient patient, List<?> iOutboxElements,
		ICommandService commandService, String attachmentsString)
		throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException{
		if (openSendMailDlg(patient, iOutboxElements, commandService,
			attachmentsString)) {
			for (Object iOutboxElement : iOutboxElements) {
				if (iOutboxElement instanceof OutboxElement) {
					OutboxServiceComponent.getService().changeOutboxElementState(
						(OutboxElement) iOutboxElement, State.SENT);
				}
			}
		}
	}

	private void sendMailWithXdm(Patient patient, List<?> iOutboxElements,
		ICommandService commandService,
		String retInfo)
		throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException{
		String[] retInfos = retInfo.split(":::");
		// a retInfo must contain at least one xdm and one attachment
		if (retInfos.length > 1
			&& openSendMailDlg(patient, iOutboxElements, commandService, retInfos[0])) {
			
			StringBuilder warnings = new StringBuilder();
			for (Object iOutboxElement : iOutboxElements) {
				if (iOutboxElement instanceof OutboxElement) {
					OutboxElement outboxElement = (OutboxElement) iOutboxElement;
					String lblOutBoxElement = outboxElement.getLabel();
					
					// check if outbox element is sent
					boolean outboxElementSent = false;
					for (String info : retInfos) {
						if (info.endsWith(lblOutBoxElement)) {
							outboxElementSent = true;
						}
					}
					if (outboxElementSent) {
						OutboxServiceComponent.getService()
							.changeOutboxElementState((OutboxElement) iOutboxElement, State.SENT);
					} else {
						warnings.append("\n");
						warnings.append(lblOutBoxElement);
					}
				}
			}
			if (warnings.length() > 0) {
				MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warnung",
					"Folgende Outbox Elemente konnten nicht versendet werden:\n" + warnings);
			}
		}
	}
	
	private Object createXDM(Patient patient, ICommandService commandService,
		String attachmentsString)
		throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException{
		Command xdmCommand = commandService
			.getCommand("at.medevit.elexis.ehc.ui.vacdoc.CreateXdmHandler");
		if (xdmCommand.isDefined()) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("at.medevit.elexis.ehc.ui.vacdoc.tmp.dir",
				attachmentsFolder.getAbsolutePath());
			params.put("at.medevit.elexis.ehc.ui.vacdoc.attachments",
				attachmentsString);
			if (patient != null) {
				params.put("at.medevit.elexis.ehc.ui.vacdoc.patient.id", patient.getId());
			}
			ParameterizedCommand parametrizedCommmand =
				ParameterizedCommand.generateCommand(xdmCommand, params);
			if (parametrizedCommmand != null) {
				Object obj = PlatformUI.getWorkbench().getService(IHandlerService.class)
					.executeCommand(parametrizedCommmand, null);
				return obj;
			}
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Fehler",
				"Es wurde kein Plugin zum erstellen von XDM Dateien gefunden.");
		}
		
		return null;
	}

	private boolean openSendMailDlg(Patient patient, List<?> iOutboxElements,
		ICommandService commandService,
		String attachmentsString)
		throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException{
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
		
		Object obj = PlatformUI.getWorkbench().getService(IHandlerService.class)
			.executeCommand(parametrizedCommmand, null);
		return Boolean.TRUE.equals(obj);
	}
	
	private Optional<File> createTempFile(OutboxElement outboxElement){
		try {
			return OutboxServiceComponent.getService().createTempFileWithContents(attachmentsFolder,
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
				sb.append(":::");
			}
			sb.append(file.getAbsolutePath());
		}
		return sb.toString();
	}
}