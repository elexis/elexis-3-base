package ch.elexis.pdfBills.privat;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class BundleURIResolver implements URIResolver {

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		return new StreamSource(BundleURIResolver.class.getClassLoader().getResourceAsStream(href));
	}
}
