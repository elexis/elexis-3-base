package ch.elexis.base.ch.arzttarife.ambulatory.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;

public class AmbulatoryTarifImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		AmbulatoryTarifsReferenceDataImporter importer = new AmbulatoryTarifsReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				AmbulatoryTarifImporterTest.class.getResourceAsStream(
						"/rsc/250808_LKAAT_1.0c.CSV"),
				null);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement element = codeElementService.loadFromString("Ambulantetarife", "C06.CB.0010", null).get();
		assertNotNull(element);
		assertNotNull(element.getText());
		assertTrue(element.getText().startsWith("Laparoskopie"));
		assertFalse(element.getText().contains("\n"));
		assertEquals(LocalDate.of(2026, 1, 1), ((IAmbulatoryAllowance) element).getValidFrom());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
