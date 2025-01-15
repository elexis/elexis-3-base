/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.projecthusky.common.communication.DocumentMetadata;
import org.projecthusky.common.enums.AdministrativeGender;
import org.projecthusky.common.enums.DocumentDescriptor;
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;
import org.projecthusky.common.model.Address;
import org.projecthusky.common.model.Identificator;
import org.projecthusky.common.model.Telecom;
import org.projecthusky.common.utils.xml.XmlMarshaller;
import org.projecthusky.communication.ConvenienceCommunication;
import org.projecthusky.communication.xd.xdm.DocumentContentAndMetadata;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Xid;


public class EhcCoreServiceTest {
	
	private static Patient patient;
	private static Mandant mandant;
	
	@BeforeClass
	public static void before() throws IOException{
		patient = new Patient("name", "firstname", "01.01.2000", Patient.FEMALE);
		patient.set(Kontakt.FLD_PHONE1, "+01555123");
		patient.set(Kontakt.FLD_MOBILEPHONE, "+01444132");
		Anschrift anschrift = new Anschrift();
		anschrift.setOrt("City");
		anschrift.setPlz("123");
		anschrift.setStrasse("Street 1");
		patient.setAnschrift(anschrift);
		addAHVNumber(patient, 1);
		
		mandant = new Mandant("mandant", "firstname", "02.02.2002", Mandant.MALE);
		mandant.set(Kontakt.FLD_PHONE1, "+01555987");
		mandant.set(Kontakt.FLD_MOBILEPHONE, "+01444987");
		anschrift = new Anschrift();
		anschrift.setOrt("City");
		anschrift.setPlz("987");
		anschrift.setStrasse("Street 2");
		mandant.setAnschrift(anschrift);
		mandant.addXid(XidConstants.DOMAIN_EAN, "2000000000002", true);
		Xid.localRegisterXIDDomainIfNotExists(XidConstants.DOMAIN_KSK, "KSK/ZSR-Nr", Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
		mandant.addXid(XidConstants.DOMAIN_KSK, "C000002", true);
	}
	
	private static void addAHVNumber(Patient pat, int index){
		String country = "756";
		String number = String.format("%09d", index);
		StringBuilder ahvBuilder = new StringBuilder(country + number);
		ahvBuilder.append(getCheckNumber(ahvBuilder.toString()));
		
		pat.addXid(XidConstants.DOMAIN_AHV, ahvBuilder.toString(), true);
	}
	
	private static String getCheckNumber(String string){
		int sum = 0;
		for (int i = 0; i < string.length(); i++) {
			// reveresd order
			char character = string.charAt((string.length() - 1) - i);
			int intValue = Character.getNumericValue(character);
			if (i % 2 == 0) {
				sum += intValue * 3;
			} else {
				sum += intValue;
			}
		}
		return Integer.toString(sum % 10);
	}

	@AfterClass
	public static void after(){
		patient.delete();
		mandant.delete();
	}
	
	@Test
	public void testGetPatientDocument(){
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		assertNotNull(cda);
		org.projecthusky.common.model.Patient cdaPatient = service.getPatient(cda);
		assertNotNull(cdaPatient);
		assertEquals("name", cdaPatient.getName().getFamily());
		assertEquals("firstname", cdaPatient.getName().getGiven());
		assertEquals(AdministrativeGender.FEMALE, cdaPatient.getAdministrativeGenderCode());
		Calendar bDay = Calendar.getInstance();
		bDay.set(2000, 00, 01, 00, 00, 00);
		bDay.set(Calendar.MILLISECOND, 00);
		assertEquals(bDay.getTime(), cdaPatient.getBirthday());
		List<Address> addresses = cdaPatient.getAddresses();
		assertFalse(addresses.isEmpty());
		assertEquals("City", addresses.get(0).getCity());
		
		List<Telecom> phones = cdaPatient.getTelecoms();
		assertFalse(phones.isEmpty());
		assertTrue(phones.stream().filter(p -> "+01555123".equals(p.getValue())).findAny().isPresent());
	}
	
	@Test
	public void testWritePatientDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String xml = XmlMarshaller.marshall(cda);
		assertTrue(xml.contains("name"));
		assertTrue(xml.contains("firstname"));
	}
	
