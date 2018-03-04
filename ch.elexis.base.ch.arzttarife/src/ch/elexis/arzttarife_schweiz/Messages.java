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
package ch.elexis.arzttarife_schweiz;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.arzttarife_schweiz.messages"; //$NON-NLS-1$
	
	public static String LaborleistungDetailDisplay_analyse;
	
	public static String RnPrintView_62;
	
	public static String RnPrintView_firstM;
	
	public static String RnPrintView_getback;
	
	public static String RnPrintView_labpoints;
	
	public static String RnPrintView_medicaments;
	
	public static String RnPrintView_migelpoints;
	
	public static String RnPrintView_no;
	
	public static String RnPrintView_otherpoints;
	
	public static String RnPrintView_page;
	
	public static String RnPrintView_page1;
	
	public static String RnPrintView_physiopoints;
	
	public static String RnPrintView_prepaid;
	
	public static String RnPrintView_remark;
	
	public static String RnPrintView_remarksp;
	
	public static String RnPrintView_secondM;
	
	public static String RnPrintView_sum;
	
	public static String RnPrintView_tarmedBill;
	
	public static String RnPrintView_tarmedPoints;
	
	public static String RnPrintView_tbBill;
	
	public static String RnPrintView_thirdM;
	
	public static String RnPrintView_topay;
	
	public static String RnPrintView_yes;
	
	public static String TarmedDetailDisplay_after;
	
	public static String TarmedDetailDisplay_DigniQual;
	
	public static String TarmedDetailDisplay_DigniQuant;
	
	public static String TarmedDetailDisplay_DoCombine;
	
	public static String TarmedDetailDisplay_DontCombine;
	
	public static String TarmedDetailDisplay_Limits;
	
	public static String TarmedDetailDisplay_max;
	
	public static String TarmedDetailDisplay_MedInter;
	
	public static String TarmedDetailDisplay_NameInternal;
	
	public static String TarmedDetailDisplay_NumbereAss;
	
	public static String TarmedDetailDisplay_PossibleAdd;
	
	public static String TarmedDetailDisplay_per;
	
	public static String TarmedDetailDisplay_Relation;
	
	public static String TarmedDetailDisplay_RiskClass;
	
	public static String TarmedDetailDisplay_Sparte;
	
	public static String TarmedDetailDisplay_TecInter;
	
	public static String TarmedDetailDisplay_TimeAct;
	
	public static String TarmedDetailDisplay_TimeBeforeAfter;
	
	public static String TarmedDetailDisplay_TimeChange;
	
	public static String TarmedDetailDisplay_TimeRoom;
	
	public static String TarmedDetailDisplay_times;
	
	public static String TarmedDetailDisplay_TimeWrite;
	
	public static String TarmedDetailDisplay_TPAss;
	
	public static String TarmedDetailDisplay_TPDoc;
	
	public static String TarmedDetailDisplay_TPTec;
	
	public static String TarmedImporter_couldntConnect;
	public static String TarmedImporter_convertTable;
	public static String TarmedImporter_importLstg;
	public static String TarmedImporter_connecting;
	public static String TarmedOptifier_perSession;
	public static String TarmedOptifier_perSide;
	public static String TarmedOptifier_perDay;
	public static String TarmedOptifier_perWeeks;
	public static String TarmedOptifier_perMonth;
	public static String TarmedOptifier_perYears;
	public static String TarmedOptifier_perCoverage;
	public static String TarmedImporter_deleteOldData;
	public static String TarmedImporter_definitions;
	public static String TarmedImporter_chapter;
	public static String TarmedOptifier_codemax;
	public static String TarmedOptifier_groupmax;
	public static String TarmedOptifier_BadType;
	public static String TarmedOptifier_NoMoreValid;
	
	public static String TarmedOptifier_NotYetValid;
	
	public static String TarmedImporter_singleLst;
	public static String TarmedImporter_enterSource;
	public static String TarmedImporter_successTitle;
	public static String TarmedImporter_successMessage;
	public static String TarmedImporter_updateVerrechnet;
	public static String TarmedImporter_updateBlock;
	public static String TarmedImporter_updateBlockWarning;
	public static String TarmedImporter_updateOldIDEntries;
	
	public static String LaborleistungImporter_AnalyseImport;
	public static String LaborleistungImporter_AnalyzeTariff;
	public static String LaborleistungImporter_pleseEnterFilename;
	
	public static String TarmedDetailDisplay_Validity;
	public static String TarmedDetailDisplay_Validity_Unlimited;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
