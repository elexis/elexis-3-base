package ch.medshare.connect.abacusjunior;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.medshare.connect.abacusjunior.messages"; //$NON-NLS-1$

  public static String AbacusJuniorAction_ConnectionName;
  public static String AbacusJuniorAction_DefaultPort;
  public static String AbacusJuniorAction_DefaultParams;

  public static String AbacusJuniorAction_ButtonName;
  public static String AbacusJuniorAction_ToolTip;

  public static String AbacusJuniorAction_LogError_Title;
  public static String AbacusJuniorAction_LogError_Text;

  public static String AbacusJuniorAction_Patient_Title;
  public static String AbacusJuniorAction_Patient_Text;

  public static String AbacusJuniorAction_RS232_Error_Title;
  public static String AbacusJuniorAction_RS232_Error_Text;

  public static String AbacusJuniorAction_RS232_Break_Title;
  public static String AbacusJuniorAction_RS232_Break_Text;

  public static String AbacusJuniorAction_RS232_Timeout_Title;
  public static String AbacusJuniorAction_RS232_Timeout_Text;

  public static String AbacusJunior_Value_LabKuerzel;
  public static String AbacusJunior_Value_LabName;
  public static String AbacusJunior_Value_Error;
  public static String Preferences_Port;
  public static String Preferences_Baud;
  public static String Preferences_Databits;
  public static String Preferences_Parity;
  public static String Preferences_Stopbits;
  public static String Preferences_Log;
  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

