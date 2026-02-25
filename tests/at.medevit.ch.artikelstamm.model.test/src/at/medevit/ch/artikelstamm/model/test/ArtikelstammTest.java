package at.medevit.ch.artikelstamm.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.elexis.core.test.util.TestUtil;
import ch.elexis.core.types.ArticleSubTyp;
import ch.elexis.core.types.ArticleTyp;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;

public class ArtikelstammTest extends AbstractTest {

	private static IElexisEntityManager entityManager;
	private static IModelService modelService;

	@BeforeClass
	public static void beforeClass() throws IOException {
		modelService = OsgiServiceUtil.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model)").get();
		entityManager = OsgiServiceUtil.getService(IElexisEntityManager.class).get();
		entityManager.getEntityManager(); // initialize the db

		assertTrue(entityManager.executeSQLScript("test_initArtikelstamm",
				TestUtil.loadFile(ArtikelstammTest.class, "/rsc/artstamm.sql")));
	}

	@Test
	public void createRemove() {
		IArtikelstammItem item = modelService.create(IArtikelstammItem.class);
		assertNotNull(item);
		// create event?
		modelService.remove(item);
	}

	@Test
	public void loadFromStoreToStringService() {
		IStoreToStringService storeToStringService = OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Identifiable loadFromString = storeToStringService
				.loadFromString("ch.artikelstamm.elexis.common.ArtikelstammItem::0768043838016013402350116").get();
		assertEquals("0768043838016013402350116", loadFromString.getId());
		assertTrue(loadFromString instanceof IArtikelstammItem);
		assertEquals("Dafalgan Sirup 30 mg/ml Kind 90 ml", ((IArtikelstammItem) loadFromString).getName());
	}

	@Test
	public void loadFromCodeElementService() {
		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString = codeElementService.loadFromString("Artikelstamm", "7680438380160", null).get();
		assertEquals("0768043838016013402350116", ((Identifiable) loadFromString).getId());
		assertTrue(loadFromString instanceof IArtikelstammItem);
	}

	@Test
	public void queryLoadImportedArticles() throws ParseException {
		// 0768047505038514989010116, 1537373533999, 0, P, 0, 116, 7680475050385,
		// 1498901,
		// Dafalgan 150 Supp 150 mg 10 Stk, , N02BE01, 7601001010703, Bristol-Myers
		// Squibb SA,
		// 1.11, 2.05, 10, 1, D, 0, , , , , 0, 10, 0, , , , 4750502,

		IArtikelstammItem dafalganArticle = modelService.load("0768047505038514989010116", IArtikelstammItem.class)
				.get();
		assertEquals(ArticleTyp.ARTIKELSTAMM, dafalganArticle.getTyp());
		assertEquals(ArticleSubTyp.PHARMA, dafalganArticle.getSubTyp());
		assertFalse(dafalganArticle.isProduct());
		assertEquals("Dafalgan 150 Supp 150 mg 10 Stk", dafalganArticle.getText());
		assertEquals(dafalganArticle.getText(), dafalganArticle.getName());
		assertEquals("7680475050385", dafalganArticle.getGtin());
		assertEquals(dafalganArticle.getGtin(), dafalganArticle.getCode());
		assertEquals("1498901", dafalganArticle.getPHAR());
		assertEquals("N02BE01", dafalganArticle.getAtcCode());
		assertEquals(new Money("1.11"), dafalganArticle.getPurchasePrice());
		assertEquals(new Money("2.05"), dafalganArticle.getSellingPrice());
		assertEquals(10, dafalganArticle.getPackageSize());
		assertTrue(dafalganArticle.isInSLList());
		assertTrue(dafalganArticle.isObligation());
		assertFalse(dafalganArticle.isLimited());
		assertFalse(dafalganArticle.isInLPPV());
		assertFalse(dafalganArticle.isNarcotic());
		assertFalse(dafalganArticle.isCalculatedPrice());
		assertEquals("4750502", dafalganArticle.getProduct().getId());
		assertEquals(Integer.valueOf(10), dafalganArticle.getDeductible());
		assertEquals("Bristol-Myers Squibb SA", dafalganArticle.getManufacturerLabel());
		assertEquals("D", dafalganArticle.getSwissmedicCategory());

		IArtikelstammItem coDafalganProduct = modelService.load("5132101", IArtikelstammItem.class).get();
		assertTrue(coDafalganProduct.isProduct());
		assertEquals(ArticleSubTyp.UNKNOWN, coDafalganProduct.getSubTyp());
		List<IArticle> packages = coDafalganProduct.getPackages();
		assertEquals(2, packages.size());

		IQuery<IArtikelstammItem> query = modelService.getQuery(IArtikelstammItem.class);
		query.and("gtin", COMPARATOR.LIKE, "768047504%");
		List<IArtikelstammItem> result = query.execute();
		assertEquals(2, result.size());
	}

	@Test
	public void setGetUnsetUserDefinedPrice() throws ParseException {
		IArtikelstammItem coDafalganArticle = modelService.load("0768051321014614988870116", IArtikelstammItem.class)
				.get();
		assertEquals(new Money("6.05"), coDafalganArticle.getSellingPrice());
		assertFalse(coDafalganArticle.isUserDefinedPrice());

		coDafalganArticle.setUserDefinedPriceValue(new Money("7.12"));
		assertEquals(new Money("7.12"), coDafalganArticle.getSellingPrice());
		assertTrue(coDafalganArticle.isUserDefinedPrice());

		coDafalganArticle.restoreOriginalSellingPrice();
		assertEquals(new Money("6.05"), coDafalganArticle.getSellingPrice());
		assertFalse(coDafalganArticle.isUserDefinedPrice());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void billing() throws ParseException {
		createEncounter();
		createUserSetActiveInContext();

		IArtikelstammItem dafalganArticle = modelService.load("0768047505038514989010116", IArtikelstammItem.class)
				.get();
		assertEquals("402", dafalganArticle.getCodeSystemCode());

		Result<IBilled> result = dafalganArticle.getOptifier().add(dafalganArticle, encounter, 1.5);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
		IBilled billed = encounter.getBilled().get(0);
		assertEquals(1.5, billed.getAmount(), 0.01);
		assertEquals(dafalganArticle.getSellingPrice().multiply(1.5), billed.getTotal());
		assertEquals(dafalganArticle.getPurchasePrice(), billed.getNetPrice());
		assertEquals(dafalganArticle.getName(), billed.getText());
		assertEquals(encounter, billed.getEncounter());

	}
}
