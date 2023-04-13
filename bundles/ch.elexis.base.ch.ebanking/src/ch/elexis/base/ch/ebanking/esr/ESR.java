/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.ebanking.esr;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IAccount;
import ch.elexis.core.services.holder.AccountServiceHolder;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.AccountTransaction;
import ch.elexis.data.AccountTransaction.Account;
import ch.elexis.data.Kontakt;
import ch.rgw.tools.StringTool;

/**
 * Repräsentation eines ESR Einzahlungsscheins tn ist die Teilnehmernummer. id
 * kann null sein, dann ist es ein VESR, oder kann die subid des Bankkunden
 * sein, dann ist es ein BESR.
 *
 * @author gerry
 *
 */
public class ESR {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String ESR_NORMAL_FONT_NAME = "esr/normalfontname"; //$NON-NLS-1$
	public static final String ESR_NORMAL_FONT_SIZE = "esr/normalfontsize"; //$NON-NLS-1$
	public static final String ESR_OCR_FONT_NAME = "esr/ocrfontname"; //$NON-NLS-1$
	public static final String ESR_OCR_FONT_SIZE = "esr/ocrfontsize"; //$NON-NLS-1$
	public static final String ESR_OCR_FONT_WEIGHT = "esr/ocrfontweight"; //$NON-NLS-1$

	public static final String ESR_NORMAL_FONT_NAME_DEFAULT = "OCR-B"; //$NON-NLS-1$
	public static final int ESR_NORMAL_FONT_SIZE_DEFAULT = 9;
	public static final String ESR_OCR_FONT_NAME_DEFAULT = "OCR-B-10 BT"; //$NON-NLS-1$
	public static final int ESR_OCR_FONT_SIZE_DEFAULT = 12;
	public static final int ESR_OCR_FONT_WEIGHT_DEFAULT = SWT.MIN;

	public static final String ESR_PRINTER_CORRECTION_X = "esr/printer_correction_x"; //$NON-NLS-1$
	public static final String ESR_PRINTER_CORRECTION_Y = "esr/printer_correction_y"; //$NON-NLS-1$
	public static final String ESR_PRINTER_BASE_OFFSET_X = "esr/printer_base_x"; //$NON-NLS-1$
	public static final String ESR_PRINTER_BASE_OFFSET_Y = "esr/printer_base_y"; //$NON-NLS-1$
	public static final int ESR_PRINTER_CORRECTION_X_DEFAULT = 0;
	public static final int ESR_PRINTER_CORRECTION_Y_DEFAULT = 0;
	// base offset depends on the printable left/top margin of a specific
	// printer
	public static final int ESR_PRINTER_BASE_OFFSET_X_DEFAULT = 0;
	public static final int ESR_PRINTER_BASE_OFFSET_Y_DEFAULT = 0;

	public static final int ESR16 = 16;
	public static final int ESR27 = 27;
	private String tn;
	private String id;
	private String userdata;
	private int reflen;

	/**
	 * BESR mit besrdata erstellen.
	 *
	 * @param ESR_tn    Teilnehmernummer im Format vv-xxx-P
	 * @param ESR_subid Kundennummer oder null
	 * @param usr       individueller Identifikationscode des EZ-Scheins (z.B. aus
	 *                  PatNr, und RnNummer aufgebaut)
	 * @param l         Länge der Referenznummer (nur 16 oder 27 zulässig)
	 */
	public ESR(String ESR_tn, String ESR_subid, String usr, int l) {
		tn = ESR_tn;
		id = ESR_subid == null ? StringUtils.EMPTY : ESR_subid;
		reflen = l - 1;
		userdata = usr;
	}

	/**
	 * Codierzeile aufbauen
	 *
	 * @param amount Betrag in Rappen
	 * @param tcCode Code des TrustCenters oder null: normale ESR-Zeile
	 * @return eine druckfertige Codierzeile
	 */
	public String createCodeline(String amount, String tcCode) {
		if (Integer.parseInt(amount) < 0) {
			amount = "0"; //$NON-NLS-1$
		}
		StringBuilder cl = new StringBuilder();
		if (tcCode == null) {
			tcCode = "01"; // ESR in CHF //$NON-NLS-1$
		}
		// Betrag auf 10 Stellen erweitert
		String betrag = wrap(tcCode + StringTool.pad(StringTool.LEFT, '0', amount, 10));
		cl.append(betrag);
		cl.append(">"); // Trennzeichen //$NON-NLS-1$
		cl.append(makeRefNr(false)); // Referenznummer
		cl.append("+ "); // Trennzeichen //$NON-NLS-1$
		cl.append(makeParticipantNumber(false)).append(">"); // Teilnehmernummer //$NON-NLS-1$
		return cl.toString();
	}

