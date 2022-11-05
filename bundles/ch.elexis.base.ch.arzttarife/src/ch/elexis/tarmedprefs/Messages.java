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
package ch.elexis.tarmedprefs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	public static final String BUNDLE_NAME = "ch.elexis.tarmedprefs.messages"; //$NON-NLS-1$

	public static String RechnungsPrefs_BillPrefs;
	public static String RechnungsPrefs_BillDetails;
	public static String RechnungsPrefs_Treator;
	public static String RechnungsPrefs_Financeinst;
	public static String RechnungsPrefs_post;
	public static String RechnungsPrefs_13;
	public static String RechnungsPrefs_department;
	public static String RechnungsPrefs_POBox;
	public static String RechnungsPrefs_POAccount;
	public static String RechnungsPrefs_bank;
	public static String RechnungsPrefs_bankconnection;
	public static String RechnungsPrefs_trustcenter;
	public static String RechnungsPrefs_Responsible_Doctor;
	public static String RechnungsPrefs_TrustCenterUsed;
	public static String RechnungsPrefs_ImagesToTrustCenter;
	public static String RechnungsPrefs_FontSlip;
	public static String RechnungsPrefs_FontWarning;
	public static String RechnungsPrefs_FontWarning2;
	public static String RechnungsPrefs_FontWarning3;
	public static String RechnungsPrefs_Font;
	public static String RechnungsPrefs_Size;
	public static String RechnungsPrefs_fontCodingLine;
	public static String RechnungsPrefs_SizeCondingLine;
	public static String RechnungsPrefs_Weight;
	public static String RechnungsPrefs_light;
	public static String RechnungsPrefs_normal;
	public static String RechnungsPrefs_bold;
	public static String RechnungsPrefs_horzCorrCodingLine;
	public static String RechnungsPrefs_vertCorrCodingLine;
	public static String RechnungsPrefs_horrzBaseOffset;
	public static String RechnungenPrefs_vertBaseOffset;
	public static String RechnungsPrefs_MandatorDetails;
	public static String RechnungsPrefs_FinanceInst;
	public static String RechnungsPrefs_paymentinst;
	public static String RechnungsPrefs_PleseChooseBank;
	public static String RechnungsPrefs_ChooseBank;
	public static String RechnungsPrefs_ChosseInst;
	public static String RechnungsPrefs_postAccount;
	public static String RechnungsPrefs_InfoPostAccount;
	public static String RechnungsPrefs_MandantType;
	public static String RechnungsPrefs_MandantType_SPECIALIST;
	public static String RechnungsPrefs_MandantType_PRACTITIONER;
	public static String MultiplikatorEditor_from;
	public static String MultiplikatorEditor_5;
	public static String MultiplikatorEditor_add;
	public static String MultiplikatorEditor_14;
	public static String Core_Date_Startdate;
	public static String MultiplikatorEditor_PleaseEnterBeginDate;
	public static String MultiplikatorEditor_NewMultipilcator;
	public static String TarmedPrefs_TPKVG;
	public static String TarmedPrefs_TPUVG;
	public static String TarmedPrefs_TPIV;
	public static String TarmedPrefs_TPMV;
	public static String TarmedRequirements_AccidentDate;
	public static String TarmedRequirements_AccidentNumberName;
	public static String TarmedRequirements_CaseNumberName;
	public static String TarmedRequirements_InsuranceNumberName;
	public static String TarmedRequirements_IntermediateName;
	public static String TarmedRequirements_kskName;
	public static String TarmedRequirements_Law;
	public static String TarmedRequirements_NifName;
	public static String TarmedRequirements_SSNName;
	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
