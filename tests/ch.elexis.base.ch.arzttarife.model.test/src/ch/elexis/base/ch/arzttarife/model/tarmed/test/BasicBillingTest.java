package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.test.AbstractTest;
import ch.rgw.tools.Result;

public class BasicBillingTest extends AbstractTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void ok_single_00_0010(){
		createEncounter();
		
		ITarmedLeistung code_000010 = AllTestsSuite.getModelService()
			.load("00.0010-20180101-KVG", ITarmedLeistung.class).get();
		Result<IBilled> result = code_000010.getOptifier().add(code_000010, encounter, 1);
		assertTrue(result.isOK());
		
		assertEquals(code_000010.getText(), result.get().getText());
//		assertEquals("0.89", result.get().getvr.getVk_scale());
//		assertEquals(1776, vr.getVk_tp());
//		assertEquals(1581, vr.getVk_preis());
		assertEquals(100, result.get().getPrimaryScale());
		assertEquals(100, result.get().getSecondaryScale());
		//	assertEquals(ElexisTypeMap.TYPE_TARMEDLEISTUNG, vr.getKlasse());
		//	assertEquals(testBehandlungen.get(0).getId(), vr.getBehandlung().getId());
		assertEquals(1, result.get().getAmount(), 0.01);
		//
		//	VerrechenbarTarmedLeistung ivlt_000750 = new VerrechenbarTarmedLeistung(code_000750);
		//	status = ivlt_000750.add(testBehandlungen.get(0), userContact, mandator);
		//	assertTrue(status.getMessage(), !status.isOK());
		//}
	}
	
}
