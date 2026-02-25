package ch.elexis.base.ch.arzttarife.occupational.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;

public class OccupationalImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from ...
		// and add to the rsc folder of test bundle
		OccupationalReferenceDataImporter importer = new OccupationalReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				OccupationalImporterTest.class.getResourceAsStream("/rsc/occupational2023_07_10.xlsx"), 211125);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement generalAllowance = codeElementService
				.loadFromString("Arbeitsmedizinische Vorsorgeuntersuchungen", "1332.50", null).get();
		assertNotNull(generalAllowance);
		assertNotNull(generalAllowance.getText());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
