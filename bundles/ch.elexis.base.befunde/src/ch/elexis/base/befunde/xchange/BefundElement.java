/*******************************************************************************
 * Copyright (c) 2008-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.befunde.xchange;

import java.util.List;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.ui.exchange.XChangeExporter;
import ch.elexis.core.ui.exchange.elements.FindingElement;
import ch.elexis.core.ui.exchange.elements.MedicalElement;
import ch.elexis.core.ui.exchange.elements.MetaElement;
import ch.elexis.core.ui.exchange.elements.ResultElement;
import ch.elexis.core.ui.exchange.elements.XidElement;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.XMLTool;

public class BefundElement extends ResultElement {

	/**
	 * Ein neues Resultat hinzufügen. Erstellt ggf. das dazugehörige FindingElement.
	 * ID des FindingElements ist die id des Messwerts mit angehängtem Spalten-Namen
	 *
	 * @param me
	 * @param mw
	 * @param fl
	 * @return
	 */
	public static BefundElement addBefund(MedicalElement me, Messwert mw, String field) {
		List<FindingElement> findings = me.getAnalyses();
		String raw_id = mw.getId() + field;
		String id = XMLTool.idToXMLID(raw_id);
		for (FindingElement fe : findings) {
			XidElement eXid = fe.getXid();
			if (eXid != null) {
				if (id.equals(eXid.getID())) {
					BefundElement bf = new BefundElement().asExporter(me.getSender(), mw, field);
					me.addAnalyse(bf);
					return bf;
				}
			}
		}
		BefundeItem bi = new BefundeItem().asExporter(me.getSender(), mw, field);
		me.addFindingItem(bi);
		BefundElement bf = new BefundElement().asExporter(me.getSender(), mw, field);
		me.addAnalyse(bf);
		return bf;
	}

	BefundElement asExporter(XChangeExporter home, Messwert mw, String field) {
		asExporter(home);
		TimeTool tt = new TimeTool(mw.getDate());
		String date = tt.toString(TimeTool.DATE_COMPACT);
		String raw_id = mw.getId() + field + date;
		setAttribute("id", XMLTool.idToXMLID(raw_id)); //$NON-NLS-1$
		setAttribute(ATTR_DATE, tt.toString(TimeTool.DATETIME_XML));
		setAttribute(ATTR_LABITEM, XMLTool.idToXMLID(mw.getId() + field));
		add(new MetaElement().asExporter(home, ATTRIB_CREATOR, Messwert.PLUGIN_ID));
		ResultElement eResult = new ResultElement();
		eResult.setText(mw.getResult(field));
		add(eResult);
		home.getContainer().addChoice(this, mw.getLabel() + ":" + field, mw); //$NON-NLS-1$
		return this;
	}
}
