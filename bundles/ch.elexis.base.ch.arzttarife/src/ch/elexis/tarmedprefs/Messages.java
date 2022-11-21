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

public class Messages{
	public static final String BUNDLE_NAME = "ch.elexis.tarmedprefs.messages"; //$NON-NLS-1$

    public static String RechnungsPrefs_BillPrefs = ch.elexis.core.l10n.Messages.RechnungsPrefs_BillPrefs;
    public static String RechnungsPrefs_BillDetails = ch.elexis.core.l10n.Messages.RechnungsPrefs_BillDetails;
    public static String RechnungsPrefs_Treator = ch.elexis.core.l10n.Messages.RechnungsPrefs_Treator;
    public static String RechnungsPrefs_Financeinst = ch.elexis.core.l10n.Messages.Core_Financial_Institution;
    public static String RechnungsPrefs_post = ch.elexis.core.l10n.Messages.RechnungsPrefs_post;
    public static String RechnungsPrefs_13 = ch.elexis.core.l10n.Messages.RechnungsPrefs_13;
    public static String RechnungsPrefs_department = ch.elexis.core.l10n.Messages.Core_Departement;
    public static String RechnungsPrefs_POBox = ch.elexis.core.l10n.Messages.Core_Post_Office_Box;
    public static String RechnungsPrefs_POAccount = ch.elexis.core.l10n.Messages.Core_Postkonto;
    public static String RechnungsPrefs_bank = ch.elexis.core.l10n.Messages.RechnungsPrefs_bank;
    public static String RechnungsPrefs_bankconnection = ch.elexis.core.l10n.Messages.RechnungsPrefs_bankconnection;
    public static String RechnungsPrefs_trustcenter = ch.elexis.core.l10n.Messages.RechnungsPrefs_trustcenter;
    public static String RechnungsPrefs_Responsible_Doctor = ch.elexis.core.l10n.Messages.RechnungsPrefs_Responsible_Doctor;
    public static String RechnungsPrefs_TrustCenterUsed = ch.elexis.core.l10n.Messages.RechnungsPrefs_TrustCenterUsed;
    public static String RechnungsPrefs_ImagesToTrustCenter = ch.elexis.core.l10n.Messages.RechnungsPrefs_ImagesToTrustCenter;
    public static String RechnungsPrefs_FontSlip = ch.elexis.core.l10n.Messages.RechnungsPrefs_FontSlip;
    public static String RechnungsPrefs_FontWarning = ch.elexis.core.l10n.Messages.RechnungsPrefs_FontWarning;
    public static String RechnungsPrefs_FontWarning2 = ch.elexis.core.l10n.Messages.RechnungsPrefs_FontWarning2;
    public static String RechnungsPrefs_FontWarning3 = ch.elexis.core.l10n.Messages.RechnungsPrefs_FontWarning3;
    public static String RechnungsPrefs_Font = ch.elexis.core.l10n.Messages.RechnungsPrefs_Font;
    public static String RechnungsPrefs_Size = ch.elexis.core.l10n.Messages.RechnungsPrefs_Size;
    public static String RechnungsPrefs_fontCodingLine = ch.elexis.core.l10n.Messages.RechnungsPrefs_fontCodingLine;
    public static String RechnungsPrefs_SizeCondingLine = ch.elexis.core.l10n.Messages.RechnungsPrefs_SizeCondingLine;
    public static String RechnungsPrefs_Weight = ch.elexis.core.l10n.Messages.RechnungsPrefs_Weight;
    public static String RechnungsPrefs_light = ch.elexis.core.l10n.Messages.RechnungsPrefs_light;
    public static String RechnungsPrefs_normal = ch.elexis.core.l10n.Messages.Core_Normal;
    public static String RechnungsPrefs_bold = ch.elexis.core.l10n.Messages.RechnungsPrefs_bold;
    public static String RechnungsPrefs_horzCorrCodingLine = ch.elexis.core.l10n.Messages.RechnungsPrefs_horzCorrCodingLine;
    public static String RechnungsPrefs_vertCorrCodingLine = ch.elexis.core.l10n.Messages.RechnungsPrefs_vertCorrCodingLine;
    public static String RechnungsPrefs_horrzBaseOffset = ch.elexis.core.l10n.Messages.RechnungsPrefs_horrzBaseOffset;
    public static String RechnungenPrefs_vertBaseOffset = ch.elexis.core.l10n.Messages.RechnungenPrefs_vertBaseOffset;
    public static String RechnungsPrefs_MandatorDetails = ch.elexis.core.l10n.Messages.RechnungsPrefs_MandatorDetails;
    public static String RechnungsPrefs_FinanceInst = ch.elexis.core.l10n.Messages.Core_Financial_Institution;
    public static String RechnungsPrefs_paymentinst = ch.elexis.core.l10n.Messages.RechnungsPrefs_paymentinst;
    public static String RechnungsPrefs_PleseChooseBank = ch.elexis.core.l10n.Messages.RechnungsPrefs_PleseChooseBank;
    public static String RechnungsPrefs_ChooseBank = ch.elexis.core.l10n.Messages.RechnungsPrefs_ChooseBank;
    public static String RechnungsPrefs_ChosseInst = ch.elexis.core.l10n.Messages.RechnungsPrefs_ChosseInst;
    public static String RechnungsPrefs_postAccount = ch.elexis.core.l10n.Messages.Core_Postkonto;
    public static String RechnungsPrefs_InfoPostAccount = ch.elexis.core.l10n.Messages.RechnungsPrefs_InfoPostAccount;
    public static String RechnungsPrefs_MandantType = ch.elexis.core.l10n.Messages.RechnungsPrefs_MandantType;
    public static String RechnungsPrefs_MandantType_SPECIALIST = ch.elexis.core.l10n.Messages.Core_Doctor_Specialist;
    public static String RechnungsPrefs_MandantType_PRACTITIONER = ch.elexis.core.l10n.Messages.RechnungsPrefs_MandantType_PRACTITIONER;
    public static String MultiplikatorEditor_from = ch.elexis.core.l10n.Messages.MultiplikatorEditor_from;
    public static String MultiplikatorEditor_5 = ch.elexis.core.l10n.Messages.Literal_Colon;
    public static String MultiplikatorEditor_add = ch.elexis.core.l10n.Messages.Core_Add_ellipsis;
    public static String MultiplikatorEditor_14 = ch.elexis.core.l10n.Messages.Literal_Colon;
    public static String Core_Date_Startdate = ch.elexis.core.l10n.Messages.Core_Date_Startdate;
    public static String MultiplikatorEditor_PleaseEnterBeginDate = ch.elexis.core.l10n.Messages.MultiplikatorEditor_PleaseEnterBeginDate;
    public static String MultiplikatorEditor_NewMultipilcator = ch.elexis.core.l10n.Messages.MultiplikatorEditor_NewMultipilcator;
    public static String TarmedPrefs_TPKVG = ch.elexis.core.l10n.Messages.TarmedPrefs_TPKVG;
    public static String TarmedPrefs_TPUVG = ch.elexis.core.l10n.Messages.TarmedPrefs_TPUVG;
    public static String TarmedPrefs_TPIV = ch.elexis.core.l10n.Messages.TarmedPrefs_TPIV;
    public static String TarmedPrefs_TPMV = ch.elexis.core.l10n.Messages.TarmedPrefs_TPMV;
    public static String TarmedRequirements_AccidentDate = ch.elexis.core.l10n.Messages.TarmedRequirements_AccidentDate;
    public static String TarmedRequirements_AccidentNumberName = ch.elexis.core.l10n.Messages.Core_Accidentnumber;
    public static String TarmedRequirements_CaseNumberName = ch.elexis.core.l10n.Messages.TarmedRequirements_CaseNumberName;
    public static String TarmedRequirements_InsuranceNumberName = ch.elexis.core.l10n.Messages.Core_Insurance_Number;
    public static String TarmedRequirements_IntermediateName = ch.elexis.core.l10n.Messages.TarmedRequirements_IntermediateName;
    public static String TarmedRequirements_kskName = ch.elexis.core.l10n.Messages.TarmedRequirements_kskName;
    public static String TarmedRequirements_Law = ch.elexis.core.l10n.Messages.Core_Law_Name;
    public static String TarmedRequirements_NifName = ch.elexis.core.l10n.Messages.Core_NIF;
    public static String TarmedRequirements_SSNName = ch.elexis.core.l10n.Messages.TarmedRequirements_SSNName;

}
