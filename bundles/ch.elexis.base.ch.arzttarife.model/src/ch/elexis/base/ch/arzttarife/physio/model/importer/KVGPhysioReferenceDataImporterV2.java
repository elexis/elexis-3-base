package ch.elexis.base.ch.arzttarife.physio.model.importer;

/**
 * KVG variant of the {@link PhysioReferenceDataImporterV2}. Not registered as a
 * component, used by {@link KVGPhysioReferenceDataImporter} to import the new
 * csv format.
 */
public class KVGPhysioReferenceDataImporterV2 extends PhysioReferenceDataImporterV2 {

	@Override
	protected String getLaw() {
		return "KVG";
	}
}
