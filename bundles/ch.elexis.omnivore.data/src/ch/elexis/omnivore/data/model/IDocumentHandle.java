package ch.elexis.omnivore.data.model;

import ch.elexis.core.model.IDocument;

public interface IDocumentHandle extends IDocument {
	
	public boolean isCategory();
	
	public boolean exportToFileSystem();
	
}
