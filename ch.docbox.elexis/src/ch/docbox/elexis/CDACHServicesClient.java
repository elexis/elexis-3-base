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
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import ch.docbox.ws.cdachservices.CDACHServices;
import ch.docbox.ws.cdachservices.CDACHServices_Service;

public class CDACHServicesClient {
	
	public static final QName SERVICE_NAME = new QName("http://ws.docbox.ch/CDACHServices/",
		"CDACHServices");
	
	public CDACHServicesClient(){}
	
	private static char[] hex = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	public final static String toHex(byte[] v){
		String out = "";
		for (int i = 0; i < v.length; i++)
			out = out + hex[(v[i] >> 4) & 0xF] + hex[v[i] & 0xF];
		return (out);
	}
	
	public static String getSHA1(String password){
		if (password == null || "".equals(password)) {
			return "";
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest();
		return toHex(digest);
	}
	
	static public CDACHServices addWsSecurityAndHttpConfig(CDACHServices_Service ss,
		final String username, final String password, String url){
		
		boolean swissmedicalsuite = false;
		if (url != null) {
			swissmedicalsuite =
				url.contains("swissmedicalsuite.test.docbox.ch")
					|| url.contains("swissmedicalsuite.docbox.ch");
		}
		final boolean hcard = swissmedicalsuite;
		
		ss.setHandlerResolver(new HandlerResolver() {
			@SuppressWarnings({
				"rawtypes"
			})
			public List<Handler> getHandlerChain(PortInfo portInfo){
				List<Handler> handlerList = new ArrayList<Handler>();
				handlerList.add(new SecurityHandler(username, password));
				return handlerList;
			}
		});
		
		CDACHServices port = ss.getCDACHServices();
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
			url);
		
		return port;
	}
	
}
