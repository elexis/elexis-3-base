package ch.netzkonzept.elexis.medidata.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlReader {

	private final static String XSLT = "resources/medidata.xslt";

	public static String extract(String path)
			throws SAXException, IOException, ParserConfigurationException, TransformerException {

		InputStream is = XmlReader.class.getClassLoader().getResourceAsStream(XSLT);

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);

		StreamSource xsltSource = new StreamSource();
		xsltSource.setInputStream(is);

		Transformer transformer = transformerFactory.newTransformer(xsltSource);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, false ? "yes" : "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		Writer out = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(out));

		is.close();

		return out.toString();
	}
}
