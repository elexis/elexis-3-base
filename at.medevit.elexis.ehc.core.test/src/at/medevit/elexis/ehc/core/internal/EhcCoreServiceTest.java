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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.ehealth_connector.cda.ch.CdaCh;
import org.ehealth_connector.cda.enums.AddressUse;
import org.ehealth_connector.cda.enums.AdministrativeGender;
import org.ehealth_connector.common.Address;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;

import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rezept;

public class EhcCoreServiceTest {
	
	private static Patient patient;
	private static Mandant mandant;
	private static Rezept rezept;
	
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
		
		rezept = new Rezept(patient);
		
		mandant = new Mandant("mandant", "firstname", "02.02.2002", Mandant.MALE);
	}
	
	@AfterClass
	public static void after(){
		patient.delete();
		mandant.delete();
	}
	
	@Test
	public void testGetPatientDocument(){
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getCdaChDocument(patient, mandant);
		assertNotNull(cda);
		org.ehealth_connector.common.Patient cdaPatient = cda.getPatient();
		assertNotNull(cdaPatient);
		assertEquals("name", cdaPatient.getName().getFamilyName());
		assertEquals("firstname", cdaPatient.getName().getGivenNames());
		assertEquals(AdministrativeGender.FEMALE, cdaPatient.getAdministrativeGenderCode());
		Calendar bDay = Calendar.getInstance();
		bDay.set(2000, 00, 01, 00, 00, 00);
		bDay.set(Calendar.MILLISECOND, 00);
		assertEquals(bDay.getTime(), cdaPatient.getBirthday());
		List<Address> addresses = cdaPatient.getAddresses();
		assertFalse(addresses.isEmpty());
		assertEquals("City", addresses.get(0).getCity());
		
		HashMap<String, AddressUse> phones = cdaPatient.getTelecoms().getPhones();
		assertFalse(phones.isEmpty());
		assertTrue(phones.containsKey("tel:+01555123"));
	}
	
	@Test
	public void testWritePatientDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getCdaChDocument(patient, mandant);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		CDAUtil.save(cda.getDocRoot().getClinicalDocument(), output);
		assertTrue(output.size() > 0);
		String xml = output.toString("UTF-8");
		assertTrue(xml.contains("name"));
		assertTrue(xml.contains("firstname"));
	}
	
	@Test
	public void testWritePatientDocumentFile() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getCdaChDocument(patient, mandant);
		
		String userHome = System.getProperty("user.home");
		String outFilePath = userHome + File.separator + "testPatientCda.xml";
		cda.saveToFile(outFilePath);
		File outFile = new File(outFilePath);
		assertTrue(outFile.exists());
		assertTrue(outFile.isFile());
		assertTrue(outFile.length() > 0);
		outFile.deleteOnExit();
	}
	
	@Test
	public void testGetDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getCdaChDocument(patient, mandant);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		CDAUtil.save(cda.getDocRoot().getClinicalDocument(), output);
		assertTrue(output.size() > 0);
		ByteArrayInputStream documentInput = new ByteArrayInputStream(output.toByteArray());
		CdaCh cdach = service.getDocument(documentInput);
		assertNotNull(cdach);
		org.ehealth_connector.common.Patient readPatient = cdach.getPatient();
		assertEquals("name", readPatient.getName().getFamilyName());
		assertEquals("firstname", readPatient.getName().getGivenNames());
		assertEquals(AdministrativeGender.FEMALE, readPatient.getAdministrativeGenderCode());
	}
	
	@Test
	public void testGetPrescriptionDocument() throws Exception{
		EhcCoreServiceImpl service = new EhcCoreServiceImpl();
		CdaCh cda = service.getPrescriptionDocument(rezept);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		CDAUtil.save(cda.getDocRoot().getClinicalDocument(), output);
		assertTrue(output.size() > 0);
	}
}
