package ch.elexis.laborimport.synlab.v2.gdt;

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.laborimport.synlab.v2.Messages;
import ch.elexis.laborimport.synlab.v2.SynlabPreferences;

public class SynlabGDTCommunicationPartner implements IGDTCommunicationPartner {
	
	public SynlabGDTCommunicationPartner(){}
	
	@Override
	public int getConnectionType(){
		return SystemConstants.FILE_COMMUNICATION;
	}
	
	@Override
	public String getConnectionString(){
		return null;
	}
	
	@Override
	public String getIncomingDirectory(){
		return null;
	}
	
	@Override
	public String getOutgoingDirectory(){
		return CoreHub.localCfg.get(SynlabPreferences.CFG_SYNLAB2_GDT_EXPORT_DIR, "");
	}
	
	@Override
	public String getRequiredFileType(){
		return GDTConstants.GDT_FILETRANSFER_TYP_FEST;
	}
	
	@Override
	public String getFixedCommmunicationFileName(){
		return "export.gdt";
	}
	
	@Override
	public String[] getSupported8402values(){
		return null;
	}
	
	@Override
	public String[] getSupported8402valuesDescription(){
		return null;
	}
	
	@Override
	public String[] getSupported8402valuesDetailDescription(){
		return null;
	}
	
	@Override
	public String getLabel(){
		return Messages.SynlabGDT_Label;
	}
	
	@Override
	public int getIncomingDefaultCharset(){
		return GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252;
	}
	
	@Override
	public int getOutgoingDefaultCharset(){
		return GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252;
	}
	
	@Override
	public String getExternalHandlerProgram(){
		return null;
	}
	
	@Override
	public String getIDReceiver(){
		return "STANDARD";
	}
	
	@Override
	public String getShortIDReceiver(){
		return "STD";
	}
	
}
