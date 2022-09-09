package at.medevit.ch.artikelstamm.model.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Money;

public class ArtikelstammImporterTest {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporterTest.class);
	private static final String gtinNonPharmaInactiveInSecond = "5011091105012";
	private static final String gtinPharmaOnlyInFirst = "7680667450023";
	private static final String gtinOnlyInSecond = "7611800080487";
	private static final String pharWithPriceOverridden = "6571270";
	private static final String gtinWithPriceOverridden = "7680651600014";
	private static final String gtinWithPkgSizeOverride = "7680651600014";
	private static final String gtinWithPkgSizeOverrideNull = "7612929028176";
	private static final String pharWithLeadingZero = "0098878";
	private static final String gtinUserPrice = "7680273040281";
	private static final String gtin14chars = "68711428066649";
	private static final String gtinPriorix = "7680581580011";
	private static final String atcPriorixFirst = "J07BD53";
	private static final String atcPriorixSecond = "J07BD54";
	private static final String gtinNonPharma = "68711428066649";
	private static final String[] slEntries = new String[] { "7680273040281", "7680651600014" };
	private static final int OLD_PKG_SIZE = 448;
	private static final int NEW_PKG_SIZE = 100;
	private static boolean isMedindex = false;

	private static ArtikelstammImporter importer;

	private static ICodeElementServiceContribution artikelstammCodeElements;

	private static IModelService artikelstammModelService;

	@BeforeClass
	public static void beforeClass() {
		importer = (ArtikelstammImporter) OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=artikelstamm_v5)").get();
		artikelstammModelService = OsgiServiceUtil.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model)").get();
		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		artikelstammCodeElements = codeElementService.getContribution(CodeElementTyp.ARTICLE, "Artikelstamm").get();
	}

	public void testImportMedindex() throws IOException, ParseException {
		isMedindex = true;
		runImport("/rsc/medindex");
	}

	// At the moment I can only activate one of the two tests or I will get errors
	// about the incompatilibity between medindex and oddb2xml
	@Test
	public void testImportOddb2xml() {
		isMedindex = false;
		runImport("/rsc/artikelstamm");
	}

	// At the moment I can only activate one of the two tests or I will get errors
	// about the incompatilibity between medindex and oddb2xml
	@Test
	public void testImportOddb2xmlFrench() {
		isMedindex = false;
		String filename = "/rsc/artikelstamm_second_v5.xml";
		String lang = "f";
		ConfigServiceHolder.get().setLocal(Preferences.ABL_LANGUAGE, lang);
		IStatus success = importer.performImport(new NullProgressMonitor(),
				ArtikelstammImporterTest.class.getResourceAsStream(filename), true, false, null);
		if (!success.isOK()) {
			String msg = String.format("Import of %s lang %s failed", filename, lang);
			fail(msg);
		}
		Optional<ICodeElement> priorix = artikelstammCodeElements.loadFromCode(gtinPriorix, Collections.emptyMap());
		log.info(priorix.get().toString());
	}

	private static void testArticlesInBoth() {
		// I would have loved to add a test that priorix has only one product
		// But it was too complicated
		if (isMedindex) {
			return;
		}
		Optional<ICodeElement> priorix = artikelstammCodeElements.loadFromCode(gtinPriorix, Collections.emptyMap());
		assertTrue(priorix.isPresent());
		assertEquals(10590, ((IArtikelstammItem) priorix.get()).getSellingPrice().getCents());

		Optional<ICodeElement> nonPharma = artikelstammCodeElements.loadFromCode(gtinNonPharma);
		nonPharma = artikelstammCodeElements.loadFromCode(gtinNonPharma);
		assertFalse(nonPharma.isPresent());
	}

	private void runImport(String baseName) {
		// Import only Pharma-Artikel
		ConfigServiceHolder.get().setLocal(Preferences.ABL_LANGUAGE, "d");
		Optional<ICodeElement> nonPharma = artikelstammCodeElements.loadFromCode(gtinNonPharma);

		assertFalse(nonPharma.isPresent());
		IStatus success = importer.performImport(new NullProgressMonitor(),
				ArtikelstammImporterTest.class.getResourceAsStream(baseName + "_first_v5.xml"), true, false, null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_first_v5 failed");
			fail(msg);
		}
		if (!isMedindex) {
			// I would have loved to add a test that priorix has only one product
			// But it was too complicated
			Optional<ICodeElement> priorix = artikelstammCodeElements.loadFromCode(gtinPriorix, Collections.emptyMap());
			assertTrue(priorix.isPresent());
			assertEquals(10590, ((IArtikelstammItem) priorix.get()).getSellingPrice().getCents());

			nonPharma = artikelstammCodeElements.loadFromCode(gtinNonPharma);
			assertFalse(nonPharma.isPresent());
		}
		success = importer.performImport(new NullProgressMonitor(),
				ArtikelstammImporterTest.class.getResourceAsStream(baseName + "_first_v5.xml"), true, true, null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_first_v5 failed");
			fail(msg);
		}
		log.debug("testImportAlreadyOkay first done");
		Optional<ICodeElement> onlyInSecond = artikelstammCodeElements.loadFromCode(gtinOnlyInSecond);
		if (onlyInSecond.isPresent()) {
			artikelstammModelService.delete((IArtikelstammItem) onlyInSecond.get());
		}

		if (!isMedindex) {
			// I would have loved to add a test that priorix has only one product
			// But it was too complicated
			Optional<ICodeElement> priorix = artikelstammCodeElements.loadFromCode(gtinPriorix);
			assertTrue(priorix.isPresent());

			assertEquals(14, gtin14chars.length());
			Optional<ICodeElement> gtin14 = artikelstammCodeElements.loadFromCode(gtin14chars);
			assertNotNull(gtin14);
			assertEquals(gtin14chars, ((IArtikelstammItem) gtin14.get()).getGtin());
			assertEquals(17820, ((IArtikelstammItem) gtin14.get()).getSellingPrice().getCents());

			nonPharma = artikelstammCodeElements.loadFromCode(gtinNonPharma);
			assertNotNull(nonPharma);
			assertEquals(gtinNonPharma, ((IArtikelstammItem) nonPharma.get()).getGtin());
			assertEquals(17820, ((IArtikelstammItem) nonPharma.get()).getSellingPrice().getCents());
		}

		Optional<ICodeElement> withUserPrice = artikelstammCodeElements.loadFromCode(gtinUserPrice);
		assertTrue(withUserPrice.isPresent());
		assertEquals(gtinUserPrice, ((IArtikelstammItem) withUserPrice.get()).getGtin());

		// Now Override Price
		Money oldPrice = ((IArtikelstammItem) withUserPrice.get()).getSellingPrice();
		try {
			((IArtikelstammItem) withUserPrice.get()).setSellingPrice(new Money("120.05"));
		} catch (ParseException e) {
		}

		// Check an article only present in first
		assertTrue(artikelstammCodeElements.loadFromCode(gtinNonPharmaInactiveInSecond).isPresent());
		assertFalse(((IArtikelstammItem) artikelstammCodeElements.loadFromCode(gtinNonPharmaInactiveInSecond).get())
				.isBlackBoxed());
		assertTrue(artikelstammCodeElements.loadFromCode(gtinPharmaOnlyInFirst).isPresent());

		// Check a new article not present in first
		onlyInSecond = artikelstammCodeElements.loadFromCode(gtinOnlyInSecond);
		assertFalse(onlyInSecond.isPresent());

		Optional<ICodeElement> item7digitPhar = artikelstammCodeElements.loadFromCode(gtinWithPriceOverridden);
		assertEquals(gtinWithPriceOverridden, ((IArtikelstammItem) item7digitPhar.get()).getGtin());
		assertEquals(pharWithPriceOverridden, ((IArtikelstammItem) item7digitPhar.get()).getPHAR());

		// Test ATC-Code from first import
		Optional<ICodeElement> priorix = artikelstammCodeElements.loadFromCode(gtinPriorix, Collections.emptyMap());
		log.debug(String.format("priorix gtin first  import %s atc %s", ((IArtikelstammItem) priorix.get()).getGtin(),
				((IArtikelstammItem) priorix.get()).getAtcCode()));
		assertEquals(atcPriorixFirst, ((IArtikelstammItem) priorix.get()).getAtcCode());

		setPkgOverride(gtinWithPkgSizeOverride);
		setPkgOverride(gtinWithPkgSizeOverrideNull);

		success = importer.performImport(new NullProgressMonitor(),
				ArtikelstammImporterTest.class.getResourceAsStream(baseName + "_second_v5.xml"), true, true, null);
		if (!success.isOK()) {
			String msg = String.format("Import of artikelstamm_second_v5.xml failed %s code %s file was {} ",
					success.getMessage(), success.getCode());
			fail(msg);
		}
		log.debug("testImportAlreadyOkay second done");

		// Test ATC-Code from first import
		priorix = artikelstammCodeElements.loadFromCode(gtinPriorix, Collections.emptyMap());
		log.debug(String.format("priorix gtin second import %s atc %s", ((IArtikelstammItem) priorix.get()).getGtin(),
				((IArtikelstammItem) priorix.get()).getAtcCode()));
		assertEquals(atcPriorixSecond, ((IArtikelstammItem) priorix.get()).getAtcCode());

		// Check a new article not present in first
		assertTrue(artikelstammCodeElements.loadFromCode(gtinOnlyInSecond).isPresent());
		Optional<ICodeElement> overridden = artikelstammCodeElements.loadFromCode(gtinWithPriceOverridden);
		assertTrue(overridden.isPresent());
		assertEquals(gtinWithPriceOverridden, ((IArtikelstammItem) overridden.get()).getGtin());
		assertEquals(pharWithPriceOverridden, ((IArtikelstammItem) overridden.get()).getPHAR());

		checkResettingPrice(gtinWithPriceOverridden, oldPrice);

		// Check PharmaCode with leading zero
		Optional<ICodeElement> itemWithLeadingZero = artikelstammCodeElements.loadFromCode(pharWithLeadingZero);
		assertTrue(itemWithLeadingZero.isPresent());
		assertEquals(pharWithLeadingZero, ((IArtikelstammItem) itemWithLeadingZero.get()).getPHAR());

		// User Verpackungseinheit must be kept after import
		overridden = artikelstammCodeElements.loadFromCode(gtinWithPkgSizeOverride);
		assertEquals(NEW_PKG_SIZE, ((IArtikelstammItem) overridden.get()).getPackageSize());
		assertFalse(((IArtikelstammItem) overridden.get()).isBlackBoxed());

		checkResettingVerpackungsEinheit(gtinWithPkgSizeOverride, OLD_PKG_SIZE);
		checkResettingVerpackungsEinheit(gtinWithPkgSizeOverrideNull, 0);

		// Check an article no long present
		Optional<ICodeElement> pharmaOnlyInFirst = artikelstammCodeElements.loadFromCode(gtinPharmaOnlyInFirst);
		if (pharmaOnlyInFirst.isPresent()) {
			log.debug("onlyInFirst {} {} isBlackBoxed {} isDeleted {}  ",
					((IArtikelstammItem) pharmaOnlyInFirst.get()).getText(),
					((IArtikelstammItem) pharmaOnlyInFirst.get()).getPHAR(),
					((IArtikelstammItem) pharmaOnlyInFirst.get()).isBlackBoxed(),
					((IArtikelstammItem) pharmaOnlyInFirst.get()).isDeleted());
		}
		assertFalse(pharmaOnlyInFirst.isPresent());
		// assertTrue(pharmaOnlyInFirst.isBlackBoxed());
		Optional<ICodeElement> nonPharmaOnlyInFirst = artikelstammCodeElements
				.loadFromCode(gtinNonPharmaInactiveInSecond);
		if (isMedindex) {
			if (nonPharmaOnlyInFirst.isPresent()) {
				log.debug("onlyInFirst {} {} isBlackBoxed {} isDeleted {}  ",
						((IArtikelstammItem) nonPharmaOnlyInFirst.get()).getText(),
						((IArtikelstammItem) nonPharmaOnlyInFirst.get()).getPHAR(),
						((IArtikelstammItem) nonPharmaOnlyInFirst.get()).isBlackBoxed(),
						((IArtikelstammItem) nonPharmaOnlyInFirst.get()).isDeleted());
			}
			assertFalse(nonPharmaOnlyInFirst.isPresent());
		} else {
			assertTrue(nonPharmaOnlyInFirst.isPresent());
			assertFalse(((IArtikelstammItem) nonPharmaOnlyInFirst.get()).isBlackBoxed());
		}

		// Next Check fails why?
		// TODO: assertTrue(onlyInFirst == null || onlyInFirst.isDeleted() );
		Optional<ICodeElement> withPkgOverride = artikelstammCodeElements.loadFromCode(gtinWithPkgSizeOverride);
		assertTrue(withPkgOverride.isPresent());
		log.debug("withPkgOverride {} {} isBlackBoxed {} isDeleted {}  ",
				((IArtikelstammItem) withPkgOverride.get()).getText(),
				((IArtikelstammItem) withPkgOverride.get()).getPHAR(),
				((IArtikelstammItem) withPkgOverride.get()).isBlackBoxed(),
				((IArtikelstammItem) withPkgOverride.get()).isDeleted());
		assertFalse(((IArtikelstammItem) withPkgOverride.get()).isBlackBoxed());
		for (String slGtin : slEntries) {
			Optional<ICodeElement> ai = artikelstammCodeElements.loadFromCode(slGtin);
			assertTrue(ai.isPresent());
			assertFalse(((IArtikelstammItem) ai.get()).isBlackBoxed());
			assertTrue(((IArtikelstammItem) ai.get()).isInSLList());
		}
	}

	private IArtikelstammItem setPkgOverride(String gtin) {
		IArtikelstammItem overridePkgSize = (IArtikelstammItem) artikelstammCodeElements.loadFromCode(gtin).get();
		assertFalse(overridePkgSize.isUserDefinedPrice());
		// Now Override PkgSize
		int old_pkg_size = overridePkgSize.getPackageSize();
		log.debug("VerpackungsEinheit after first import {} getVerpackungsEinheit {} via FLD_PKG_SIZE '{}'",
				gtinWithPkgSizeOverride, old_pkg_size, overridePkgSize.getPackageSize());
		assertFalse(overridePkgSize.isUserDefinedPrice());
		old_pkg_size = overridePkgSize.getPackageSize();
		log.debug("VerpackungsEinheit after first import {} getVerpackungsEinheit {} via FLD_PKG_SIZE '{}'",
				gtinWithPkgSizeOverride, old_pkg_size, overridePkgSize.getPackageSize());

		overridePkgSize.setUserDefinedPkgSizeValue(NEW_PKG_SIZE);
		assertTrue(overridePkgSize.isUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getUserDefinedPkgSize());
		assertEquals(NEW_PKG_SIZE, overridePkgSize.getPackageSize());
		Object value = artikelstammModelService.getEntityProperty("pkg_size", overridePkgSize);
		if (value instanceof String) {
			assertTrue(NEW_PKG_SIZE * -1 == Integer.parseInt((String) value));
		}
		artikelstammModelService.save(overridePkgSize);
		return overridePkgSize;
	}

	private static void checkResettingPrice(String gtin, Money expectedPrice) {
		IArtikelstammItem item = (IArtikelstammItem) artikelstammCodeElements.loadFromCode(gtin).get();
		log.debug("checkResettingPrice: phar {} gtin {} user {} old {} before restoring {}", item.getPHAR(),
				item.getGtin(), expectedPrice, item.getSellingPrice());
		item.restoreOriginalSellingPrice();
		log.debug("checkResettingPrice: phar {} gtin {} old {} restored {}", item.getPHAR(), item.getGtin(),
				expectedPrice, item.getSellingPrice());
		// TODO: assertEquals(expectedPrice, item.getPublicPrice(), 0.1);
	}

	private static void checkResettingVerpackungsEinheit(String gtin, int expectedPkgSize) {
		IArtikelstammItem item = (IArtikelstammItem) artikelstammCodeElements.loadFromCode(gtin).get();
		log.debug("checkResettingVerpackungsEinheit: phar {} gtin {} old {} before restoring {}", item.getPHAR(),
				item.getGtin(), expectedPkgSize, item.getSellingPrice());
		item.restoreOriginalPackageSize();
		int newEinheit = item.getPackageSize();
		log.debug("checkResettingVerpackungsEinheit: phar {} gtin {} old {} restored {}", item.getPHAR(),
				item.getGtin(), expectedPkgSize, newEinheit);
		assertEquals(expectedPkgSize, newEinheit);
	}

	@After
	public void cleanup() throws Exception {
	}
}
