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
import java.util.LinkedList;
import java.util.List;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class GDTSatzNachricht6310 extends GDTSatzNachricht {

	DateiArchivierungsKenner[] dateiArchivierungsKenner = null;
	String[] aktuelleDiagnose = null;
	String[] befund = null;
	String[] fremdBefund = null;
	String[] kommentar = null;
	String[] anzahlFortsetzungsZeilen6228 = null;
	String[] ergebnistabellenText = null;
	TestIdent[] testIdent = null;

	public GDTSatzNachricht6310(String patientenkennung, String nachnamePatient, String vornamePatient,
			String geburtsdatumPatient, String namenszusatz, String titelPatient, String versichertenNrPatient,
			String wohnortPatient, String strassePatient, String versichertenartPatient, String geschlechtPatient,
			String groessePatient, String gewichtPatient, String muttersprachePatient, String guvskf,
			String tagErhebungBehandlungsdaten, String uhrzeitErhebungBehandlungsdaten, String[] aktuelleDiagnose, // 6205
			String[] befund, // 6220
			String[] fremdbefund, // 6221
			String[] kommentar, // 6227

			// GEHÖREN ZUSAMMEN
			String[] anzahlFortsetzungsZeilen6228, // 6226
			String[] ergebnistabellenText, // 6228
			// TODO
			TestIdent[] testIdent, DateiArchivierungsKenner[] dateiArchivierungsKenner, // 6202, beinhaltet 6203, 6204,
																						// 6205
			FreieKategorie[] freieKategorie, // 6330, beinhaltet 6331, 6332 - 6339

			String signatur, String gdtIdReceiver, String gdtIdSender, String zeichensatz, String gdtVersion) {
		super(GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN, gdtIdReceiver, gdtIdSender, zeichensatz,
				gdtVersion);

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
		values.put(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD, guvskf);

		values.put(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN, tagErhebungBehandlungsdaten);
		values.put(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN, uhrzeitErhebungBehandlungsdaten);
		values.put(GDTConstants.FELDKENNUNG_SIGNATUR, signatur);

		this.dateiArchivierungsKenner = dateiArchivierungsKenner;
		this.aktuelleDiagnose = aktuelleDiagnose;
		this.befund = befund;
		this.fremdBefund = fremdbefund;
		this.kommentar = kommentar;
		this.anzahlFortsetzungsZeilen6228 = anzahlFortsetzungsZeilen6228;
		this.ergebnistabellenText = ergebnistabellenText;
		this.testIdent = testIdent;
	}

	/**
	 * Parse an incoming 6310 Message from a String array
	 *
	 * @param satznachricht
	 * @return class if successful else null
	 */
	public static GDTSatzNachricht6310 createfromStringArray(String[] satznachricht) {
		HashMap<Integer, String> ds = new LinkedHashMap<Integer, String>();
		LinkedList<String> aktDiag = new LinkedList<String>();
		LinkedList<String> befund = new LinkedList<String>();
		LinkedList<String> fremdBefund = new LinkedList<String>();
		LinkedList<String> kommentar = new LinkedList<String>();
		LinkedList<String> anzahlFortsetzungszeilen6228 = new LinkedList<String>();
		LinkedList<String> ergebnistabellenText = new LinkedList<String>();
		LinkedList<DateiArchivierungsKenner> dateiArchivierungskenner = new LinkedList<DateiArchivierungsKenner>();
		DateiArchivierungsKenner currDateiArchivierungsKenner = null;
		LinkedList<TestIdent> testIdents = new LinkedList<TestIdent>();
		TestIdent currTestIdent = null;
		LinkedList<FreieKategorie> freieKategorien = new LinkedList<FreieKategorie>();
		FreieKategorie currFreieKategorie = null;

		for (int i = 0; i < satznachricht.length; i++) {
			int length = Integer.parseInt(satznachricht[i].substring(0, 3));
			int feldkennung = Integer.parseInt(satznachricht[i].substring(3, 7));
			String value = satznachricht[i].substring(7, length - 2);
			// TEST-IDENT
			if (feldkennung >= 8410 && feldkennung <= 8480) {
				currTestIdent = handleCurrentTestIdent(testIdents, currTestIdent, feldkennung, value);
				continue;
			}
			// FREIE KATEGORIE
			if (feldkennung >= 6330 && feldkennung <= 6399) {
				handleCurrentFreieKategorie(freieKategorien, currFreieKategorie, feldkennung, value);
				continue;
			}
			// DATEI-ARCHIVIERUNG
			if (feldkennung >= 6302 && feldkennung <= 6305) {
				handleCurrentDateiArchivierungsKenner(dateiArchivierungskenner, currDateiArchivierungsKenner,
						feldkennung, value);
				continue;
			}
			switch (feldkennung) {
			case GDTConstants.FELDKENNUNG_AKTUELLE_DIAGNOSE:
				aktDiag.add(value);
				break;
			case GDTConstants.FELDKENNUNG_BEFUND:
				befund.add(value);
				break;
			case GDTConstants.FELDKENNUNG_FREMDBEFUND:
				fremdBefund.add(value);
				break;
			case GDTConstants.FELDKENNUNG_KOMMENTAR:
				kommentar.add(value);
				break;
			case GDTConstants.FELDKENNUNG_ANZAHL_FOLGEZEILEN:
				anzahlFortsetzungszeilen6228.add(value);
				break;
			case GDTConstants.FELDKENNUNG_ERGEBNISTABELLENTEXT_FORMATIERT:
				ergebnistabellenText.add(value);
				break;
			default:
				ds.put(feldkennung, value);
				break;
			}
		}

		if (currDateiArchivierungsKenner != null)
			dateiArchivierungskenner.add(currDateiArchivierungsKenner);
		if (currFreieKategorie != null)
			freieKategorien.add(currFreieKategorie);
		if (currTestIdent != null)
			testIdents.add(currTestIdent);

		return new GDTSatzNachricht6310(ds.get(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAME), ds.get(GDTConstants.FELDKENNUNG_PATIENT_VORNAME),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ), ds.get(GDTConstants.FELDKENNUNG_PATIENT_TITEL),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_WOHNORT), ds.get(GDTConstants.FELDKENNUNG_PATIENT_STRASSE),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT), ds.get(GDTConstants.FELDKENNUNG_PATIENT_GROESSE),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_GEWICHT),
				ds.get(GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE),
				ds.get(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD),
				ds.get(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN),
				ds.get(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN),
				aktDiag.toArray(new String[] {}), befund.toArray(new String[] {}), fremdBefund.toArray(new String[] {}),
				kommentar.toArray(new String[] {}), anzahlFortsetzungszeilen6228.toArray(new String[] {}),
				ergebnistabellenText.toArray(new String[] {}), testIdents.toArray(new TestIdent[] {}),
				dateiArchivierungskenner.toArray(new DateiArchivierungsKenner[] {}),
				freieKategorien.toArray(new FreieKategorie[] {}), ds.get(GDTConstants.FELDKENNUNG_SIGNATUR),
				ds.get(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER), ds.get(GDTConstants.FELDKENNUNG_GDT_ID_SENDER),
				ds.get(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ), ds.get(GDTConstants.FELDKENNUNG_VERSION_GDT));
	}

	/**
	 *
	 * @param testIdents
	 * @param currTestIdent
	 * @param feldkennung
	 * @param value
	 */
	private static TestIdent handleCurrentTestIdent(LinkedList<TestIdent> testIdents, TestIdent currTestIdent,
			int feldkennung, String value) {
		switch (feldkennung) {
		case GDTConstants.FELDKENNUNG_TEST_IDENT:
			if (currTestIdent == null) {
				currTestIdent = new TestIdent();
			} else {
				testIdents.add(currTestIdent);
			}
			currTestIdent.setTestIdent(value);
			break;
		default:
			if (currTestIdent == null) {
				currTestIdent = new TestIdent();
			}
			currTestIdent.setValue(feldkennung, value);
		}
		return currTestIdent;
	}

	/**
	 *
	 * @param freieKategorien
	 * @param currFreieKategorie
	 * @param feldkennung
	 * @param value
	 */
	private static void handleCurrentFreieKategorie(LinkedList<FreieKategorie> freieKategorien,
			FreieKategorie currFreieKategorie, int feldkennung, String value) {

		switch (feldkennung) {
		case GDTConstants.FELDKENNUNG_NAME_DER_FREIEN_KATEGORIE: {
			if (currFreieKategorie == null) {
				currFreieKategorie = new FreieKategorie();
			} else {
				freieKategorien.add(currFreieKategorie);
			}
			currFreieKategorie.setName(value);
			break;
		}
		default:
			if (currFreieKategorie == null) {
				currFreieKategorie = new FreieKategorie();
			}
			currFreieKategorie.setValue(feldkennung, value);
			break;
		}
	}

	/**
	 * Bearbeite einen Eintrag des Types Datei Archivierungskenner mit seinen
	 * Folge-Einträgen (abhängigkeit).
	 *
	 * @param dateiArchivierungskenner
	 * @param currDateiArchivierungsKenner
	 * @param feldkennung
	 * @param value
	 */
	private static void handleCurrentDateiArchivierungsKenner(
			LinkedList<DateiArchivierungsKenner> dateiArchivierungskenner,
			DateiArchivierungsKenner currDateiArchivierungsKenner, int feldkennung, String value) {
		switch (feldkennung) {
		case GDTConstants.FELDKENNUNG_DATEI_ARCHIVIERUNGSNUMMER:
			if (currDateiArchivierungsKenner == null) {
				currDateiArchivierungsKenner = new DateiArchivierungsKenner(value, null, null, null);
			} else {
				dateiArchivierungskenner.add(currDateiArchivierungsKenner);
			}
			break;
		case GDTConstants.FELDKENNUNG_DATEIFORMAT:
			if (currDateiArchivierungsKenner == null) {
				currDateiArchivierungsKenner = new DateiArchivierungsKenner(null, value, null, null);
			} else {
				currDateiArchivierungsKenner.setValue(GDTConstants.FELDKENNUNG_DATEIFORMAT, value);
			}
		case GDTConstants.FELDKENNUNG_DATEIINHALT:
			if (currDateiArchivierungsKenner == null) {
				currDateiArchivierungsKenner = new DateiArchivierungsKenner(null, null, value, null);
			} else {
				currDateiArchivierungsKenner.setValue(GDTConstants.FELDKENNUNG_DATEIINHALT, value);
			}
		case GDTConstants.FELDKENNUNG_VERWEIS_AUF_DATEI:
			if (currDateiArchivierungsKenner == null) {
				currDateiArchivierungsKenner = new DateiArchivierungsKenner(null, null, null, value);
			} else {
				currDateiArchivierungsKenner.setValue(GDTConstants.FELDKENNUNG_VERWEIS_AUF_DATEI, value);
			}
		default:
			break;
		}
	}

	@Override
	protected void createMessage() {
		super.createMessage();

		addLine(GDTConstants.FELDKENNUNG_SOFTWAREVERANTWORTLICHER + GDTConstants.SOFTWAREVERWANTWORTLICHER);
		addLine(GDTConstants.FELDKENNUNG_SOFTWARE + GDTConstants.SOFTWARE);
		addLine(GDTConstants.FELDKENNUNG_RELEASE_STAND_DER_SOFTWARE + GDTConstants.SOFTWARE_RELEASE_STAND);

		addLine(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_NAMENSZUSATZ);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_NAME);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VORNAME);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GEBURTSDATUM);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_TITEL);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENNUMMER);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_WOHNORT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_STRASSE);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_VERSICHERTENART);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GESCHLECHT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GROESSE);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_GEWICHT);
		ifSetAddLine(GDTConstants.FELDKENNUNG_PATIENT_MUTTERSPRACHE);
		ifSetAddLine(GDTConstants.FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD);
		ifSetAddLine(GDTConstants.FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);
		ifSetAddLine(GDTConstants.FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN);

		if (aktuelleDiagnose != null)
			for (int i = 0; i < aktuelleDiagnose.length; i++)
				addLine(GDTConstants.FELDKENNUNG_AKTUELLE_DIAGNOSE + aktuelleDiagnose[i]);
		if (befund != null)
			for (int i = 0; i < befund.length; i++)
				addLine(GDTConstants.FELDKENNUNG_BEFUND + befund[i]);
		if (fremdBefund != null)
			for (int i = 0; i < fremdBefund.length; i++)
				addLine(GDTConstants.FELDKENNUNG_FREMDBEFUND + fremdBefund[i]);
		if (kommentar != null)
			for (int i = 0; i < kommentar.length; i++)
				addLine(GDTConstants.FELDKENNUNG_KOMMENTAR + kommentar[i]);
		if (anzahlFortsetzungsZeilen6228 != null)
			for (int i = 0; i < anzahlFortsetzungsZeilen6228.length; i++)
				addLine(GDTConstants.FELDKENNUNG_ANZAHL_FOLGEZEILEN + anzahlFortsetzungsZeilen6228[i]);
		if (ergebnistabellenText != null)
			for (int i = 0; i < ergebnistabellenText.length; i++)
				addLine(GDTConstants.FELDKENNUNG_ERGEBNIS_TEXT + ergebnistabellenText[i]);

		// TODO: OUTPUT DATEI ARCHITIVERUNGSKENNER 6302

		// TODO: OUTPUT FREIE KATEGORIE 6330 - 6399

		if (testIdent != null) {
			for (int i = 0; i < testIdent.length; i++) {
				TestIdent t = testIdent[i];
				if (t.getTestIdent() == null) {
					addLine(GDTConstants.FELDKENNUNG_TEST_IDENT + "TestNumber" + (i + 1));
				} else {
					addLine(GDTConstants.FELDKENNUNG_TEST_IDENT + t.getTestIdent());
				}
				if (t.getTestBezeichnung() != null)
					addLine(GDTConstants.FELDKENNUNG_TESTBEZEICHNUNG + t.getTestBezeichnung());
				if (t.getProbenmaterialIdent() != null)
					addLine(GDTConstants.FELDKENNUNG_PROBENMATERIAL_IDENT + t.getProbenmaterialIdent());
				if (t.getProbenmaterialIndex() != null)
					addLine(GDTConstants.FELDKENNUNG_PROBENMATERIAL_INDEX + t.getProbenmaterialIndex());
				if (t.getProbenmaterialBezeichnung() != null)
					addLine(GDTConstants.FELDKENNUNG_PROBENMATERIAL_BEZEICHNUNG + t.getProbenmaterialBezeichnung());
				List<String> probenMaterialSpezfikation = t.getProbenmaterialSpezifikation();
				if (probenMaterialSpezfikation != null && probenMaterialSpezfikation.size() > 0) {
					for (String string : probenMaterialSpezfikation) {
						if (string != null)
							addLine(GDTConstants.FELDKENNUNG_PROBENMATERIAL_SPEZIFIKATION + string);
					}
				}

				if (t.getEinheitenFuerDatenstrom() != null)
					addLine(GDTConstants.FELDKENNUNG_EINHEIT_FUER_DATENSTROM + t.getEinheitenFuerDatenstrom());
				List<String> datenstrom = t.getDatenStrom();
				if (datenstrom != null && datenstrom.size() > 0) {
					for (String string : datenstrom) {
						if (string != null)
							addLine(GDTConstants.FELDKENNUNG_DATENSTROM + string);
					}
				}

				if (t.getTestStatus() != null)
					addLine(GDTConstants.FELDKENNUNG_TESTSTATUS + t.getTestStatus());
				if (t.getErgebnisWert() != null)
					addLine(GDTConstants.FELDKENNUNG_ERGEBNIS_WERT + t.getErgebnisWert());
				if (t.getEinheit() != null)
					addLine(GDTConstants.FELDKENNUNG_EINHEIT + t.getEinheit());
				if (t.getAbnahmeDatum() != null)
					addLine(GDTConstants.FELDKENNUNG_ABNAHME_DATUM + t.getAbnahmeDatum());
				if (t.getAbnahmeZeit() != null)
					addLine(GDTConstants.FELDKENNUNG_ABNAHME_ZEIT + t.getAbnahmeZeit());
				if (t.getNormalwertText() != null)
					addLine(GDTConstants.FELDKENNUNG_NORMALWERT_TEXT + t.getNormalwertText());
				if (t.getNormalwertUntereGrenze() != null)
					addLine(GDTConstants.FELDKENNUNG_NORMALWERT_UNTERE_GRENZE + t.getNormalwertUntereGrenze());
				if (t.getNormalwertObereGrenze() != null)
					addLine(GDTConstants.FELDKENNUNG_NORMALWERT_OBERE_GRENZE + t.getNormalwertObereGrenze());

				List<String> anmerkungen = t.getAnmerkung();
				if (anmerkungen != null) {
					for (String string : anmerkungen) {
						if (string != null)
							addLine(GDTConstants.FELDKENNUNG_TEST_IDENT_ANMERKUNG + string);
					}
				}
				List<String> ergebnisTexte = t.getErgebnisText();
				if (ergebnisTexte != null) {
					for (String string : ergebnisTexte) {
						if (string != null)
							addLine(GDTConstants.FELDKENNUNG_ERGEBNIS_TEXT + string);
					}
				}

			}
		}
		ifSetAddLine(GDTConstants.FELDKENNUNG_SIGNATUR);
	}

	public TestIdent[] getTestIdent() {
		return testIdent;
	}
}