	/**
	 * Zeile Referenznummer aufbauen
	 *
	 * @param withSpaces true: in Fünfergruppen aufteilen
	 * @return die gebrauchsfertige Referenznummer
	 */
	public String makeRefNr(boolean withSpaces) {
		StringBuilder ret = new StringBuilder();
		int il = id.length();
		int ul = userdata.length();
		int space = reflen - ul - il;
		if (space < 0) {
			userdata = userdata.substring(space * -1);
			SWTHelper.showError(Messages.ESR_esr_invalid, Messages.ESR_warning_esr_not_correct);
			ret.append(id).append(userdata);
		} else {
			ret.append(id).append(StringTool.filler("0", space)).append( //$NON-NLS-1$
					userdata);
		}

		String refnr = wrap(ret.toString());
		if (withSpaces == false) {
			return refnr;
		}
		if (refnr.length() == 16) {
			return refnr.substring(0, 2) + StringUtils.SPACE + refnr.substring(3, 6) + StringUtils.SPACE // $NON-NLS-1$
					+ refnr.substring(7);
		} else if (refnr.length() == 27) {
			String g1 = refnr.substring(0, 2);
			String g2 = refnr.substring(2, 7);
			String g3 = refnr.substring(7, 12);
			String g4 = refnr.substring(12, 17);
			String g5 = refnr.substring(17, 22);
			String g6 = refnr.substring(22);
			return g1 + StringUtils.SPACE + g2 + StringUtils.SPACE + g3 + StringUtils.SPACE + g4 + StringUtils.SPACE
					+ g5 + StringUtils.SPACE + g6; // $NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			return "** ERROR **"; //$NON-NLS-1$
		}
	}

	/**
	 * Teilnehmernummer aufbauen
	 *
	 * @param withSeparators true: Bindestriche an geeigneten Stellen, wie im
	 *                       KOnstruktor eingegeben
	 * @return die gebrauchsfertige Teilnehmernummer
	 */
	public String makeParticipantNumber(boolean withSeparators) {
		if (withSeparators == true) {
			return tn;
		}
		String[] ptn = tn.split("\\s*-\\s*"); //$NON-NLS-1$
		if (ptn.length != 3) {
			log.error(Messages.ESR_bad_user_defin + tn);
			return Messages.ESR_errorMark;
		}
		return ptn[0] + StringTool.pad(StringTool.LEFT, '0', ptn[1], 6) + ptn[2];
	}

	/**
	 * Eine beliebige Ziffernfolge mit der Modulo-10 Prüfsumme verpacken
	 *
	 * @param number darf nur aus Ziffern bestehen
	 * @return die Eingabefolge, ergänzt um ihre Prüfziffer
	 */
	public String wrap(String number) {
		int row = 0;
		String nr = number.replaceAll("[^0-9]", StringUtils.EMPTY); //$NON-NLS-1$
		for (int i = 0; i < nr.length(); i++) {
			int col = Integer.parseInt(nr.substring(i, i + 1));
			row = checksum[row][col];
		}
		return number + Integer.toString(checksum[row][10]);

	}

	/** X-Offset der ESR-Codierzeile */
	public int getESRLineX() {
		int printerCorrectionX = CoreHub.localCfg.get(ESR_PRINTER_CORRECTION_X, ESR_PRINTER_CORRECTION_X_DEFAULT);

		return 59 + printerCorrectionX;
	}

	/** Y-Offset der ESR-Codierzeile */
	public int getESRLineY() {
		int printerCorrectionY = CoreHub.localCfg.get(ESR_PRINTER_CORRECTION_Y, ESR_PRINTER_CORRECTION_Y_DEFAULT);

		return 192 + 85 + printerCorrectionY;
	}

	/** Breite der ESR-Codierzeile */
	public int getESRLineWidth() {
		return 140;
	}

	/** Höhe der ESR-Codierzeile */
	public int getESRLineHeight() {
		return 4;
	}

