package ch.elexis.base.ch.arzttarife.importer;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import jakarta.inject.Inject;

public class Ps25Importer extends ImporterPage {

	@Inject
	private IReferenceDataImporterService importerService;

	public Ps25Importer() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public Composite createPage(Composite parent) {
		FileBasedImporter importer = new ImporterPage.FileBasedImporter(parent, this);
		importer.setFilter(new String[] { "*.xlsx", "*" },
				new String[] { "XLSX", "Alle Dateien" });
		importer.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return importer;
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		try (FileInputStream tarifInputStream = new FileInputStream(results[0])) {
			IReferenceDataImporter importer = importerService.getImporter("ps25")
					.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
			return importer.performImport(monitor, tarifInputStream, null);
		}
	}

	@Override
	public String getDescription() {
		return "PS25 Tarif XLSX";
	}

	@Override
	public String getTitle() {
		return "PS25 Tarif";
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IPs25Leistung.class.getName());
	}
}