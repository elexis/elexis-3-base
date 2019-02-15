package ch.elexis.labortarif2009.data;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.interfaces.IReferenceDataImporter;

@Component
public class ReferenceDataImporterHolder {
	
	private static IReferenceDataImporter referenceDataImporter;
	
	@Reference(target = "(" + IReferenceDataImporter.REFERENCEDATAID + "=analysenliste)")
	public void setModelService(IReferenceDataImporter referenceDataImporter){
		ReferenceDataImporterHolder.referenceDataImporter = referenceDataImporter;
	}
	
	public static IReferenceDataImporter get(){
		if (referenceDataImporter == null) {
			throw new IllegalStateException("No IReferenceDataImporter available");
		}
		return referenceDataImporter;
	}
}
