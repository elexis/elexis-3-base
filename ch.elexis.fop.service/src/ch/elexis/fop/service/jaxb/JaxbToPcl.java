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

public class JaxbToPcl implements IFormattedOutput {
	private static JaxbToPcl instance;
	
	public static JaxbToPcl getInstance(){
		if (instance == null)
			instance = new JaxbToPcl();
		return instance;
	}
	
	@Override
	public void transform(Object jaxbObject, InputStream xslt, OutputStream pcl){
		transform(jaxbObject, xslt, pcl, null);
	}
	
	@Override
	public void transform(Object jaxbObject, InputStream xslt, OutputStream pcl,
		Map<String, String> transformerParameters){
		JaxbToMimeType.getInstance().transform(jaxbObject, xslt, pcl, MimeConstants.MIME_PCL,
			transformerParameters);
	}
	
}
