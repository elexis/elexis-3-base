/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.constants;

public class GDTConstants {
	
	public static final String GDT_VERSION = "02.10";
	public static final String SOFTWAREVERWANTWORTLICHER = "MEDELEXIS AG";
	public static final String SOFTWARE = "MEDELEXIS";
	public static final String SOFTWARE_RELEASE_STAND = "2.1";
	
	public static final String GDT_SHORT_ID_DEFAULT = "ELXS";

	// Dateityp für Datenaustausch
	public static final String GDT_FILETRANSFER_TYP_FEST = "fest";
	public static final String GDT_FILETRANSFER_TYPE_HOCHZAEHLEND = "hochzaehlend";
	
	
	// Nach GDT 2.1 definierte Satzarten
	public static final int SATZART_STAMMDATEN_ANFORDERN = 6300;
	public static final int SATZART_STAMMDATEN_UEBERMITTELN = 6301;
	public static final int SATZART_UNTERSUCHUNG_ANFORDERN = 6302;
	public static final int SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN = 6310;
	public static final int SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN = 6311;
	
	// Feldkennungen
	public static final int FELDKENNUNG_SATZIDENTIFIKATION = 8000;
	public static final int FELDKENNUNG_SATZLAENGE = 8100;
	public static final int FELDKENNUNG_GDT_ID_EMPFAENGER = 8315;
	public static final int FELDKENNUNG_GDT_ID_SENDER = 8316;
	public static final int FELDKENNUNG_VERWENDETER_ZEICHENSATZ = 9206;
	public static final int FELDKENNUNG_VERSION_GDT = 9218;
	public static final int FELDKENNUNG_PATIENT_KENNUNG = 3000;
	public static final int FELDKENNUNG_PATIENT_NAMENSZUSATZ = 3100;
	public static final int FELDKENNUNG_PATIENT_NAME = 3101;
	public static final int FELDKENNUNG_PATIENT_VORNAME = 3102;
	/** Format: TTMMJJJJ */
	public static final int FELDKENNUNG_PATIENT_GEBURTSDATUM = 3103;
	public static final int FELDKENNUNG_PATIENT_TITEL = 3104;
	public static final int FELDKENNUNG_PATIENT_VERSICHERTENNUMMER = 3105;
	public static final int FELDKENNUNG_PATIENT_WOHNORT = 3106;
	public static final int FELDKENNUNG_PATIENT_STRASSE = 3107;
	public static final int FELDKENNUNG_PATIENT_VERSICHERTENART = 3108;
	public static final int FELDKENNUNG_PATIENT_GESCHLECHT = 3110;
	public static final int FELDKENNUNG_PATIENT_GROESSE = 3622;
	public static final int FELDKENNUNG_PATIENT_GEWICHT = 3623;
	public static final int FELDKENNUNG_PATIENT_MUTTERSPRACHE = 3628;	
	public static final int FELDKENNUNG_TAG_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN = 6200;
	/** Format: HHMMSS */
	public static final int FELDKENNUNG_UHRZEIT_DER_ERHEBUNG_VON_BEHANDLUNGSDATEN = 6201;
	public static final int FELDKENNUNG_AKTUELLE_DIAGNOSE = 6205;
	public static final int FELDKENNUNG_BEFUND = 6220;
	public static final int FELDKENNUNG_FREMDBEFUND = 6221;
	public static final int FELDKENNUNG_ANZAHL_FOLGEZEILEN = 6226;
	public static final int FELDKENNUNG_KOMMENTAR = 6227;
	public static final int FELDKENNUNG_ERGEBNISTABELLENTEXT_FORMATIERT = 6228;
	public static final int FELDKENNUNG_DATEI_ARCHIVIERUNGSNUMMER = 6302;
	public static final int FELDKENNUNG_DATEIFORMAT = 6303;
	public static final int FELDKENNUNG_DATEIINHALT = 6304;
	public static final int FELDKENNUNG_VERWEIS_AUF_DATEI = 6305;
	public static final int FELDKENNUNG_NAME_DER_FREIEN_KATEGORIE = 6330;
	public static final int FELDKENNUNG_INHALT_DER_FREIEN_KATEGORIE = 6331;
	
