/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package ch.docbox.elexis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDACHServicesClient {

	private static Logger log = LoggerFactory.getLogger(CDACHServicesClient.class);

	public static final QName SERVICE_NAME = new QName("http://ws.docbox.ch/CDACHServices/", "CDACHServices");

	public CDACHServicesClient() {
	}

	private static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public final static String toHex(byte[] v) {
		String out = StringUtils.EMPTY;
		for (int i = 0; i < v.length; i++)
			out = out + hex[(v[i] >> 4) & 0xF] + hex[v[i] & 0xF];
		return (out);
	}

	public static String getSHA1(String password) {
		if (password == null || StringUtils.EMPTY.equals(password)) {
			return StringUtils.EMPTY;
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return toHex(digest);
		} catch (NoSuchAlgorithmException e) {
			log.error("Error in getSHA1, returning empty string", e);
		} catch (UnsupportedEncodingException e) {
			log.error("Error in getSHA1, returning empty string", e);
		}

		return StringUtils.EMPTY;
	}
}