	@Test
	public void testWritePatientDocumentFile() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		
		String userHome = System.getProperty("user.home");
		String outFilePath = userHome + File.separator + "testPatientCda.xml";
		service.saveToFile(cda, outFilePath);
		File outFile = new File(outFilePath);
		assertTrue(outFile.exists());
		assertTrue(outFile.isFile());
		assertTrue(outFile.length() > 0);
		outFile.deleteOnExit();
	}
	
	@Test
	public void testGetDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String xmlString = XmlMarshaller.marshall(cda);
		assertTrue(xmlString.length() > 0);
		ByteArrayInputStream documentInput = new ByteArrayInputStream(xmlString.getBytes());
		POCDMT000040ClinicalDocument document = service.loadDocument(documentInput);
		assertNotNull(document);
		org.projecthusky.common.model.Patient readPatient = service.getPatient(document);
		assertEquals("name", readPatient.getName().getFamily());
		assertEquals("firstname", readPatient.getName().getGiven());
		assertEquals(AdministrativeGender.FEMALE, readPatient.getAdministrativeGenderCode());
	}
	
	@Test
	public void testGetXdmAsStream() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		
		InputStream input = service.getXdmAsStream(cda);
		assertTrue(input != null);
		File testTmp = File.createTempFile("ehccoretest_", ".tmp");
		try(FileOutputStream fout = new FileOutputStream(testTmp)) {
			IOUtils.copy(input, fout);
			input.close();
		}
		
		List<org.projecthusky.common.model.Patient> patients = service.getXdmPatients(testTmp);
		assertFalse(patients.isEmpty());
		List<Identificator> ids = patients.get(0).getIds();
		assertFalse(ids.isEmpty());
		Identificator id = ids.get(0);
		assertNotNull(id);
		assertEquals("7560000000011", id.getExtension());
	}
	
	@Test
	public void testWriteXdm() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		POCDMT000040ClinicalDocument cda = service.createDocument(patient, mandant);
		String xmlString = XmlMarshaller.marshall(cda);
		
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		DocumentMetadata metaData = conCom.addDocument(DocumentDescriptor.CDA_R2,
				new ByteArrayInputStream(xmlString.getBytes()));
		assertNotNull(metaData);
		List<Identificator> ids = service.getPatient(cda).getIds();
		if (!ids.isEmpty()) {
			metaData.setDestinationPatientId(ids.get(0));
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		conCom.createXdmContents(output);
		assertTrue(output.size() > 0);
	}
	
	@Test
	public void testReadXdm() throws Exception {
		File xdmFile = createTempFile("/rsc/xdm/test_xdm.zip");

		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		List<DocumentContentAndMetadata> documents = service.getXdmDocuments(xdmFile);
		assertNotNull(documents);
		assertFalse(documents.isEmpty());
		DocumentMetadata meta = documents.get(0).getDocEntry();
		assertNotNull(meta);
		Identificator id = meta.getPatientId();
		assertNotNull(id);
		assertEquals("7560000000011", id.getExtension());
		POCDMT000040ClinicalDocument clinicalDocument = service.getDocument(documents.get(0));
		assertNotNull(clinicalDocument);
		String xmlString = XmlMarshaller.marshall(clinicalDocument);
		assertNotNull(xmlString);
	}
	
	private File createTempFile(String string) throws IOException{
		File ret = File.createTempFile("test_", ".tmp");
		if (ret != null) {
			try (FileOutputStream output = new FileOutputStream(ret)) {
				BufferedInputStream input =
					new BufferedInputStream(getClass().getResourceAsStream(string));
				IOUtils.copy(input, output);
				input.close();
			}
		}
		return ret;
	}
}
