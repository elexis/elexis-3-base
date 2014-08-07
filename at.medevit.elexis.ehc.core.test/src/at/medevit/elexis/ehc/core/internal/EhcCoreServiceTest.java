package at.medevit.elexis.ehc.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.data.Patient;
import ehealthconnector.cda.documents.ch.CdaCh;
import ehealthconnector.cda.documents.ch.ConvenienceUtilsEnums.AdministrativeGenderCode;

public class EhcCoreServiceTest {
	
	private static Patient patient;
	
	@BeforeClass
	public static void before() throws IOException{
		patient = new Patient("name", "firstname", "01.01.2000", Patient.FEMALE);
	}
	
	@AfterClass
	public static void after(){
		patient.delete();
	}
	
	@Test
	public void testGetPatientDocument(){
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getPatientDocument(patient);
		assertNotNull(cda);
		ehealthconnector.cda.documents.ch.Patient cdaPatient = cda.cGetPatient();
		assertNotNull(cdaPatient);
		assertEquals("name", cdaPatient.cGetName().cGetName());
		assertEquals("firstname", cdaPatient.cGetName().cGetFirstName());
		assertEquals(AdministrativeGenderCode.Female, cdaPatient.cGetGender());
		assertEquals("01.01.2000", cdaPatient.cGetBirthDate());
	}
	
	@Test
	public void testWritePatientDocument() throws UnsupportedEncodingException{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getPatientDocument(patient);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		cda.cPrintXmlToStream(output);
		assertTrue(output.size() > 0);
		String xml = output.toString("UTF-8");
		assertTrue(xml.contains("name"));
		assertTrue(xml.contains("firstname"));
	}
	
	@Test
	public void testWritePatientDocumentFile() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getPatientDocument(patient);
		
		String userHome = System.getProperty("user.home");
		String outFilePath = userHome + File.separator + "testPatientCda.xml";
		cda.cSaveToFile(outFilePath);
		File outFile = new File(outFilePath);
		assertTrue(outFile.exists());
		assertTrue(outFile.isFile());
		assertTrue(outFile.length() > 0);
		outFile.deleteOnExit();
	}
	
	@Test
	public void testGetDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getPatientDocument(patient);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		cda.cPrintXmlToStream(output);
		assertTrue(output.size() > 0);
		ByteArrayInputStream documentInput = new ByteArrayInputStream(output.toByteArray());
		CdaCh cdach = service.getDocument(documentInput);
		assertNotNull(cdach);
		ehealthconnector.cda.documents.ch.Patient readPatient = cdach.cGetPatient();
		assertEquals("name", readPatient.cGetName().cGetName());
		assertEquals("firstname", readPatient.cGetName().cGetFirstName());
		assertEquals(AdministrativeGenderCode.Female, readPatient.cGetGender());
	}
}
