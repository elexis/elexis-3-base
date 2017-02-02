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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.fop.apps.MimeConstants;

import ch.elexis.core.services.IFormattedOutput;

public class JaxbToPdf implements IFormattedOutput {
	private static JaxbToPdf instance;
	
	private JaxbToPdf(){
		
	}
	
	public static JaxbToPdf getInstance(){
		if (instance == null)
			instance = new JaxbToPdf();
		return instance;
	}
	
	@Override
	public void transform(Object jaxbObject, InputStream xslt, OutputStream pdf){
		transform(jaxbObject, xslt, pdf, null);
	}
	
	public void transform(Object jaxbObject, InputStream xslt, OutputStream pdf,
		Map<String, String> transformerParameters){
		JaxbToMimeType.getInstance().transform(jaxbObject, xslt, pdf, MimeConstants.MIME_PDF,
			transformerParameters);
	}
}
