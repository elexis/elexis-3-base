/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.atc_codes.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ATCParser extends DefaultHandler {
	
	private List<ATCDefinition> definitions = new ArrayList<ATCDefinition>();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException{
		if (localName.equals("row")) {
			ATCDefinition def = new ATCDefinition();
			def.atcCode = attributes.getValue(0);
			def.name = attributes.getValue(1);
			definitions.add(def);
		}
	}
	
	public class ATCDefinition {
		public String name;
		public String atcCode;
	}
	
	public List<ATCDefinition> getDefinitions(){
		return definitions;
	}
}