	public static final int FELDKENNUNG_GERAETE_UND_VERFAHRENSSPEZIFISCHES_KENNFELD = 8402;
	
	public static final int FELDKENNUNG_TEST_IDENT = 8410;
	public static final int FELDKENNUNG_TESTBEZEICHNUNG = 8411;
	public static final int FELDKENNUNG_TESTSTATUS = 8418;
	public static final int FELDKENNUNG_ERGEBNIS_WERT = 8420;
	public static final int FELDKENNUNG_EINHEIT = 8421;
	public static final int FELDKENNUNG_PROBENMATERIAL_IDENT = 8428;
	public static final int FELDKENNUNG_PROBENMATERIAL_INDEX = 8429;
	public static final int FELDKENNUNG_PROBENMATERIAL_BEZEICHNUNG = 8430;
	public static final int FELDKENNUNG_PROBENMATERIAL_SPEZIFIKATION = 8431;
	
	public static final int FELDKENNUNG_ABNAHME_DATUM = 8432;
	public static final int FELDKENNUNG_EINHEIT_FUER_DATENSTROM = 8437;
	public static final int FELDKENNUNG_DATENSTROM = 8438;
	/** Format: HHMMSS */
	public static final int FELDKENNUNG_ABNAHME_ZEIT = 8439;
	public static final int FELDKENNUNG_NORMALWERT_TEXT = 8460;
	public static final int FELDKENNUNG_NORMALWERT_UNTERE_GRENZE = 8461;
	public static final int FELDKENNUNG_NORMALWERT_OBERE_GRENZE = 8462;
	public static final int FELDKENNUNG_TESTBEZOGENE_HINWEISE = 8470;
	public static final int FELDKENNUNG_TEST_IDENT_ANMERKUNG = 8470;
	public static final int FELDKENNUNG_ERGEBNIS_TEXT = 8480;
	public static final int FELDKENNUNG_SIGNATUR = 8990;
	
	public static final String FELDKENNUNG_SOFTWAREVERANTWORTLICHER = "0102";
	public static final String FELDKENNUNG_SOFTWARE = "0103";
	public static final String FELDKENNUNG_RELEASE_STAND_DER_SOFTWARE = "0132";
	
	// Feldkennung Zeichensatz 9206 Inhalt
	public static final int ZEICHENSATZ_7BIT = 1;
	public static final int ZEICHENSATZ_IBM_CP_437 = 2;	// STANDARD-ZEICHENSATZ
	public static final int ZEICHENSATZ_ISO8859_1_ANSI_CP_1252 = 3;
	
	public static final String ZEICHENSATZ_7BIT_CHARSET_STRING = "US-ASCII";
	public static final String ZEICHENSATZ_IBM_CP_437_CHARSET_STRING = "cp437"; // STANDARD-ZEICHENSATZ
	public static final String ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING = "Cp1252";
	
	// Feldkennung Versichertenart 3108 Inhalt
	public static final int VERSICHERTENART_MITGLIED = 1;
	public static final int VERSICHERTENART_FAMILIENVERSICHERTER = 3;
	public static final int VERSICHERTENART_RENTNER = 5;
	
	// Feldkennung Geschlecht 3110
	public static final int SEX_MALE = 1;
	public static final int SEX_FEMALE = 2;
	
	
	public static String getCharsetStringByInt(int charset) {
		switch (charset) {
		case 1: return ZEICHENSATZ_7BIT_CHARSET_STRING;
		case 2: return ZEICHENSATZ_IBM_CP_437_CHARSET_STRING;
		case 3: return ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING;
		default:return null;
		}
	}
	
	public static int getCharsetIntByString(String charsetString) {
		if(charsetString.equalsIgnoreCase(ZEICHENSATZ_7BIT_CHARSET_STRING)) return ZEICHENSATZ_7BIT;
		if(charsetString.equalsIgnoreCase(ZEICHENSATZ_IBM_CP_437_CHARSET_STRING)) return ZEICHENSATZ_IBM_CP_437;
		if(charsetString.equalsIgnoreCase(ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING)) return ZEICHENSATZ_ISO8859_1_ANSI_CP_1252;
		return 0;
	}
}
