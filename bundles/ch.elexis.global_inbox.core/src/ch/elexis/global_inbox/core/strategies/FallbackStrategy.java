package ch.elexis.global_inbox.core.strategies;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;

public class FallbackStrategy implements IImportStrategy {
	private final IImportStrategy first;
	private final IImportStrategy second;

	public FallbackStrategy(IImportStrategy first, IImportStrategy second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean importFile(IVirtualFilesystemHandle file) {
		if (first.importFile(file)) {
			return true;
		}
		return second.importFile(file);
	}
}