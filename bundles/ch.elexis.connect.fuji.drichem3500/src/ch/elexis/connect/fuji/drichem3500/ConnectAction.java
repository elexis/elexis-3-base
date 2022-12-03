/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher - bugfixes
 *    T. Huster - reworked for ethernet
 *
 * Sponsoring: Polymed AG
 *
 *******************************************************************************/

package ch.elexis.connect.fuji.drichem3500;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.ui.util.SWTHelper;

public class ConnectAction extends Action {

	public static final String CONFIGURATION_SERIAL = "Seriell";
	public static final String CONFIGURATION_ETHERNET = "Ethernet";

	private SerialListener serialConnection;
	private EthernetListener ethernetConnection;

	private ILaboratory myLab;
	private static Logger logger = LoggerFactory.getLogger(ConnectAction.class);

	public ConnectAction() {
		super("Fuji DriChem", AS_CHECK_BOX);
		setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.connect.fuji.drichem3500", "icons/fuji.png")); //$NON-NLS-1$
		setToolTipText("Mit Fuji Dri-Chem 3500 verbinden");
		myLab = LabImportUtilHolder.get().getOrCreateLabor("Dri-Chem");
	}

	@Override
	public void run() {
		if (isChecked()) { // we've just been turned on. whow!
			// start ethernet connection if ethernet is selected
			if ((CoreHub.localCfg.get(Preferences.SELECTED_CONNECTION, CONFIGURATION_SERIAL))
					.equals(CONFIGURATION_ETHERNET)) {
				if (ethernetConnection == null) {
					ethernetConnection = new EthernetListener(
							Integer.parseInt(CoreHub.localCfg.get(Preferences.ETHERNET_PORT, "5000")), this);
				}
				ethernetConnection.startThread();
			} else { // start serial connection
				if (serialConnection == null) {
					serialConnection = new SerialListener(this);
				}

				if (serialConnection.connect()) {
					logger.debug("Started serial connection for Fuji Drichem");
				} else {
					setChecked(false);
					SWTHelper.showError("Fehler",
							"Konnte seriellen Port nicht öffnen.\nBitte Einstellungen zu Fuji Drichem überprüfen!");
					logger.warn("Could not open serial connection");
				}
			}
		} else { // if the user turns off before we received data, stop waiting.
			// stop running connection
			if (ethernetConnection != null) {
				ethernetConnection.stopThread();
			}
			if (serialConnection != null) {
				serialConnection.disconnect();
				serialConnection = null;
			}
			setChecked(false);
		}
	}

	public ILaboratory getMyLab() {
		return myLab;
	}
}
