package ch.elexis.base.ch.arzttarife.ps25.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.occupational.model.importer.OccupationalImporterTest;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;

public class Ps25ImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from ...
		// and add to the rsc folder of test bundle
		Ps25ReferenceDataImporter importer = new Ps25ReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				OccupationalImporterTest.class.getResourceAsStream("/rsc/PS25_2026-06-19.xlsx"), 1);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement ps25 = codeElementService
				.loadFromString("PS25", "PS06.110.2", null).get();
		assertNotNull(ps25);
		assertNotNull(ps25.getText());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
