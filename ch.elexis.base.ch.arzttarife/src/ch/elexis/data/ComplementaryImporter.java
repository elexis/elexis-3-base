package ch.elexis.data;

import java.io.FileInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.data.importer.ComplementaryReferenceDataImporter;

public class ComplementaryImporter extends ImporterPage {
	
	@Override
	public Composite createPage(Composite parent){
		return new FileBasedImporter(parent, this);
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		
		ComplementaryReferenceDataImporter importer = new ComplementaryReferenceDataImporter();
		return importer.performImport(monitor, new FileInputStream(results[0]), null);
	}
	
	@Override
	public String getDescription(){
		return "Komplementärmedizin-Tarif";
	}
	
	@Override
	public String getTitle(){
		return "Komplementärmedizin";
	}
}
