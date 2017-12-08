package at.medevit.elexis.cobasmira.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import at.medevit.elexis.cobasmira.model.CobasMiraPatientResult;

public class CobasMiraPatientResultTest {
	
	@Test
	public void testPatientResultInstantiation(){
		String testString = "20 R B1 GPTD 01 0001 BENZER     +3.38719E+01 00 21 O N 00";
		CobasMiraPatientResult resultTest = new CobasMiraPatientResult(testString);
		
		assertEquals(20, resultTest.getLineCode());
		assertEquals('R', resultTest.getWorklistType());
		assertEquals("B1", resultTest.getTestNumber());
		assertEquals("GPTD", resultTest.getTestName());
		assertEquals(01, resultTest.getTestResultIndex());
		assertEquals(0001, resultTest.getSampleCupNumber());
		assertEquals("BENZER", resultTest.getPatientIdentification());
		assertEquals(33.8719, resultTest.getConcentration(), 0.00001);
		assertEquals(0, resultTest.getNoOfDigitsBehindDecimalPoint());
		assertEquals(21, resultTest.getUnitCode());
		assertEquals('O', resultTest.getResultType());
		assertEquals('N', resultTest.getFlag());
		assertEquals(0, resultTest.getRemark());
	}
	
	@Test
	public void testMalformedTestPatientResultInstantiation(){
		String testString = "";
		CobasMiraPatientResult resultTest = new CobasMiraPatientResult(testString);
		
		assertEquals(0, resultTest.getLineCode());
		assertNotSame('R', resultTest.getWorklistType());
		assertNotSame("B1", resultTest.getTestNumber());
		assertNotSame("GPTD", resultTest.getTestName());
		assertNotSame(01, resultTest.getTestResultIndex());
		assertNotSame(0001, resultTest.getSampleCupNumber());
		assertNotSame("BENZER", resultTest.getPatientIdentification());
		assertEquals(0, resultTest.getNoOfDigitsBehindDecimalPoint());
		assertNotSame(21, resultTest.getUnitCode());
		assertNotSame('O', resultTest.getResultType());
		assertNotSame('N', resultTest.getFlag());
		assertEquals(0, resultTest.getRemark());
	}
}
