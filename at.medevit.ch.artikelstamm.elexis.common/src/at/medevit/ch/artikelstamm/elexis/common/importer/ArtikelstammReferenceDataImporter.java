package at.medevit.ch.artikelstamm.elexis.common.importer;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;

public class ArtikelstammReferenceDataImporter extends AbstractReferenceDataImporter {
	
	@Override
	public @NonNull Class<?> getReferenceDataTypeResponsibleFor(){
		return ArtikelstammItem.class;
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, @NonNull InputStream input,
		@Nullable Integer newVersion){
		return ArtikelstammImporter.performImport(monitor, input, newVersion);	
	}
	
	@Override
	public int getCurrentVersion(){
		return ArtikelstammItem.getCurrentVersion();
	}
	
}
