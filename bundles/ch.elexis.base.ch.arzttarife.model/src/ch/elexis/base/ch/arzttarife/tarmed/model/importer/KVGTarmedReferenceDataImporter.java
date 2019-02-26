package ch.elexis.base.ch.arzttarife.tarmed.model.importer;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.interfaces.IReferenceDataImporter;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=tarmed_kvg_34")
public class KVGTarmedReferenceDataImporter extends TarmedReferenceDataImporter
		implements IReferenceDataImporter {
	
	@Override
	protected String getLaw(){
		return "KVG";
	}
}
