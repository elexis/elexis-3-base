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
package ch.elexis.fop.service.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import ch.elexis.fop.service.FormattedOutputFactory;
import ch.elexis.fop.service.XSLTUtil;
import jakarta.xml.bind.JAXB;

public class DomToMimeType {
	private static Logger logger = LoggerFactory.getLogger(DomToMimeType.class);
	private static DomToMimeType instance;

	private DomToMimeType() {

	}

	public static DomToMimeType getInstance() {
		if (instance == null)
			instance = new DomToMimeType();
		return instance;
	}

	/**
	 * Transform a given DOM Document object into a specific output object
	 *
	 * @param jaxbObject            a {@link JAXB} annotated element as source
	 * @param xslt                  the XSLT stylesheet as {@link InputStream}
	 *                              element
	 * @param outputStream          the {@link OutputStream} to output to
	 * @param outputFormat          the requested output format
	 *                              {@link MimeConstants}
	 * @param transformerParameters key/value parameters to be passed to the
	 *                              transformer, can be <code>null</code>
	 */
	public void transform(Object documentObject, InputStream xslt, OutputStream outputStream, String outputFormat,
			Map<String, String> transformerParameters, URIResolver resolver) {
		if (!(documentObject instanceof Document))
			return;
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
			DOMSource src = new DOMSource((Document) documentObject);

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
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}
