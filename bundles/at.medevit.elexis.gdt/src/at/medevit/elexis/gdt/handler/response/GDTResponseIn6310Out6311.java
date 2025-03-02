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

import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6310;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6311;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Patient;

public class GDTResponseIn6310Out6311 {

	/**
	 * Given a 6310 Satznachricht, create a Satznachricht to show the respective
	 * information
	 *
	 * @param in
	 * @return
	 */
	public static GDTSatzNachricht6311 createResponse(GDTSatzNachricht6310 in) {
		String patientenKennung = in.getValue(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);
		String gdtSender = in.getValue(GDTConstants.FELDKENNUNG_GDT_ID_SENDER);

		Patient pat = null;
		if (patientenKennung != null) {
			pat = Patient.loadByPatientID(patientenKennung);
		}

		String datumBehandlungsdaten = in.getValue(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);
		String uhrzeitBehandlungsdaten = in
				.getValue(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);

		GDTSatzNachricht6311 gdt6311 = new GDTSatzNachricht6311(
				(pat != null) ? pat.get(Patient.FLD_PATID) : in.getValue(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG),
				in.getValue(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ), (pat != null) ? pat.getName() : null,
				(pat != null) ? pat.getVorname() : null, GDTSatzNachrichtHelper.deliverBirthdate(pat),
				(pat != null) ? pat.get(Patient.TITLE) : null, datumBehandlungsdaten, uhrzeitBehandlungsdaten,
				in.getValue(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD),
				(in.getTestIdent().length > 1) ? in.getTestIdent()[0].getAbnahmeDatum() : null,
				(in.getTestIdent().length > 1) ? in.getTestIdent()[0].getAbnahmeZeit() : null, gdtSender,
				CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_ID, null),
				GDTConstants.ZEICHENSATZ_IBM_CP_437 + StringUtils.EMPTY, GDTConstants.GDT_VERSION);

		return gdt6311;
	}

}
