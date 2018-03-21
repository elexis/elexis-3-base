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

package com.hilotec.elexis.pluginstatistiken;

/**
 * Wir benutzen hier der Sauberkeit halber eine eigene Exception, auch wenn die im Moment noch nicht
 * sehr viel tut.
 * 
 * @author Antoine Kaufmann
 */
public class PluginstatistikException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PluginstatistikException(String message){
		super(message);
	}
}
