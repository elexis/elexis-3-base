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

import java.util.LinkedList;
import java.util.List;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class TestIdent {

	String testIdent;
	String testBezeichnung;
	String probenmaterialIdent;
	String probenmaterialIndex;
	String probenmaterialBezeichnung;
	List<String> probenmaterialSpezifikation = new LinkedList<String>();
	String einheitenFuerDatenstrom;
	List<String> datenStrom = new LinkedList<String>();
	String testStatus;
	String ergebnisWert;
	String einheit;
	String abnahmeDatum;
	String normalwertText;
	String normalwertUntereGrenze;
	String normalwertObereGrenze;
	List<String> anmerkung = new LinkedList<String>();
	List<String> ergebnisText = new LinkedList<String>();
	String abnahmeZeit;

	public void setValue(int feldkennung, String value) {
		switch (feldkennung) {
		case GDTConstants.FELDKENNUNG_TEST_IDENT:
			testIdent = value;
			break;
		case GDTConstants.FELDKENNUNG_TESTBEZEICHNUNG:
			testBezeichnung = value;
			break;
		case GDTConstants.FELDKENNUNG_PROBENMATERIAL_IDENT:
			probenmaterialIdent = value;
			break;
		case GDTConstants.FELDKENNUNG_PROBENMATERIAL_INDEX:
			probenmaterialIndex = value;
			break;
		case GDTConstants.FELDKENNUNG_PROBENMATERIAL_BEZEICHNUNG:
			probenmaterialBezeichnung = value;
			break;
		case GDTConstants.FELDKENNUNG_PROBENMATERIAL_SPEZIFIKATION:
			probenmaterialSpezifikation.add(value);
			break;
		case GDTConstants.FELDKENNUNG_EINHEIT_FUER_DATENSTROM:
			einheitenFuerDatenstrom = value;
			break;
		case GDTConstants.FELDKENNUNG_DATENSTROM:
			datenStrom.add(value);
			break;
		case GDTConstants.FELDKENNUNG_TESTSTATUS:
			testStatus = value;
			break;
		case GDTConstants.FELDKENNUNG_ERGEBNIS_WERT:
			ergebnisWert = value;
			break;
		case GDTConstants.FELDKENNUNG_EINHEIT:
			einheit = value;
			break;
		case GDTConstants.FELDKENNUNG_ABNAHME_DATUM:
			abnahmeDatum = value;
			break;
		case GDTConstants.FELDKENNUNG_ABNAHME_ZEIT:
			abnahmeZeit = value;
			break;
		case GDTConstants.FELDKENNUNG_NORMALWERT_TEXT:
			normalwertText = value;
			break;
		case GDTConstants.FELDKENNUNG_NORMALWERT_UNTERE_GRENZE:
			normalwertUntereGrenze = value;
			break;
		case GDTConstants.FELDKENNUNG_NORMALWERT_OBERE_GRENZE:
			normalwertObereGrenze = value;
			break;
		case GDTConstants.FELDKENNUNG_TEST_IDENT_ANMERKUNG:
			anmerkung.add(value);
			break;
		case GDTConstants.FELDKENNUNG_ERGEBNIS_TEXT:
			ergebnisText.add(value);
			break;
		default:
			break;
		}
	}

	public String getAbnahmeZeit() {
		return abnahmeZeit;
	}

	public void setAbnahmeZeit(String abnahmeZeit) {
		this.abnahmeZeit = abnahmeZeit;
	}

	public void setTestIdent(String value) {
		this.testIdent = value;
	}

	public String getTestBezeichnung() {
		return testBezeichnung;
	}

	public void setTestBezeichnung(String testBezeichnung) {
		this.testBezeichnung = testBezeichnung;
	}

	public String getProbenmaterialIdent() {
		return probenmaterialIdent;
	}

	public void setProbenmaterialIdent(String probenmaterialIdent) {
		this.probenmaterialIdent = probenmaterialIdent;
	}

	public String getProbenmaterialIndex() {
		return probenmaterialIndex;
	}

	public void setProbenmaterialIndex(String probenmaterialIndex) {
		this.probenmaterialIndex = probenmaterialIndex;
	}

	public String getProbenmaterialBezeichnung() {
		return probenmaterialBezeichnung;
	}

	public void setProbenmaterialBezeichnung(String probenmaterialBezeichnung) {
		this.probenmaterialBezeichnung = probenmaterialBezeichnung;
	}

	public List<String> getProbenmaterialSpezifikation() {
		return probenmaterialSpezifikation;
	}

	public void setProbenmaterialSpezifikation(List<String> probenmaterialSpezifikation) {
		this.probenmaterialSpezifikation = probenmaterialSpezifikation;
	}

	public String getEinheitenFuerDatenstrom() {
		return einheitenFuerDatenstrom;
	}

	public void setEinheitenFuerDatenstrom(String einheitenFuerDatenstrom) {
		this.einheitenFuerDatenstrom = einheitenFuerDatenstrom;
	}

	public List<String> getDatenStrom() {
		return datenStrom;
	}

	public void setDatenStrom(List<String> datenStrom) {
		this.datenStrom = datenStrom;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	public String getErgebnisWert() {
		return ergebnisWert;
	}

	public void setErgebnisWert(String ergebnisWert) {
		this.ergebnisWert = ergebnisWert;
	}

	public String getEinheit() {
		return einheit;
	}

	public void setEinheit(String einheit) {
		this.einheit = einheit;
	}

	public String getAbnahmeDatum() {
		return abnahmeDatum;
	}

	public void setAbnahmeDatum(String abnahmeDatum) {
		this.abnahmeDatum = abnahmeDatum;
	}

	public String getNormalwertText() {
		return normalwertText;
	}

	public void setNormalwertText(String normalwertText) {
		this.normalwertText = normalwertText;
	}

	public String getNormalwertUntereGrenze() {
		return normalwertUntereGrenze;
	}

	public void setNormalwertUntereGrenze(String normalwertUntereGrenze) {
		this.normalwertUntereGrenze = normalwertUntereGrenze;
	}

	public String getNormalwertObereGrenze() {
		return normalwertObereGrenze;
	}

	public void setNormalwertObereGrenze(String normalwertObereGrenze) {
		this.normalwertObereGrenze = normalwertObereGrenze;
	}

	public List<String> getAnmerkung() {
		return anmerkung;
	}

	public void setAnmerkung(List<String> anmerkung) {
		this.anmerkung = anmerkung;
	}

	public List<String> getErgebnisText() {
		return ergebnisText;
	}

	public void setErgebnisText(List<String> ergebnisText) {
		this.ergebnisText = ergebnisText;
	}

	public String getTestIdent() {
		return testIdent;
	}
}
