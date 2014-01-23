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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import at.medevit.atc_codes.ATCCode;
import at.medevit.atc_codes.ATCCodeServiceImpl;
import at.medevit.atc_codes.parser.ATCDDDParser;
import at.medevit.atc_codes.parser.ATCParser;

public class ATCCodesTest {
	
	public static final String ATC_CODES_MAP_SER = "ATCCodesMap.ser";
	public static final String ATC_FILE = "2013ATC.XML";
	public static final String ATC_DDD_FILE = "2013ATC_ddd.xml";
	
	ATCParser parser = new ATCParser();
	ATCDDDParser adddParser = new ATCDDDParser();
	
	@Test
	public void testReadATCFile() throws IOException{
		URL atcCodesFileUrl = ATCCodesTest.class.getResource(ATC_FILE);
		File atcCodesFile = new File(atcCodesFileUrl.getFile());
		ATCCodes.getInstance().readXMLFile(atcCodesFile, parser);
		Assert.assertEquals(5771, parser.getDefinitions().size());
	}
	
	@Test
	public void testReadATCDDDFile() throws IOException{
		URL atcDDDCodesFileUrl = ATCCodesTest.class.getResource(ATC_DDD_FILE);
		File atcDDDCodesFile = new File(atcDDDCodesFileUrl.getFile());
		ATCCodes.getInstance().readXMLFile(atcDDDCodesFile, adddParser);
		Assert.assertEquals(1851, adddParser.getDddDefinitions().size());
	}
	
	@Test
	public void testInitHashMap() throws IOException{
		URL atcCodesFileUrl = ATCCodesTest.class.getResource(ATC_FILE);
		File atcCodesFile = new File(atcCodesFileUrl.getFile());
		
		URL atcDDDCodesFileUrl = ATCCodesTest.class.getResource(ATC_DDD_FILE);
		File atcDDDCodesFile = new File(atcDDDCodesFileUrl.getFile());
		
		ATCCodes ac = ATCCodes.getInstance();
		ac.readXMLFile(atcCodesFile, parser);
		ac.readXMLFile(atcDDDCodesFile, adddParser);
		ac.initHashMap(parser.getDefinitions(), adddParser.getDddDefinitions());
		
		HashMap<String, ATCCode> atcCodesMap = ac.getAtcCodesMap();
		
		// use buffering
		OutputStream file = new FileOutputStream(ATC_CODES_MAP_SER);
		OutputStream buffer = new BufferedOutputStream(file);
		ObjectOutput output = new ObjectOutputStream(buffer);
		output.writeObject(atcCodesMap);
		output.close();
		
		System.out.println("Written to ");
		
		ATCCode c = ac.getATCCode("A01AB10");
		System.out.println(c.atcCode + " " + c.name + " " + c.level + " " + c.administrativeCode);
	}
	
	@Test
	public void testATCCodeServiceImplTest() throws IOException{
		URL atcCodesFileUrl = ATCCodesTest.class.getResource(ATC_FILE);
		File atcCodesFile = new File(atcCodesFileUrl.getFile());
		
		URL atcDDDCodesFileUrl = ATCCodesTest.class.getResource(ATC_DDD_FILE);
		File atcDDDCodesFile = new File(atcDDDCodesFileUrl.getFile());
		
		ATCCodes ac = ATCCodes.getInstance();
		ac.readXMLFile(atcCodesFile, parser);
		ac.readXMLFile(atcDDDCodesFile, adddParser);
		ac.initHashMap(parser.getDefinitions(), adddParser.getDddDefinitions());
		
		ATCCodeServiceImpl asi = new ATCCodeServiceImpl();
		List<ATCCode> result = asi.getHierarchyForATCCode("B03BB01");
		Assert.assertEquals(5, result.size());
		for (ATCCode atcCode : result) {
			System.out.println(atcCode.level + " " + atcCode.atcCode + "\t\t" + atcCode.name + " ("
				+ atcCode.dddComment + ")");
		}
	}
}
