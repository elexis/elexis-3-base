/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.base.ch.ebanking.esr.messages"; //$NON-NLS-1$

	public static String ESR_bad_user_defin;
	public static String ESR_errorMark;
	public static String ESR_esr_invalid;
	public static String ESR_warning_esr_not_correct;
	public static String ESRFile_cannot_read_esr;
	public static String ESRFile_esrfile_not_founde;
	public static String ESRFile_ExceptionParsing;
	public static String ESRFile_file_already_read;
	public static String ESRRecordDialog_addedDate;
	public static String ESRRecordDialog_amount;
	public static String ESRRecordDialog_billNotFound;
	public static String ESRRecordDialog_billNr;
	public static String ESRRecordDialog_booked;
	public static String ESRRecordDialog_bookedDate;
	public static String ESRRecordDialog_bookRecord;
	public static String ESRRecordDialog_changeBillNr;
	public static String ESRRecordDialog_deleteRecord;
	public static String ESRRecordDialog_detailsForESRRecord;
	public static String ESRRecordDialog_dontBookRecord;
	public static String ESRRecordDialog_dontchange;
	public static String ESRRecordDialog_editRecord;
	public static String ESRRecordDialog_esrType;
	public static String ESRRecordDialog_file;
	public static String ESRRecordDialog_noValidBillFound;
	public static String ESRRecordDialog_patient;
	public static String ESRRecordDialog_pleaseEnterNewBilNr;
	public static String ESRRecordDialog_pleaseSelectPatient;
	public static String ESRRecordDialog_readInDate;
	public static String ESRRecordDialog_receivedDate;
	public static String ESRRecordDialog_selectPatient;
	public static String ESRRecordDialog_stornoESR;
	public static String ESRRecordDialog_vESRForBill;
	public static String ESRRecordDialog_warningEditing;
	public static String ESRView_booked;
	public static String ESRView_couldnotread;
	public static String ESRView_errorESR;
	public static String ESRView_errorESR2;
	public static String ESRView_errrorESR2;
	public static String ESRView_ESR_finished;
	public static String ESRView_headline;
	public static String ESRView_interrupted;
	public static String ESRView_ispaid;
	public static String ESRView_loadESR;
	public static String ESRView_morethan;
	public static String ESRView_not_booked;
	public static String ESRView_compulsoryExecution;
	public static String ESRView_isInCompulsoryExecution;
	public static String ESRView_paid;
	public static String ESRView_paymentfor;
	public static String ESRView_read_ESR;
	public static String ESRView_read_ESR_explain;
	public static String ESRView_reading_ESR;
	public static String ESRView_rechnung;
	public static String ESRView_selectESR;
	public static String ESRView_showESRData;
	public static String ESRView_storno_for;
	public static String ESRView_toohigh;
	public static String ESRView_vesrfor;
	public static String ESRView2_accountedDate;
	public static String ESRView2_addedDate;
	public static String ESRView2_amount;
	public static String ESRView2_billNumber;
	public static String ESRView2_booking;
	public static String ESRView2_date;
	public static String ESRView2_file;
	public static String ESRView2_loadingESR;
	public static String ESRView2_notbooked;
	public static String ESRView2_patient;
	public static String ESRView2_readDate;
	public static String LoadESRFileHandler_notAssignable;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
