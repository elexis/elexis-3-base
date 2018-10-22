package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.elexis.core.utils.OsgiServiceUtil;

public class TarmedLeistungTest extends AbstractTest {
	
	@Test
	public void queryLoad(){
		ITarmedLeistung loaded = AllTestsSuite.getModelService()
			.load("00.0010-20180101-KVG", ITarmedLeistung.class).get();
		assertTrue(loaded instanceof ITarmedLeistung);
	}
	
	@Test
	public void loadFromStoreToStringService(){
		IStoreToStringService storeToStringService =
			OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Identifiable loadFromString = storeToStringService
			.loadFromString("ch.elexis.data.TarmedLeistung::00.0010-20180101-KVG").get();
		assertEquals("00.0010-20180101-KVG", loadFromString.getId());
		assertTrue(loadFromString instanceof ITarmedLeistung);
		assertEquals("Konsultation, erste 5 Min. (Grundkonsultation)",
			((ITarmedLeistung) loadFromString).getText());
	}
	
	@Test
	public void loadFromCodeElementService(){
		ICodeElementService codeElementService =
			OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString = codeElementService.loadFromString(
			ch.elexis.core.jpa.entities.TarmedLeistung.CODESYSTEM_NAME, "00.0056", null).get();
		assertEquals("00.0056-20180101-KVG", ((Identifiable) loadFromString).getId());
		assertTrue(loadFromString instanceof ITarmedLeistung);
	}
	
}