	/**
	 * Druckt einen BESR auf einen Rechnungsvordruck, der im TextContainer bereits
	 * eingelesen ist. Der EInzahlungsschein wird als unterer Anhang des Vordrucks
	 * erwartet. Die Ränder des Vordrucks müssen rundherum auf 5mm definiert sein.
	 */
	public boolean printBESR(Kontakt bank, Kontakt schuldner, Kontakt empfaenger, String betragInRappen,
			TextContainer text) {
		// Eine Zeile des Post-Vorgabe ESR sind 4.23mm (1/6 Zoll)
		int yBase = 192; // Offset Einzahlungsschein 19.2cm (absolut vom
		// Papierrand)
		int xBase = 0; // Offset Einzahlunsschein 0mm (absolut vom Papierrand)
		int wFr = 40; // Breite des Franken-Felds
		int hFr = 6; // Höhe des FrankenFeld
		int wRp = 10; // Breite des Rappen-Felds
		int wRef = 81; // Breite des Ref-Nr-Felds
		int hRef = 6; // Höhe des Ref-Nr-Felds
		int xRef = 63; // x-Offset des Ref-Nr-Felds von xGiro
		int yRef = 33; // y-Offset des Ref-Nr-Felds
		int xGiro = 60; // x-Offset des Giro-Abschnitts
		int hAdr = 10; // Höhe des Adressat-Felds
		int hBeg = 20; // Höhe des Begüpnstigten-Felds
		int xKonto = 22; // x-Offset der Kontonummer
		int wKonto = 30;
		int yKonto = 42; // y-Offset der Kontonummer
		int hKonto = 5; // Höhe der Kontonummer
		int yGarant1 = 60; // y-Offset des Absender-Adressblocks auf dem
		// Empfangsschein
		int yGarant2 = 50; // y-Offset des Absender-Adressblocks auf dem
		// Girozettel
		int wAdresse = 55; // Breite des Adressfeldes (unabhängig von Offsets)
		int manualYOffsetESR = CoreHub.localCfg.get(ESR_PRINTER_BASE_OFFSET_Y, ESR_PRINTER_BASE_OFFSET_Y_DEFAULT);
		int manualXOffsetESR = CoreHub.localCfg.get(ESR_PRINTER_BASE_OFFSET_X, ESR_PRINTER_BASE_OFFSET_X_DEFAULT);

		ITextPlugin p = text.getPlugin();
		String fontName = CoreHub.localCfg.get(ESR_NORMAL_FONT_NAME, ESR_NORMAL_FONT_NAME_DEFAULT);
		int fontSize = CoreHub.localCfg.get(ESR_NORMAL_FONT_SIZE, ESR_NORMAL_FONT_SIZE_DEFAULT);
		p.setFont(fontName, SWT.NORMAL, fontSize);

		// Korrekturen aus den Einstellungen anwenden.
		xBase += manualXOffsetESR;
		yBase += manualYOffsetESR;
		xGiro += manualXOffsetESR;

		if (bank != null && bank.isValid()) {
			// BESR

			// Bank
			StringBuilder badr = new StringBuilder();
			badr.append(bank.get("Bezeichnung1")).append(StringUtils.SPACE).append( //$NON-NLS-1$
					bank.get("Bezeichnung2")).append(StringUtils.LF).append( //$NON-NLS-1$
							bank.get("Plz")) //$NON-NLS-1$
					.append(StringUtils.SPACE).append(bank.get("Ort")); //$NON-NLS-1$
			// auf Abschnitt
			p.insertTextAt(xBase, yBase + 8, wAdresse, hAdr - 2, badr.toString(), SWT.LEFT);
			// auf Giro-Zettel
			p.insertTextAt(xGiro, yBase + 8, wAdresse, hAdr - 2, badr.toString(), SWT.LEFT);

			// Empfaenger
			// auf Abschnitt
			p.insertTextAt(xBase, yBase + 20, wAdresse, hBeg - 1, empfaenger.getPostAnschrift(true), SWT.LEFT);
			// auf Giro-Zettel
			p.insertTextAt(xGiro, yBase + 20, wAdresse, hBeg - 1, empfaenger.getPostAnschrift(true), SWT.LEFT);
		} else {
			// VESR

			int height = hAdr + 2 + hBeg;
			p.insertTextAt(xBase, yBase + 8, wAdresse, height, empfaenger.getPostAnschrift(true), SWT.LEFT);
			p.insertTextAt(xGiro, yBase + 8, wAdresse, height, empfaenger.getPostAnschrift(true), SWT.LEFT);
		}

		// Geldbetrag in Boxen für Fr. und Rp. einsetzen
		int betrag = Integer.parseInt(betragInRappen);
		int fr = betrag / 100;
		int rp = betrag - (100 * fr);

		String Franken = Integer.toString(fr);
		String Rappen = StringTool.pad(StringTool.LEFT, '0', Integer.toString(rp), 2);
		p.insertTextAt(xBase + 3, yBase + 50, wFr, hFr, Franken, SWT.CENTER);

		p.insertTextAt(xBase + 45, yBase + 50, wRp, hFr, Rappen, SWT.LEFT);

		// Referenznummer
		p.insertTextAt(xGiro + xRef, yBase + yRef, wRef, hRef, makeRefNr(true), SWT.CENTER);
		// Kontonummer
		String konto = makeParticipantNumber(true);
		p.insertTextAt(xBase + xKonto, yBase + yKonto, wKonto, hKonto, konto, SWT.LEFT);
		p.insertTextAt(xGiro + xKonto, yBase + yKonto, wKonto, hKonto, konto, SWT.LEFT);

		// remove leading zeros from reference number
		String refNr = makeRefNr(false).replaceFirst("^0+", StringUtils.EMPTY); //$NON-NLS-1$

		// Schzuldneradresse. Links mit refNr grad darüber, auf Giro-Abshcnitt ohne
		// refNr
		String abs1 = refNr + StringUtils.LF + schuldner.getPostAnschrift(true);
		p.insertTextAt(xBase, yBase + yGarant1, wAdresse, 25, abs1, SWT.LEFT);
		p.insertTextAt(xGiro + xRef, yBase + yGarant2, wAdresse, 25, schuldner.getPostAnschrift(true), SWT.LEFT);

		p.insertTextAt(xGiro + 5, yBase + 50, wFr, hFr, Franken, SWT.CENTER);
		p.insertTextAt(xGiro + 45, yBase + 50, wRp, hFr, Rappen, SWT.LEFT);
		printESRCodeLine(p, betragInRappen, null);

		return true;
	}

