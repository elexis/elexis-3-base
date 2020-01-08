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
package ch.elexis.fop.service.xmlstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.URIResolver;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class XmlStreamToPdf implements IFormattedOutput {
	private static XmlStreamToPdf instance;
	
	private XmlStreamToPdf(){
		
	}
	
	public static XmlStreamToPdf getInstance(){
		if (instance == null)
			instance = new XmlStreamToPdf();
		return instance;
	}
	
	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream pdf){
		transform(xmlStream, xslt, pdf, null);
	}
	
	@Override
	public void transform(Object xmlStream, InputStream xslt, OutputStream pdf,
		Map<String, String> transformerParameters, URIResolver resolver){
		if (xmlStream instanceof InputStream) {
			XmlStreamToMimeType.getInstance().transform(
				(InputStream) xmlStream, xslt,
				pdf, MimeConstants.MIME_PDF, transformerParameters, resolver);
		} else {
			throw new IllegalStateException(
				"Input Object [" + xmlStream + "] is not of type InputStream");
		}
	}
}
