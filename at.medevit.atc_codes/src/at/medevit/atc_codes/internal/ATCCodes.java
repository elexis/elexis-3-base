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
package at.medevit.atc_codes.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.parser.ATCDDDParser;
import at.medevit.atc_codes.parser.ATCDDDParser.ATCDDDDefinition;
import at.medevit.atc_codes.parser.ATCParser;
import at.medevit.atc_codes.parser.ATCParser.ATCDefinition;

public class ATCCodes {
	
	public static final String ATC_CODES_SERIALIZED_FILE = "/lib/ATCCodesMap.ser";
	
	private static ATCCodes instance = null;
	private HashMap<String, ATCCode> atcCodesMap = null;
	
	private ATCCodes(){}
	
	public static ATCCodes getInstance(){
		if (instance == null) {
			instance = new ATCCodes();
		}
		return instance;
	}
	
	public void initHashMapFromSerializedObject(){
		try {
			// use buffering
			InputStream is = ATCCodes.class.getResourceAsStream(ATC_CODES_SERIALIZED_FILE);
			ObjectInput input = new ObjectInputStream(is);
			try {
				// deserialize the List
				atcCodesMap =
					(HashMap<java.lang.String, at.medevit.atc_codes.ATCCode>) input.readObject();
			} finally {
				input.close();
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected HashMap<String, ATCCode> getAtcCodesMap(){
		return atcCodesMap;
	}
	
	public void init(final File atcFile, final File atcDDDFile){
		ATCParser atcParser = new ATCParser();
		ATCDDDParser atcdddParser = new ATCDDDParser();
		
		try {
			readXMLFile(atcFile, atcParser);
			readXMLFile(atcDDDFile, atcdddParser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ATCDefinition> atcDefinitions = atcParser.getDefinitions();
		HashMap<String, ATCDDDDefinition> atcDDDDefinitions = atcdddParser.getDddDefinitions();
		initHashMap(atcDefinitions, atcDDDDefinitions);
	}
	
	protected void initHashMap(List<ATCDefinition> atcDefinitions,
		HashMap<String, ATCDDDDefinition> atcDDDDefinitions){
		atcCodesMap = new HashMap<String, at.medevit.atc_codes.ATCCode>();
		
		for (ATCDefinition def : atcDefinitions) {
			ATCDDDDefinition dddDef = atcDDDDefinitions.get(def.atcCode);
			int level = determineLevel(def.atcCode);
			
			float ddd = 0.0f;
			ATCCode.DDD_UNIT_TYPE dddUt = null;
			String dddAc = null;
			String dddComment = null;
			if (dddDef != null) {
				ddd =
					(dddDef.ddd != null && dddDef.ddd.length() > 0) ? Float.parseFloat(dddDef.ddd)
							: 0.0f;
				if (dddDef.unitType != null)
					dddUt = ATCCode.DDD_UNIT_TYPE.valueOf(dddDef.unitType.toUpperCase());
				if (dddDef.admCode != null)
					dddAc = dddDef.admCode;
				if (dddDef.dddComment != null)
					dddComment = dddDef.dddComment;
			}
			
			ATCCode c = new ATCCode(def.atcCode, def.name, level, ddd, dddUt, dddAc, dddComment);
			atcCodesMap.put(def.atcCode, c);
		}
	}
	
	private int determineLevel(String atcCode){
		switch (atcCode.length()) {
		case 7:
			return 5;
		case 5:
			return 4;
		case 4:
			return 3;
		case 3:
			return 2;
		case 1:
			return 1;
		default:
			return 0;
		}
	}
	
	protected void readXMLFile(File inFile, DefaultHandler parser) throws IOException{
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(parser);
			xr.setErrorHandler(parser);
			
			FileReader fr = new FileReader(inFile);
			xr.parse(new InputSource(fr));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param atcCode
	 * @return {@link ATCCode} if valid ATC code, else <code>null</code>
	 */
	public ATCCode getATCCode(String atcCode){
		if (atcCodesMap == null)
			initHashMapFromSerializedObject();
		return atcCodesMap.get(atcCode.trim());
	}
}
