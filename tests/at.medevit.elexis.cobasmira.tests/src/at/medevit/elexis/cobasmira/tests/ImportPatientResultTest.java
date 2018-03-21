package at.medevit.elexis.cobasmira.tests;

import junit.framework.Assert;

import org.junit.Test;

import at.medevit.elexis.cobasmira.resulthandler.ImportPatientResult;

public class ImportPatientResultTest {
	
	@Test
	public void roundingTest(){
		float f = Float.parseFloat("1234.56789");
		float f2 = Float.parseFloat("12.345678");
		
		Assert.assertEquals(1235.0, ImportPatientResult.roundToDecimals(f, 0), 0);
		Assert.assertEquals(1234.6, ImportPatientResult.roundToDecimals(f, 1), 0.0001);
		Assert.assertEquals(1234.57, ImportPatientResult.roundToDecimals(f, 2), 0.0001);
		Assert.assertEquals(1234.568, ImportPatientResult.roundToDecimals(f, 3), 0.0001);
		
		Assert.assertEquals(12, ImportPatientResult.roundToDecimals(f2, 0), 0.0001);
		Assert.assertEquals(12.3, ImportPatientResult.roundToDecimals(f2, 1), 0.0001);
		Assert.assertEquals(12.35, ImportPatientResult.roundToDecimals(f2, 2), 0.0001);
		Assert.assertEquals(12.346, ImportPatientResult.roundToDecimals(f2, 3), 0.0001);
	}
	
}
