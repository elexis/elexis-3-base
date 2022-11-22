/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.base.ch.ebanking;

import org.eclipse.osgi.util.NLS;

public class Messages{
    public static String ESRFile_ExceptionParsing = ch.elexis.core.l10n.Messages.ESRFile_ExceptionParsing;
    public static String ESRFile_cannot_read_esr = ch.elexis.core.l10n.Messages.ESRFile_cannot_read_esr;
    public static String ESRFile_esrfile_not_founde = ch.elexis.core.l10n.Messages.ESRFile_esrfile_not_founde;
    public static String ESRFile_file_already_read = ch.elexis.core.l10n.Messages.ESRFile_file_already_read;
    public static String ESRRecordDialog_addedDate = ch.elexis.core.l10n.Messages.Invoice_date_paid;
    public static String ESRRecordDialog_amount = ch.elexis.core.l10n.Messages.ESRRecordDialog_amount;
    public static String ESRRecordDialog_billNotFound = ch.elexis.core.l10n.Messages.ESRRecordDialog_billNotFound;
    public static String ESRRecordDialog_billNr = ch.elexis.core.l10n.Messages.ESRRecordDialog_billNr;
    public static String ESRRecordDialog_bookRecord = ch.elexis.core.l10n.Messages.ESRRecordDialog_bookRecord;
    public static String ESRRecordDialog_booked = ch.elexis.core.l10n.Messages.ESRRecordDialog_booked;
    public static String ESRRecordDialog_bookedDate = ch.elexis.core.l10n.Messages.ESRRecordDialog_bookedDate;
    public static String ESRRecordDialog_changeBillNr = ch.elexis.core.l10n.Messages.ESRRecordDialog_changeBillNr;
    public static String ESRRecordDialog_deleteRecord = ch.elexis.core.l10n.Messages.ESRRecordDialog_deleteRecord;
    public static String ESRRecordDialog_detailsForESRRecord = ch.elexis.core.l10n.Messages.ESRRecordDialog_detailsForESRRecord;
    public static String ESRRecordDialog_dontBookRecord = ch.elexis.core.l10n.Messages.ESRRecordDialog_dontBookRecord;
    public static String ESRRecordDialog_dontchange = ch.elexis.core.l10n.Messages.ESRRecordDialog_dontchange;
    public static String ESRRecordDialog_editRecord = ch.elexis.core.l10n.Messages.ESRRecordDialog_editRecord;
    public static String ESRRecordDialog_esrType = ch.elexis.core.l10n.Messages.ESRRecordDialog_esrType;
    public static String ESRRecordDialog_file = ch.elexis.core.l10n.Messages.ESRRecordDialog_file;
    public static String ESRRecordDialog_noValidBillFound = ch.elexis.core.l10n.Messages.ESRRecordDialog_noValidBillFound;
    public static String ESRRecordDialog_patient = ch.elexis.core.l10n.Messages.Core_Patient;
    public static String ESRRecordDialog_pleaseEnterNewBilNr = ch.elexis.core.l10n.Messages.ESRRecordDialog_pleaseEnterNewBilNr;
    public static String ESRRecordDialog_pleaseSelectPatient = ch.elexis.core.l10n.Messages.ESRRecordDialog_pleaseSelectPatient;
    public static String ESRRecordDialog_readInDate = ch.elexis.core.l10n.Messages.Core_has_read;
    public static String ESRRecordDialog_receivedDate = ch.elexis.core.l10n.Messages.ESRRecordDialog_receivedDate;
    public static String ESRRecordDialog_selectPatient = ch.elexis.core.l10n.Messages.Core_Select_Patient;
    public static String ESRRecordDialog_stornoESR = ch.elexis.core.l10n.Messages.ESRRecordDialog_stornoESR;
    public static String ESRRecordDialog_vESRForBill = ch.elexis.core.l10n.Messages.Invoice_VESR_for_RP;
    public static String ESRRecordDialog_warningEditing = ch.elexis.core.l10n.Messages.ESRRecordDialog_warningEditing;
    public static String ESRView2_accountedDate = ch.elexis.core.l10n.Messages.Invoice_billed;
    public static String ESRView2_addedDate = ch.elexis.core.l10n.Messages.Invoice_date_paid;
    public static String ESRView2_amount = ch.elexis.core.l10n.Messages.ESRView2_amount;
    public static String ESRView2_billNumber = ch.elexis.core.l10n.Messages.ESRView2_billNumber;
    public static String ESRView2_booking = ch.elexis.core.l10n.Messages.ESRView2_booking;
    public static String ESRView2_date = ch.elexis.core.l10n.Messages.ESRView2_date;
    public static String ESRView2_file = ch.elexis.core.l10n.Messages.Core_File;
    public static String ESRView2_loadingESR = ch.elexis.core.l10n.Messages.Core_Load_ESR;
    public static String ESRView2_notbooked = ch.elexis.core.l10n.Messages.ESRView2_notbooked;
    public static String ESRView2_patient = ch.elexis.core.l10n.Messages.Core_Patient;
    public static String ESRView2_readDate = ch.elexis.core.l10n.Messages.Core_has_read;
    public static String ESRView_ESR_finished = ch.elexis.core.l10n.Messages.ESRView_ESR_finished;
    public static String ESRView_booked = ch.elexis.core.l10n.Messages.ESRView_booked;
    public static String ESRView_compulsoryExecution = ch.elexis.core.l10n.Messages.ESRView_compulsoryExecution;
    public static String ESRView_couldnotread = ch.elexis.core.l10n.Messages.ESRView_couldnotread;
    public static String ESRView_errorESR = ch.elexis.core.l10n.Messages.ESRView_errorESR;
    public static String ESRView_errorESR2 = ch.elexis.core.l10n.Messages.Core_Error_Reading_ESR;
    public static String ESRView_errrorESR2 = ch.elexis.core.l10n.Messages.Core_Error_Reading_ESR;
    public static String ESRView_headline = ch.elexis.core.l10n.Messages.ESRView_headline;
    public static String ESRView_interrupted = ch.elexis.core.l10n.Messages.ESRView_interrupted;
    public static String ESRView_isInCompulsoryExecution = ch.elexis.core.l10n.Messages.ESRView_isInCompulsoryExecution;
    public static String ESRView_ispaid = ch.elexis.core.l10n.Messages.ESRView_ispaid;
    public static String ESRView_loadESR = ch.elexis.core.l10n.Messages.Core_Load_ESR;
    public static String ESRView_morethan = ch.elexis.core.l10n.Messages.ESRView_morethan;
    public static String ESRView_not_booked = ch.elexis.core.l10n.Messages.ESRView_not_booked;
    public static String ESRView_paid = ch.elexis.core.l10n.Messages.ESRView_paid;
    public static String ESRView_paymentfor = ch.elexis.core.l10n.Messages.ESRView_paymentfor;
    public static String ESRView_read_ESR = ch.elexis.core.l10n.Messages.ESRView_read_ESR;
    public static String ESRView_read_ESR_explain = ch.elexis.core.l10n.Messages.ESRView_read_ESR_explain;
    public static String ESRView_reading_ESR = ch.elexis.core.l10n.Messages.ESRView_reading_ESR;
    public static String ESRView_rechnung = ch.elexis.core.l10n.Messages.ESRView_rechnung;
    public static String ESRView_selectESR = ch.elexis.core.l10n.Messages.ESRView_selectESR;
    public static String ESRView_showESRData = ch.elexis.core.l10n.Messages.ESRView_showESRData;
    public static String ESRView_storno_for = ch.elexis.core.l10n.Messages.ESRView_storno_for;
    public static String ESRView_toohigh = ch.elexis.core.l10n.Messages.ESRView_toohigh;
    public static String ESRView_vesrfor = ch.elexis.core.l10n.Messages.Invoice_VESR_for_RP;
    public static String ESR_bad_user_defin = ch.elexis.core.l10n.Messages.ESR_bad_user_defin;
    public static String ESR_errorMark = ch.elexis.core.l10n.Messages.ESR_errorMark;
    public static String ESR_esr_invalid = ch.elexis.core.l10n.Messages.ESR_esr_invalid;
    public static String ESR_warning_esr_not_correct = ch.elexis.core.l10n.Messages.ESR_warning_esr_not_correct;
    public static String LoadESRFileHandler_notAssignable = ch.elexis.core.l10n.Messages.LoadESRFileHandler_notAssignable;

}
