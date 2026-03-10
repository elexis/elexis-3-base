package ch.elexis.omnivore.model.util;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.impl.DocumentDocHandle;

public class Utils {

	public static IVirtualFilesystemHandle getStorageFile(IDocumentHandle docHandle, boolean force) {
		if (docHandle instanceof DocumentDocHandle) {
			DocumentDocHandle impl = (DocumentDocHandle) docHandle;
			return impl.getStorageFile(force);
		}
		return null;
	}
}