	/**
	 * ESR-Codierzeile auf das im TextContainer befindliche Blatt drucken
	 *
	 * @param tcCode Code des TrustCenters oder null. Bei null wird eine Post-ESR
	 *               erstellt, sonst eine TC-ESR
	 */
	public void printESRCodeLine(ITextPlugin p, String betragInRappen, String tcCode) {
		String besr = createCodeline(betragInRappen, tcCode);

		String fontname = CoreHub.localCfg.get(ESR_OCR_FONT_NAME, ESR_OCR_FONT_NAME_DEFAULT);
		int fontscale = CoreHub.localCfg.get(ESR_OCR_FONT_SIZE, ESR_OCR_FONT_SIZE_DEFAULT);
		int fontweight = CoreHub.localCfg.get(ESR_OCR_FONT_WEIGHT, ESR_OCR_FONT_WEIGHT_DEFAULT);
		// String fontname=Hub.localCfg.get("esr/ocrfont", "OCR-B-10 BT");
		// int fontscale=Hub.localCfg.get("esr/fontscale", 12);
		p.setFont(fontname, fontweight, fontscale);
		// int y=(int)Math.round(getESRLineY());
		p.insertTextAt(getESRLineX(), getESRLineY(), getESRLineWidth(), getESRLineHeight(), besr, SWT.CENTER);
	}

	/** Array für den modulo-10-Prüfsummencode */
	private static final int[][] checksum = { { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5, 0 }, { 9, 4, 6, 8, 2, 7, 1, 3, 5, 0, 9 },
			{ 4, 6, 8, 2, 7, 1, 3, 5, 0, 9, 8 }, { 6, 8, 2, 7, 1, 3, 5, 0, 9, 4, 7 },
			{ 8, 2, 7, 1, 3, 5, 0, 9, 4, 6, 6 }, { 2, 7, 1, 3, 5, 0, 9, 4, 6, 8, 5 },
			{ 7, 1, 3, 5, 0, 9, 4, 6, 8, 2, 4 }, { 1, 3, 5, 0, 9, 4, 6, 8, 2, 7, 3 },
			{ 3, 5, 0, 9, 4, 6, 8, 2, 7, 1, 2 }, { 5, 0, 9, 4, 6, 8, 2, 7, 1, 3, 1 } };

	private static IAccount esrAccount;

	/**
	 * Get an {@link Account} that should be used for {@link AccountTransaction}
	 * created from an ESR record.
	 *
	 * @return
	 */
	public static IAccount getAccount() {
		if (esrAccount == null) {
			HashMap<Integer, IAccount> accountsMap = AccountServiceHolder.get().getAccounts();
			Collection<IAccount> accounts = accountsMap.values();
			for (IAccount account : accounts) {
				if (account.getName().contains("ESR")) { //$NON-NLS-1$
					esrAccount = account;
					break;
				}
			}
		}
		return esrAccount;
	}
}
