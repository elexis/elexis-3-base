package ch.elexis.fop.service.xmlstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.URIResolver;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class XmlStreamToPcl implements IFormattedOutput {
	private static XmlStreamToPcl instance;

	private XmlStreamToPcl() {

	}

	public static XmlStreamToPcl getInstance() {
		if (instance == null)
			instance = new XmlStreamToPcl();
		return instance;
	}

	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream pcl) {
		transform(xmlStream, xslt, pcl, null);
	}

	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream pcl,
			Map<String, String> transformerParameters, URIResolver resolver) {
		if (xmlStream instanceof InputStream) {
			XmlStreamToMimeType.getInstance().transform((InputStream) xmlStream, xslt, pcl, MimeConstants.MIME_PCL,
					transformerParameters, resolver);
		} else {
			throw new IllegalStateException("Input Object [" + xmlStream + "] is not of type InputStream");
		}
	}
}