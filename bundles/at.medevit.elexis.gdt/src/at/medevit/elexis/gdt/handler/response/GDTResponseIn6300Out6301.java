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
package at.medevit.elexis.gdt.handler.response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6300;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6301;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Patient;

/**
 * Erstelle eine Antwort mit Datensatz 6301 auf Anfrage 6300
 *
 * @author marco
 *
 */
public class GDTResponseIn6300Out6301 {

	private static Log logger = Log.get(GDTResponseIn6300Out6301.class.getName());

	public static GDTSatzNachricht6301 createResponse(GDTSatzNachricht6300 in) {
		String patientenKennung = in.getValue(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);
		String gdtSender = in.getValue(GDTConstants.FELDKENNUNG_GDT_ID_SENDER);

		Patient pat = null;
		if (patientenKennung != null) {
			pat = Patient.loadByPatientID(patientenKennung);
		} else {
			pat = ElexisEventDispatcher.getSelectedPatient();
		}

		if (pat == null || (!pat.isValid())) {
			String message = "GDT (6300): Stammdatenübermittlung für unbekannten oder ungültigen Patienten angefordert. Patientenkennung: "
					+ patientenKennung;
			Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			logger.log(message, Log.WARNINGS);
			return null;
		}

		return new GDTSatzNachricht6301(pat.get(Patient.FLD_PATID), pat.getName(), pat.getVorname(),
				GDTSatzNachrichtHelper.deliverBirthdate(pat), null, pat.get(Patient.TITLE), null,
				pat.get(Patient.FLD_ZIP) + StringUtils.SPACE + pat.get(Patient.FLD_PLACE), pat.get(Patient.FLD_STREET),
				null, GDTSatzNachrichtHelper.bestimmeGeschlechtsWert(pat.get(Patient.FLD_SEX)), null, null, null,
				gdtSender, CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_ID, null),
				GDTConstants.ZEICHENSATZ_IBM_CP_437 + StringUtils.EMPTY, GDTConstants.GDT_VERSION);
	}

}
