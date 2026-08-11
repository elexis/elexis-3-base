package ch.elexis.base.ch.arzttarife.ps25.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Ps25LeistungTest {

	@Test
	public void convertTaxpunkteToInternalPoints() {
		assertEquals(22000, Ps25Leistung.toBilledPoints("220"));
		assertEquals(220, Ps25Leistung.toBilledPoints("2,2"));
		assertEquals(0, Ps25Leistung.toBilledPoints(null));
		assertEquals(0, Ps25Leistung.toBilledPoints("invalid"));
	}
}
