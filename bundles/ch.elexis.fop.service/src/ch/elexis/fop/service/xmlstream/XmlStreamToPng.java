package ch.elexis.fop.service.xmlstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class XmlStreamToPng implements IFormattedOutput {
	private static XmlStreamToPng instance;
	
	private XmlStreamToPng(){
		
	}
	
	public static XmlStreamToPng getInstance(){
		if (instance == null)
			instance = new XmlStreamToPng();
		return instance;
	}
	
	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream png){
		transform(xmlStream, xslt, png, null);
	}
	
	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream png,
		Map<String, String> transformerParameters){
		if (xmlStream instanceof InputStream) {
			XmlStreamToMimeType.getInstance().transform((InputStream) xmlStream, xslt,
				png, MimeConstants.MIME_PNG, transformerParameters);
			transform(xmlStream, xslt, png, null);
		} else {
			throw new IllegalStateException(
				"Input Object [" + xmlStream + "] is not of type InputStream");
		}
	}
}
