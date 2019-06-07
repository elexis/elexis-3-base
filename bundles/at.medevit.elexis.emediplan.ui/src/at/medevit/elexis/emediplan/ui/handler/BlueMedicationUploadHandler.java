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
import ch.elexis.omnivore.data.DocHandle;
import ch.rgw.tools.Result;

public class BlueMedicationUploadHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection selection =
			(StructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof DocHandle) {
				DocHandle docHandle = (DocHandle) object;
				if (docHandle.getMimeType().endsWith("pdf")
					|| docHandle.getTitle().endsWith(".pdf")) {
					Result<String> result = BlueMedicationServiceHolder.getService().uploadDocument(
						docHandle.getPatient(),
						docHandle.createTemporaryFile(docHandle.getTitle()));
					if (result.isOK()) {
						// open the uri of the result
						Program.launch(result.get());
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
