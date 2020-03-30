package ch.elexis.connect.sysmex.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import ch.elexis.connect.sysmex.packages.AbstractUrinData.ResultInfo;
import ch.elexis.connect.sysmex.test.AllTests;

public class SysmexUc1000Test {
	
	@Test
	public void uc1000_1() throws IOException{
		InputStream in = AllTests.class.getResourceAsStream("/rsc/UC-1000_1.txt");
		String content = AllTests.getTextBetween(AllTests.STX, AllTests.ETX, in);
		assertNotNull(content);
		UC1000Data data = new UC1000Data();
		assertEquals(data.getSize(), content.length());
		data.parse(content);
		
		ResultInfo uroInfo = data.getResultInfo("uro");
		assertNotNull(uroInfo);
		assertTrue(uroInfo.isAnalyzed());
		assertEquals("3+", uroInfo.getQualitativValue());
		assertEquals("8.0", uroInfo.getSemiQualitativValue());
		
		ResultInfo bidInfo = data.getResultInfo("bid");
		assertNotNull(bidInfo);
		assertTrue(bidInfo.isAnalyzed());
		assertEquals("-", bidInfo.getQualitativValue());
		assertEquals("", bidInfo.getSemiQualitativValue());
		
		ResultInfo bilInfo = data.getResultInfo("bil");
		assertNotNull(bilInfo);
		assertTrue(bilInfo.isAnalyzed());
		
		ResultInfo ketInfo = data.getResultInfo("ket");
		assertNotNull(ketInfo);
		assertTrue(ketInfo.isAnalyzed());
		
		ResultInfo gluInfo = data.getResultInfo("glu");
		assertNotNull(gluInfo);
		assertTrue(gluInfo.isAnalyzed());
		
		ResultInfo proInfo = data.getResultInfo("pro");
		assertNotNull(proInfo);
		assertTrue(proInfo.isAnalyzed());
		
		ResultInfo phInfo = data.getResultInfo("ph");
		assertNotNull(phInfo);
		assertTrue(phInfo.isAnalyzed());
		
		ResultInfo nitInfo = data.getResultInfo("nit");
		assertNotNull(nitInfo);
		assertTrue(nitInfo.isAnalyzed());
		
		ResultInfo leuInfo = data.getResultInfo("leu");
		assertNotNull(leuInfo);
		assertTrue(leuInfo.isAnalyzed());
		
		ResultInfo sgInfo = data.getResultInfo("sg");
		assertNotNull(sgInfo);
		assertTrue(sgInfo.isAnalyzed());
		
		ResultInfo creInfo = data.getResultInfo("cre");
		assertNotNull(creInfo);
		assertTrue(creInfo.isAnalyzed());
		
		ResultInfo albInfo = data.getResultInfo("alb");
		assertNotNull(albInfo);
		assertTrue(albInfo.isAnalyzed());
	}
	
	@Test
	public void uc1000_2() throws IOException{
		InputStream in = AllTests.class.getResourceAsStream("/rsc/UC-1000_2.txt");
		String content = AllTests.getTextBetween(AllTests.STX, AllTests.ETX, in);
		assertNotNull(content);
		UC1000Data data = new UC1000Data();
		assertEquals(data.getSize(), content.length());
		data.parse(content);
		
		ResultInfo uroInfo = data.getResultInfo("uro");
		assertNotNull(uroInfo);
		assertTrue(uroInfo.isAnalyzed());
		assertEquals("normal", uroInfo.getQualitativValue());
		assertEquals("", uroInfo.getSemiQualitativValue());
		
		String patId = data.getPatientId();
		assertNotNull(patId);
		assertFalse(patId.isEmpty());
		assertEquals("340", patId);
	}
}
