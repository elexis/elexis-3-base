package at.medevit.elexis.emediplan.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.emediplan.core.BlueMedicationServiceHolder;
import at.medevit.elexis.emediplan.core.UploadResult;
import ch.elexis.data.Patient;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.rgw.tools.Result;

public class BlueMedicationUploadHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection selection =
			(StructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof IDocumentHandle) {
				IDocumentHandle docHandle = (IDocumentHandle) object;
				if (docHandle.getMimeType().endsWith("pdf")
					|| docHandle.getTitle().endsWith(".pdf")) {
					Result<UploadResult> result = BlueMedicationServiceHolder.getService()
						.uploadDocument(Patient.load(docHandle.getPatient().getId()),
							docHandle.getAsFile());
					if (result.isOK()) {
						// open the uri of the result
						Program.launch(result.get().getUrl());
						BlueMedicationServiceHolder.getService().addPendingUploadResult(docHandle,
							result.get());
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(),
							"BlueMedication",
							"Beim hochladen der Datei ist ein Fehler aufgetreten.");
					}
				}
			}
		}
		return null;
	}
}
