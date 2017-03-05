package ch.elexis.fop.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import ch.elexis.core.services.IFormattedOutput;
import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;
import ch.elexis.fop.service.test.AllTests;
import ch.elexis.fop.service.test.TestJaxbAddress;
import ch.elexis.fop.service.test.TestJaxbContact;
import ch.elexis.fop.service.test.TestJaxbContacts;

public class OutputTypePdfTest {
	
	private static FormattedOutputFactory factory;
	
	@BeforeClass
	public static void beforeClass(){
		factory = new FormattedOutputFactory();
		factory.activate();
	}
	
	@Test
	public void jaxbToPdfFactoryTest() throws IOException{
		TestJaxbContacts contacts = new TestJaxbContacts();
		TestJaxbContact contact = new TestJaxbContact();
		contact.setFirstname("Donald");
		contact.setLastname("Duck");
		TestJaxbAddress address = new TestJaxbAddress();
		address.setCity("Entenhausen");
		address.setStreet("Entenweg 5");
		address.setZip("123");
		contact.getAddresses().add(address);
		contacts.getContact().add(contact);
		
		IFormattedOutput toPdfFactory =
			factory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF);
		assertNotNull(toPdfFactory);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		toPdfFactory.transform(contacts,
			AllTests.getXsltInputStream("default.xsl"), output);
		output.close();
		assertTrue(output.size() > 0);
	}
	
	@Test
	public void xmlToPdfFactoryTest() throws IOException{
		IFormattedOutput toPdfFactory =
			factory.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PDF);
		assertNotNull(toPdfFactory);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		toPdfFactory.transform(AllTests.getXmlInputStream("default.xml"),
			AllTests.getXsltInputStream("default.xsl"), output);
		output.close();
		assertTrue(output.size() > 0);
	}
	
	@Test
	public void domToPdfFactoryTest()
		throws ParserConfigurationException, SAXException, IOException{
		IFormattedOutput toPdfFactory =
			factory.getFormattedOutputImplementation(ObjectType.DOM, OutputType.PDF);
		assertNotNull(toPdfFactory);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		toPdfFactory.transform(AllTests.getDomInputStream("default.xml"),
			AllTests.getXsltInputStream("default.xsl"), output);
		output.close();
		assertTrue(output.size() > 0);
	}
}
