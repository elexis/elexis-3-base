package at.medevit.elexis.cobasmira.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.cobasmira.model.CobasMiraMessage;

public class CobasMiraMessageTest {
	private static String headerFile = "rsc/sampleHeader.txt";
	private static String textFile = "rsc/sampleText.txt";
	private static String inputHeader;
	private static String inputText;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		byte[] bufferHeader = new byte[(int) new File(headerFile).length()];
		byte[] bufferText = new byte[(int) new File(textFile).length()];
		FileInputStream isHeader = new FileInputStream(headerFile);
		FileInputStream isText = new FileInputStream(textFile);
		
		isHeader.read(bufferHeader);
		isText.read(bufferText);
		inputHeader = new String(bufferHeader);
		inputText = new String(bufferText);
		isHeader.close();
		isText.close();
	}
	
	@Test
	public void testCobasMiraInstantiation(){
		assertEquals(22, inputHeader.trim().length());
		
		int inputTextNoEntries = inputText.split("\n").length;
		CobasMiraMessage cmm = new CobasMiraMessage();
		cmm.setHeader(inputHeader);
		cmm.setText(inputText);
		assertEquals(02, cmm.getInstrumentCode());
		assertEquals("26-4983", cmm.getSystemIdentification());
		assertEquals(03, cmm.getBlockType());
		assertEquals(inputTextNoEntries, cmm.getNoPatientResults());
	}
}
