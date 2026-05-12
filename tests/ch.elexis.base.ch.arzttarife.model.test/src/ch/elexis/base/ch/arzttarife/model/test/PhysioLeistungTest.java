package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.physio.PhysioPackage;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.rgw.tools.Result;

public class PhysioLeistungTest extends AbstractTest {

	@Test
	public void queryLoad() {
		IQuery<IPhysioLeistung> query = AllTestsSuite.getModelService().getQuery(IPhysioLeistung.class);
		query.and(PhysioPackage.Literals.IPHYSIO_LEISTUNG__ZIFFER, COMPARATOR.EQUALS, "7330");
		Optional<IPhysioLeistung> loaded = query.executeSingleResult();
		assertTrue(loaded.isPresent());
	}

	@Test
	public void loadFromStoreToStringService() {
		IQuery<IPhysioLeistung> query = AllTestsSuite.getModelService().getQuery(IPhysioLeistung.class);
		query.and(PhysioPackage.Literals.IPHYSIO_LEISTUNG__ZIFFER, COMPARATOR.EQUALS, "7330");
		Optional<IPhysioLeistung> loaded = query.executeSingleResult();

		IStoreToStringService storeToStringService = OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Optional<String> string = storeToStringService.storeToString(loaded.get());
		assertTrue(string.isPresent());
		Identifiable loadFromString = storeToStringService.loadFromString(string.get()).get();
		assertEquals("7330", ((IPhysioLeistung) loadFromString).getCode());
		assertEquals("Sitzungspauschale für Gruppentherapie", ((IPhysioLeistung) loadFromString).getText());
	}

	@Test
	public void loadFromCodeElementService() {
		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString = codeElementService.loadFromString("Physiotherapie", "7311", null).get();
		assertTrue(loadFromString instanceof IPhysioLeistung);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void billing() throws ParseException {
		createEncounter();

		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor("Physiotherapie", 0.89,
				LocalDate.of(2000, 1, 1));

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPhysioLeistung sitzungsPauschale = (IPhysioLeistung) codeElementService
				.loadFromString("Physiotherapie", "7301", null).get();
		Result<IBilled> result = sitzungsPauschale.getOptifier().add(sitzungsPauschale, encounter, 1);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
		IBilled billed = encounter.getBilled().get(0);
		assertEquals(1, billed.getAmount(), 0.01);
		assertEquals(sitzungsPauschale.getText(), billed.getText());
		assertEquals(100, billed.getPrimaryScale());
		assertEquals(100, billed.getSecondaryScale());
		assertEquals(4800, billed.getPoints());
		assertEquals(4272, billed.getTotal().getCents());
		assertEquals(sitzungsPauschale.getText(), billed.getText());
		assertEquals(encounter, billed.getEncounter());

		coreModelService.remove(factor);
	}
}
