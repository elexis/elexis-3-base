/*******************************************************************************
 * Copyright (c) 2015 MEDEVIT and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package ch.elexis.fop.service.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.fop.service.FormattedOutputFactory;
import ch.elexis.fop.service.JaxbUtil;
import ch.elexis.fop.service.XSLTUtil;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBException;

public class JaxbToMimeType {
	private static Logger logger = LoggerFactory.getLogger(JaxbToMimeType.class);
	private static JaxbToMimeType instance;

	private JaxbToMimeType() {

	}

	public static JaxbToMimeType getInstance() {
		if (instance == null)
			instance = new JaxbToMimeType();
		return instance;
	}

	/**
	 * Transform a given jaxb annotated object into a specific output object
	 *
	 * @param jaxbObject            a {@link JAXB} annotated element as source
	 * @param xslt                  the XSLT stylesheet as {@link InputStream}
	 *                              element
	 * @param outputStream          the {@link OutputStream} to output to
	 * @param outputFormat          the requested output format
	 *                              {@link MimeConstants}
	 * @param transformerParameters key/value parameters to be passed to the
	 *                              transformer, can be <code>null</code>
	 * @param resolver
	 */
	public void transform(Object jaxbObject, InputStream xslt, OutputStream outputStream, String outputFormat,
			Map<String, String> transformerParameters, URIResolver resolver) {

		FOUserAgent foUserAgent = FormattedOutputFactory.getFopFactory().newFOUserAgent();
		// configure foUserAgent as desired

		// Setup output
		try {
			// Construct fop with desired output format
			Fop fop = FormattedOutputFactory.getFopFactory().newFop(outputFormat, foUserAgent, outputStream);
			// Setup XSLT
			Transformer transformer = XSLTUtil.getTransformerForXSLT(xslt, resolver);

			if (transformerParameters != null && transformerParameters.keySet().size() > 0) {
				for (String keyParameter : transformerParameters.keySet()) {
					transformer.setParameter(keyParameter, transformerParameters.get(keyParameter));
				}
			}

			// Setup input for XSLT transformation
			ByteArrayOutputStream output = JaxbUtil.getOutputStreamForObject(jaxbObject);
			Source src = new StreamSource(new ByteArrayInputStream(output.toByteArray()));

			// Resulting SAX events (the generated FO) must be piped through to
			// FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Start XSLT transformation and FOP processing
			transformer.transform(src, res);
		} catch (TransformerException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} catch (FOPException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} catch (IOException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} catch (JAXBException e) {
			logger.error("Error during XML tranformation.", e);
			throw new IllegalStateException(e);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
