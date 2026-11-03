package ch.elexis.fop.service.jaxb;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.URIResolver;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

/**
 * Creates PDF documents conforming to PDF/A-1b. All fonts used by the
 * stylesheet have to be embeddable, the base 14 fonts like Helvetica are not
 * allowed by the standard.
 */
public class JaxbToPdfA implements IFormattedOutput {
	private static JaxbToPdfA instance;

	private JaxbToPdfA() {

	}

	public static JaxbToPdfA getInstance() {
		if (instance == null)
			instance = new JaxbToPdfA();
		return instance;
	}

	@Override
	public void transform(Object jaxbObject, InputStream xslt, OutputStream pdf) {
		transform(jaxbObject, xslt, pdf, null);
	}

	public void transform(Object jaxbObject, InputStream xslt, OutputStream pdf,
			Map<String, String> transformerParameters, URIResolver resolver) {
		JaxbToMimeType.getInstance().transform(jaxbObject, xslt, pdf, MimeConstants.MIME_PDF, transformerParameters,
				resolver, true);
	}
}
