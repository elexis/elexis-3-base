/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 *******************************************************************************/

package com.hilotec.elexis.pluginstatistiken.schnittstelle;

import java.util.List;

/**
 * Einzelne Tabelle in einer IDatenquelle.
 * 
 * @author Antoine Kaufmann
 */
public interface ITabelle {
	/**
	 * Name dieser Tabelle auslesen
	 * 
	 * @return Tabellenname
	 */
	public String getName();
	
	/**
	 * Liste aller Datensaetze in dieser Tabelle holen
	 * 
	 * TODO: Vielleicht waere es nicht die duemmste Idee hier ueber etwas iteratoraehnliches
	 * nachzudenken, vorallem bei grossen Daten- bestaenden (grosse Tabellen oder noch schlimmer:
	 * Joins).
	 * 
	 * @return Datensatzliste
	 */
	public List<IDatensatz> getDatensaetze();
}
