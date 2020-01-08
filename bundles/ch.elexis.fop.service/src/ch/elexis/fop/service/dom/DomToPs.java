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

public class DomToPs implements IFormattedOutput {
	private static DomToPs instance;
	
	private DomToPs(){}
	
	public static DomToPs getInstance(){
		if (instance == null)
			instance = new DomToPs();
		return instance;
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream ps){
		transform(documentObject, xslt, ps, null);
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream ps,
		Map<String, String> transformerParameters, URIResolver resolver){
		DomToMimeType.getInstance().transform(documentObject, xslt, ps,
			MimeConstants.MIME_POSTSCRIPT, transformerParameters, resolver);
	}
}
