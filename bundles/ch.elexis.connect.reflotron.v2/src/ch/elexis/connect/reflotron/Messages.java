package ch.elexis.connect.reflotron;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	public static final String BUNDLE_NAME = "ch.elexis.connect.reflotron.messages";

	public static String ReflotronSprintAction_29;
	public static String ReflotronSprintAction_ConnectionName;
	public static String ReflotronSprintAction_DefaultPort;
	public static String ReflotronSprintAction_DefaultTimeout;
	public static String ReflotronSprintAction_DefaultParams;
	public static String ReflotronSprintAction_DeviceName;

	public static String ReflotronSprintAction_ButtonName;
	public static String ReflotronSprintAction_IncompleteDataRecordMsg;
	public static String ReflotronSprintAction_ToolTip;

	public static String ReflotronSprintAction_LogError_Title;
	public static String ReflotronSprintAction_LogError_Text;
	public static String ReflotronSprintAction_NoPatientMsg;

	public static String ReflotronSprintAction_ProbeError_Title;

	public static String ReflotronSprintAction_Patient_Title;
	public static String ReflotronSprintAction_Patient_Text;
	public static String ReflotronSprintAction_PatientHeaderString;

	public static String ReflotronSprintAction_ResendMsg;
	public static String ReflotronSprintAction_RS232_Error_Title;
	public static String ReflotronSprintAction_RS232_Error_Text;

	public static String ReflotronSprintAction_RS232_Break_Title;
	public static String ReflotronSprintAction_RS232_Break_Text;

	public static String ReflotronSprintAction_RS232_Timeout_Title;
	public static String ReflotronSprintAction_RS232_Timeout_Text;
	public static String ReflotronSprintAction_UnknownPatientMsg;
	public static String ReflotronSprintAction_WaitMsg;
	public static String ReflotronSprintAction_WertHeaderString;

	public static String ReflotronSprintAction_UnknownPatientHeaderString;
	public static String ReflotronSprintAction_NoPatientInfo;
	public static String ReflotronSprintAction_ValueInfoMsg;

	public static String Preferences_Port;
	public static String Preferences_Baud;
	public static String Preferences_Databits;
	public static String Preferences_Parity;
	public static String Preferences_Stopbits;
	public static String Preferences_Timeout;
	public static String Preferences_Backgroundprocess;
	public static String Preferences_Log;
	public static String Reflotron_Probe_ResultatMsg;
	public static String Reflotron_Value_LabKuerzel;
	public static String Reflotron_Value_LabName;
	public static String Reflotron_Value_High;
	public static String Reflotron_Value_Low;
	public static String Reflotron_Value_Error;
	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
