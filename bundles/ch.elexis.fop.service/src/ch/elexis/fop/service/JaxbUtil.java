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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class JaxbUtil {

	public static ByteArrayOutputStream getOutputStreamForObject(Object jaxbObj)
			throws UnsupportedEncodingException, IOException, JAXBException {
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		OutputStreamWriter writer;

		writer = new OutputStreamWriter(ret, "UTF-8");

		JAXBContext jc = JAXBContext.newInstance(jaxbObj.getClass());
		Marshaller m = jc.createMarshaller();
		m.setProperty("jaxb.encoding", "UTF-8");
		m.marshal(jaxbObj, writer);

		writer.close();

		return ret;
	}
}
