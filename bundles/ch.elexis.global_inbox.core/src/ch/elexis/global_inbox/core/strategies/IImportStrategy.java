package ch.elexis.global_inbox.core.strategies;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

public interface IImportStrategy {
	/**
	 * Attempts to import the file.
	 * 
	 * @param file The file handle to be imported
	 * @return true if imported, otherwise false
	 */
	boolean importFile(IVirtualFilesystemHandle file);
}