/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.arzttarife.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrustCenters {
	public static final int CTESIAS = 51;
	public static final int GALLONET = 52;
	public static final int HAWATRUST = 53;
	public static final int MEDKEY = 54;
	public static final int PONTENOVA = 55;
	public static final int SYNDATA = 55;
	public static final int TC_AARGAU = 57;
	public static final int THURCARE = 58;
	public static final int TC_TICINO = 59;
	public static final int ZUERIDOC = 60;
	public static final int TRUSTMED = 61;
	public static final int VD_CDC = 62;
	public static final int TC_TEST = 69;

	public static List<String> getTCList() {
		ArrayList<String> list = new ArrayList<String>(tc.size());
		for (String o : tc.keySet()) {
			list.add(o);
		}
		return list;
	}

	/** EAN des TrustCenters holen */
	public static String getTCEAN(String tcName) {
		return getTCEAN(tc.get(tcName));
	}

	public static String getTCEAN(Integer tcCode) {
		if (tcCode == null) {
			return null;
		}
		return tcEAN.get(tcCode);
	}

	// Ctésias cte 7601001370210 51
	// GallOnet gal 7601001370241 52
	// hawatrust haw 7601001370159 53
	// medkey med 7601001370333 54
	// PonteNova pon 7601001370203 55
	// syndata syn 7601001370166 56
	// TC Aargau aar 7601001370135 57
	// TC thurcare thu 7601001370173 58
	// TC ticino tic 7601001370722 59
	// ZueriDoc zue 7601001370456 60
	// trustmed tru 7601001370227 61
	// CdC Vd 7609999036705 62
	// TC test tes 7601001370128 69

	public static final HashMap<String, Integer> tc = new HashMap<String, Integer>();
	public static final HashMap<Integer, String> tcEAN = new HashMap<Integer, String>();
	static {
		tc.put("Ctésias", CTESIAS); //$NON-NLS-1$
		tcEAN.put(CTESIAS, "7601001370210"); //$NON-NLS-1$
		tc.put("GallOnet", GALLONET); //$NON-NLS-1$
		tcEAN.put(GALLONET, "7601001370241"); //$NON-NLS-1$
		tc.put("hawatrust", HAWATRUST); //$NON-NLS-1$
		tcEAN.put(HAWATRUST, "7601001370159"); //$NON-NLS-1$
		tc.put("+medkey", MEDKEY); //$NON-NLS-1$
		tcEAN.put(MEDKEY, "7601001370333"); //$NON-NLS-1$
		tc.put("PonteNova", PONTENOVA); //$NON-NLS-1$
		tcEAN.put(PONTENOVA, "7601001370203"); //$NON-NLS-1$
		tc.put("syndata", SYNDATA); //$NON-NLS-1$
		tcEAN.put(SYNDATA, "7601001370166"); //$NON-NLS-1$
		tc.put("TC Aargau", TC_AARGAU); //$NON-NLS-1$
		tcEAN.put(TC_AARGAU, "7601001370135"); //$NON-NLS-1$
		tc.put("thurcare", THURCARE); //$NON-NLS-1$
		tcEAN.put(THURCARE, "7601001370173"); //$NON-NLS-1$
		tc.put("TC Ticino", TC_TICINO); //$NON-NLS-1$
		tcEAN.put(TC_TICINO, "7601001370722"); //$NON-NLS-1$
		tc.put("TC züridoc", ZUERIDOC); //$NON-NLS-1$
		tcEAN.put(ZUERIDOC, "7601001370456"); //$NON-NLS-1$
		tc.put("trustmed", TRUSTMED); //$NON-NLS-1$
		tcEAN.put(TRUSTMED, "7601001370227"); //$NON-NLS-1$
		tc.put("TC test", TC_TEST); //$NON-NLS-1$
		tcEAN.put(TC_TEST, "7601001370128"); //$NON-NLS-1$
	}
}
