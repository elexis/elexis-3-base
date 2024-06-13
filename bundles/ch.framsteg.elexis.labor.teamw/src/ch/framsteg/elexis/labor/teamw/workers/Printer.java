package ch.framsteg.elexis.labor.teamw.workers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Printer {

	private final static String IDENT_NUMBER = "indent-number";
	private final static String YES = "yes";
	private final static String NO = "no";
	
	Logger logger = LoggerFactory.getLogger(Printer.class);

	public void print(String message)
			throws SAXException, IOException, ParserConfigurationException, TransformerException {
		InputSource src = new InputSource(new StringReader(message));
		boolean ignoreDeclaration = true;

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute(IDENT_NUMBER, 4);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? YES : NO);
		transformer.setOutputProperty(OutputKeys.INDENT, YES);

		Writer out = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(out));

		logger.info(out.toString());
	}
}
