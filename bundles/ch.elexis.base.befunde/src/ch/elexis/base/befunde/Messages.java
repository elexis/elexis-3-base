/*******************************************************************************
 * Copyright (c) 2005-2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: Messwert.java 1185 2006-10-29 15:29:30Z rgw_ch $
 *******************************************************************************/

package ch.elexis.base.befunde;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ch.rgw.tools.ExHandler;

/**
 * This is the basis of the internationalization system. Every single String that is visible to the
 * user should be translatet through this mechanism The text file messages.properties contains the
 * "generic" versions of all translatable Strings. To create a new language file, one must only
 * create a file called messages_<language>_locale.properties, where locale is optionale. Thus you
 * might create messages_fr.properties to create a french localization or messages_fr_CH.properties
 * to create an even more specific version
 * 
 * @author gerry
 * 
 */
public class Messages {
	private static final String BUNDLE_NAME = "ch.elexis.base.befunde.messages"; //$NON-NLS-1$
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private Messages(){}
	
	public static String getString(String key){
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getString(String key, Object[] params){
		if (params == null) {
			return getString(key);
		}
		try {
			return java.text.MessageFormat.format(getString(key), params);
		} catch (Exception e) {
			ExHandler.handle(e);
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
