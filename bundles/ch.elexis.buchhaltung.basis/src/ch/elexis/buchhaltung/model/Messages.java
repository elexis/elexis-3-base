/*******************************************************************************
 * Copyright (c) 2006-2010, Gerry Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gerry Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.model;

import org.eclipse.osgi.util.NLS;

public class Messages {
	public static String AlleLeistungen_ActivityText = ch.elexis.core.l10n.Messages.Core_Text;
	public static String AlleLeistungen_BillState = ch.elexis.core.l10n.Messages.AlleLeistungen_BillState;
	public static String AlleLeistungen_Description = ch.elexis.core.l10n.Messages.AlleLeistungen_Description;
	public static String AlleLeistungen_Doctor = ch.elexis.core.l10n.Messages.Core_RegularPhysiscion;
	public static String AlleLeistungen_InvoicingParty = ch.elexis.core.l10n.Messages.Core_Invoicingparty;
	public static String AlleLeistungen_Mandator = ch.elexis.core.l10n.Messages.Core_Mandator;
	public static String AlleLeistungen_NoBill = ch.elexis.core.l10n.Messages.AlleLeistungen_NoBill;
	public static String AlleLeistungen_PatientCity = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientCity;
	public static String AlleLeistungen_PatientDateOfBirth = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientDateOfBirth;
	public static String AlleLeistungen_PatientFirstname = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientFirstname;
	public static String AlleLeistungen_PatientId = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientId;
	public static String AlleLeistungen_PatientName = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientName;
	public static String AlleLeistungen_PatientSex = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientSex;
	public static String AlleLeistungen_PatientZip = ch.elexis.core.l10n.Messages.AlleLeistungen_PatientZip;
	public static String AlleLeistungen_PurchaseCosts = ch.elexis.core.l10n.Messages.AlleLeistungen_PurchaseCosts;
	public static String AlleLeistungen_Quantity = ch.elexis.core.l10n.Messages.AlleLeistungen_Quantity;
	public static String AlleLeistungen_SaleCosts = ch.elexis.core.l10n.Messages.AlleLeistungen_SaleCosts;
	public static String AlleLeistungen_Sales = ch.elexis.core.l10n.Messages.AlleLeistungen_Sales;
	public static String AlleLeistungen_TariffType = ch.elexis.core.l10n.Messages.AlleLeistungen_TariffType;
	public static String AlleLeistungen_TarmedAL = ch.elexis.core.l10n.Messages.AlleLeistungen_TarmedAL;
	public static String AlleLeistungen_TarmedCode = ch.elexis.core.l10n.Messages.AlleLeistungen_TarmedCode;
	public static String AlleLeistungen_TarmedMissing = ch.elexis.core.l10n.Messages.AlleLeistungen_TarmedMissing;
	public static String AlleLeistungen_TarmedTL = ch.elexis.core.l10n.Messages.AlleLeistungen_TarmedTL;
	public static String AlleLeistungen_TaxPointValue = ch.elexis.core.l10n.Messages.AlleLeistungen_TaxPointValue;
	public static String AlleLeistungen_Title = ch.elexis.core.l10n.Messages.AlleLeistungen_Title;
	public static String AlleLeistungen_TreatmentDate = ch.elexis.core.l10n.Messages.AlleLeistungen_TreatmentDate;
	public static String AlleLeistungen_User = ch.elexis.core.l10n.Messages.Benutzer;
	public static String AlleLeistungen_VAT = ch.elexis.core.l10n.Messages.AlleLeistungen_VAT;
	public static String FakturaJournal_Amount = ch.elexis.core.l10n.Messages.FakturaJournal_Amount;
	public static String FakturaJournal_DatabaseQuery = ch.elexis.core.l10n.Messages.Core_Database_Query;
	public static String FakturaJournal_Date = ch.elexis.core.l10n.Messages.FakturaJournal_Date;
	public static String FakturaJournal_FA = ch.elexis.core.l10n.Messages.FakturaJournal_FA;
	public static String FakturaJournal_Faktura = ch.elexis.core.l10n.Messages.FakturaJournal_Faktura;
	public static String FakturaJournal_FakturaJournal = ch.elexis.core.l10n.Messages.FakturaJournal_FakturaJournal;
	public static String FakturaJournal_GU = ch.elexis.core.l10n.Messages.FakturaJournal_GU;
	public static String FakturaJournal_PatientNr = ch.elexis.core.l10n.Messages.FakturaJournal_PatientNr;
	public static String FakturaJournal_ST = ch.elexis.core.l10n.Messages.FakturaJournal_ST;
	public static String FakturaJournal_Text = ch.elexis.core.l10n.Messages.Core_Text;
	public static String FakturaJournal_Type = ch.elexis.core.l10n.Messages.FakturaJournal_Type;
	public static String FakturaJournalDetail_Beschreibung = ch.elexis.core.l10n.Messages.FakturaJournalDetail_Beschreibung;
	public static String FakturaJournalDetail_Name = ch.elexis.core.l10n.Messages.FakturaJournalDetail_Name;
	public static String FakturaJournalDetail_Patient = ch.elexis.core.l10n.Messages.Core_Patient;
	public static String FakturaJournalDetail_Rechnungsempfaenger = ch.elexis.core.l10n.Messages.FakturaJournalDetail_Rechnungsempfaenger;
	public static String FakturaJournalDetail_Rechnungssteller = ch.elexis.core.l10n.Messages.Core_Invoicingparty;
	public static String ListeNachFaelligkeit_Amount = ch.elexis.core.l10n.Messages.ListeNachFaelligkeit_Amount;
	public static String ListeNachFaelligkeit_AnalyzingBills = ch.elexis.core.l10n.Messages.AnalyzingBills;
	public static String ListeNachFaelligkeit_BillNr = ch.elexis.core.l10n.Messages.ListeNachFaelligkeit_BillNr;
	public static String ListeNachFaelligkeit_BillsAfterDaysDue = ch.elexis.core.l10n.Messages.ListeNachFaelligkeit_BillsAfterDaysDue;
	public static String ListeNachFaelligkeit_DatabaseQuery = ch.elexis.core.l10n.Messages.Core_Database_Query;
	public static String ListeNachFaelligkeit_Due = ch.elexis.core.l10n.Messages.ListeNachFaelligkeit_Due;
	public static String ListeNachFaelligkeit_PatientNr = ch.elexis.core.l10n.Messages.Core_Patient_Number;
	public static String OffenePostenListe_AnalyzingBills = ch.elexis.core.l10n.Messages.AnalyzingBills;
	public static String OffenePostenListe_BillNr = ch.elexis.core.l10n.Messages.OffenePostenListe_BillNr;
	public static String OffenePostenListe_BillState = ch.elexis.core.l10n.Messages.OffenePostenListe_BillState;
	public static String OffenePostenListe_DatabaseQuery = ch.elexis.core.l10n.Messages.Core_Database_Query;
	public static String OffenePostenListe_Open = ch.elexis.core.l10n.Messages.OffenePostenListe_Open;
	public static String OffenePostenListe_OpenAmount = ch.elexis.core.l10n.Messages.Invoice_amount_due;
	public static String OffenePostenListe_OpenBillsPer = ch.elexis.core.l10n.Messages.OffenePostenListe_OpenBillsPer;
	public static String OffenePostenListe_PatientNr = ch.elexis.core.l10n.Messages.Core_Patient_Number;
	public static String ZahlungsJournal_AD = ch.elexis.core.l10n.Messages.ZahlungsJournal_AD;
	public static String ZahlungsJournal_Amount = ch.elexis.core.l10n.Messages.ZahlungsJournal_Amount;
	public static String ZahlungsJournal_DatabaseQuery = ch.elexis.core.l10n.Messages.Core_Database_Query;
	public static String ZahlungsJournal_Date = ch.elexis.core.l10n.Messages.ZahlungsJournal_Date;
	public static String ZahlungsJournal_PatientNr = ch.elexis.core.l10n.Messages.Core_Patient_Number;
	public static String ZahlungsJournal_PaymentJournal = ch.elexis.core.l10n.Messages.ZahlungsJournal_PaymentJournal;
	public static String ZahlungsJournal_Text = ch.elexis.core.l10n.Messages.Core_Text;
	public static String ZahlungsJournal_Type = ch.elexis.core.l10n.Messages.ZahlungsJournal_Type;
	public static String ZahlungsJournal_TZ = ch.elexis.core.l10n.Messages.ZahlungsJournal_TZ;
	public static String ZahlungsJournal_ZA = ch.elexis.core.l10n.Messages.ZahlungsJournal_ZA;

}
