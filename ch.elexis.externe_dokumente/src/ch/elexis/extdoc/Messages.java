package ch.elexis.extdoc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.extdoc.messages"; //$NON-NLS-1$
	public static String ExterneDokumente_pop_menu;
	public static String ExterneDokumente_delete_files;
	public static String ExterneDokumente_click_to_sort_by_date;
	public static String ExterneDokumente_click_to_sort_by_name;
	public static String ExterneDokumente_Concerns;
	public static String ExterneDokumente_could_not_read_File;
	public static String ExterneDokumente_delete;
	public static String ExterneDokumente_delete_doc;
	public static String ExterneDokumente_email_app;
	public static String ExterneDokumente_exception_while_copying;
	public static String ExterneDokumente_externe_dokumente;
	public static String ExterneDokumente_file_date;
	public static String ExterneDokumente_file_name;
	public static String ExterneDokumente_import_failed;
	public static String ExterneDokumente_imported;
	public static String ExterneDokumente_loading;
	public static String ExterneDokumente_modified_time;
	public static String ExterneDokumente_move_into_subdir;
	public static String ExterneDokumente_move_into_subdir_tooltip;
	public static String ExterneDokumente_no_files_found;
	public static String ExterneDokumente_no_patient_found;
	public static String ExterneDokumente_not_defined_in_preferences;
	public static String ExterneDokumente_open;
	public static String ExterneDokumente_OpenFileTip;
	public static String ExterneDokumente_openFolder;
	public static String ExterneDokumente_openFolderTip;
	public static String ExterneDokumente_path_name_preference;
	public static String ExterneDokumente_propeties;
	public static String ExterneDokumente_read_errpor;
	public static String ExterneDokumente_rename_or_change_date;
	public static String ExterneDokumente_renaming_file;
	public static String ExterneDokumente_select_patient_first;
	public static String ExterneDokumente_sendEmail;
	public static String ExterneDokumente_sendEmailTip;
	public static String ExterneDokumente_shold_doc_be_delted;
	public static String ExterneDokumente_shorthand_for_path;
	public static String ExterneDokumente_verify_files;
	public static String ExterneDokumente_verify_files_Belong_to_patient;
	public static String MoveIntoSubDirsDialog_finished;
	public static String MoveIntoSubDirsDialog_no_old_Files_found;
	public static String MoveIntoSubDirsDialog_sub_task;
	public static String VerifierDialog_name;
	public static String VerifierDialog_verify_job_name;
	public static String FileEditDialog_attribute_to_new_patient;
	public static String FileEditDialog_extension;
	public static String FileEditDialog_file_name;
	public static String FileEditDialog_file_date_and_explanation;
	public static String FileEditDialog_file_properties;
	public static String FileEditDialog_really_attribute_to_new_patient;
	public static String FileEditDialog_17;
	public static String FileEditDialog_18;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
