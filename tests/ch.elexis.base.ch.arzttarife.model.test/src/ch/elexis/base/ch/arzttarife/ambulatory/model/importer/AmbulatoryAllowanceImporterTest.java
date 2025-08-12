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
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class AmbulatoryAllowanceImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		AmbulatoryAllowanceReferenceDataImporter importer = new AmbulatoryAllowanceReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				AmbulatoryAllowanceImporterTest.class.getResourceAsStream(
						"/rsc/250410_Anhang_A1_Katalog_der_Ambulanten_Pauschalen_CSV_v1.1b.csv"),
				null);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement element = codeElementService.loadFromString("Ambulantepauschalen", "C01.50C", null).get();
		assertNotNull(element);
		assertNotNull(element.getText());
		assertTrue(element.getText().startsWith("Lumbalpunktion"));
		assertFalse(element.getText().contains("\n"));
		assertEquals(LocalDate.of(2026, 1, 1), ((IAmbulatoryAllowance) element).getValidFrom());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
