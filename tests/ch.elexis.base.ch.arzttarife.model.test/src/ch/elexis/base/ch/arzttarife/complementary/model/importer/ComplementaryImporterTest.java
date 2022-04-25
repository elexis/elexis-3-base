package ch.elexis.base.ch.arzttarife.complementary.model.importer;

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

public class ComplementaryImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		// IMPORTANT download the database from ...
		// and add to the rsc folder of test bundle
		ComplementaryReferenceDataImporter importer = new ComplementaryReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				ComplementaryImporterTest.class.getResourceAsStream("/rsc/complementary_171229.csv"), null);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement bachFlowers = codeElementService.loadFromString("Komplementärmedizin", "1022", null).get();
		assertNotNull(bachFlowers);
		assertNotNull(bachFlowers.getText());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
