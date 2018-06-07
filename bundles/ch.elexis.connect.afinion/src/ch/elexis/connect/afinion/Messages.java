package ch.elexis.connect.afinion;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.elexis.connect.afinion.messages";

  public static String AfinionAS100Action_ConnectionName;
  public static String AfinionAS100Action_DefaultPort;
  public static String AfinionAS100Action_DefaultTimeout;
  public static String AfinionAS100Action_DefaultParams;
  public static String AfinionAS100Action_DeviceName;

  public static String AfinionAS100Action_ButtonName;
  public static String AfinionAS100Action_ToolTip;

  public static String AfinionAS100Action_LogError_Title;
  public static String AfinionAS100Action_LogError_Text;
  public static String AfinionAS100Action_NoPatientSelectedMsg;
  public static String AfinionAS100Action_NoValuesMsg;

  public static String AfinionAS100Action_Patient_Title;
  public static String AfinionAS100Action_Patient_Text;
  public static String AfinionAS100Action_PatientHeaderString;
  public static String AfinionAS100Action_ProbeError_Title;

  public static String AfinionAS100Action_RS232_Error_Title;
  public static String AfinionAS100Action_RS232_Error_Text;

  public static String AfinionAS100Action_RS232_Break_Title;
  public static String AfinionAS100Action_RS232_Break_Text;

  public static String AfinionAS100Action_RS232_Timeout_Title;
  public static String AfinionAS100Action_RS232_Timeout_Text;
  public static String AfinionAS100Action_UnknownPatientHeaderString;
  public static String AfinionAS100Action_ValueOutOfRangeWarning;
  public static String AfinionAS100Action_NoPatientInfo;
  public static String AfinionAS100Action_ValueInfoMsg;
  public static String AfinionAS100Action_WaitMsg;

  public static String Afinion_Value_LabKuerzel;
  public static String Afinion_Value_LabName;
  public static String Afinion_Value_High;
  public static String Afinion_Value_Low;
  public static String Afinion_Value_Error;
  
  public static String Preferences_Port;
  public static String Preferences_Baud;
  public static String Preferences_Databits;
  public static String Preferences_Parity;
  public static String Preferences_Stopbits;
  public static String Preferences_Timeout;
  public static String Preferences_Backgroundprocess;
  public static String Preferences_Log;
  public static String Preferences_btnCheckButton_text;
  public static String Preferences_lblAdditionalSettings_text;
		  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

