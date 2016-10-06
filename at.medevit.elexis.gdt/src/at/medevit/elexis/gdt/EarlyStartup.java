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
