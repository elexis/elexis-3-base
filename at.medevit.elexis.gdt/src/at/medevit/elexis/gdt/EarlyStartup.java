/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt;

import java.io.File;
import java.util.List;

import org.eclipse.ui.IStartup;

import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.tools.DirectoryWatcher;
import at.medevit.elexis.gdt.tools.GDTCommPartnerCollector;
import ch.elexis.core.ui.util.Log;

public class EarlyStartup implements IStartup {
	
	private static Log logger = Log.get(EarlyStartup.class.getName());
	
	@Override
	public void earlyStartup(){
		
		List<IGDTCommunicationPartner> lp = GDTCommPartnerCollector.getRegisteredCommPartners();
		if (lp == null) {
			logger.log("There are no registered communication partners", Log.DEBUGMSG);
			return;
		}
		for (IGDTCommunicationPartner igdtCommunicationPartner : lp) {
			if (igdtCommunicationPartner.getConnectionType() == SystemConstants.FILE_COMMUNICATION) {
				String incomingDirString = igdtCommunicationPartner.getIncomingDirectory();
				logger.log("Found directory " + incomingDirString + "to watch by comm partner "
					+ igdtCommunicationPartner.getLabel(), Log.DEBUGMSG);
				File incomingDir = null;
				if (incomingDirString != null)
					incomingDir = new File(incomingDirString);
				if (incomingDir != null && incomingDir.isDirectory())
					DirectoryWatcher.getInstance().addDirectoryToWatch(incomingDir);
			}
		}
	}
	
}
