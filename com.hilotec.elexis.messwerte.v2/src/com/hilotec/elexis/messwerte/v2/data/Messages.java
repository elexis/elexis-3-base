/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.hilotec.elexis.messwerte.v2.data.messages"; //$NON-NLS-1$
	public static String DataAccessor_DataDescription;
	public static String DataAccessor_FilterAll;
	public static String DataAccessor_FilterFirst;
	public static String DataAccessor_FilterFirstSince;
	public static String DataAccessor_FilterLast;
	public static String DataAccessor_FilterLastBefore;
	public static String DataAccessor_InvalidFieldName;
	public static String DataAccessor_InvalidMeasureType;
	public static String DataAccessor_InvalidParameter;
	public static String DataAccessor_NoDataField;
	public static String DataAccessor_NotFound;
	public static String DataAccessor_Title;
	public static String MessungKonfiguration_ErrorInXML;
	public static String MessungKonfiguration_ErrorInXMLOnLine;
	public static String MessungKonfiguration_ErrorReadXML;
	public static String MessungKonfiguration_ErrorReadXMLFailure;
	public static String MessungKonfiguration_UnknownFieldType;
	public static String MesswertBase_DataField;
	public static String MesswertBase_Failure1;
	public static String MesswertBase_Failure2;
	public static String MesswertBase_NoData;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
