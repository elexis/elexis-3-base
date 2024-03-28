package at.medevit.elexis.bluemedication.ui.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.bluemedication.core.BlueMedicationServiceHolder;
import at.medevit.elexis.bluemedication.core.UploadResult;
import at.medevit.elexis.bluemedication.ui.document.DocumentStoreServiceHolder;
import ch.elexis.core.documents.DocumentStore;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.rgw.tools.Result;

public class BlueMedicationDownloadHandler extends AbstractHandler implements IHandler {

	private ICommandService commandService;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		commandService = (ICommandService) HandlerUtil.getActiveWorkbenchWindow(event)
				.getService(ICommandService.class);

		StructuredSelection selection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof IDocumentHandle) {
				IDocumentHandle docHandle = (IDocumentHandle) object;
				Optional<UploadResult> pending = BlueMedicationServiceHolder.getService()
						.getPendingUploadResult(docHandle);
				if (pending.isPresent()) {
					if ("chmed".equals(pending.get().getTyp())) {
						downloadAndImportChmed(docHandle, pending.get());
					} else if ("pdf".equals(pending.get().getTyp())) {
						downloadAndImportPdf(docHandle, pending.get());
					}
					// always remove pending upload
					BlueMedicationServiceHolder.getService().removePendingUploadResult(docHandle);
				}
			}
		}
		return null;
	}

	private void downloadAndImportPdf(IDocumentHandle docHandle, UploadResult uploadResult) {
		Result<String> pdf = BlueMedicationServiceHolder.getService().downloadExtendedPdf(uploadResult);
		if (pdf.isOK()) {
			DocumentStore documentsService = DocumentStoreServiceHolder.getService();
			// debug code, save to new document
			// IDocument extDocument = documentsService.createDocument(
			// "ch.elexis.data.store.omnivore",
			// docHandle.getPatient().getId(), "ext" + docHandle.getTitle() + ".pdf",
			// docHandle.getCategory().getName());
			// documentsService.saveDocument(extDocument,
			// new FileInputStream(new File(pdf.get())));
			try (InputStream fis = new FileInputStream(new File(pdf.get()))) {
				documentsService.saveDocument(docHandle, fis);
			} catch (ElexisException | IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error saving pdf", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Beim speichern des pdf ist ein Fehler aufgetreten. Bitte starten sie den Vorgang neu.");
			}
		} else {
			LoggerFactory.getLogger(getClass()).error("Error downloading pdf");
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Beim download des pdf ist ein Fehler aufgetreten. Bitte starten sie den Vorgang neu.");
		}
	}

	private void downloadAndImportChmed(IDocumentHandle docHandle, UploadResult uploadResult)
			throws ExecutionException {
		Result<String> emediplan = BlueMedicationServiceHolder.getService().downloadEMediplan(uploadResult);
		if (emediplan.isOK()) {
			if (uploadResult.isUploadedMediplan()) {
				if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Import",
						"Möchten Sie die aktuelle Medikation mit dem erstellten eMediplan überschreiben?")) {
					Command directImportCommand = commandService
							.getCommand("at.medevit.elexis.emediplan.ui.directImport");

					HashMap<String, String> params = new HashMap<String, String>();

					params.put("at.medevit.elexis.emediplan.ui.directImport.parameter.emediplan", emediplan.get());
					params.put("at.medevit.elexis.emediplan.ui.directImport.parameter.patientid",
							docHandle.getPatient().getId());
					params.put("at.medevit.elexis.emediplan.ui.directImport.parameter.stopreason",
							"BlueMedication EMediplan Import");

					ParameterizedCommand parametrizedCommmand = ParameterizedCommand
							.generateCommand(directImportCommand, params);

					try {
						PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand,
								null);
					} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Beim import des eMediplan ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
					}
				}
			} else {
				Command openImportCommand = commandService.getCommand("at.medevit.elexis.emediplan.ui.openImport");

				HashMap<String, String> params = new HashMap<String, String>();

				params.put("at.medevit.elexis.emediplan.ui.openImport.parameter.emediplan", emediplan.get());
				params.put("at.medevit.elexis.emediplan.ui.openImport.parameter.patientid",
						docHandle.getPatient().getId());

				ParameterizedCommand parametrizedCommmand = ParameterizedCommand.generateCommand(openImportCommand,
						params);

				try {
					PlatformUI.getWorkbench().getService(IHandlerService.class).executeCommand(parametrizedCommmand,
							null);
				} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Beim öffnen des eMediplan Import ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
				}
			}
		} else {
			LoggerFactory.getLogger(getClass()).error("Error on download", emediplan.toString());
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Beim download ist ein Fehler aufgetreten. Bitte starten sie den Abgleich neu.");
		}
	}
}
