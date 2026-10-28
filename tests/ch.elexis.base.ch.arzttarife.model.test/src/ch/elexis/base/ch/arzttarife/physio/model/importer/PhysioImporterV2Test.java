package ch.elexis.base.ch.arzttarife.physio.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.physio.model.PhysioLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

public class PhysioImporterV2Test {

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

		// import v2 with kvg law
		retStatus = (Status) kvgImporter.performImport(new NullProgressMonitor(),
				PhysioImporterTest.class.getResourceAsStream("/rsc/Tarif_312_Physiotherapie_KVG_20260227.csv"), 270101);
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

		ICodeElement tarif27710 = codeElementService
				.loadFromString("Physiotherapie", "27710", Map.of(ContextKeys.DATE, LocalDate.of(2025, 10, 1)))
				.orElse(null);
		assertNull(tarif27710);
		tarif27710 = codeElementService
				.loadFromString("Physiotherapie", "27710", Map.of(ContextKeys.DATE, LocalDate.of(2027, 1, 1)))
				.orElse(null);
		assertNotNull(tarif27710);

		ICodeElement tarif27100 = codeElementService
				.loadFromString("Physiotherapie", "27100", Map.of(ContextKeys.DATE, LocalDate.of(2025, 10, 1)))
				.orElse(null);
		assertNull(tarif27100);
		tarif27100 = codeElementService
				.loadFromString("Physiotherapie", "27100", Map.of(ContextKeys.DATE, LocalDate.of(2026, 7, 1)))
				.orElse(null);
		assertNotNull(tarif27710);

		OsgiServiceUtil.ungetService(codeElementService);
	}

	@Test
	public void performImportNewFormat() {
		// import the new csv format (Tarif 312) via the physio_kvg importer
		KVGPhysioReferenceDataImporter importer = new KVGPhysioReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PhysioImporterV2Test.class.getResourceAsStream("/rsc/Tarif_312_Physiotherapie_KVG_20260227.csv"), 260701);
		assertEquals(IStatus.OK, retStatus.getCode());

		// service with tax points price
		IPhysioLeistung leistung = load("27110");
		assertNotNull(leistung);
		assertEquals("Einzelbehandlung am Praxisstandort pro 5 Min.", leistung.getText());
		assertEquals("Einzelbehandlung am Praxisstandort", leistung.getKapitel());
		assertEquals("737", leistung.getTP());
		assertEquals(LocalDate.of(2027, 1, 1), leistung.getValidFrom());
		assertEquals("KVG", leistung.getLaw());
		assertNotNull(leistung.getInterpretation());
		// tags in the interpretation are replaced at import
		assertTrue(leistung.getInterpretation().contains("Positionsbeschreibung"));
		assertTrue(leistung.getInterpretation().contains("Anwendungsregel"));
		assertFalse(leistung.getInterpretation().contains("[POSITION_DESCRIPTION]"));
		assertFalse(leistung.getInterpretation().contains("[APPLICATION_RULE]"));

		// service with fixed price (chf)
		IPhysioLeistung fixPreis = load("27820");
		assertNotNull(fixPreis);
		assertEquals("1200", fixPreis.getTP());
		assertTrue(fixPreis.getText().endsWith("\n[FIXPRICE]"));

		// marker position without price
		IPhysioLeistung marker = load("27100");
		assertNotNull(marker);
		assertNull(marker.getTP());
		assertEquals(LocalDate.of(2026, 7, 1), marker.getValidFrom());

		// material at purchase price
		IPhysioLeistung material = load("27910");
		assertNotNull(material);
		assertNull(material.getTP());

		// older kvg entries are closed at the first valid from of the new tarif
		IQuery<IPhysioLeistung> openKvg = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		openKvg.and("law", COMPARATOR.EQUALS, "KVG");
		openKvg.and("validUntil", COMPARATOR.EQUALS, null);
		for (IPhysioLeistung open : openKvg.execute()) {
			assertTrue("Open KVG entry with validFrom " + open.getValidFrom(),
					!open.getValidFrom().isBefore(LocalDate.of(2026, 7, 1)));
		}
	}

	@Test
	public void performImportOldFormat() {
		// the old csv format is still detected and imported by the unchanged implementation
		KVGPhysioReferenceDataImporter importer = new KVGPhysioReferenceDataImporter();
		Status retStatus = (Status) importer.performImport(new NullProgressMonitor(),
				PhysioImporterV2Test.class.getResourceAsStream("/rsc/physiotarif_kvg2025_07_01.csv"), 250701);
		assertEquals(IStatus.OK, retStatus.getCode());

		IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		query.and("ziffer", COMPARATOR.EQUALS, "7362");
		query.and("law", COMPARATOR.EQUALS, "KVG");
		query.and("validUntil", COMPARATOR.EQUALS, null);
		List<IPhysioLeistung> loaded = query.execute();
		assertFalse(loaded.isEmpty());
		// fix price from the old format is not scaled
		assertEquals("5000", loaded.get(0).getTP());
		assertTrue(loaded.get(0).getText().endsWith("\n[FIXPRICE]"));
	}

	private IPhysioLeistung load(String ziffer) {
		IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		query.and("ziffer", COMPARATOR.EQUALS, ziffer);
		query.and("validUntil", COMPARATOR.EQUALS, null);
		List<IPhysioLeistung> loaded = query.execute();
		assertEquals(1, loaded.size());
		return loaded.get(0);
	}
}
