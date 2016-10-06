/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class FreieKategorie {

	String name;
	HashMap<Integer, String> value = new LinkedHashMap<Integer, String>();
	
	public void setName(String value){
		name = value;
		
	}
	
	public String getValue(int feldkennung) {
		return value.get(feldkennung);
	}

	public void setValue(int feldkennung, String value){
		this.value.put(feldkennung, value);
	}
}
