package at.medevit.elexis.gdt.defaultfilecp;

import at.medevit.elexis.gdt.constants.GDTConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.activator.CoreHubHelper;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.rgw.io.Settings;

public class FileCommPartner {
	
	public static final String DEFAULT_COMM_PARTNER_ID = "DEFAULT";
	private static final String CFG_GDT = "GDT";
	private static final String FILETRANSFER_NAME = "fileTransferName";
	private static final String FILETRANSFER_USED_TYPE = "fileTransferUsedType";
	private static final String FILETRANSFER_DIRECTORY = "fileTransferDirectory";
	private static final String FILETRANSFER_IN_DIRECTORY = "fileTransferInDirectory";
	private static final String FILETRANSFER_OUT_DIRECTORY = "fileTransferOutDirectory";
	private static final String FILETRANSFER_LONG_ID_RECEIVER = "longIDReceiver";
	private static final String FILETRANSFER_SHORT_ID_RECEIVER = "longIDReceiver";
	private static final String FILETRANSFER_EXECUTABLE = "executable";
	private static final String FILETRANSFER_VIEWEREXECUTABLE = "viewerexecutable";
	private static final String FILETRANSFER_ADDITIONAL_PARAMS = "additionalParams";
	
	public static final String CFG_GDT_FILETRANSFER_IDS = CFG_GDT + "/fileTransferTypes";
	private static final String CFG_GDT_FILETRANSFER_GLOBAL =
		CFG_GDT + "fileTransferSettingsGlobal";
	
	public static final String COMM_PARTNER_SEPERATOR = ",;,";
	
	private String id;
	private final SettingsPreferenceStore preferenceStore;
	
	public FileCommPartner(){
		this(DEFAULT_COMM_PARTNER_ID);
		
	}
	
	public FileCommPartner(String id){
		this.id = id;
		preferenceStore =
			new SettingsPreferenceStore(FileCommPartner.isFileTransferGlobalConfigured()
					? CoreHub.globalCfg : CoreHub.localCfg);
		
		// in the past v3.1 the default key was locally configured as an other key
		// we need to transfer the old key to the new one
		if (DEFAULT_COMM_PARTNER_ID.equals(id) && !isFileTransferGlobalConfigured()) {
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory",
				getFileTransferDirectory(), false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory",
				getFileTransferInDirectory(), false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferDirectory",
				getFileTransferOutDirectory(), false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/fileTransferUsedType",
				getFileTransferUsedType(), false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/longIDReceiver",
				getFileTransferIdReceiver(), false);
			CoreHubHelper.transformConfigKey("GDT/defaultfilecp/executable",
				getFileTransferExecuteable(), false);
		}
	}
	
	public static String[][] comboCharsetSelektor = new String[][] {
		{
			"7Bit", GDTConstants.ZEICHENSATZ_7BIT_CHARSET_STRING
		}, {
			"IBM (Standard) CP 437", GDTConstants.ZEICHENSATZ_IBM_CP_437_CHARSET_STRING
		}, {
			"ISO8859-1 (ANSI) CP 1252",
			GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING
		}
	};
	
	public String getFileTransferName(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_NAME;
	}
	
	public String getFileTransferUsedType(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_USED_TYPE;
	}
	
	public String getFileTransferDirectory(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_DIRECTORY;
	}
	
	public String getFileTransferInDirectory(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_IN_DIRECTORY;
	}
	
	public String getFileTransferOutDirectory(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_OUT_DIRECTORY;
	}
	
	public String getFileTransferIdReceiver(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_LONG_ID_RECEIVER;
	}
	
	public String getFileTransferShortIdReceiver(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_SHORT_ID_RECEIVER;
	}
	
	public String getFileTransferExecuteable(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_EXECUTABLE;
	}
	
	public String getFileTransferViewerExecuteable(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_VIEWEREXECUTABLE;
	}
	
	public String getFileAdditionalParams(){
		return CFG_GDT + "/" + getId() + "/" + FILETRANSFER_ADDITIONAL_PARAMS;
	}

	public String getId(){
		return id;
	}
	
	public static boolean isFileTransferGlobalConfigured(){
		return ConfigServiceHolder.getGlobal(FileCommPartner.CFG_GDT_FILETRANSFER_GLOBAL, false);
	}
	
	public static void setFileTransferConfiguration(boolean global){
		ConfigServiceHolder.setGlobal(FileCommPartner.CFG_GDT_FILETRANSFER_GLOBAL, global);
	}
	
	public static String[] getAllFileCommPartnersArray(){
		if (isFileTransferGlobalConfigured()) {
			return CoreHub.globalCfg
				.get(FileCommPartner.CFG_GDT_FILETRANSFER_IDS,
					FileCommPartner.DEFAULT_COMM_PARTNER_ID)
				.split(FileCommPartner.COMM_PARTNER_SEPERATOR);
		} else {
			return CoreHub.localCfg
				.get(FileCommPartner.CFG_GDT_FILETRANSFER_IDS,
					FileCommPartner.DEFAULT_COMM_PARTNER_ID)
				.split(FileCommPartner.COMM_PARTNER_SEPERATOR);
		}
	}
	
	public Settings getSettings(){
		return preferenceStore.getBase();
	}
}
