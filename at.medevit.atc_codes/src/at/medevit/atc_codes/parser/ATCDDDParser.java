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

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ATCDDDParser extends DefaultHandler {
	
	// TODO there may exist multiple entries per ATC-Code depending on AdmCode
	// currently these are silently thrown away, if later on they may be used
	// somehow this has to be changed!
	private HashMap<String, ATCDDDDefinition> dddDefinitions = new HashMap<String, ATCDDDDefinition>();
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException{
		if (localName.equals("row")) {
			ATCDDDDefinition def = new ATCDDDDefinition();
			for (int i = 0; i < attributes.getLength(); i++) {
				String key = attributes.getLocalName(i);
				String value = attributes.getValue(i);
				if (key.equalsIgnoreCase("DDD")) {
					def.ddd = value;
				} else if (key.equalsIgnoreCase("UnitType")) {
					def.unitType = value;
				} else if (key.equalsIgnoreCase("AdmCode")) {
					def.admCode = value;
				} else if (key.equalsIgnoreCase("DDDComment")) {
					def.dddComment = value;
				}
			}
			dddDefinitions.put(attributes.getValue(0), def);
		}
	}
	
	public class ATCDDDDefinition {
		public String ddd;
		public String unitType;
		public String admCode;
		public String dddComment;
	}
	
	public HashMap<String, ATCDDDDefinition> getDddDefinitions(){
		return dddDefinitions;
	}
}
