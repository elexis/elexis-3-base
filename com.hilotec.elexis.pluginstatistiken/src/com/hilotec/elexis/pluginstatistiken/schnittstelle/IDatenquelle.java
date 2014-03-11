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
 * Interface fuer eine Datenquelle fuer das Statistik-Plugin. Eine Datenquelle stellt mehrere
 * Tabellen zur Verfuegung. Dieses Interface ist dafuer gedacht an den Erweiterungspunkt
 * com.hilotec.elexis.pluginstatistiken.Datenquelle angehaengt zu werden.
 * 
 * Als Beispiel sei hier auf die Datenquelle des hilotec-messwerte Plugins verwiesen.
 * 
 * @author Antoine Kaufmann
 */
public interface IDatenquelle {
	/**
	 * Name dieser Datenquelle auslesen
	 * 
	 * @return Name
	 */
	public String getName();
	
	/**
	 * Liste aller Tabellen, die diese Datenquelle liefert, auslesen.
	 * 
	 * @return Liste aller Tabellen
	 */
	public List<ITabelle> getTabellen();
	
	/**
	 * Bestimmte Tabelle anhand des Namens auslesen
	 * 
	 * @param name
	 *            Tabellenname
	 * 
	 * @return Tabelle oder null falls diese nicht existiert.
	 */
	public ITabelle getTabelle(String name);
}
