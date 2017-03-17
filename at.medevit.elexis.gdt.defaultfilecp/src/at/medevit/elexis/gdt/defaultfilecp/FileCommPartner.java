package at.medevit.elexis.gdt.defaultfilecp;

import at.medevit.elexis.gdt.constants.GDTConstants;
import ch.elexis.core.data.activator.CoreHub;

public class FileCommPartner {
	
	public static final String DEFAULT_COMM_PARTNER_NAME = "DEFAULT";
	private static final String CFG_GDT = "GDT";
	private static final String FILETRANSFER_USED_TYPE = "fileTransferUsedType";
	private static final String FILETRANSFER_DIRECTORY = "fileTransferDirectory";
	private static final String FILETRANSFER_IN_DIRECTORY = "fileTransferInDirectory";
	private static final String FILETRANSFER_OUT_DIRECTORY = "fileTransferOutDirectory";
	private static final String FILETRANSFER_LONG_ID_RECEIVER = "longIDReceiver";
	private static final String FILETRANSFER_SHORT_ID_RECEIVER = "longIDReceiver";
	private static final String FILETRANSFER_EXECUTABLE = "executable";
	private static final String FILETRANSFER_ADDITIONAL_PARAMS = "additionalParams";
	
	public static final String CFG_GDT_FILETRANSFER_NAMES = CFG_GDT + "/fileTransferTypes";
	
	public static final String COMM_PARTNER_SEPERATOR = ",;,";
	
	private String name = DEFAULT_COMM_PARTNER_NAME;
	
	public FileCommPartner(){
		
	}
	
	public FileCommPartner(String name){
		this.name = name;
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
	
	public String getFileTransferUsedType(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_USED_TYPE;
	}
	
	public String getFileTransferDirectory(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_DIRECTORY;
	}
	
	public String getFileTransferInDirectory(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_IN_DIRECTORY;
	}
	
	public String getFileTransferOutDirectory(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_OUT_DIRECTORY;
	}
	
	public String getFileTransferIdReceiver(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_LONG_ID_RECEIVER;
	}
	
	public String getFileTransferShortIdReceiver(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_SHORT_ID_RECEIVER;
	}
	
	public String getFileTransferExecuteable(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_EXECUTABLE;
	}
	
	public String getFileAdditionalParams(){
		return CFG_GDT + "/" + getName() + "/" + FILETRANSFER_ADDITIONAL_PARAMS;
	}
	
	public static String[] getAllFileCommPartnersArray(){
		return getAllFileCommPartners().split(COMM_PARTNER_SEPERATOR);
	}
	
	public static String getAllFileCommPartners(){
		return CoreHub.localCfg.get(CFG_GDT_FILETRANSFER_NAMES, DEFAULT_COMM_PARTNER_NAME);
	}
	
	public void removeFileCommPartner(String name){
		String cfg = FileCommPartner.getAllFileCommPartners();
		if (cfg.contains(name)) {
			String newCfg = cfg.replaceFirst(COMM_PARTNER_SEPERATOR + name, "");
			updateFileCommPartner(newCfg);
		}
	}
	
	public boolean addFileCommPartner(String name){
		String cfg = FileCommPartner.getAllFileCommPartners();
		if (!cfg.contains(name)) {
			updateFileCommPartner(cfg + COMM_PARTNER_SEPERATOR + name);
			return true;
		}
		return false;
		
	}
	
	private void updateFileCommPartner(String cfg){
		CoreHub.localCfg.set(CFG_GDT_FILETRANSFER_NAMES, cfg);
	}
	
	public String getName(){
		return name;
	}
}
