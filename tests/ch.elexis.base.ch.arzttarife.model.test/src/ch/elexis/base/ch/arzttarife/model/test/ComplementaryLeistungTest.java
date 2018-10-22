package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.elexis.core.utils.OsgiServiceUtil;

public class ComplementaryLeistungTest extends AbstractTest {
	
	@Test
	public void queryLoad(){
		IComplementaryLeistung loaded = AllTestsSuite.getModelService()
			.load("1134-20140101", IComplementaryLeistung.class).get();
		assertTrue(loaded instanceof IComplementaryLeistung);
	}
	
	@Test
	public void loadFromStoreToStringService(){
		IStoreToStringService storeToStringService =
			OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Identifiable loadFromString = storeToStringService
			.loadFromString("ch.elexis.data.ComplementaryLeistung::1134-20140101").get();
		assertEquals("1134-20140101", loadFromString.getId());
		assertTrue(loadFromString instanceof IComplementaryLeistung);
		assertEquals("Reflexzonentherapie, pro 5 Minuten",
			((IComplementaryLeistung) loadFromString).getText());
		assertEquals(
			"Beinhaltet Körperreflexzonen, Muskelreflexzonenmassage, sowie Mikrosysteme wie Ohren, Hände und Füsse",
			((IComplementaryLeistung) loadFromString).getDescription());
	}
	
	@Test
	public void loadFromCodeElementService(){
		ICodeElementService codeElementService =
			OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString =
			codeElementService.loadFromString("Komplementärmedizin", "1111", null).get();
		assertEquals("1111-20090101", ((Identifiable) loadFromString).getId());
		assertTrue(loadFromString instanceof IComplementaryLeistung);
	}
	
	@Test
	public void billing() throws ParseException{
		//		createEncounter();
		//		
		//		IPhysioLeistung sitzungsPauschale = AllTestsSuite.getModelService()
		//			.load("If1afa9fb481c7dee0295", IPhysioLeistung.class).get();
		//		Result<IBilled> result = sitzungsPauschale.getOptifier().add(sitzungsPauschale, encounter, 1.5);
		//		assertTrue(result.isOK());
		//		assertFalse(encounter.getBilled().isEmpty());
		//		IBilled billed = encounter.getBilled().get(0);
		//		assertEquals(1.5, billed.getAmount(), 0.01);
		//		//		assertEquals(sitzungsPauschale.getSellingPrice().multiply(1.5), billed.getTotal());
		//		//		assertEquals(sitzungsPauschale.getPurchasePrice(), billed.getNetPrice());
		//		//		assertEquals(sitzungsPauschale.getName(), billed.getText());
		//		assertEquals(encounter, billed.getEncounter());
		
	}
}
