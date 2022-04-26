package ch.elexis.omnivore;

public class PreferenceConstants {

	public static final String PREFBASE = Constants.PLUGIN_ID + "/";
	public static final String STOREFSGLOBAL = PREFBASE + "store_in_fs_global";
	public static final String STOREFS = PREFBASE + "store_in_fs";
	public static final String BASEPATH = PREFBASE + "basepath";
	public static final String CATEGORIES = PREFBASE + "categories";
	public static final String DATE_MODIFIABLE = PREFBASE + "date_modifiable";
	public static final String PREFERENCE_SRC_PATTERN = PREFBASE + "src_pattern";
	public static final String PREFERENCE_DEST_DIR = PREFBASE + "dest_dir";
	public static final String PREF_MAX_FILENAME_LENGTH = PREFBASE + "max_filename_length";
	public static final String TWAINACCESS_TYPE = PREFBASE + "twainaccess_type";
	public static final String AUTO_BILLING = PREFBASE + "automatic_billing";
	public static final String AUTO_BILLING_BLOCK = PREFBASE + "automatic_billing_block";

	// The following setting is used in ch.elexis.omnivore.data/DocHandle.java.
	// Linux and MacOS may be able to handle longer filenames, but we observed that
	// Windows 7 64-bit
	// will not import files with names longer than 80 chars.
	// So I make this setting configurable. Including a safe default and limits that
	// a user cannot
	// exceed.
	public static final Integer OmnivoreMax_Filename_Length_Min = 12;
	public static final Integer OmnivoreMax_Filename_Length_Default = 120;
	public static final Integer OmnivoreMax_Filename_Length_Max = 255;

	// For automatic archiving of incoming files:
	// Here is a comfortable way to specify how many rules shall be available:
	// The individual Strings in the following arrays may be left empty - they will
	// be automatically
	// filled.
	// But the smaller number of entries for Src and Dest determines
	// how many rule editing field pairs are provided on the actual preferences
	// page, and processed
	// later on.
	// The actual content of all field labels, and all preference store keys,
	// is computed from content of the messages.properties file.
	// I've tested the construction of the preferences dialog with fields for some
	// 26 rules, worked
	// like a charm :-)
	public static final String[] PREF_SRC_PATTERN = { "", "", "", "", "" };
	public static final String[] PREF_DEST_DIR = { "", "", "", "", "" };
	public static final int nPREF_SRC_PATTERN = PREF_SRC_PATTERN.length;
	public static final int nPREF_DEST_DIR = PREF_DEST_DIR.length;
}
