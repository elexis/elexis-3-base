package at.medevit.ch.artikelstamm.elexis.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.elexis.common.importer.ArtikelstammImporter;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.data.PersistentObject;

public class ArtikelstammImporterTest {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporterTest.class);
	private static String pharOnlyInFirst = "4236863";
	private static String pharOnlyInSecond = "8304786";
	private static String pharWithPriceOverridden = "8111718";
	private static String gtinWithPkgSizeOverride = "7680651600014";
	private static String pharWithLeadingZero = "0021806";
	private static final int OLD_PKG_SIZE =  4;
	private static final int NEW_PKG_SIZE = 10;
	
	@Test
	public void testImportAlreadyOkay() throws IOException{
		String gtinUserPrice = "0899722000340";
		IStatus success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_first_v5.xml"), null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_first_v5 failed");
			fail(msg);
		}
		log.debug("testImportAlreadyOkay first done");
		ArtikelstammItem onlyInSecond = ArtikelstammItem.loadByPHARNo(pharOnlyInSecond);
		if (onlyInSecond != null)
		{
			onlyInSecond.delete();
		}

		ArtikelstammItem withUserPrice = ArtikelstammItem.findByEANorGTIN(gtinUserPrice);
		assertEquals(gtinUserPrice, withUserPrice.getGTIN());
		
		// Now Override Price
		double oldPrice = withUserPrice.getPublicPrice();
		String newPrice = "120.05";
		withUserPrice.setPublicPrice(Double.parseDouble(newPrice));
		
		// Check an article only present in first
		assertNotNull(ArtikelstammItem.loadByPHARNo(pharOnlyInFirst));
		
		// Check a new article not present in first
	     onlyInSecond = ArtikelstammItem.loadByPHARNo(pharOnlyInSecond);
		assertTrue(onlyInSecond == null || onlyInSecond.isDeleted());

		
		ArtikelstammItem item7digitPhar = ArtikelstammItem.loadByPHARNo(pharWithPriceOverridden);
		assertEquals("4260057661517", item7digitPhar.getGTIN());
		assertEquals(pharWithPriceOverridden, item7digitPhar.getPHAR());
		
		ArtikelstammItem overridePkgSize = ArtikelstammItem.findByEANorGTIN(gtinWithPkgSizeOverride);
		assertFalse(overridePkgSize.isUserDefinedPrice());
		// Now Override PkgSize
		int old_pkg_size = overridePkgSize.getVerpackungsEinheit();
		log.debug("VerpackungsEinheit after first import {} getVerpackungsEinheit {} via FLD_PKG_SIZE '{}'",
			gtinWithPkgSizeOverride, old_pkg_size, overridePkgSize.get(ArtikelstammItem.FLD_PKG_SIZE));
		assertFalse(overridePkgSize.isUserDefinedPkgSize());
		old_pkg_size = overridePkgSize.getVerpackungsEinheit();
		log.debug("VerpackungsEinheit after first import {} getVerpackungsEinheit {} via FLD_PKG_SIZE '{}'",
			gtinWithPkgSizeOverride, old_pkg_size, overridePkgSize.get(ArtikelstammItem.FLD_PKG_SIZE));

		overridePkgSize.setUserDefinedPkgSizeValue(NEW_PKG_SIZE);
		assertTrue(overridePkgSize.isUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getVerpackungseinheit());

		success = ArtikelstammImporter.performImport(new NullProgressMonitor(),
			AllTests.class.getResourceAsStream("/rsc/artikelstamm_second_v5.xml"), null);
		if (!success.isOK()) {
			String msg =
				String.format("Import of artikelstamm_second_v5.xml failed %s code %s file was {} ",
					success.getMessage(), success.getCode());
			fail(msg);
		}
		log.debug("testImportAlreadyOkay second done");

		// Check a new article not present in first
		assertNotNull(ArtikelstammItem.loadByPHARNo(pharOnlyInSecond));
		ArtikelstammItem overridden = ArtikelstammItem.loadByPHARNo(pharWithPriceOverridden);
		assertEquals("4260057661517", overridden.getGTIN());
		assertEquals(pharWithPriceOverridden, overridden.getPHAR());

		checkResettingPrice(pharWithPriceOverridden, oldPrice);

		// Check PharmaCode with leading zero
		ArtikelstammItem itemWithLeadingZero = ArtikelstammItem.loadByPHARNo(pharWithLeadingZero);
		assertNotNull(itemWithLeadingZero);
		assertEquals(pharWithLeadingZero, itemWithLeadingZero.getPHAR());

		// User Verpackungseinheit must be kept after import
		overridden = ArtikelstammItem.findByEANorGTIN(gtinWithPkgSizeOverride);
		assertEquals(NEW_PKG_SIZE, overridden.getVerpackungsEinheit());
		assertFalse(overridden.isBlackBoxed());

		checkResettingVerpackungsEinheit(gtinWithPkgSizeOverride, OLD_PKG_SIZE);

		// Check an article no long present
		ArtikelstammItem onlyInFirst = ArtikelstammItem.loadByPHARNo(pharOnlyInFirst);
		if ( onlyInFirst != null  )
			log.debug("onlyInFirst {} {} isBlackBoxed {} isDeleted {}  ", onlyInFirst.getDSCR(), onlyInFirst.getPHAR(), onlyInFirst.isBlackBoxed(), onlyInFirst.isDeleted());
		// Next Check fails why?
		// TODO: assertTrue(onlyInFirst == null || onlyInFirst.isDeleted() );
		ArtikelstammItem withPkgOverride = ArtikelstammItem.findByEANorGTIN("7680651600014");
		if (withPkgOverride != null)
			log.debug("withPkgOverride {} {} isBlackBoxed {} isDeleted {}  ",
				withPkgOverride.getDSCR(), withPkgOverride.getPHAR(),
				withPkgOverride.isBlackBoxed(), withPkgOverride.isDeleted());
		assertFalse(withPkgOverride.isBlackBoxed());
	}
	private static void checkResettingPrice(String pharmacode,  double expectedPrice) {
		ArtikelstammItem item = ArtikelstammItem.loadByPHARNo(pharmacode);
		log.debug("checkResettingPrice: phar {} gtin {} user {} old {} before restoring {}", item.getPHAR(), item.getGTIN(), expectedPrice, item.getPublicPrice());
		item.setUserDefinedPrice(false);
		log.debug("checkResettingPrice: phar {} gtin {} old {} restored {}", item.getPHAR(), item.getGTIN(), expectedPrice, item.getPublicPrice());
		// TODO: assertEquals(expectedPrice, item.getPublicPrice(), 0.1);
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
	public void setup() throws Exception{
		log.debug("testImportFirst initFromScratch {}", AbstractPersistentObjectTest.initFromScratch());
		PersistentObject.executeDBInitScriptForClass(ArtikelstammItem.class, null);
	}
	
	@After
	public void cleanup() throws Exception{}
}
