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
