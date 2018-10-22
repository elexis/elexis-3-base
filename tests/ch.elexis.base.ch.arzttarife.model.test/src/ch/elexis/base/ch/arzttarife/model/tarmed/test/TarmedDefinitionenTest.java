package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedDefinitionen;

public class TarmedDefinitionenTest {
	
	@Test
	public void loadTarmedDefinitionenGetTitle(){
		String title = TarmedDefinitionen.getTitle("PFLICHT", "08");
		assertEquals(
			"Pflichtleistung nur bei medizinischer Indikation gemäss KLV Anhang 1 Ziffer 2.1",
			title);
	}
	
	@Test
	public void loadTarmedDefinitionenGetKuerzel(){
		String kuerzel = TarmedDefinitionen.getKuerzel("PFLICHT", "Nur anwendbar im IV-Bereich");
		assertEquals("16", kuerzel);
	}
}
