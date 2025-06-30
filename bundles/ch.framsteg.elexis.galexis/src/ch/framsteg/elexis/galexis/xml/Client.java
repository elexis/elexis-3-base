package ch.framsteg.elexis.galexis.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Client {


	public static String send(String message) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request;

		try {
			request = HttpRequest.newBuilder().uri(URI.create("https://test.e-galexis.com/testPOS/"))
					.header("Content-Type", "text/xml; charset=UTF-8").POST(BodyPublishers.ofString(message)).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			InputSource src = new InputSource(new StringReader(response.body()));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, true ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter out = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(out));
			System.out.println(out.toString());
			return out.toString();
		} catch (IOException | TransformerException | InterruptedException | SAXException
				| ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
