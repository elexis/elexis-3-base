package ch.elexis.estudio.clustertec;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Provides the {@link Schema} instances for the Zur Rose prescription schemas
 * bundled below {@link #SCHEMA_BASE_PATH}. The schemas include each other
 * relatively, both includes and the schema itself are resolved from the bundle,
 * so that validation never touches the network.
 */
public class ClustertecSchemaUtil {

	public static final String SCHEMA_BASE_PATH = "/rsc/schema/prescriptionrose/"; //$NON-NLS-1$
	public static final String SCHEMA_PRESCRIPTION = "prescription.xsd"; //$NON-NLS-1$
	public static final String SCHEMA_PRESCRIPTION_RESPONSE = "prescriptionResponse.xsd"; //$NON-NLS-1$

	private static Logger log = LoggerFactory.getLogger(ClustertecSchemaUtil.class);

	private static final Map<String, Schema> SCHEMA_CACHE = new ConcurrentHashMap<>();

	private ClustertecSchemaUtil() {
		// static access only
	}

	public static Schema getSchema(String schemaFileName) {
		return SCHEMA_CACHE.computeIfAbsent(schemaFileName, ClustertecSchemaUtil::loadSchema);
	}

	private static Schema loadSchema(String schemaFileName) {
		URL schemaUrl = getSchemaEntry(schemaFileName);
		if (schemaUrl == null) {
			log.error("Bundled XSD not found: {}{}", SCHEMA_BASE_PATH, schemaFileName); //$NON-NLS-1$
			return null;
		}
		try (InputStream schemaStream = schemaUrl.openStream()) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); //$NON-NLS-1$
			factory.setResourceResolver(new BundleResourceResolver());
			return factory.newSchema(new StreamSource(schemaStream, schemaUrl.toExternalForm()));
		} catch (SAXException | IOException e) {
			log.error("Could not load bundled XSD {}", schemaUrl, e); //$NON-NLS-1$
			return null;
		}
	}

	public static URL getSchemaEntry(String fileName) {
		String path = SCHEMA_BASE_PATH + fileName;
		Bundle bundle = FrameworkUtil.getBundle(ClustertecSchemaUtil.class);
		if (bundle != null) {
			URL entry = bundle.getEntry(path);
			if (entry != null) {
				return entry;
			}
		}
		return ClustertecSchemaUtil.class.getResource(path);
	}

	private static class BundleResourceResolver implements LSResourceResolver {

		@Override
		public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
				String baseURI) {
			if (systemId == null) {
				return null;
			}
			String fileName = systemId.substring(systemId.lastIndexOf('/') + 1);
			URL entry = getSchemaEntry(fileName);
			if (entry == null) {
				log.error("Could not find bundled XSD for {}", systemId); //$NON-NLS-1$
				return null;
			}
			try {
				return new BundleLSInput(publicId, entry.toExternalForm(), entry.openStream());
			} catch (IOException e) {
				log.error("Could not open bundled XSD {}", entry, e); //$NON-NLS-1$
				return null;
			}
		}
	}

	private static class BundleLSInput implements LSInput {

		private final String publicId;
		private final String systemId;
		private final InputStream inputStream;

		BundleLSInput(String publicId, String systemId, InputStream inputStream) {
			this.publicId = publicId;
			this.systemId = systemId;
			this.inputStream = inputStream;
		}

		@Override
		public String getPublicId() {
			return publicId;
		}

		@Override
		public void setPublicId(String publicId) {
			// not implemented
		}

		@Override
		public String getSystemId() {
			return systemId;
		}

		@Override
		public void setSystemId(String systemId) {
			// not implemented
		}

		@Override
		public String getBaseURI() {
			return systemId;
		}

		@Override
		public void setBaseURI(String baseURI) {
			// not implemented
		}

		@Override
		public InputStream getByteStream() {
			return inputStream;
		}

		@Override
		public void setByteStream(InputStream byteStream) {
			// not implemented
		}

		@Override
		public Reader getCharacterStream() {
			return null;
		}

		@Override
		public void setCharacterStream(Reader characterStream) {
			// not implemented
		}

		@Override
		public String getStringData() {
			return null;
		}

		@Override
		public void setStringData(String stringData) {
			// not implemented
		}

		@Override
		public String getEncoding() {
			return null;
		}

		@Override
		public void setEncoding(String encoding) {
			// not implemented
		}

		@Override
		public boolean getCertifiedText() {
			return false;
		}

		@Override
		public void setCertifiedText(boolean certifiedText) {
			// not implemented
		}
	}
}
