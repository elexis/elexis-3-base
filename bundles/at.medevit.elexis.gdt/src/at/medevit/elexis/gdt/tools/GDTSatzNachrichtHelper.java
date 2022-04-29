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
package at.medevit.elexis.gdt.tools;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.TimeTool.TimeFormatException;
import at.medevit.elexis.gdt.constants.GDTConstants;

/**
 * Hilfsklasse für die Behandlung von GDT Satzarten {@link GDTConstants}
 *
 * @author M. Descher
 *
 */
public class GDTSatzNachrichtHelper {

	// Lese Kennung der Arbeitsstation

	public static String getValueIfExists(int feldkennungGesucht, String[] satznachricht) {
		String value = null;
		for (int i = 0; i < satznachricht.length; i++) {
			int length = Integer.parseInt(satznachricht[i].substring(0, 3));
			int feldkennung = Integer.parseInt(satznachricht[i].substring(3, 7));
			if (feldkennung == feldkennungGesucht)
				value = satznachricht[i].substring(7, length - 2);
			// TODO: Auto-concatenate of n occurence strings?
		}
		return value;
	}

	public static String bestimmeGeschlechtsWert(String sex) {
		if (sex == null)
			return null;

		String sexCode = null;
		sex = sex.trim();
		if (sex.equalsIgnoreCase("m"))
			sexCode = GDTConstants.SEX_MALE + StringUtils.EMPTY;
		if (sex.equalsIgnoreCase("w") || sex.equalsIgnoreCase("f"))
			sexCode = GDTConstants.SEX_FEMALE + StringUtils.EMPTY;
		return sexCode;
	}

	public static String deliverBirthdate(Patient pat) {
		String dob = null;
		try {
			TimeTool tt = new TimeTool(pat.getGeburtsdatum(), true);
			Date d = tt.getTime();
			SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy");
			dob = sd.format(d);
		} catch (TimeFormatException e) {
			return null;
		}
		return dob;
	}
}
