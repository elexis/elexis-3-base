/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.gdt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GdtEncoder {
	private ByteArrayOutputStream fullGdtFile;
	private static Logger logger = LoggerFactory.getLogger(GdtEncoder.class);

	public GdtEncoder(String formId) throws UnsupportedOperationException {
		try {
			fullGdtFile = new ByteArrayOutputStream();

			ByteArrayOutputStream patientData = new ByteArrayOutputStream();
			patientData.write(new GdtBody(formId).toString().getBytes());

			int gdtSize = patientData.size() + 13 + 16;
			String gdtSizeMask = "0000000";
			String gdtSizeString = "0168004" // Satzlänge seit GDT 3.1 8004 und nicht 8100
					+ gdtSizeMask.substring(0, gdtSizeMask.length() - Integer.toString(gdtSize).length())
					+ Integer.toString(gdtSize) + "\r\n";

			fullGdtFile.write("01380006301\r\n".getBytes());
			fullGdtFile.write(gdtSizeString.getBytes());
			patientData.writeTo(fullGdtFile);
		} catch (IOException e) {
			logger.error("Error creating GDT file", e);
		}
	}

	@Override
	public String toString() {
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encoded = encoder.encode(fullGdtFile.toByteArray());
		return new String(encoded);
	}

}
