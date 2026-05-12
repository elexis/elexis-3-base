package ch.elexis.base.ch.arzttarife.physio.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.ContextKeys;

public class PhysioImporterTest {

	@Test
	public void performImport() throws FileNotFoundException, SQLException {

		// create database before adding law
		// import old kvg with old version, use same import as uvg for old version
		PhysioReferenceDataImporter importer = new PhysioReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PhysioImporterTest.class.getResourceAsStream("/rsc/physiotarif2018_09_05.csv"), 180905);
		assertEquals(IStatus.OK, retStatus.getCode());

		// import with kvg law
		KVGPhysioReferenceDataImporter kvgImporter = new KVGPhysioReferenceDataImporter();
		retStatus = (Status) kvgImporter.performImport(new NullProgressMonitor(),
				PhysioImporterTest.class.getResourceAsStream("/rsc/physiotarif_kvg2025_07_01.csv"), 250701);
		assertEquals(IStatus.OK, retStatus.getCode());

		// import new tarif no law for uvg iv mv
		retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PhysioImporterTest.class.getResourceAsStream("/rsc/physiotarif2025_07_01.csv"), 250701);
		assertEquals(IStatus.OK, retStatus.getCode());

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		ICodeElement tarif7301 = codeElementService
				.loadFromString("Physiotherapie", "7301", Map.of(ContextKeys.DATE, LocalDate.of(2018, 10, 1))).get();
		assertNotNull(tarif7301);
		assertNotNull(tarif7301.getText());
		assertEquals(StringUtils.EMPTY, ((PhysioLeistung) tarif7301).getEntity().getLaw());

		tarif7301 = codeElementService
				.loadFromString("Physiotherapie", "7301", Map.of(ContextKeys.DATE, LocalDate.of(2025, 10, 1))).get();
		assertNotNull(tarif7301);
		assertNotNull(tarif7301.getText());
		assertEquals("KVG", ((PhysioLeistung) tarif7301).getEntity().getLaw());

		assertTrue(codeElementService
				.loadFromString("Physiotherapie", "25.110", Map.of(ContextKeys.DATE, LocalDate.of(2018, 10, 1)))
				.isEmpty());
		
		ICodeElement tarif25110 = codeElementService
				.loadFromString("Physiotherapie", "25.110", Map.of(ContextKeys.DATE, LocalDate.of(2025, 10, 1))).get();
		assertNotNull(tarif25110);
		assertNotNull(tarif25110.getText());
		assertEquals(StringUtils.EMPTY, ((PhysioLeistung) tarif25110).getEntity().getLaw());

		OsgiServiceUtil.ungetService(codeElementService);
	}
}
