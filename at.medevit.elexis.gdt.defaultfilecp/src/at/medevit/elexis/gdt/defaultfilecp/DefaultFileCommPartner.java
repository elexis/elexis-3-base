/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.defaultfilecp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.medevit.elexis.gdt.constants.Feld8402Constants;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartnerProvider;
import ch.elexis.core.data.activator.CoreHub;

public class DefaultFileCommPartner implements IGDTCommunicationPartnerProvider {
	
	private FileCommPartner defaultFileCommPartner = new FileCommPartner();
	
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
				.get(defaultFileCommPartner.getFileTransferDirectory(), "") + ")";
	}
		
	@Override
	public String getIDReceiver(){
		return CoreHub.localCfg.get(
				defaultFileCommPartner.getFileTransferIdReceiver(), "MEDICALDEVICE");
	}
	
	@Override
	public String getShortIDReceiver(){
		return CoreHub.localCfg.get(
				defaultFileCommPartner.getFileTransferShortIdReceiver(), "MDEV");
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
		return CoreHub.localCfg.get(defaultFileCommPartner.getFileTransferUsedType(), GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND);
	}
	
	@Override
	public String getIncomingDirectory(){
		return CoreHub.localCfg.get(defaultFileCommPartner.getFileTransferDirectory(),
			"");
	}
	
	@Override
	public String getOutgoingDirectory(){
		return CoreHub.localCfg.get(defaultFileCommPartner.getFileTransferDirectory(),
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
			CoreHub.localCfg.get(defaultFileCommPartner.getFileTransferExecuteable(),
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

	@Override
	public List<IGDTCommunicationPartner> getChildCommunicationPartners() {
		List<IGDTCommunicationPartner> communicationPartners = new ArrayList<IGDTCommunicationPartner>();
		final DefaultFileCommPartner parent = this;
		for (String name : FileCommPartner.getAllFileCommPartnersArray())
		{
			if (!defaultFileCommPartner.getName().equals(name))
			{
				final FileCommPartner fileCommPartner = new FileCommPartner(name);
				communicationPartners.add(new IGDTCommunicationPartner() {
					
					@Override
					public String[] getSupported8402valuesDetailDescription() {
						return parent.getSupported8402valuesDetailDescription();
					}
					
					@Override
					public String[] getSupported8402valuesDescription() {
						return parent.getSupported8402valuesDescription();
					}
					
					@Override
					public String[] getSupported8402values() {
						return parent.getSupported8402values();
					}
					
					@Override
					public String getShortIDReceiver() {
						return CoreHub.localCfg.get(fileCommPartner.getFileTransferShortIdReceiver(), "MDEV");
					}
					
					@Override
					public String getRequiredFileType() {
						return CoreHub.localCfg.get(fileCommPartner.getFileTransferUsedType(), GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND);
					}
					
					@Override
					public String getOutgoingDirectory() {
						return CoreHub.localCfg.get(fileCommPartner.getFileTransferDirectory(),"");
					}
					
					@Override
					public int getOutgoingDefaultCharset() {
						return parent.getOutgoingDefaultCharset();
					}
					
					@Override
					public String getLabel() {
						return "Erweiterte-Datei-Kommunikation ("
								+ CoreHub.localCfg
									.get(fileCommPartner.getFileTransferDirectory(), "") + ")";
					}
					
					@Override
					public String getIncomingDirectory() {
						return CoreHub.localCfg.get(fileCommPartner.getFileTransferDirectory(), "");
					}
					
					@Override
					public int getIncomingDefaultCharset() {
						return parent.getIncomingDefaultCharset();
					}
					
					@Override
					public String getIDReceiver() {
						return CoreHub.localCfg.get(fileCommPartner.getFileTransferIdReceiver(), "MEDICALDEVICE");
					}
					
					@Override
					public String getFixedCommmunicationFileName() {
						return parent.getFixedCommmunicationFileName();
					}
					
					@Override
					public String getExternalHandlerProgram() {
						String executable =
								CoreHub.localCfg.get(fileCommPartner.getFileTransferExecuteable(),
									null);
							if (executable != null) {
								File execFile = new File(executable);
								if (execFile.canExecute())
									return executable;
							}
							return null;
					}
					
					@Override
					public int getConnectionType() {
						return parent.getConnectionType();
					}
					
					@Override
					public String getConnectionString() {
						return parent.getConnectionString();
					}
				});
			
			}
			
		}
		return communicationPartners;
	}
	
}
