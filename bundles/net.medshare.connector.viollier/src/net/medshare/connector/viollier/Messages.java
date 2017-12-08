/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2013
 * 
 *******************************************************************************/
package net.medshare.connector.viollier;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "net.medshare.connector.viollier.messages"; //$NON-NLS-1$
	
	public static String Preferences_undefiniert;
	public static String Preferences_GlobalSettings;
	public static String Preferences_LocalSettingsFor;
	public static String Preferences_MandantSettingsFor;
	
	public static String Preferences_LoginUrl;
	public static String Preferences_ConsultItUrl;
	public static String Preferences_OrderItUrl;
	
	public static String Preferences_UserName;
	public static String Preferences_UserPassword;
	public static String Preferences_ViollierClientId;
	
	public static String Preferences_PreferedPresentation;
	
	public static String DefaultSetting_LoginUrl;
	public static String DefaultSetting_ConsultItUrl;
	public static String DefaultSetting_OrderItUrl;
	
	public static String Handler_errorTitleGetCookie;
	public static String Handler_errorMessageGetCookie;
	public static String Handler_errorMessageNoPatientSelected;
	public static String Handler_errorTitleNoPatientSelected;
	
	public static String Handler_errorMessageNoLabResultSelected;
	public static String Handler_errorTitleNoLabResultSelected;
	
	public static String Handler_errorMessageNoOrderFound;
	public static String Handler_errorTitleNoOrderFound;
	
	public static String Exception_errorMessageNoUserPasswordDefined;
	public static String Exception_errorTitleNoUserPasswordDefined;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
