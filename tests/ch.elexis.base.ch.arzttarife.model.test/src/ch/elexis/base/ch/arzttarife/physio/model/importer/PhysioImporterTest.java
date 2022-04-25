package ch.elexis.base.ch.arzttarife.physio.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class PhysioImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from ...
		// and add to the rsc folder of test bundle
		PhysioReferenceDataImporter importer = new PhysioReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PhysioImporterTest.class.getResourceAsStream("/rsc/physiotarif2018_09_05.csv"), 180905);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement generalAllowance = codeElementService.loadFromString("Physiotherapie", "7301", null).get();
		assertNotNull(generalAllowance);
		assertNotNull(generalAllowance.getText());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
