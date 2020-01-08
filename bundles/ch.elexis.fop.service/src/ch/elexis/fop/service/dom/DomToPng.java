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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.transform.URIResolver;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class DomToPng implements IFormattedOutput {
	private static DomToPng instance;
	
	private DomToPng(){}
	
	public static DomToPng getInstance(){
		if (instance == null)
			instance = new DomToPng();
		return instance;
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream png){
		transform(documentObject, xslt, png, null);
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream png,
		Map<String, String> transformerParameters, URIResolver resolver){
		DomToMimeType.getInstance().transform(documentObject, xslt, png, MimeConstants.MIME_PNG,
			transformerParameters, resolver);
	}
}
