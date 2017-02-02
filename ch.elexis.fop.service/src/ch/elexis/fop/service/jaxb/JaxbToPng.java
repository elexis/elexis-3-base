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

public class JaxbToPng implements IFormattedOutput {
	private static JaxbToPng instance;
	
	private JaxbToPng(){
		
	}
	
	public static JaxbToPng getInstance(){
		if (instance == null)
			instance = new JaxbToPng();
		return instance;
	}
	
	@Override
	public void transform(Object jaxbObject, InputStream xslt, OutputStream png){
		transform(jaxbObject, xslt, png, null);
	}
	
	public void transform(Object jaxbObject, InputStream xslt, OutputStream png,
		Map<String, String> transformerParameters){
		JaxbToMimeType.getInstance().transform(jaxbObject, xslt, png, MimeConstants.MIME_PNG,
			transformerParameters);
	}
}
