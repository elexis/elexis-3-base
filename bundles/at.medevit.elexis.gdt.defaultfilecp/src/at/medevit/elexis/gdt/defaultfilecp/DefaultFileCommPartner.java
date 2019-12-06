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
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.constants.Feld8402Constants;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.interfaces.HandlerProgramType;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartnerProvider;

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
		return defaultFileCommPartner.getSettings()
			.get(defaultFileCommPartner.getFileTransferName(), "") + " ("
			+ defaultFileCommPartner.getSettings()
				.get(defaultFileCommPartner.getFileTransferDirectory(), "")
			+ ")";
	}
		
	@Override
	public String getIDReceiver(){
		return defaultFileCommPartner.getSettings().get(
				defaultFileCommPartner.getFileTransferIdReceiver(), "MEDICALDEVICE");
	}
	
	@Override
	public String getShortIDReceiver(){
		return defaultFileCommPartner.getSettings().get(
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
		return defaultFileCommPartner.getSettings().get(
			defaultFileCommPartner.getFileTransferUsedType(),
			GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND);
	}
	
	@Override
	public String getIncomingDirectory(){
		return defaultFileCommPartner.getSettings().get(
			defaultFileCommPartner.getFileTransferInDirectory(),
			"");
	}
	
	@Override
	public String getOutgoingDirectory(){
		return defaultFileCommPartner.getSettings().get(
			defaultFileCommPartner.getFileTransferOutDirectory(),
			"");
	}
	
	@Override
	public int getIncomingDefaultCharset(){
		String charset =
			defaultFileCommPartner.getSettings().get(GDTPreferenceConstants.CFG_GDT_CHARSET,
					GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING);
		return GDTConstants.getCharsetIntByString(charset);
	}
	
	@Override
	public int getOutgoingDefaultCharset(){
		String charset =
			defaultFileCommPartner.getSettings().get(GDTPreferenceConstants.CFG_GDT_CHARSET,
					GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING);
		return GDTConstants.getCharsetIntByString(charset);
	}
	
	@Override
	public String getExternalHandlerProgram(HandlerProgramType handlerType){
		String executable = null;
		if (handlerType == HandlerProgramType.VIEWER) {
			executable = defaultFileCommPartner.getSettings()
				.get(defaultFileCommPartner.getFileTransferViewerExecuteable(), null);
		} else {
			executable = defaultFileCommPartner.getSettings()
				.get(defaultFileCommPartner.getFileTransferExecuteable(), null);
		}
		LoggerFactory.getLogger(getClass())
			.info("Find external handler [" + executable + "] of [" + defaultFileCommPartner.getId()
				+ "] in [" + defaultFileCommPartner.getSettings().getClass().getSimpleName() + "]");
		if (executable != null) {
			File execFile = new File(executable);
			if (execFile.canExecute()) {
				return executable;
			} else {
				LoggerFactory.getLogger(getClass())
					.warn("Can not execute external handler [" + executable + "]");
			}
		}
		return null;	
	}
	
	@Override
	public String getFixedCommmunicationFileName(){
		return null;
	}
	
	@Override
	public String getId(){
		return defaultFileCommPartner.getId();
	}

	@Override
	public List<IGDTCommunicationPartner> getChildCommunicationPartners() {
		List<IGDTCommunicationPartner> communicationPartners = new ArrayList<IGDTCommunicationPartner>();
		final DefaultFileCommPartner parent = this;
		for (String id : FileCommPartner.getAllFileCommPartnersArray())
		{
			if (!defaultFileCommPartner.getId().equals(id))
			{
				final FileCommPartner fileCommPartner = new FileCommPartner(id);
				communicationPartners.add(new IGDTCommunicationPartnerProvider() {
					
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
						return defaultFileCommPartner.getSettings()
							.get(fileCommPartner.getFileTransferShortIdReceiver(), "MDEV");
					}
					
					@Override
					public String getRequiredFileType() {
						return defaultFileCommPartner.getSettings().get(
							fileCommPartner.getFileTransferUsedType(),
							GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND);
					}
					
					@Override
					public String getOutgoingDirectory() {
						return defaultFileCommPartner.getSettings().get(
							fileCommPartner.getFileTransferOutDirectory(),
							"");
					}
					
					@Override
					public int getOutgoingDefaultCharset() {
						return parent.getOutgoingDefaultCharset();
					}
					
					@Override
					public String getLabel() {
						return defaultFileCommPartner.getSettings()
							.get(fileCommPartner.getFileTransferName(), "")
							+ " ("
							+ defaultFileCommPartner.getSettings()
									.get(fileCommPartner.getFileTransferDirectory(), "") + ")";
					}
					
					@Override
					public String getIncomingDirectory() {
						return defaultFileCommPartner.getSettings().get(
							fileCommPartner.getFileTransferInDirectory(),
							"");
					}
					
					@Override
					public int getIncomingDefaultCharset() {
						return parent.getIncomingDefaultCharset();
					}
					
					@Override
					public String getIDReceiver() {
						return defaultFileCommPartner.getSettings()
							.get(fileCommPartner.getFileTransferIdReceiver(), "MEDICALDEVICE");
					}
					
					@Override
					public String getFixedCommmunicationFileName() {
						return parent.getFixedCommmunicationFileName();
					}
					
					@Override
					public String getExternalHandlerProgram(HandlerProgramType handlerType){
						String executable = null;
						if (handlerType == HandlerProgramType.VIEWER) {
							executable = defaultFileCommPartner.getSettings().get(
								defaultFileCommPartner.getFileTransferViewerExecuteable(), null);
						} else {
							executable = defaultFileCommPartner.getSettings()
								.get(defaultFileCommPartner.getFileTransferExecuteable(), null);
						}
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
					
					@Override
					public List<IGDTCommunicationPartner> getChildCommunicationPartners(){
						return Collections.emptyList();
					}
					
					@Override
					public String getId(){
						return fileCommPartner.getId();
					}
				});
			
			}
			
		}
		return communicationPartners;
	}
}
