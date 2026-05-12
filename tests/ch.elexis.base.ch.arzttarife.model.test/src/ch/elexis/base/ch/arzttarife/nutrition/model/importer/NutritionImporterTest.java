package ch.elexis.base.ch.arzttarife.nutrition.model.importer;

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

public class NutritionImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from ...
		// and add to the rsc folder of test bundle
		NutritionReferenceDataImporter importer = new NutritionReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				NutritionImporterTest.class.getResourceAsStream("/rsc/nutrition2021_11_25.csv"), 211125);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement generalAllowance = codeElementService.loadFromString("Ernährungsberatung", "7811", null).get();
		assertNotNull(generalAllowance);
		assertNotNull(generalAllowance.getText());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
