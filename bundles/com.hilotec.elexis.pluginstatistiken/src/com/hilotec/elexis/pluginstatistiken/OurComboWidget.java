/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    G. Weirich - modify to API Changes in 2.1 (ElexisEventDispatcher)
 *    
 *******************************************************************************/

package com.hilotec.elexis.pluginstatistiken;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.hilotec.elexis.pluginstatistiken.config.Konfiguration;
import com.hilotec.elexis.pluginstatistiken.config.KonfigurationQuery;

import ch.unibe.iam.scg.archie.model.RegexValidation;
import ch.unibe.iam.scg.archie.ui.widgets.ComboWidget;

/**
 * Wir benutzen hier ein eigenes ComboWidget, da eine statische Liste mit Eintraegen, wie sie das
 * von Archie gestellte Widget bietet, fuer unsere Beduerfnisse nicht reicht, da die vorhandenen
 * Abfragen erst nach dem Parsen der Konfigurationsdatei zur Verfuegung stehen.
 * 
 * @author Antoine Kaufmann
 */
public class OurComboWidget extends ComboWidget {
	public OurComboWidget(Composite parent, int style, String labelText, RegexValidation regex){
		super(parent, style, labelText, regex);
		
		List<KonfigurationQuery> ql = Konfiguration.getInstance().getQueries();
		String queries[] = new String[ql.size()];
		int i = 0;
		for (KonfigurationQuery kq : ql) {
			queries[i++] = kq.getTitle();
		}
		this.setItems(queries);
	}
}
