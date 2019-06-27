package at.medevit.elexis.emediplan.ui.handler;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import at.medevit.elexis.emediplan.Startup;
import at.medevit.elexis.emediplan.core.BlueMedicationServiceHolder;
import at.medevit.elexis.emediplan.core.UploadResult;
import ch.elexis.omnivore.data.DocHandle;
import ch.rgw.tools.Result;

public class BlueMedicationDownloadHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		StructuredSelection selection =
			(StructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty()) {
			Object object = selection.getFirstElement();
			if (object instanceof DocHandle) {
				DocHandle docHandle = (DocHandle) object;
					Optional<UploadResult> pending = BlueMedicationServiceHolder.getService().getPendingUploadResult(docHandle);
				if (pending.isPresent()) {
					Result<String> emediplan = BlueMedicationServiceHolder.getService()
						.downloadEMediplan(pending.get().getId());
					if (emediplan.isOK()) {
						Startup.openEMediplanImportDialog(emediplan.get(),
							docHandle.getPatient().getId());
						BlueMedicationServiceHolder.getService()
							.removePendingUploadResult(docHandle);
					}
				}
			}
		}
		return null;
	}
}
