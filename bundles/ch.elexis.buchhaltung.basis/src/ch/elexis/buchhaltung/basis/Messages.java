/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.buchhaltung.basis;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.buchhaltung.basis.messages";
	public static String AlleLeistungen_ActivityText;
	public static String AlleLeistungen_BillState;
	public static String AlleLeistungen_Description;
	public static String AlleLeistungen_Doctor;
	public static String AlleLeistungen_InvoicingParty;
	public static String AlleLeistungen_Mandator;
	public static String AlleLeistungen_NoBill;
	public static String AlleLeistungen_PatientCity;
	public static String AlleLeistungen_PatientDateOfBirth;
	public static String AlleLeistungen_PatientFirstname;
	public static String AlleLeistungen_PatientId;
	public static String AlleLeistungen_PatientName;
	public static String AlleLeistungen_PatientSex;
	public static String AlleLeistungen_PatientZip;
	public static String AlleLeistungen_PurchaseCosts;
	public static String AlleLeistungen_Quantity;
	public static String AlleLeistungen_SaleCosts;
	public static String AlleLeistungen_Sales;
	public static String AlleLeistungen_TariffType;
	public static String AlleLeistungen_TarmedAL;
	public static String AlleLeistungen_TarmedCode;
	public static String AlleLeistungen_TarmedMissing;
	public static String AlleLeistungen_TarmedTL;
	public static String AlleLeistungen_TaxPointValue;
	public static String AlleLeistungen_Title;
	public static String AlleLeistungen_TreatmentDate;
	public static String AlleLeistungen_User;
	public static String AlleLeistungen_VAT;
	public static String FakturaJournalDetail_Beschreibung;
	public static String FakturaJournalDetail_Name;
	public static String FakturaJournalDetail_Patient;
	public static String FakturaJournalDetail_Rechnungsempfaenger;
	public static String FakturaJournalDetail_Rechnungssteller;
	public static String FakturaJournal_Amount;
	public static String FakturaJournal_DatabaseQuery;
	public static String FakturaJournal_Date;
	public static String FakturaJournal_FA;
	public static String FakturaJournal_Faktura;
	public static String FakturaJournal_FakturaJournal;
	public static String FakturaJournal_GU;
	public static String FakturaJournal_PatientNr;
	public static String FakturaJournal_ST;
	public static String FakturaJournal_Text;
	public static String FakturaJournal_Type;
	public static String ListeNachFaelligkeit_Amount;
	public static String ListeNachFaelligkeit_AnalyzingBills;
	public static String ListeNachFaelligkeit_BillNr;
	public static String ListeNachFaelligkeit_BillsAfterDaysDue;
	public static String ListeNachFaelligkeit_DatabaseQuery;
	public static String ListeNachFaelligkeit_Due;
	public static String ListeNachFaelligkeit_PatientNr;
	public static String OffenePostenListe_AnalyzingBills;
	public static String OffenePostenListe_BillNr;
	public static String OffenePostenListe_BillState;
	public static String OffenePostenListe_DatabaseQuery;
	public static String OffenePostenListe_Open;
	public static String OffenePostenListe_OpenAmount;
	public static String OffenePostenListe_OpenBillsPer;
	public static String OffenePostenListe_PatientNr;
	public static String ZahlungsJournal_AD;
	public static String ZahlungsJournal_Amount;
	public static String ZahlungsJournal_DatabaseQuery;
	public static String ZahlungsJournal_Date;
	public static String ZahlungsJournal_PatientNr;
	public static String ZahlungsJournal_PaymentJournal;
	public static String ZahlungsJournal_TZ;
	public static String ZahlungsJournal_Text;
	public static String ZahlungsJournal_Type;
	public static String ZahlungsJournal_ZA;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
