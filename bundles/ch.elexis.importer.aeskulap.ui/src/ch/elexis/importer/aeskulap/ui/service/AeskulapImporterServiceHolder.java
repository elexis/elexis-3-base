package ch.elexis.importer.aeskulap.ui.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.importer.aeskulap.core.IAeskulapImporter;

@Component
public class AeskulapImporterServiceHolder {

	private static IAeskulapImporter importer;

	@Reference
	public void setAeskulapImporter(IAeskulapImporter importer) {
		AeskulapImporterServiceHolder.importer = importer;
	}

	public static IAeskulapImporter get() {
		if (importer == null) {
			throw new IllegalStateException("No IAeskulapImporter implementation available");
		}
		return importer;
	}
}
