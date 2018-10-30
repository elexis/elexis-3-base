package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedExtension;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class TarmedLeistungTest {
	
	@Test
	public void queryLoad(){
		ITarmedLeistung loaded = AllTestsSuite.getModelService()
			.load("00.0010-20180101-KVG", ITarmedLeistung.class).get();
		assertTrue(loaded instanceof ITarmedLeistung);
		
		ITarmedExtension extension = loaded.getExtension();
		assertTrue(extension instanceof ITarmedExtension);
		assertEquals(23, extension.getLimits().size());
		assertEquals("H", extension.getLimits().get("LEISTUNG_TYP"));
		
		ITarmedLeistung parent = loaded.getParent();
		assertTrue(parent instanceof ITarmedLeistung);
		assertEquals("00.01.01-20010101-KVG", parent.getId());
		
		List<String> serviceGroups = loaded.getServiceGroups(LocalDate.now());
		assertEquals(3, serviceGroups.size());
		assertTrue(serviceGroups.contains("03"));
		assertTrue(serviceGroups.contains("18"));
		assertTrue(serviceGroups.contains("58"));
		
		assertFalse(loaded.requiresSide());
		assertEquals("H", loaded.getServiceTyp());
	}
	
	@Test
	public void loadArticleInBlock(){
		ITarmedLeistung loaded = AllTestsSuite.getModelService()
			.load("00.0150-20180101-KVG", ITarmedLeistung.class).get();
		assertTrue(loaded instanceof ITarmedLeistung);
		
		List<String> serviceBlocks = loaded.getServiceBlocks(LocalDate.now());
		assertEquals(1, serviceBlocks.size());
		assertTrue(serviceBlocks.contains("01"));
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
	
	@Test
	public void getDistinctAvailableLaws(){
		List<String> availableLaws = TarmedLeistung.getAvailableLaws();
		assertTrue(availableLaws.contains("KVG"));
	}
	
	@Test
	public void getFromCode(){
		IBillable fromCode = TarmedLeistung.getFromCode("00.0010", LocalDate.now(), "KVG");
		assertTrue(fromCode instanceof ITarmedLeistung);
		assertEquals("00.0010-20180101-KVG", fromCode.getId());
		
		fromCode = TarmedLeistung.getFromCode("00.0010", LocalDate.of(2015, 1, 1), "KVG");
		assertEquals("00.0010-20010101-KVG", fromCode.getId());
		
		fromCode = TarmedLeistung.getFromCode("00.0010", null);
		assertEquals("00.0010-20010101", fromCode.getId());
	}
	
}
