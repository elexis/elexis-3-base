package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.core.test.AbstractTest;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;

public class PhysioLeistungTest extends AbstractTest {
	
	@Test
	public void queryLoad(){
		IPhysioLeistung loaded = AllTestsSuite.getModelService()
			.load("If1afa9fb481c7dee0295", IPhysioLeistung.class).get();
		assertTrue(loaded instanceof IPhysioLeistung);
	}
	
	@Test
	public void loadFromStoreToStringService(){
		IStoreToStringService storeToStringService =
			OsgiServiceUtil.getService(IStoreToStringService.class).get();
		Identifiable loadFromString = storeToStringService
			.loadFromString("ch.elexis.data.PhysioLeistung::If1afa9fb481c7dee0295").get();
		assertEquals("If1afa9fb481c7dee0295", loadFromString.getId());
		assertTrue(loadFromString instanceof IPhysioLeistung);
		assertEquals("Sitzungspauschale für Medizinische Trainingstherapie (MTT)",
			((IPhysioLeistung) loadFromString).getText());
	}
	
	@Test
	public void loadFromCodeElementService(){
		ICodeElementService codeElementService =
			OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString =
			codeElementService.loadFromString("Physiotherapie", "7311", null).get();
		assertEquals("Z28cef8c54b698df80289", ((Identifiable) loadFromString).getId());
		assertTrue(loadFromString instanceof IPhysioLeistung);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void billing() throws ParseException{
		createEncounter();
		
		//		assertEquals(validDefault.getTitel(), vr.getLeistungenText());
		//		assertEquals("0.89", vr.getVk_scale());
		//		assertEquals(4800, vr.getVk_tp());
		//		assertEquals(4272, vr.getVk_preis());
		//		assertEquals(100, vr.getScale());
		//		assertEquals(100, vr.getScale2());
		//		assertEquals(validDefault.getId(), vr.getLeistungenCode());
		//		assertEquals(testBehandlungen.get(0).getId(), vr.getBehandlung().getId());
		//		assertEquals(ElexisTypeMap.TYPE_PHYSIOLEISTUNG, vr.getKlasse());
		//		assertEquals(1, vr.getZahl());
		
		IPhysioLeistung sitzungsPauschale = AllTestsSuite.getModelService()
			.load("Gf7674a6c1f947dfc0287", IPhysioLeistung.class).get();
		Result<IBilled> result =
			sitzungsPauschale.getOptifier().add(sitzungsPauschale, encounter, 1);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
		IBilled billed = encounter.getBilled().get(0);
		assertEquals(1, billed.getAmount(), 0.01);
		assertEquals(sitzungsPauschale.getText(), billed.getText());
		assertEquals(100, billed.getPrimaryScale());
		assertEquals(100, billed.getSecondaryScale());
		//		assertEquals(4800, billed.getPoints());
		//		assertEquals(4272, billed.getTotal());
		//		assertEquals(sitzungsPauschale.getSellingPrice().multiply(1.5), billed.getTotal());
		//		assertEquals(sitzungsPauschale.getPurchasePrice(), billed.getNetPrice());
		//		assertEquals(sitzungsPauschale.getName(), billed.getText());
		assertEquals(encounter, billed.getEncounter());
		
	}
}
