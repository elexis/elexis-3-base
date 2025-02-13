package org.iatrix.bestellung.rose.test.gs1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.iatrix.bestellung.rose.Constants;
import org.iatrix.bestellung.rose.Sender;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.types.Country;
import ch.elexis.core.ui.exchange.XChangeException;


public class BestellungRoseGS1Test {

	private static IModelService modelService;

	private static IOrder order;


	@BeforeClass
	public static void beforeClass() {
		if (modelService == null) {
			modelService = AllTests.getModelService();
		}
	}


	private IArticle loadOrCreateArticle(String gtin, String name, String pharmacode, int packageSize) {
		return modelService.findAll(IArticle.class).stream().filter(a -> gtin.equals(a.getGtin())).findFirst()
				.orElseGet(() -> {
					IArticle newArticle = modelService.create(IArticle.class);
					newArticle.setName(name);
					newArticle.setGtin(gtin);
					newArticle.setPackageSize(packageSize);
					newArticle.setAtcCode(pharmacode);
					modelService.save(newArticle);
					return newArticle;
				});
	}

	private IContact loadOrCreateContact(String description1, String description2, String city, String country,
			String email) {

		Optional<IContact> existingContact = modelService.findAll(IContact.class).stream()
				.filter(contact -> description1.equals(contact.getDescription1())).findFirst();

		if (existingContact.isPresent()) {
			return existingContact.get();
		}
		ConfigServiceHolder.setGlobal(Constants.CFG_ROSE_CLIENT_NUMBER, "999972");

		IContact newContact = modelService.create(IContact.class);
		newContact.setDescription1(description1);
		newContact.setDescription2(description2);
		newContact.setCity(city);
		newContact.setCountry(Country.valueOf(country));
		newContact.setEmail(email);
		newContact.setStreet("Teststraße 123");
		newContact.setZip("12345");

		modelService.save(newContact);

		return newContact;
	}


	private void createOrderWithMultipleArticles(List<String> names, List<String> gtins, List<String> pharmacodes,
			List<Integer> packageSizes, List<Integer> quantities) {
		order = modelService.create(IOrder.class);
		order.setTimestamp(LocalDateTime.now());
		order.setName("JUnit Test Bestellung");
		modelService.save(order);

		IContact roseContact = loadOrCreateContact("Rose Apotheke", "Test Apotheke", "Rupperswil", "CH",
				"test@test.com");

		for (int i = 0; i < names.size(); i++) {
			IArticle article = loadOrCreateArticle(gtins.get(i), names.get(i), pharmacodes.get(i), packageSizes.get(i));

			IOrderEntry entry = modelService.create(IOrderEntry.class);
			entry.setOrder(order);
			entry.setArticle(article);
			entry.setAmount(quantities.get(i));
			entry.setProvider(roseContact);
			modelService.save(entry);
		}

		modelService.refresh(order, true);
	}

	@Test
	public void testCreateOrder() {
		List<String> names = List.of("ASPIRIN S Tabl 500 mg 20 Stk", "Lyrica Kaps 300 mg 168 Stk",
				"Tamec Filmtabl 20 mg 100 Stk");
		List<String> gtins = List.of("7680629110019", "7680570570542", "7680549890404");
		List<String> pharmacodes = List.of("6132151", "2929013", "2304023");
		List<Integer> pkgSizes = List.of(20, 168, 100);
		List<Integer> amounts = List.of(2, 5, 6);

		createOrderWithMultipleArticles(names, gtins, pharmacodes, pkgSizes, amounts);

		assertNotNull("Bestellung wurde nicht erstellt.", order);
		assertFalse("Bestellung enthält keine Einträge!", order.getEntries().isEmpty());
	}

	@Test
	public void testStoreOrder() throws XChangeException {
		assertNotNull("Keine Order vorhanden!", order);

		Sender sender = new Sender();
		sender.store(order);

		assertNotNull("Bestellung konnte nicht gespeichert werden.", order);
	}

	/**
	 * Testet den Export einer Bestellung über den Sender.
	 */
	@Test
	public void testFinalizeExport() throws XChangeException {
		assertNotNull("Keine Order vorhanden!", order);
		Sender sender = new Sender(true);
		sender.store(order);
		sender.finalizeExport();
	}
}
