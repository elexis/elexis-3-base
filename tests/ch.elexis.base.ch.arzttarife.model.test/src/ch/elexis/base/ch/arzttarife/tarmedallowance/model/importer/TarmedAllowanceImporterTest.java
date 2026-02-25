package ch.elexis.base.ch.arzttarife.tarmedallowance.model.importer;

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

import ch.elexis.base.ch.arzttarife.pandemie.model.importer.PandemieImporterTest;
import ch.elexis.base.ch.arzttarife.tarmedallowance.ITarmedAllowance;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;

public class TarmedAllowanceImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		TarmedAllowanceReferenceDataImporter importer = new TarmedAllowanceReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PandemieImporterTest.class.getResourceAsStream("/rsc/tarmedpauschalen.xlsx"), null);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement eyeNew = codeElementService.loadFromString("Tarmedpauschalen", "08.0908.01.05", null).get();
		assertNotNull(eyeNew);
		assertNotNull(eyeNew.getText());
		assertTrue(eyeNew.getText().startsWith("Katarakt bei PatientIn mit höherem Risiko für Komplikationen"));
		assertFalse(eyeNew.getText().contains("\n"));
		assertEquals(LocalDate.of(2021, 1, 1), ((ITarmedAllowance) eyeNew).getValidFrom());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
