/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.elexis.core.jdt.NonNull;

public class ArticleToImmunisationModel {
	
	private static Map<String, List<String>> atim = null;
	
	public static @NonNull List<String> getImmunisationForAtcCode(String atcCode) {
		if(atim == null) initModel();
		List<String> list = atim.get(atcCode.trim());
		if(list==null) return Collections.emptyList();
		return list;
	}
	
	private static void initModel(){
		atim = new HashMap<>();
		
		inl("J07AE01;J07AE");
		inl("J07AN01;J07AN");
		inl("J07AG01;J07AG");
		inl("J07AH04;J07AH");
		inl("J07AH07;J07AH");
		inl("J07AH08;J07AH");
		inl("J07AJ52;J07AF,J07AM,J07AJ");
		inl("J07AL01;J07AL");
		inl("J07AL02;J07AL");
		inl("J07AM01;J07AM");
		inl("J07AM51;J07AF,J07AM");		
		inl("J07AP01;J07AP");
		inl("J07BA01;J07BA01");
		inl("J07BA02;J07BA02");
		inl("J07BB02;J07BB");
		inl("J07BC01;J07BC01");	
		inl("J07BC02;J07BC02");
		inl("J07BC20;J07BC01,J07BC02");
		inl("J07BD52;J07BD,J07BE,J07BJ");
		inl("J07BD54;J07BD,J07BE,J07BJ,J07BK");
		inl("J07BF03;J07BF");
		inl("J07BG01;J07BG");
		inl("J07BH01;J07BH");
		inl("J07BK01;J07BK");
		inl("J07BL01;J07BL");
		inl("J07BM01;J07BM");
		inl("J07BM02;J07BM");
		inl("J07CA01;J07AF,J07AM,J07AJ");
		inl("J07CA02;J07AF,J07AM,J07AJ,J07BF");
		inl("J07CA06;J07AF,J07AM,J07AJ,J07BF,J07AG");
		inl("J07CA09;J07AF,J07AM,J07AJ,J07BF,J07AG,J07BC01");
		
		atim = Collections.unmodifiableMap(atim);
	}

	private static void inl(String string){
		String[] s = string.split(";");
		atim.put(s[0], il(s[1]));
	}

	private static List<String> il(String string){
		String[] split = string.split(",");
		List<String> ret = new ArrayList<>(split.length);
			for (String s : split) {
				ret.add(s);
			}
		ret = Collections.unmodifiableList(ret);
		return ret;
	}
}
