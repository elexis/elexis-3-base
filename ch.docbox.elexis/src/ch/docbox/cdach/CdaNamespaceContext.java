/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.cdach;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class CdaNamespaceContext implements NamespaceContext {
	
	public String getNamespaceURI(String prefix){
		if (prefix == null)
			throw new NullPointerException("Null prefix");
		else if ("cda".equals(prefix))
			return "urn:hl7-org:v3";
		else if ("xml".equals(prefix))
			return XMLConstants.XML_NS_URI;
		return XMLConstants.NULL_NS_URI;
	}
	
	// This method isn't necessary for XPath processing.
	public String getPrefix(String uri){
		throw new UnsupportedOperationException();
	}
	
	// This method isn't necessary for XPath processing either.
	@SuppressWarnings("rawtypes")
	public Iterator getPrefixes(String uri){
		throw new UnsupportedOperationException();
	}
}
