package ch.elexis.base.ch.arzttarife.physio.model.importer;

import org.osgi.service.component.annotations.Component;

import ch.elexis.core.interfaces.IReferenceDataImporter;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=physio_kvg")
public class KVGPhysioReferenceDataImporter extends PhysioReferenceDataImporter implements IReferenceDataImporter {

	@Override
	protected String getLaw() {
		return "KVG";
	}
}
