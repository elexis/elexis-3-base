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
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;
import java.util.LinkedHashMap;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class GDTSatzNachricht6301 extends GDTSatzNachricht {

	/**
	 * Erstellt eine GDT Satzart Nachricht vom Typ "Stammdaten übermitteln" 6301
	 *
	 * @param patientenkennung
	 * @param nachnamePatient
	 * @param vornamePatient
	 * @param geburtsdatumPatient    Format: TTMMJJJJ
	 * @param namenszusatz           optional
	 * @param titelPatient           optional
	 * @param versichertenNrPatient  optional
	 * @param wohnortPatient         optional
	 * @param strassePatient         optional
	 * @param versichertenartPatient optional
	 * @param geschlechtPatient      optional
	 * @param groessePatient         optional
	 * @param gewichtPatient         optional
	 * @param muttersprachePatient   optional
	 * @param gdtIdReceiver          optional
	 * @param gdtIdSender            optional
	 * @param zeichensatz            optional
	 * @param gdtVersion             - default: {@link GDTConstants#GDT_VERSION}
	 */
	public GDTSatzNachricht6301(String patientenkennung, String nachnamePatient, String vornamePatient,
			String geburtsdatumPatient, String namenszusatz, String titelPatient, String versichertenNrPatient,
			String wohnortPatient, String strassePatient, String versichertenartPatient, String geschlechtPatient,
			String groessePatient, String gewichtPatient, String muttersprachePatient, String gdtIdReceiver,
			String gdtIdSender, String zeichensatz, String gdtVersion) {
		super(GDTConstants.SATZART_STAMMDATEN_UEBERMITTELN, gdtIdReceiver, gdtIdSender, zeichensatz, gdtVersion);

		values.put(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG, patientenkennung);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_NAME, nachnamePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_VORNAME, vornamePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM, geburtsdatumPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ, namenszusatz);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_TITEL, titelPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER, versichertenNrPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_WOHNORT, wohnortPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_STRASSE, strassePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART, versichertenartPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT, geschlechtPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_GROESSE, groessePatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_GEWICHT, gewichtPatient);
		values.put(GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE, muttersprachePatient);
	}

	@Override
	protected void createMessage() {
		super.createMessage();
		addLine(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);

		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ);
		addLine(GDTConstants.FELDKENNUNG_PATIENT_NAME);
		addLine(GDTConstants.FELDKENNUNG_PATIENT_VORNAME);
		addLine(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_TITEL);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_WOHNORT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_STRASSE);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GROESSE);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GEWICHT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE);
	};

	/**
	 * Bequemlichkeits Konstruktor für Datensatz 6301 - Beinhaltet nur die MUSS
	 * Felder
	 *
	 * @param patientenkennung
	 * @param nachnamePatient
	 * @param vornamePatient
	 * @param geburtsdatumPatient Format: TTMMJJJJ
	 * @param gdtVersion          - default: {@link GDTConstants#GDT_VERSION}
	 */
	GDTSatzNachricht6301(String patientenkennung, String nachnamePatient, String vornamePatient,
			String geburtsdatumPatient, String gdtVersion) {
		this(patientenkennung, nachnamePatient, vornamePatient, geburtsdatumPatient, null, null, null, null, null, null,
				null, null, null, null, null, null, null, gdtVersion);
	}

	/**
	 * Parse an incoming 6301 Message from a String array
	 *
	 * @param satznachricht
	 * @return class if successful else null
	 */
	public static GDTSatzNachricht6301 createfromStringArray(String[] satznachricht) {
		HashMap<Integer, String> ds = new LinkedHashMap<Integer, String>();

		for (int i = 0; i < satznachricht.length; i++) {
			int length = Integer.parseInt(satznachricht[i].substring(0, 3));
			int feldkennung = Integer.parseInt(satznachricht[i].substring(3, 7));
			String value = satznachricht[i].substring(7, length - 2);
			ds.put(feldkennung, value);
		}

		int satzkennung = Integer.parseInt(ds.get(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION));
		if (satzkennung != GDTConstants.SATZART_STAMMDATEN_UEBERMITTELN)
			return null;

		return new GDTSatzNachricht6301(ds.get(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAME), ds.get(GDTConstants.FELDKENNUNG_PATIENT_VORNAME),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ), ds.get(GDTConstants.FELDKENNUNG_PATIENT_TITEL),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_WOHNORT), ds.get(GDTConstants.FELDKENNUNG_PATIENT_STRASSE),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT), ds.get(GDTConstants.FELDKENNUNG_PATIENT_GROESSE),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GEWICHT),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE),
				ds.get(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER), ds.get(GDTConstants.FELDKENNUNG_GDT_ID_SENDER),
				ds.get(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ), ds.get(GDTConstants.FELDKENNUNG_VERSION_GDT));
	}
}
