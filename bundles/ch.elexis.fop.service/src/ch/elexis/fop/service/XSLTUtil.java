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
package ch.elexis.fop.service;

import java.io.InputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSLTUtil {
	private static Logger logger = LoggerFactory.getLogger(XSLTUtil.class);

	public static Transformer getTransformerForXSLT(InputStream xslt, URIResolver resolver)
			throws TransformerConfigurationException {
		TransformerFactory factory = TransformerFactory.newInstance();
		if (resolver != null) {
			factory.setURIResolver(resolver);
		}
		Transformer ret = factory.newTransformer(new StreamSource(xslt));
		ret.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		ret.setErrorListener(new ErrorListener() {

			@Override
			public void warning(TransformerException exception) throws TransformerException {
				logger.warn(exception.getMessage());
			}

			@Override
			public void fatalError(TransformerException exception) throws TransformerException {
				logger.error("Fatal Error processing XSLT", exception);
				throw exception;
			}

			@Override
			public void error(TransformerException exception) throws TransformerException {
				logger.error("Error processing XSLT", exception);
				throw exception;

			}
		});
		return ret;
	}
}
