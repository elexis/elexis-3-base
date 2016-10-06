/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.defaultfilecp;

import java.io.File;

import at.medevit.elexis.gdt.constants.Feld8402Constants;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.defaultfilecp.ui.GDTPreferencePageFileTransfer;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import ch.elexis.core.data.activator.CoreHub;

public class DefaultFileCommPartner implements IGDTCommunicationPartner {
	
	@Override
	public int getConnectionType(){
		return SystemConstants.FILE_COMMUNICATION;
	}
	
	@Override
	public String getConnectionString(){
		return null;
	}
	
	@Override
	public String getLabel(){
		return "Standard-Datei-Kommunikation ("
			+ CoreHub.localCfg
				.get(GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_DIRECTORY, "") + ")";
	}
	
	@Override
	public String getIDReceiver(){
		return CoreHub.localCfg.get(
			GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_LONG_ID_RECEIVER, "MEDICALDEVICE");
	}
	
	@Override
	public String getShortIDReceiver(){
		return CoreHub.localCfg.get(
			GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_SHORT_ID_RECEIVER, "MDEV");
	}
	
	@Override
	public String[] getSupported8402values(){
		return Feld8402Constants.enumNameToStringArray(Feld8402Constants.ALL.values());
	}
	
	@Override
	public String[] getSupported8402valuesDescription(){
		return Feld8402Constants.enumNameToStringArrayDescription(Feld8402Constants.ALL.values());
	}
	
	@Override
	public String[] getSupported8402valuesDetailDescription(){
		return null;
	}
	
	@Override
	public String getRequiredFileType(){
		return CoreHub.localCfg.get(GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_USED_TYPE,
			GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND);
	}
	
	@Override
	public String getIncomingDirectory(){
		return CoreHub.localCfg.get(GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_DIRECTORY,
			"");
	}
	
	@Override
	public String getOutgoingDirectory(){
		return CoreHub.localCfg.get(GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_DIRECTORY,
			"");
	}
	
	@Override
	public int getIncomingDefaultCharset(){
		String charset =
				CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_CHARSET,
					GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING);
		return GDTConstants.getCharsetIntByString(charset);
	}
	
	@Override
	public int getOutgoingDefaultCharset(){
		String charset =
				CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_CHARSET,
					GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING);
		return GDTConstants.getCharsetIntByString(charset);
	}
	
	@Override
	public String getExternalHandlerProgram(){
		String executable =
			CoreHub.localCfg.get(GDTPreferencePageFileTransfer.CFG_GDT_FILETRANSFER_EXECUTABLE,
				null);
		if (executable != null) {
			File execFile = new File(executable);
			if (execFile.canExecute())
				return executable;
		}
		return null;
	}
	
	@Override
	public String getFixedCommmunicationFileName(){
		return null;
	}
	
}
