/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class FreieKategorie {

	String name;
	HashMap<Integer, String> value = new LinkedHashMap<Integer, String>();

	public void setName(String value) {
		name = value;

	}

	public String getValue(int feldkennung) {
		return value.get(feldkennung);
	}

	public void setValue(int feldkennung, String value) {
		this.value.put(feldkennung, value);
	}
}
