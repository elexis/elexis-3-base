package at.medevit.elexis.bluemedication.ui.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.StructuredSelection;

import at.medevit.elexis.bluemedication.core.BlueMedicationServiceHolder;
import ch.elexis.omnivore.model.IDocumentHandle;

public class BluemedicationPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("isUploadable".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) receiver;
				if (!selection.isEmpty()) {
					Object object = selection.getFirstElement();
					if (object instanceof IDocumentHandle) {
						IDocumentHandle docHandle = (IDocumentHandle) object;
						if (docHandle.getMimeType().toLowerCase().endsWith("pdf")
								|| docHandle.getTitle().toLowerCase().endsWith(".pdf")) {
							return !BlueMedicationServiceHolder.getService().getPendingUploadResult(object).isPresent();
						}
					}
				}
			}
		} else if ("isDownloadable".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) receiver;
				if (!selection.isEmpty()) {
					Object object = selection.getFirstElement();
					if (object instanceof IDocumentHandle) {
						return BlueMedicationServiceHolder.getService().getPendingUploadResult(object).isPresent();
					}
				}
			}
		}
		return false;
	}
}
