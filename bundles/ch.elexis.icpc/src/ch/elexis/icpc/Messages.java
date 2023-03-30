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
package ch.elexis.icpc;

public class Messages {
	public static String IcpcCode_class_A = ch.elexis.core.l10n.Messages.IcpcCode_class_A;
	public static String IcpcCode_class_B = ch.elexis.core.l10n.Messages.IcpcCode_class_B;
	public static String IcpcCode_class_D = ch.elexis.core.l10n.Messages.IcpcCode_class_D;
	public static String IcpcCode_class_F = ch.elexis.core.l10n.Messages.IcpcCode_class_F;
	public static String IcpcCode_class_H = ch.elexis.core.l10n.Messages.IcpcCode_class_H;
	public static String IcpcCode_class_K = ch.elexis.core.l10n.Messages.IcpcCode_class_K;
	public static String IcpcCode_class_L = ch.elexis.core.l10n.Messages.IcpcCode_class_L;
	public static String IcpcCode_class_N = ch.elexis.core.l10n.Messages.IcpcCode_class_N;
	public static String IcpcCode_class_P = ch.elexis.core.l10n.Messages.IcpcCode_class_P;
	public static String IcpcCode_class_R = ch.elexis.core.l10n.Messages.IcpcCode_class_R;
	public static String IcpcCode_class_S = ch.elexis.core.l10n.Messages.IcpcCode_class_S;
	public static String IcpcCode_class_T = ch.elexis.core.l10n.Messages.IcpcCode_class_T;
	public static String IcpcCode_class_U = ch.elexis.core.l10n.Messages.IcpcCode_class_U;
	public static String IcpcCode_class_W = ch.elexis.core.l10n.Messages.IcpcCode_class_W;
	public static String IcpcCode_class_X = ch.elexis.core.l10n.Messages.IcpcCode_class_X;
	public static String IcpcCode_class_Y = ch.elexis.core.l10n.Messages.IcpcCode_class_Y;
	public static String IcpcCode_class_Z = ch.elexis.core.l10n.Messages.IcpcCode_class_Z;
	public static String IcpcCode_comp_1 = ch.elexis.core.l10n.Messages.IcpcCode_comp_1;
	public static String IcpcCode_comp_2 = ch.elexis.core.l10n.Messages.IcpcCode_comp_2;
	public static String IcpcCode_comp_3 = ch.elexis.core.l10n.Messages.IcpcCode_comp_3;
	public static String IcpcCode_comp_4 = ch.elexis.core.l10n.Messages.IcpcCode_comp_4;
	public static String IcpcCode_comp_5 = ch.elexis.core.l10n.Messages.IcpcCode_comp_5;
	public static String IcpcCode_comp_6 = ch.elexis.core.l10n.Messages.IcpcCode_comp_6;
	public static String IcpcCode_comp_7 = ch.elexis.core.l10n.Messages.IcpcCode_comp_7;
	public static String StartDate = ch.elexis.core.l10n.Messages.StartDate;
	public static String Title = ch.elexis.core.l10n.Messages.Title;
	public static String Number = ch.elexis.core.l10n.Messages.Core_Number;
	public static String Status = ch.elexis.core.l10n.Messages.Status;
	public static String Active = ch.elexis.core.l10n.Messages.Core_IsActive;
	public static String Inactive = ch.elexis.core.l10n.Messages.Inactive;
	public static String EpisodeEditDialog_Title = ch.elexis.core.l10n.Messages.EpisodeEditDialog_Title;
	public static String EpisodeEditDialog_Create = ch.elexis.core.l10n.Messages.EpisodeEditDialog_Create;
	public static String EpisodeEditDialog_Edit = ch.elexis.core.l10n.Messages.EpisodeEditDialog_Edit;
	public static String EpisodeEditDialog_EnterData = ch.elexis.core.l10n.Messages.EpisodeEditDialog_EnterData;
	public static String LoadESRFileHandler_notAssignable = ch.elexis.core.l10n.Messages.LoadESRFileHandler_notAssignable;

	public static final String[] classes = { Messages.IcpcCode_class_A, Messages.IcpcCode_class_B,
			Messages.IcpcCode_class_D, Messages.IcpcCode_class_F, Messages.IcpcCode_class_H, Messages.IcpcCode_class_K,
			Messages.IcpcCode_class_L, Messages.IcpcCode_class_N, Messages.IcpcCode_class_P, Messages.IcpcCode_class_R,
			Messages.IcpcCode_class_S, Messages.IcpcCode_class_T, Messages.IcpcCode_class_U, Messages.IcpcCode_class_W,
			Messages.IcpcCode_class_X, Messages.IcpcCode_class_Y, Messages.IcpcCode_class_Z };
	/*
	 * public static final String[] components_de={ };
	 */
	public static final String[] components = { Messages.IcpcCode_comp_1, Messages.IcpcCode_comp_2,
			Messages.IcpcCode_comp_3, Messages.IcpcCode_comp_4, Messages.IcpcCode_comp_5, Messages.IcpcCode_comp_6,
			Messages.IcpcCode_comp_7 };

	public static String getStatusText(int status) {
		if (status == 1) {
			return Active;
		} else {
			return Inactive;
		}
	}
}
