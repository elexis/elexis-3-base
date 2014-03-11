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

/**
 * Einzelner Datensatz in einer ITabelle.
 * 
 * @author Antoine Kaufmann
 */
public interface IDatensatz {
	/**
	 * Spalte anhand des Spaltennamens auslesen
	 * 
	 * @param name
	 *            Spaltenname
	 * 
	 * @return Spaltenwert oder null falls die Spalte nicht existiert
	 */
	String getSpalte(String name);
}
