/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package com.hilotec.elexis.messwerte.v2;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "com.hilotec.elexis.messwerte.v2.messages"; //$NON-NLS-1$
    public static String DBError;
    public static String DBErrorTable;
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
    public static String ExportDialog_CSV_Export;
    public static String ExportDialog_ExceptionDialog;
    public static String ExportDialog_Exception_Datum;
    public static String ExportDialog_Exception_PatNumber;
    public static String ExportDialog_btnDateAll;
    public static String ExportDialog_btnDateFromTo;
    public static String ExportDialog_btnPatAll;
    public static String ExportDialog_btnPatFromTo;
    public static String ExportDialog_lblDate;
    public static String ExportDialog_lblDateTo;
    public static String ExportDialog_lblPatTo;
    public static String ExportDialog_lblPatient;
    public static String InvalidMessage_EnumValue;
    public static String MessungBearbeiten_EditMessung;
    public static String MessungBearbeiten_InvalidValue;
    public static String MessungBearbeiten_MessungLabel;
    public static String MessungBearbeiten_PatientLabel;
    public static String MessungKonfiguration_ErrorInXML;
    public static String MessungKonfiguration_ErrorInXMLOnLine;
    public static String MessungKonfiguration_ErrorReadXML;
    public static String MessungKonfiguration_ErrorReadXMLFailure;
    public static String MessungKonfiguration_UnknownFieldType;
    public static String MessungenUebersichtV21_Cancelled;
    public static String MessungenUebersichtV21_Error;
    public static String MessungenUebersichtV21_Information;
    public static String MessungenUebersichtV21_Initializing;
    public static String MessungenUebersicht_Table_Datum;
    public static String MessungenUebersicht_action_copy;
    public static String MessungenUebersicht_action_copy_ToolTip;
    public static String MessungenUebersicht_action_copy_error;
    public static String MessungenUebersicht_action_copy_errorMessage;
    public static String MessungenUebersicht_action_edit;
    public static String MessungenUebersicht_action_edit_ToolTip;
    public static String MessungenUebersicht_action_export;
    public static String MessungenUebersicht_action_export_ToolTip;
    public static String MessungenUebersicht_action_export_aborted;
    public static String MessungenUebersicht_action_export_error;
    public static String MessungenUebersicht_action_export_filepath_error;
    public static String MessungenUebersicht_action_export_progress;
    public static String MessungenUebersicht_action_export_success;
    public static String MessungenUebersicht_action_export_title;
    public static String MessungenUebersicht_action_loeschen;
    public static String MessungenUebersicht_action_loeschen_ToolTip;
    public static String MessungenUebersicht_action_loeschen_delete_0;
    public static String MessungenUebersicht_action_loeschen_delete_1;
    public static String MessungenUebersicht_action_neu;
    public static String MessungenUebersicht_action_neu_ToolTip;
    public static String MessungenUebersicht_action_reload;
    public static String MessungenUebersicht_action_reload_ToolTip;
    public static String MessungenUebersicht_kein_Patient;
    public static String MesswertBase_DataField;
    public static String MesswertBase_Failure1;
    public static String MesswertBase_Failure2;
    public static String MesswertBase_NoData;
    public static String MesswertTypBool_No;
    public static String MesswertTypBool_Yes;
    public static String MesswertTypNum_CastFailure;
    public static String MesswertTypScale_CastFailure;

    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
