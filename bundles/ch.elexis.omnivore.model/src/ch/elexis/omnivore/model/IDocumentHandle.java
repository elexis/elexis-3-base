package ch.elexis.omnivore.model;

import java.io.File;

import ch.elexis.core.model.IDocument;

public interface IDocumentHandle extends IDocument {
	
	public boolean isCategory();
	
	public File getAsFile();
	
	public boolean exportToFileSystem();
}
