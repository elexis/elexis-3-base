package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedGroup;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLimitation;

public class TarmedGroupTest {

	@Test
	public void findGroup() {
		ITarmedGroup found = TarmedGroup.find("05", "KVG", LocalDate.now()).get();
		assertTrue(found instanceof ITarmedGroup);
		assertEquals("GRP05-20180101-KVG", found.getId());
	}

	@Test
	public void getExclusions() {
		ITarmedGroup grp01 = AllTestsSuite.getModelService().load("GRP01-20010101", ITarmedGroup.class).get();
		assertTrue(grp01 instanceof ITarmedGroup);
		List<TarmedExclusion> exclusions = ((TarmedGroup) grp01).getExclusions(LocalDate.now());
		assertEquals(10, exclusions.size());
	}

	@Test
	public void getLimitations() {
		// http://www.tarmed-browser.ch/de/leistungsgruppen/04-arztliche-leistungen-in-abwesenheit-des-patienten-bei-personen-uber-6-jahre-und-unter-75-jahre
		ITarmedGroup grp01 = AllTestsSuite.getModelService().load("GRP04-20180101-KVG", ITarmedGroup.class).get();
		assertTrue(grp01 instanceof ITarmedGroup);
		List<TarmedLimitation> limitations = grp01.getLimitations();
		assertEquals(1, limitations.size());
		assertEquals("Codes der Gruppe 04 maximal 30 Mal pro 3 Monat(e)", limitations.get(0).toString());
	}

}
