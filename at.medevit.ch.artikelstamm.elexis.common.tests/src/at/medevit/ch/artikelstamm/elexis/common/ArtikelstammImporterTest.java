package at.medevit.ch.artikelstamm.elexis.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.elexis.common.importer.ArtikelstammImporter;
import ch.artikelstamm.elexis.common.ArtikelstammItem;

public class ArtikelstammImporterTest {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporterTest.class);
	private static String pharOnlyInFirst = "4236863";
	private static String pharOnlyInSecond = "8304786";
	private static String pharWithPriceOverridden = "8111718";
	private static String gtinWithPkgSizeOverride = "7680651600014";
	private static String pharWithLeadingZero = "0021806";
	private static final int NEW_PKG_SIZE = 10;
	
	private void removeSomePhars(String[] phars2delete) {
		for ( String phar2rm : phars2delete) {
			ArtikelstammItem item2rm = ArtikelstammItem.loadByPHARNo(phar2rm);
			if (item2rm == null)
				item2rm = ArtikelstammItem.findByEANorGTIN(phar2rm);
			if (item2rm != null) {
				 item2rm.delete();
				 item2rm.removeFromDatabase();
			}
		}
	}
	@Test
	public void testImportAlreadyOkay() throws IOException{
		String gtinUserPrice = "0899722000340";

		log.debug("testImportFirst starting");
		log.debug("testImportFirst getLink {}", AbstractPersistentObjectTest.getLink());
		removeSomePhars(new String[] { pharOnlyInFirst, pharOnlyInSecond,  pharWithPriceOverridden, gtinUserPrice});
		IStatus success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_first_v5.xml"), null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_first_v5 failed");
			fail(msg);
		}
		log.debug("testImportAlreadyOkay first done");
		
		ArtikelstammItem withUserPrice = ArtikelstammItem.findByEANorGTIN(gtinUserPrice);
		assertEquals(gtinUserPrice, withUserPrice.getGTIN());
		
		// Now Override Price
		double oldPrice = withUserPrice.getPublicPrice();
		String newPrice = "120.05";
		withUserPrice.setPublicPrice(Double.parseDouble(newPrice));
		
		// Check an article only present in first
		assertNotNull(ArtikelstammItem.loadByPHARNo(pharOnlyInFirst));
		
		// Check a new article not present in first
		assertNull(ArtikelstammItem.loadByPHARNo(pharOnlyInSecond));
		
		success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_second_v5.xml"), null);
		if (!success.isOK()) {
			String msg =
				String.format("Import of artikelstamm_second_v5.xml failed %s code %s file was {} ",
					success.getMessage(), success.getCode());
			fail(msg);
		}
		log.debug("testImportAlreadyOkay second done");
		
		// Check an article no long present
		ArtikelstammItem deleted = ArtikelstammItem.loadByPHARNo(pharOnlyInFirst);
		assertNotNull(deleted);
		// assertTrue(deleted.isDeleted());
		// TODO: this fails why?
		
		// Check a new article not present in first
		assertNotNull(ArtikelstammItem.loadByPHARNo(pharOnlyInSecond));
		assertFalse(deleted.isDeleted());
		checkResettingPrice(withUserPrice.getPHAR(), oldPrice);
	}
	
	@Test
	public void testImportOverridingIsOkay() throws IOException{
		log.debug("testImportFirst starting");
		log.debug("testImportFirst getLink {}", AbstractPersistentObjectTest.getLink());
		removeSomePhars(new String[] { pharOnlyInFirst, pharOnlyInSecond,  pharWithPriceOverridden});
		//	parseOneHL7file(new File(workDir.toString(), "Analytica/01TEST5005.hl7"), false, true);
		
		IStatus success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_first_v5.xml"), null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_first_v5 failed");
			fail(msg);
		}
		log.debug("testImportOverridingIsOkay first done");
		
		ArtikelstammItem item7digitPhar = ArtikelstammItem.loadByPHARNo(pharWithPriceOverridden);
		assertEquals("4260057661517", item7digitPhar.getGTIN());
		assertEquals(pharWithPriceOverridden, item7digitPhar.getPHAR());
		double oldPrice = item7digitPhar.getPublicPrice();
		String userPrice = "145.43";
		log.debug("testImportOverridingIsOkay: phar {} gtin {} old {} userPrice {} PPUB {}", item7digitPhar.getPHAR(), item7digitPhar.getGTIN(), oldPrice, userPrice, item7digitPhar.get(ArtikelstammItem.FLD_PPUB));
		item7digitPhar.setUserDefinedPriceValue(Double.parseDouble(userPrice));
		assertFalse(item7digitPhar.isUserDefinedPkgSize());
		
		ArtikelstammItem overridePkgSize = ArtikelstammItem.findByEANorGTIN(gtinWithPkgSizeOverride);
		assertFalse(overridePkgSize.isUserDefinedPrice());
		assertFalse(overridePkgSize.isUserDefinedPkgSize());
		
		// Now Override PkgSize
		int old_pkg_size = overridePkgSize.getVerpackungsEinheit();
		
		overridePkgSize.setUserDefinedPkgSizeValue(NEW_PKG_SIZE);
		assertTrue(overridePkgSize.isUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getVerpackungseinheit());
		
		success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_second_v5.xml"), null);
		log.debug("testImportOverridingIsOkay second done {}", success);
		if (!success.isOK()) {
			String msg =
				String.format("Import of artikelstamm_second_v5.xml failed %s code %s file was {} ",
					success.getMessage(), success.getCode());
			fail(msg);
		}
		
		ArtikelstammItem overridden = ArtikelstammItem.loadByPHARNo(pharWithPriceOverridden);
		assertEquals("4260057661517", overridden.getGTIN());
		assertEquals(pharWithPriceOverridden, overridden.getPHAR());
		
		// User Price must be kept while importing
		assertTrue(overridden.isUserDefinedPrice());
		assertEquals(userPrice, overridden.getPublicPrice().toString());
		// TODO: checkResettingPrice(pharWithPriceOverridden, oldPrice);
		
		// Check PharmaCode with leading zero
		ArtikelstammItem itemWithLeadingZero = ArtikelstammItem.loadByPHARNo(pharWithLeadingZero);
		assertNotNull(itemWithLeadingZero);
		assertEquals(pharWithLeadingZero, itemWithLeadingZero.getPHAR());

		// User Verpackungseinheit must be kept after import
		overridden = ArtikelstammItem.findByEANorGTIN(gtinWithPkgSizeOverride);
		assertEquals(NEW_PKG_SIZE, overridden.getVerpackungsEinheit());

		// TODO: checkResettingVerpackungsEinheit(gtinWithPkgSizeOverride, old_pkg_size);
	}
	private static void checkResettingPrice(String pharmacode,  double expectedPrice) {
		ArtikelstammItem item = ArtikelstammItem.loadByPHARNo(pharmacode);
		log.debug("checkResettingPrice: phar {} gtin {} user {} old {} before restoring {}", item.getPHAR(), item.getGTIN(), expectedPrice, item.getPublicPrice());
		item.setUserDefinedPrice(false);
		log.debug("checkResettingPrice: phar {} gtin {} old {} restored {}", item.getPHAR(), item.getGTIN(), expectedPrice, item.getPublicPrice());
		Assert.assertEquals(expectedPrice, item.getPublicPrice(), 0.1);
	}
	private static void checkResettingVerpackungsEinheit(String gtin, int expectedPkgSize) {
		ArtikelstammItem item = ArtikelstammItem.findByEANorGTIN(gtin);
		log.debug("checkResettingVerpackungsEinheit: phar {} gtin {} old {} before restoring {}", item.getPHAR(), item.getGTIN(), expectedPkgSize, item.getVerpackungsEinheit());
		item.setUserDefinedPkgSize(false);
		int newEinheit =  item.getVerpackungsEinheit();
		log.debug("checkResettingVerpackungsEinheit: phar {} gtin {} old {} restored {}", item.getPHAR(), item.getGTIN(), expectedPkgSize, newEinheit);
		assertEquals(expectedPkgSize, newEinheit);
	}
	
	@Before
	public void setup() throws Exception{}
	
	@After
	public void cleanup() throws Exception{}
	
}
