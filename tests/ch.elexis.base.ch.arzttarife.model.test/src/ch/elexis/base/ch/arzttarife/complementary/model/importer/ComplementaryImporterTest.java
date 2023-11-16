package ch.elexis.base.ch.arzttarife.complementary.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
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
		
		InputStream updateStream = ComplementaryImporterTest.class.getResourceAsStream("/rsc/complementary_v4.csv");
		if (updateStream != null) {
			retStatus = (Status) importer.performImport(new NullProgressMonitor(), updateStream,
					Integer.valueOf(4));
			assertEquals(IStatus.OK, retStatus.getCode());
			assertEquals(4, importer.getCurrentVersion());

			Optional<ICodeElement> code1076 = codeElementService.loadFromString("Komplementärmedizin", "1076",
					Collections.singletonMap(ContextKeys.DATE, LocalDate.of(2023, 01, 01)));
			assertTrue(code1076.isEmpty());
			code1076 = codeElementService.loadFromString("Komplementärmedizin", "1076",
					Collections.singletonMap(ContextKeys.DATE, LocalDate.of(2022, 12, 30)));
			assertFalse(code1076.isEmpty());
		}
	}
}
