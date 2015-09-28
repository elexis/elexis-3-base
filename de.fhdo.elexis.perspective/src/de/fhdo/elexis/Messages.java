package de.fhdo.elexis;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.fhdo.elexis.messages"; //$NON-NLS-1$
	public static String DeleteHandler_Really_Want_To_Delete_selected_Perspective;
	public static String DeleteHandler_CannotDeleteInternalPerspective;
	public static String DeleteHandler_ErrorWhileDeleting;
	public static String DeleteHandler_ReallyDelete;
	public static String ExportHandler_Error;
	public static String ExportHandler_Error_Exporting;
	public static String ExportHandler_ErrorOccured;
	public static String ImportHandler_Abort;
	public static String ImportHandler_Name_Import_Already_Exists;
	public static String ImportHandler_Overwrite;
	public static String ImportHandler_OverWrite_Perspective;
	public static String ImportHandler_Rename;
	public static String ImportHandler_Rename_Perspective;
	public static String ImportHandler_Saved_As;
	public static String ImportHandler_Successfully_Imported;
	public static String ImportHandler_Unable_to_load_Perspective;
	public static String ImportHandler_What_Should_Be_Done;
	public static String ImportHandler_Cannot_Overwrite_Perspective;
	public static String ImportHandler_Choose_new_name_for_Perspective;
	public static String ImportHandler_Imported_perspectives_successfully;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
