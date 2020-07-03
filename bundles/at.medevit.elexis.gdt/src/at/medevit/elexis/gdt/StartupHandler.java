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

import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.gdt.constants.SystemConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.tools.DirectoryWatcher;
import at.medevit.elexis.gdt.tools.GDTCommPartnerCollector;

@Component(property = EventConstants.EVENT_TOPIC + "=" + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class StartupHandler implements EventHandler {
	
	private static Logger logger = LoggerFactory.getLogger(StartupHandler.class);
	
	@Override
	public void handleEvent(Event event){
		List<IGDTCommunicationPartner> lp = GDTCommPartnerCollector.getRegisteredCommPartners();
		if (lp == null) {
			logger.info("There are no registered communication partners");
			return;
		}
		for (IGDTCommunicationPartner igdtCommunicationPartner : lp) {
			if (igdtCommunicationPartner.getConnectionType() == SystemConstants.FILE_COMMUNICATION) {
				String incomingDirString = igdtCommunicationPartner.getIncomingDirectory();
				logger.info("Found directory " + incomingDirString + "to watch by comm partner "
					+ igdtCommunicationPartner.getLabel());
				File incomingDir = null;
				if (incomingDirString != null)
					incomingDir = new File(incomingDirString);
				if (incomingDir != null && incomingDir.isDirectory())
					DirectoryWatcher.getInstance().addDirectoryToWatch(incomingDir);
			}
		}
	}
}
