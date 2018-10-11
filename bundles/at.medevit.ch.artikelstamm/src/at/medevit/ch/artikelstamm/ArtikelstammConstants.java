/*******************************************************************************
 * Copyright (c) 2013 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm;

public class ArtikelstammConstants {
	
	public static final String EXTINFO_VAL_PPUB_OVERRIDE_STORE = "PPUB_OVERRIDE_STORE";
	public static final String EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE = "PKG_SIZE_OVERRIDE_STORE";
	
	public static final String STS_CLASS = "ch.artikelstamm.elexis.common.ArtikelstammItem";
	public static final String CODESYSTEM_NAME = "Artikelstamm";
	
	/**
	 * The element type:<br>
	 * <br>
	 * <b>P</b> a Pharma items<br>
	 * <b>N</b> a Non-Pharma items</br>
	 * <b>X</b> a Product
	 */
	public enum TYPE {
			P, N, X
	};
	
}
