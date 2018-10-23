package ch.elexis.base.ch.arzttarife.tarmed.importer;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.elexis.core.interfaces.AbstractReferenceDataImporter;

public class TarmedReferenceDataImporter extends AbstractReferenceDataImporter {
	
	public static final String CFG_REFERENCEINFO_AVAILABLE =
		"ch.elexis.data.importer.TarmedReferenceDataImporter/referenceinfoavailable";

	@Override
	public Class<?> getReferenceDataTypeResponsibleFor(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus performImport(IProgressMonitor ipm, InputStream input, Integer newVersion){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentVersion(){
		// TODO Auto-generated method stub
		return 0;
	}
}
