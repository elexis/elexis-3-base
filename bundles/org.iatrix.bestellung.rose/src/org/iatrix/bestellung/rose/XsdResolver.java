package org.iatrix.bestellung.rose;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class XsdResolver implements LSResourceResolver {

	private static final Logger logger = LoggerFactory.getLogger(XsdResolver.class);

	private static final Map<String, String> XSD_RESOURCE_MAP = new HashMap<>();

	static {
		XSD_RESOURCE_MAP.put("eComCommon.xsd", "/rsc/gs1/ecom/eComCommon.xsd");
		XSD_RESOURCE_MAP.put("SharedCommon.xsd", "/rsc/gs1/shared/SharedCommon.xsd");
		XSD_RESOURCE_MAP.put("StandardBusinessDocumentHeader.xsd", "/rsc/sbdh/StandardBusinessDocumentHeader.xsd");
		XSD_RESOURCE_MAP.put("DocumentIdentification.xsd", "/rsc/sbdh/DocumentIdentification.xsd");
		XSD_RESOURCE_MAP.put("Partner.xsd", "/rsc/sbdh/Partner.xsd");
		XSD_RESOURCE_MAP.put("Manifest.xsd", "/rsc/sbdh/Manifest.xsd");
		XSD_RESOURCE_MAP.put("BusinessScope.xsd", "/rsc/sbdh/BusinessScope.xsd");
		XSD_RESOURCE_MAP.put("BasicTypes.xsd", "/rsc/sbdh/BasicTypes.xsd");
	}

	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		try {
			String resourcePath = XSD_RESOURCE_MAP.get(systemId.substring(systemId.lastIndexOf('/') + 1));
			if (resourcePath == null) {
				logger.error("Could not find XSD: {}", systemId);
				return null;
			}

			InputStream stream = getClass().getResourceAsStream(resourcePath);
			if (stream == null) {
				logger.error("Could not load XSD resource: {}", resourcePath);
				return null;
			}

			return new InputSourceImpl(publicId, systemId, stream);
		} catch (Exception e) {
			logger.error("Error when resolving the resource: {}", systemId, e);
			return null;
		}
	}
}

class InputSourceImpl implements LSInput {
	private final String publicId;
	private final String systemId;
	private final InputStream inputStream;

	public InputSourceImpl(String publicId, String systemId, InputStream inputStream) {
		this.publicId = publicId;
		this.systemId = systemId;
		this.inputStream = inputStream;
	}

	@Override
	public String getPublicId() {
		return publicId;
	}

	@Override
	public String getSystemId() {
		return systemId;
	}

	@Override
	public InputStream getByteStream() {
		return inputStream;
	}

	@Override
	public void setByteStream(InputStream byteStream) {
		// Nicht implementiert
	}

	@Override
	public String getBaseURI() {
		return null;
	}

	@Override
	public void setBaseURI(String baseURI) {
		// Nicht implementiert
	}

	@Override
	public String getStringData() {
		return null;
	}

	@Override
	public void setStringData(String stringData) {
		// Nicht implementiert
	}

	@Override
	public Reader getCharacterStream() {
		return null;
	}

	@Override
	public void setCharacterStream(Reader characterStream) {
		// Nicht implementiert
	}

	@Override
	public String getEncoding() {
		return null;
	}

	@Override
	public void setEncoding(String encoding) {
		// Nicht implementiert
	}

	@Override
	public boolean getCertifiedText() {
		return false;
	}

	@Override
	public void setCertifiedText(boolean certifiedText) {
		// Nicht implementiert
	}

	@Override
	public void setSystemId(String systemId) {
		// Nicht implementiert
	}

	@Override
	public void setPublicId(String publicId) {
		// Nicht implementiert
	}
}