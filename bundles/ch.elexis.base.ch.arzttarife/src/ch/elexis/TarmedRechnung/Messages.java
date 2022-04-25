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
package ch.elexis.TarmedRechnung;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.TarmedRechnung.messages"; //$NON-NLS-1$
	public static String RechnungsDrucker_AllFinishedNoErrors;
	public static String RechnungsDrucker_AskSaveForTrustCenter;
	public static String RechnungsDrucker_Couldntbeprintef;
	public static String RechnungsDrucker_CouldntOpenPrintView;
	public static String RechnungsDrucker_Directory;
	public static String RechnungsDrucker_DirNameMissingCaption;
	public static String RechnungsDrucker_DirnameMissingText;
	public static String RechnungsDrucker_ErrorsWhiilePrintingAdvice;
	public static String RechnungsDrucker_ErrorsWhilePrinting;
	public static String RechnungsDrucker_FileForTrustCenter;
	public static String RechnungsDrucker_PrintAsTarmed;
	public static String RechnungsDrucker_PrintingBills;
	public static String RechnungsDrucker_PrintingFinished;
	public static String RechnungsDrucker_TheBill;
	public static String RechnungsDrucker_toPrinter;
	public static String RechnungsDrucker_WithESR;
	public static String RechnungsDrucker_WithForm;
	public static String RechnungsDrucker_IgnoreFaults;
	public static String RechnungsDrucker_MessageErrorInternal;
	public static String RechnungsDrucker_MessageErrorWhilePrinting;
	public static String ResponseAnalyzer_56;
	public static String ResponseAnalyzer_57;
	public static String ResponseAnalyzer_58;
	public static String ResponseAnalyzer_59;
	public static String ResponseAnalyzer_60;
	public static String ResponseAnalyzer_61;
	public static String ResponseAnalyzer_62;
	public static String ResponseAnalyzer_63;
	public static String ResponseAnalyzer_64;
	public static String ResponseAnalyzer_65;
	public static String ResponseAnalyzer_66;
	public static String ResponseAnalyzer_67;
	public static String ResponseAnalyzer_68;
	public static String ResponseAnalyzer_69;
	public static String ResponseAnalyzer_70;
	public static String ResponseAnalyzer_71;
	public static String ResponseAnalyzer_72;
	public static String ResponseAnalyzer_73;
	public static String ResponseAnalyzer_74;
	public static String ResponseAnalyzer_75;
	public static String ResponseAnalyzer_BillIsNotKnown;
	public static String ResponseAnalyzer_BillNumber;
	public static String ResponseAnalyzer_Code;
	public static String ResponseAnalyzer_Date;
	public static String ResponseAnalyzer_ErrorCode;
	public static String ResponseAnalyzer_Intermediate;
	public static String ResponseAnalyzer_MoreInformationsRequested;
	public static String ResponseAnalyzer_NotDeclaredCorrectly;
	public static String ResponseAnalyzer_Patient;
	public static String ResponseAnalyzer_Pending;
	public static String ResponseAnalyzer_PleaseResend;
	public static String ResponseAnalyzer_Receiver;
	public static String ResponseAnalyzer_State;
	public static String ResponseAnalyzer_State2;
	public static String ResponseAnalyzer_StateRejected;
	public static String Validator_NoCase;
	public static String Validator_NoDiagnosis;
	public static String Validator_NoEAN;
	public static String Validator_NoEAN2;
	public static String Validator_NoMandator;
	public static String Validator_NoName;
	public static String XMLExporter_AHVInvalid;
	public static String XMLExporter_Change;
	public static String XMLExporter_CouldNotWriteFile;
	public static String XMLExporter_Currency;
	public static String XMLExporter_ErroneusBill;
	public static String XMLExporter_ErrorCaption;
	public static String XMLExporter_ErrorInBill;
	public static String XMLExporter_IVCaseNumberInvalid;
	public static String XMLExporter_MandatorErrorCaption;
	public static String XMLExporter_MandatorErrorText;
	public static String XMLExporter_MandatorErrorEsr;
	public static String XMLExporter_NIFInvalid;
	public static String XMLExporter_NoPatientText;
	public static String XMLExporter_NoPostal;
	public static String XMLExporter_PleaseEnterOutputDirectoryForBills;
	public static String XMLExporter_ReadErrorCaption;
	public static String XMLExporter_ReadErrorText;
	public static String XMLExporter_StornoImpossibleCaption;
	public static String XMLExporter_StornoImpossibleText;
	public static String XMLExporter_SumMismatch;
	public static String XMLExporter_TarmedForTrustCenter;
	public static String XMLExporter_unknown;
	public static String XMLExporter_Unknown;
	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
