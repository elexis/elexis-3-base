package ch.elexis.pdfBills;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;

public class TarmedXmlUtil {

	/**
	 * Modify the print at intermediate attribute of the tarmed xml {@link Document}
	 * and return a modified {@link Document} object.
	 * 
	 * @param document
	 * @param value
	 * @return
	 */
	public static Document setPrintAtIntermediate(Document document, boolean value) {
		String version = TarmedJaxbUtil.getXMLVersion(document);
		if ("4.5".equals(version)) {
			ch.fd.invoice450.request.RequestType invoiceRequest = TarmedJaxbUtil.unmarshalInvoiceRequest450(document);
			setPrintAtIntermediate(invoiceRequest, value);

			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			if (TarmedJaxbUtil.marshallInvoiceRequest(invoiceRequest, xmlOutput)) {
				SAXBuilder builder = new SAXBuilder();
				try {
					return builder.build(new StringReader(xmlOutput.toString()));
				} catch (IOException | JDOMException e) {
					LoggerFactory.getLogger(TarmedXmlUtil.class).error("Error loading existing xml document", e);
				}
			}
		} else {
			LoggerFactory.getLogger(TarmedXmlUtil.class)
					.error("Could not modify xml document with version [" + version + "]");
		}
		return document;
	}

	private static void setPrintAtIntermediate(ch.fd.invoice450.request.RequestType invoiceRequest, boolean value) {
		if (invoiceRequest != null && invoiceRequest.getProcessing() != null) {
			invoiceRequest.getProcessing().setPrintAtIntermediate(value);
		}
	}
}
