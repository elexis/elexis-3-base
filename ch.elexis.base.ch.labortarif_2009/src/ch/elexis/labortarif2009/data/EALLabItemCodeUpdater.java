/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - initial API and implementation
 ******************************************************************************/
package ch.elexis.labortarif2009.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ch.elexis.data.LabItem;
import ch.elexis.data.Query;
import ch.elexis.labortarif2009.data.EALBlocksCodeUpdater.AnalysenUpdateTarifResolver;
import ch.rgw.tools.TimeTool;

public class EALLabItemCodeUpdater {
	
	public Object updateLabItemCodeAnalysen(){
		int absoluteCnt = 0;
		HashSet<String> problems = new HashSet<String>();

		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		List<LabItem> items = qli.execute();
		
		TimeTool today = new TimeTool();
		AnalysenUpdateTarifResolver resolver = new AnalysenUpdateTarifResolver();

		for (LabItem labItem : items) {
			String code = labItem.getBillingCode();
			if (code != null && !code.isEmpty()) {
				Labor2009Tarif tarif = (Labor2009Tarif) resolver.getTarif(code, today);
				if (tarif != null) {
					labItem.setBillingCode(tarif.getCode());
					absoluteCnt++;
				} else {
					problems.add(labItem.getName() + " -> " + code);
				}
			}
		}

		ArrayList<String> problemsList = new ArrayList<String>(problems);
		Collections.sort(problemsList);
		StringBuilder problemsString = new StringBuilder();
		for (String string : problemsList) {
			problemsString.append("- ").append(string).append("\n");
		}
		
		return absoluteCnt
			+ " EAL codes in Labor Parametern angepasst.\nIn folgenden Parametern sind noch fehlerhafte Leistungen\n"
			+ problemsString.toString();
	}
}
