package ch.elexis.connect.sysmex.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import ch.elexis.connect.sysmex.packages.AbstractUrinData.ResultInfo;
import ch.elexis.connect.sysmex.packages.UC1000Data;

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
		ResultInfo bilInfo = data.getResultInfo("bil");
		assertNotNull(bilInfo);
		assertTrue(bilInfo.isAnalyzed());
	}
}
