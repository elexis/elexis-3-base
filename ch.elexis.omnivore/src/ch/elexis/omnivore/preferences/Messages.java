package ch.elexis.omnivore.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.omnivore.preferences.messages"; //$NON-NLS-1$
 	public static String Omnivore_jsErrNoActivator;
 	public static String Omnivore_jsPREF_omnivore_js;
 	public static String Omnivore_jsPREF_MAX_FILENAME_LENGTH;
 	public static String Omnivore_jsPREF_automatic_archiving_of_processed_files;
 	public static String Omnivore_jsPREF_Rule;
 	public static String Omnivore_jsPREF_SRC_PATTERN;
	public static String Omnivore_jsPREF_DEST_DIR;
	public static String Omnivore_jsPREF_construction_of_temporary_filename;
	public static String Omnivore_jsPREF_cotf_constant1;
	public static String Omnivore_jsPREF_cotf_pid;
	public static String Omnivore_jsPREF_cotf_fn;
	public static String Omnivore_jsPREF_cotf_gn;
	public static String Omnivore_jsPREF_cotf_dob;
	public static String Omnivore_jsPREF_cotf_dt;
	public static String Omnivore_jsPREF_cotf_dk;
	public static String Omnivore_jsPREF_cotf_dguid;
	public static String Omnivore_jsPREF_cotf_random;
	public static String Omnivore_jsPREF_cotf_constant2;
	public static String Omnivore_jsPREF_cotf_fill_lead_char;
	public static String Omnivore_jsPREF_cotf_num_digits;
	public static String Omnivore_jsPREF_cotf_add_trail_char;
															
 	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
