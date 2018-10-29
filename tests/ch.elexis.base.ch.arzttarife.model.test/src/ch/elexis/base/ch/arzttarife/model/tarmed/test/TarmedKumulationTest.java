package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusion;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedExclusive;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedKumulationType;

public class TarmedKumulationTest {
	
	@Test
	public void testGetExclusionsMasterCodeDate(){
		String exclusions = TarmedKumulation.getExclusions("17.0010", LocalDate.now());
		assertEquals("17.0090,17.0090,17.0080,17.0080,17.0080,17.0090,17.0090,17.0080,17.0090,17.0080", exclusions);
	}
	
	@Test
	public void testGetExclusionsMasterCodeMasterArtDate(){
		List<TarmedExclusion> exclusions = TarmedKumulation.getExclusions("17.0010",
			TarmedKumulationType.SERVICE, LocalDate.now(), null);
		assertEquals(10, exclusions.size());
		
		exclusions = TarmedKumulation.getExclusions("17.0010", TarmedKumulationType.SERVICE,
			LocalDate.now(), "KVG");
		assertEquals(6, exclusions.size());
	}
	
	@Test
	public void testGetExclusivesMasterCodeMasterArtDate(){
		List<TarmedExclusive> exclusives = TarmedKumulation.getExclusives("00.1420",
			TarmedKumulationType.SERVICE, LocalDate.now(), null);
		assertEquals(3, exclusives.size());
		exclusives = TarmedKumulation.getExclusives("00.1420", TarmedKumulationType.SERVICE,
			LocalDate.now(), "KVG");
		assertEquals(2, exclusives.size());
	}
	
}
