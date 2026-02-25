package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.rgw.tools.Result;

public class ComplementaryLeistungTest extends AbstractTest {

	@Test
	public void queryLoad() {
		IComplementaryLeistung loaded = AllTestsSuite.getModelService()
				.load("1134-20140101", IComplementaryLeistung.class).get();
		assertTrue(loaded instanceof IComplementaryLeistung);
	}

	@Test
	public void loadFromStoreToStringService() {
		IStoreToStringService storeToStringService = OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Identifiable loadFromString = storeToStringService
				.loadFromString("ch.elexis.data.ComplementaryLeistung::1134-20140101").get();
		assertEquals("1134-20140101", loadFromString.getId());
		assertTrue(loadFromString instanceof IComplementaryLeistung);
		assertEquals("Reflexzonentherapie, pro 5 Minuten", ((IComplementaryLeistung) loadFromString).getText());
		assertEquals(
				"Beinhaltet Körperreflexzonen, Muskelreflexzonenmassage, sowie Mikrosysteme wie Ohren, Hände und Füsse",
				((IComplementaryLeistung) loadFromString).getDescription());
		OsgiServiceUtil.ungetService(storeToStringService);
	}

	@Test
	public void loadFromCodeElementService() {
		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString = codeElementService.loadFromString("Komplementärmedizin", "1111", null).get();
		assertEquals("1111-20090101", ((Identifiable) loadFromString).getId());
		assertTrue(loadFromString instanceof IComplementaryLeistung);
		OsgiServiceUtil.ungetService(codeElementService);
	}

	@Test
	public void billing() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IComplementaryLeistung complementaryLeistung = (IComplementaryLeistung) codeElementService
				.loadFromString("Komplementärmedizin", "1111", null).get();
		Result<IBilled> result = complementaryLeistung.getOptifier().add(complementaryLeistung, encounter, 1);
		assertTrue(result.isOK());
		assertEquals(1, encounter.getBilled().size());
		IBilled billed = encounter.getBilled().get(0);
		assertEquals(1, billed.getAmount(), 0.01);
		assertEquals(0, billed.getPoints());

		encounter.removeBilled(billed);

		complementaryLeistung.setFixedValue(5000);
		result = complementaryLeistung.getOptifier().add(complementaryLeistung, encounter, 1);
		assertTrue(result.isOK());
		assertEquals(1, encounter.getBilled().size());
		billed = encounter.getBilled().get(0);
		assertEquals(1, billed.getAmount(), 0.01);
		assertEquals(5000, billed.getPoints());
		// complementary uses billing system factor if present
		assertEquals(billed.getFactor() * 50, billed.getPrice().getAmount(), 0.01);

		assertEquals(encounter, billed.getEncounter());
		OsgiServiceUtil.ungetService(codeElementService);
	}
}
