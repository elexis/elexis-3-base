package ch.elexis.omnivore.model;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

public interface IDocumentHandle extends IDocument {

	public boolean isCategory();

	public IVirtualFilesystemHandle getHandle();

	public boolean exportToFileSystem();
}
