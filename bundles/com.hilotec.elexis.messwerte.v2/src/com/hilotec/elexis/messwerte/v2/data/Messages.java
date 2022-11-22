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

public class Messages{
    public static String DataAccessor_DataDescription = ch.elexis.core.l10n.Messages.DataAccessor_DataDescription;
    public static String DataAccessor_FilterAll = ch.elexis.core.l10n.Messages.DataAccessor_FilterAll;
    public static String DataAccessor_FilterFirst = ch.elexis.core.l10n.Messages.DataAccessor_FilterFirst;
    public static String DataAccessor_FilterFirstSince = ch.elexis.core.l10n.Messages.DataAccessor_FilterFirstSince;
    public static String DataAccessor_FilterLast = ch.elexis.core.l10n.Messages.DataAccessor_FilterLast;
    public static String DataAccessor_FilterLastBefore = ch.elexis.core.l10n.Messages.DataAccessor_FilterLastBefore;
    public static String DataAccessor_InvalidFieldName = ch.elexis.core.l10n.Messages.Core_Invalid_Fieldname;
    public static String DataAccessor_InvalidMeasureType = ch.elexis.core.l10n.Messages.DataAccessor_InvalidMeasureType;
    public static String DataAccessor_InvalidParameter = ch.elexis.core.l10n.Messages.Core_Invalid_Parameter;
    public static String DataAccessor_NoDataField = ch.elexis.core.l10n.Messages.DataAccessor_NoDataField;
    public static String DataAccessor_NotFound = ch.elexis.core.l10n.Messages.DataAccessor_NotFound;
    public static String DataAccessor_Title = ch.elexis.core.l10n.Messages.DataAccessor_Title;
    public static String MessungKonfiguration_ErrorInXML = ch.elexis.core.l10n.Messages.MessungKonfiguration_ErrorInXML;
    public static String MessungKonfiguration_ErrorInXMLOnLine = ch.elexis.core.l10n.Messages.MessungKonfiguration_ErrorInXMLOnLine;
    public static String MessungKonfiguration_ErrorReadXML = ch.elexis.core.l10n.Messages.MessungKonfiguration_ErrorReadXML;
    public static String MessungKonfiguration_ErrorReadXMLFailure = ch.elexis.core.l10n.Messages.MessungKonfiguration_ErrorReadXMLFailure;
    public static String MessungKonfiguration_UnknownFieldType = ch.elexis.core.l10n.Messages.MessungKonfiguration_UnknownFieldType;
    public static String MesswertBase_DataField = ch.elexis.core.l10n.Messages.MesswertBase_DataField;
    public static String MesswertBase_Failure1 = ch.elexis.core.l10n.Messages.MesswertBase_Failure1;
    public static String MesswertBase_Failure2 = ch.elexis.core.l10n.Messages.MesswertBase_Failure2;
    public static String MesswertBase_NoData = ch.elexis.core.l10n.Messages.MesswertBase_NoData;
	static {
		// initialize resource bundle
		NLS.initializeMessages("com.hilotec.elexis.messwerte.v2.data.messages", //$NON-NLS-1$
				Messages.class);
	}


}
