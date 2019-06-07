package at.medevit.elexis.emediplan.ui.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.StructuredSelection;

import ch.elexis.omnivore.data.DocHandle;

public class EMediplanPropertyTester extends PropertyTester {
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue){
		if ("isUploadable".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof StructuredSelection) {
				StructuredSelection selection = (StructuredSelection) receiver;
				if (!selection.isEmpty()) {
					Object object = selection.getFirstElement();
					if (object instanceof DocHandle) {
						DocHandle docHandle = (DocHandle) object;
						if (docHandle.getMimeType().endsWith("pdf")
							|| docHandle.getTitle().endsWith(".pdf")) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
