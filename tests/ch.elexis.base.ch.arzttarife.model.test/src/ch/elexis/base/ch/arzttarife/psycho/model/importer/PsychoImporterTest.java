package ch.elexis.base.ch.arzttarife.psycho.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.pandemie.model.importer.PandemieImporterTest;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class PsychoImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {
		PsychoReferenceDataImporter importer = new PsychoReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PandemieImporterTest.class.getResourceAsStream("/rsc/20220609_PsyTarif_DE.xlsx"), null);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement diagnostic = codeElementService.loadFromString("Psychotherapie", "PA010", null).get();
		assertNotNull(diagnostic);
		assertNotNull(diagnostic.getText());
		assertTrue(diagnostic.getText().startsWith("Diagnostik und Therapie mit einem Patienten"));
		assertFalse(diagnostic.getText().contains("\n"));
		assertTrue(StringUtils.isNumeric(((IPsychoLeistung) diagnostic).getTP()));
		assertEquals(LocalDate.of(2022, 7, 1), ((IPsychoLeistung) diagnostic).getValidFrom());

		ICodeElement percent = codeElementService.loadFromString("Psychotherapie", "PN020", null).get();
		assertNotNull(percent);
		assertNotNull(percent.getText());
		assertFalse(diagnostic.getText().contains("\n"));
		assertTrue(((IPsychoLeistung) percent).getTP().startsWith("%"));
		assertEquals(LocalDate.of(2022, 7, 1), ((IPsychoLeistung) diagnostic).getValidFrom());

		OsgiServiceUtil.ungetService(codeElementService);
	}
}
