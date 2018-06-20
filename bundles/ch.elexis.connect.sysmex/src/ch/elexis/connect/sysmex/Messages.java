package ch.elexis.connect.sysmex;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.elexis.connect.sysmex.messages";

  public static String SysmexAction_ConnectionName;
  public static String SysmexAction_DefaultPort;
  public static String SysmexAction_DefaultTimeout;
  public static String SysmexAction_DefaultParams;
  public static String SysmexAction_DeviceName;

  public static String SysmexAction_ButtonName;
  public static String SysmexAction_ToolTip;

  public static String SysmexAction_LogError_Title;
  public static String SysmexAction_LogError_Text;
  public static String SysmexAction_NoPatientMsg;

  public static String SysmexAction_ProbeError_Title;

  public static String SysmexAction_Patient_Title;
  public static String SysmexAction_Patient_Text;
  public static String SysmexAction_PatientHeaderString;

  public static String SysmexAction_ResendMsg;
  public static String SysmexAction_RS232_Error_Title;
  public static String SysmexAction_RS232_Error_Text;

  public static String SysmexAction_RS232_Break_Title;
  public static String SysmexAction_RS232_Break_Text;

  public static String SysmexAction_RS232_Timeout_Title;
  public static String SysmexAction_RS232_Timeout_Text;
  public static String SysmexAction_WaitMsg;
  public static String SysmexAction_ErrorTitle;
  public static String SysmexAction_WrongDataFormat;

  public static String Preferences_Port;
  public static String Preferences_Baud;
  public static String Preferences_Databits;
  public static String Preferences_Parity;
  public static String Preferences_Stopbits;
  public static String Preferences_Timeout;
  public static String Preferences_Backgroundprocess;
  public static String Preferences_Log;
  public static String Preferences_Modell;
  public static String Preferences_RDW;
  public static String Preferences_Verbindung;
  public static String Sysmex_Probe_ResultatMsg;
  public static String Sysmex_Value_LabKuerzel;
  public static String Sysmex_Value_LabName;
  public static String Sysmex_Value_High;
  public static String Sysmex_Value_Low;
  public static String Sysmex_Value_Error;

  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

