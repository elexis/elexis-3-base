/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeService;
import at.medevit.atc_codes.parser.ATCDDDParser.ATCDDDDefinition;
import at.medevit.atc_codes.parser.ATCParser.ATCDefinition;

public class ATCCodes {
	
	public static final String ATC_CODES_SERIALIZED_FILE = "/lib/ATCCodesMap.ser";
	
	private static ATCCodes instance = null;
	private HashMap<String, ATCCode> atcCodesMap = null;
	private ATCHierarchyComparator ahc = new ATCHierarchyComparator();
	
	private ATCCodes(){
		initHashMapFromSerializedObject();
	}
	
	public static ATCCodes getInstance(){
		if (instance == null) {
			instance = new ATCCodes();
		}
		return instance;
	}
	
	private void initHashMapFromSerializedObject(){
		try {
			// use buffering
			InputStream is = ATCCodes.class.getResourceAsStream(ATC_CODES_SERIALIZED_FILE);
			if (is == null) {
				// patch to load library from within non-osgi environment
				is = ATCCodes.class.getResourceAsStream("/ATCCodesMap.ser");
			}
			ObjectInput input = new ObjectInputStream(is);
			try {
				// deserialize the List
				atcCodesMap =
					(HashMap<java.lang.String, at.medevit.atc_codes.ATCCode>) input.readObject();
			} finally {
				input.close();
			}
		} catch (ClassNotFoundException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected HashMap<String, ATCCode> getAtcCodesMap(){
		return atcCodesMap;
	}
	
	protected void initHashMap(List<ATCDefinition> atcDefinitions,
		HashMap<String, ATCDDDDefinition> atcDDDDefinitions, HashMap<String, String> atcCodeToGerman){
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
			
			String germanName = atcCodeToGerman.get(def.atcCode);
			
			ATCCode c =
				new ATCCode(def.atcCode, def.name, germanName, level, ddd, dddUt, dddAc, dddComment);
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
	
	/**
	 * Used in test code only
	 * 
	 * @param inFile
	 * @param parser
	 * @throws IOException
	 */
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
		return atcCodesMap.get(atcCode.trim());
	}
	
	/**
	 * @see ATCCodeService#getATCCodesMatchingName(String, int)
	 */
	public List<ATCCode> getATCCodesMatchingName(String name, int language, int matchType){
		List<ATCCode> ret = new ArrayList<>();
		
		Collection<ATCCode> values = atcCodesMap.values();
		
		if (matchType == ATCCodeService.MATCH_NAME_BY_NAME_ONLY) {
			matchByNameOnly(ret, values, language, name);
		} else {
			matchByNameOrATC(ret, values, language, name);
		}
		
		orderByATCHierarchy(ret);
		
		return ret;
	}
	
	/**
	 * Orders the elements in the list according to the ATC hierarchy
	 * 
	 * @param ret
	 */
	private void orderByATCHierarchy(List<ATCCode> ret){
		Collections.sort(ret, ahc);
	}
	
	private void matchByNameOrATC(List<ATCCode> ret, Collection<ATCCode> values, int language,
		String name){
		if (language == ATCCodeService.ATC_NAME_LANGUAGE_GERMAN) {
			for (ATCCode atcCode : values) {
				if ((atcCode.name_german != null && atcCode.name_german.toLowerCase().contains(
					name.toLowerCase()))
					|| atcCode.atcCode.contains(name)) {
					ret.add(atcCode);
				} else if (atcCode.name != null
					&& atcCode.name.toLowerCase().contains(name.toLowerCase())) {
					ret.add(atcCode);
				}
			}
		} else {
			for (ATCCode atcCode : values) {
				if (atcCode.name != null && atcCode.name.toLowerCase().contains(name.toLowerCase())) {
					ret.add(atcCode);
				}
			}
		}
	}
	
	private void matchByNameOnly(List<ATCCode> ret, Collection<ATCCode> values, int language,
		String name){
		if (language == ATCCodeService.ATC_NAME_LANGUAGE_GERMAN) {
			for (ATCCode atcCode : values) {
				if (atcCode.name_german != null
					&& atcCode.name_german.toLowerCase().contains(name.toLowerCase())) {
					ret.add(atcCode);
				} else if (atcCode.name != null
					&& atcCode.name.toLowerCase().contains(name.toLowerCase())) {
					ret.add(atcCode);
				}
			}
		} else {
			for (ATCCode atcCode : values) {
				if (atcCode.name != null && atcCode.name.toLowerCase().contains(name.toLowerCase())) {
					ret.add(atcCode);
				}
			}
		}
	}
	
	public List<ATCCode> getAllATCCodes(){
		ArrayList<ATCCode> list = new ArrayList<ATCCode>(atcCodesMap.values());
		orderByATCHierarchy(list);
		return Collections.unmodifiableList(list);
	}
}
