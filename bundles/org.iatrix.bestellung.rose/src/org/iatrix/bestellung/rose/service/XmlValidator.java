package org.iatrix.bestellung.rose.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.iatrix.bestellung.rose.Constants;
import org.iatrix.bestellung.rose.XsdResolver;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

public class XmlValidator {

	public void validateXml(String xml) throws IllegalStateException {
		try {
			setXmlLimits();
			Bundle bundle = Platform.getBundle(Constants.PLUGIN_ID);
			if (bundle == null) {
				throw new IllegalStateException("Bundle not found: " + Constants.PLUGIN_ID);
			}
			URL xsdUrl = bundle.getEntry(Constants.XSD_RELATIVE_PATH);
			if (xsdUrl == null) {
				throw new IllegalStateException("XSD file not found: " + Constants.XSD_RELATIVE_PATH);
			}

			URL resolvedUrl = FileLocator.resolve(xsdUrl);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.setResourceResolver(new XsdResolver());

			try (InputStream xsdStream = resolvedUrl.openStream()) {
				Schema schema = factory.newSchema(new StreamSource(xsdStream));
				Validator validator = schema.newValidator();
				validator.validate(new StreamSource(new java.io.StringReader(xml)));
			}
		} catch (SAXException | IOException e) {
			throw new IllegalStateException("XML validation failed: " + e.getMessage());
		}
	}

	private void setXmlLimits() {
		System.setProperty("jdk.xml.maxOccurLimit", "10000");
		System.setProperty("jdk.xml.entityExpansionLimit", "10000");
		System.setProperty("jdk.xml.elementAttributeLimit", "10000");
		System.setProperty("jdk.xml.totalEntitySizeLimit", "10000000");
		System.setProperty("jdk.xml.maxElementDepth", "10000");
	}
}
