package ch.elexis.fop.service.xmlstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.URIResolver;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class XmlStreamToPs implements IFormattedOutput {
	private static XmlStreamToPs instance;

	private XmlStreamToPs() {

	}

	public static XmlStreamToPs getInstance() {
		if (instance == null)
			instance = new XmlStreamToPs();
		return instance;
	}

	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream ps) {
		transform(xmlStream, xslt, ps, null);
	}

	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream ps,
			Map<String, String> transformerParameters, URIResolver resolver) {
		if (xmlStream instanceof InputStream) {
			XmlStreamToMimeType.getInstance().transform((InputStream) xmlStream, xslt, ps,
					MimeConstants.MIME_POSTSCRIPT, transformerParameters, resolver);
		} else {
			throw new IllegalStateException("Input Object [" + xmlStream + "] is not of type InputStream");
		}
	}
}