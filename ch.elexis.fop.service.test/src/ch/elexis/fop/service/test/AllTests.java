package ch.elexis.fop.service.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ch.elexis.fop.service.FormattedOutputFactoryTest;
import ch.elexis.fop.service.OutputTypePdfTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FormattedOutputFactoryTest.class, OutputTypePdfTest.class
})
public class AllTests {
	
	public static InputStream getXmlInputStream(String string){
		return AllTests.class.getResourceAsStream("/rsc/xml/" + string);
	}
	
	public static InputStream getXsltInputStream(String string){
		return AllTests.class.getResourceAsStream("/rsc/xslt/" + string);
	}
	
	public static void writeFile(byte[] content, String filename)
		throws FileNotFoundException, IOException{
		String userHomeDir = System.getProperty("user.home");
		if (!userHomeDir.endsWith(File.separator)) {
			userHomeDir += File.separator;
		}
		File file = new File(userHomeDir + filename);
		LoggerFactory.getLogger(AllTests.class).info("Writing file " + file.getAbsolutePath());
		try (FileOutputStream output = new FileOutputStream(file)) {
			output.write(content);
		}
	}
	
	public static Document getDomInputStream(String string)
		throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(getXmlInputStream(string));
	}
}

