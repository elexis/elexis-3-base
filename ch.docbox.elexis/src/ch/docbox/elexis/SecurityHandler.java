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

import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {
	
	private String username;
	private String password;
	
	public SecurityHandler(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public Set<QName> getHeaders(){
		return new TreeSet<QName>();
	}
	
	public boolean handleMessage(SOAPMessageContext context){
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue()) {
			try {
				SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
				SOAPFactory factory = SOAPFactory.newInstance();
				String prefix = "wsse";
				String uri =
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
				SOAPElement securityElem = factory.createElement("Security", prefix, uri);
				SOAPElement usernameTokenEl = factory.createElement("UsernameToken", prefix, uri);
				SOAPElement usernameEl = factory.createElement("Username", prefix, uri);
				SOAPElement passwordEl = factory.createElement("Password", prefix, uri);
				usernameEl.setTextContent(username);
				passwordEl.setTextContent(password);
				usernameTokenEl.addChildElement(usernameEl);
				usernameTokenEl.addChildElement(passwordEl);
				securityElem.addChildElement(usernameTokenEl);
				SOAPHeader header = envelope.addHeader();
				header.addChildElement(securityElem);
			} catch (Exception e) {
				System.out.println("Exception in handler: " + e);
			}
		} else {
			// inbound
		}
		return true;
	}
	
	public boolean handleFault(SOAPMessageContext context){
		return true;
	}
	
	public void close(MessageContext context){
		//
	}
}
