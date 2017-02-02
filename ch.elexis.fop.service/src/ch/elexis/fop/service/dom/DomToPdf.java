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

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class DomToPdf implements IFormattedOutput {
	
	private static DomToPdf instance;
	
	private DomToPdf(){}
	
	public static DomToPdf getInstance(){
		if (instance == null)
			instance = new DomToPdf();
		return instance;
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream pdf){
		transform(documentObject, xslt, pdf, null);
	}
	
	@Override
	public void transform(Object documentObject, InputStream xslt, OutputStream pdf,
		Map<String, String> transformerParameters){
		DomToMimeType.getInstance().transform(documentObject, xslt, pdf, MimeConstants.MIME_PDF,
			transformerParameters);
	}
}
