package ch.elexis.fop.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.fop.apps.MimeConstants;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.elexis.core.services.IFormattedOutputFactory.ObjectType;
import ch.elexis.core.services.IFormattedOutputFactory.OutputType;

public class FormattedOutputFactoryTest {
	
	private static FormattedOutputFactory factory;
	
	@BeforeClass
	public static void beforeClass(){
		factory = new FormattedOutputFactory();
		factory.activate();
	}
	
	@Test
	public void getFormattedOutputImplementationTest(){
		// all DOM implementations
		//		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.DOM, OutputType.PCL));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.DOM, OutputType.PDF));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.DOM, OutputType.PNG));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.DOM, OutputType.PS));
		// all JAXB implementations
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PCL));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PDF));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PNG));
		assertNotNull(factory.getFormattedOutputImplementation(ObjectType.JAXB, OutputType.PS));
		// all XMLSTREAM implementations
		//		assertNotNull(
		//			factory.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PCL));
		assertNotNull(
			factory.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PDF));
		assertNotNull(
			factory.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PNG));
		assertNotNull(
			factory.getFormattedOutputImplementation(ObjectType.XMLSTREAM, OutputType.PS));
	}
	
	@Ignore
	@Test
	public void getRegisteredFontsTest(){
		String[] fonts = FormattedOutputFactory.getRegisteredFonts(MimeConstants.MIME_PDF);
		assertNotNull(fonts);
		assertTrue(fonts.length > 0);
		
		fonts = FormattedOutputFactory.getRegisteredFonts(MimeConstants.MIME_PNG);
		assertNotNull(fonts);
		assertTrue(fonts.length > 0);
		
		fonts = FormattedOutputFactory.getRegisteredFonts(MimeConstants.MIME_POSTSCRIPT);
		assertNotNull(fonts);
		assertTrue(fonts.length > 0);
		
		fonts = FormattedOutputFactory.getRegisteredFonts(MimeConstants.MIME_PCL);
		assertNotNull(fonts);
		assertTrue(fonts.length > 0);
	}
	
	@Test
	public void getFopFactoryTest(){
		assertNotNull(FormattedOutputFactory.getFopFactory());
	}
}

