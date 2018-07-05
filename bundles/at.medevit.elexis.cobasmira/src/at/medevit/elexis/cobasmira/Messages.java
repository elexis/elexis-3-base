/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package at.medevit.elexis.cobasmira;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "at.medevit.elexis.cobasmira.messages";

    public static String CobasMiraAction_ButtonName;
    public static String CobasMiraAction_ConnectionName;
    public static String CobasMiraAction_DefaultIgnoreUser;
    public static String CobasMiraAction_DefaultParams;
    public static String CobasMiraAction_DefaultPort;
    public static String CobasMiraAction_DefaultTimeout;
    public static String CobasMiraAction_DeviceName;
    public static String CobasMiraAction_LogError_Text;
    public static String CobasMiraAction_LogError_Title;
    public static String CobasMiraAction_OwnLabIdentification;
    public static String CobasMiraAction_ProbeInput;
    public static String CobasMiraAction_ProbeInputFault;
    public static String CobasMiraAction_RS232_Break_Text;
    public static String CobasMiraAction_RS232_Break_Title;
    public static String CobasMiraAction_RS232_Error_Title;
    public static String CobasMiraAction_RS232_Timeout_Text;
    public static String CobasMiraAction_RS232_Timeout_Title;
    public static String CobasMiraAction_ToolTip;
    public static String CobasMiraAction_UnknownPatientHeaderString;
    public static String CobasMiraAction_ValueInfoMsg;
    public static String CobasMiraAction_WaitMsg;
    public static String CobasMiraAction_WrongPatientID;
    public static String DeviceView_tblclmnNoKommastellen_text;
    public static String DeviceView_tblclmnNoKommastellen_toolTipText;
    public static String Message_notset;
    public static String Preferences_Backgroundprocess;
    public static String Preferences_Baud;
    public static String Preferences_Databits;
    public static String Preferences_FlowCtrlIn;
    public static String Preferences_FlowCtrlOut;
    public static String Preferences_IgnoreUserOnInput;
    public static String Preferences_LabIdentification;
    public static String Preferences_Log;
    public static String Preferences_Parity;
    public static String Preferences_Port;
    public static String Preferences_Stopbits;
    public static String Preferences_Timeout;
    public static String Preferences_btnBrowse_text;
    public static String Preferences_lblMappingDatei;
    public static String Preferences_lblcontrolLogLoc;
    public static String UI_dateTime;
    public static String UI_description;
    public static String UI_elexis_state;
    public static String UI_type;
  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

