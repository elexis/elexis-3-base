package ch.elexis.regiomed.order.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.IStock;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.regiomed.order.handler.RegiomedSender;
import ch.elexis.regiomed.order.preferences.RegiomedConstants;

public class RegiomedSenderTest {

	private HttpTestServer server;
	private IModelService modelService;

	private IContact supplier;
	private IArticle article;
	private IStock stock;
	private IOrder order;
	private IOrderEntry entry;

	@Before
	public void setUp() throws Exception {
		server = new HttpTestServer();
		server.start();

		modelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
				.orElseThrow(() -> new IllegalStateException("ModelService nicht gefunden"));

		ConfigServiceHolder.get().set(RegiomedConstants.PREF_BASE_URL, "http://localhost:" + HttpTestServer.HTTP_PORT);
		ConfigServiceHolder.get().set(RegiomedConstants.PREF_EMAIL, "test@arzt.ch");
		ConfigServiceHolder.get().set(RegiomedConstants.PREF_PASSWORD, "testpass");
		ConfigServiceHolder.get().set("ch.elexis.regiomed.checkOrder", false);

		createTestModelData();
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			try {
				server.stop();
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (entry != null) {
			modelService.remove(entry);
		}
		if (order != null) {
			modelService.remove(order);
		}
		if (article != null) {
			modelService.remove(article);
		}
		if (stock != null) {
			modelService.remove(stock);
		}
		if (supplier != null) {
			modelService.remove(supplier);
		}
	}

	private void createTestModelData() {
		supplier = modelService.create(IContact.class);
		supplier.setCode("SUP1");
		supplier.setDescription1("Regiomed Lieferant");
		modelService.save(supplier);

		ConfigServiceHolder.get().set(RegiomedConstants.CFG_REGIOMED_SUPPLIER, supplier.getId());

		article = modelService.create(IArticle.class);
		article.setName("Aspirin Test");
		article.setCode("12345");
		article.setTyp(ArticleTyp.EIGENARTIKEL);
		article.setGtin("7600000000001");
		article.setExtInfo("pharmacode", "12345");
		modelService.save(article);

		stock = modelService.create(IStock.class);
		stock.setCode("LAGER1");
		modelService.save(stock);

		order = modelService.create(IOrder.class);
		order.setTimestamp(LocalDateTime.now());
		order.setName("Test Order Regiomed");
		modelService.save(order);
	}

	@Test
	public void testFullOrderProcess() throws Exception {
		entry = modelService.create(IOrderEntry.class);
		entry.setOrder(order);
		entry.setArticle(article);
		entry.setStock(stock);
		entry.setProvider(supplier);
		entry.setAmount(5);
		entry.setState(OrderEntryState.OPEN);
		modelService.save(entry);

		String jsonResponse = "{" + "\"checkSuccess\": true," + "\"orderSent\": true," + "\"articlesOK\": 1,"
				+ "\"articlesNOK\": 0," + "\"articles\": ["
				+ "  {\"pharmaCode\": 12345, \"eanID\": 7600000000001, \"success\": true}" + "]"
				+ "}";
		server.setResponseBody(jsonResponse);

		RegiomedSender sender = new RegiomedSender();
		assertTrue("Sender sollte die Bestellung verarbeiten können", sender.canHandle(order));

		sender.store(order);
		sender.finalizeExport();

		String sentBody = server.getRequestBody();
		assertNotNull(sentBody);
		assertTrue(sentBody.contains("\"quantity\":5"));

		Optional<IOrderEntry> loadedEntry = modelService.load(entry.getId(), IOrderEntry.class);
		assertTrue(loadedEntry.isPresent());
		IOrderEntry freshEntry = loadedEntry.get();
		modelService.refresh(freshEntry);

		assertEquals("Status sollte auf ORDERED stehen", OrderEntryState.ORDERED, freshEntry.getState());
	}
}