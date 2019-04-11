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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.icpc.messages"; //$NON-NLS-1$
	public static String IcpcCode_class_A;
	public static String IcpcCode_class_B;
	public static String IcpcCode_class_D;
	public static String IcpcCode_class_F;
	public static String IcpcCode_class_H;
	public static String IcpcCode_class_K;
	public static String IcpcCode_class_L;
	public static String IcpcCode_class_N;
	public static String IcpcCode_class_P;
	public static String IcpcCode_class_R;
	public static String IcpcCode_class_S;
	public static String IcpcCode_class_T;
	public static String IcpcCode_class_U;
	public static String IcpcCode_class_W;
	public static String IcpcCode_class_X;
	public static String IcpcCode_class_Y;
	public static String IcpcCode_class_Z;
	public static String IcpcCode_comp_1;
	public static String IcpcCode_comp_2;
	public static String IcpcCode_comp_3;
	public static String IcpcCode_comp_4;
	public static String IcpcCode_comp_5;
	public static String IcpcCode_comp_6;
	public static String IcpcCode_comp_7;
	public static String StartDate;
	public static String Title;
	public static String Number;
	public static String Status;
	public static String Active;
	public static String Inactive;
	public static String EpisodeEditDialog_Title;
	public static String EpisodeEditDialog_Create;
	public static String EpisodeEditDialog_Edit;
	public static String EpisodeEditDialog_EnterData;
	public static String LoadESRFileHandler_notAssignable;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
	
	public static final String[] classes = {
		Messages.IcpcCode_class_A, Messages.IcpcCode_class_B, Messages.IcpcCode_class_D,
		Messages.IcpcCode_class_F, Messages.IcpcCode_class_H, Messages.IcpcCode_class_K,
		Messages.IcpcCode_class_L, Messages.IcpcCode_class_N, Messages.IcpcCode_class_P,
		Messages.IcpcCode_class_R, Messages.IcpcCode_class_S, Messages.IcpcCode_class_T,
		Messages.IcpcCode_class_U, Messages.IcpcCode_class_W, Messages.IcpcCode_class_X,
		Messages.IcpcCode_class_Y, Messages.IcpcCode_class_Z
	};
	/*
	 * public static final String[] components_de={ };
	 */
	public static final String[] components = {
		Messages.IcpcCode_comp_1, Messages.IcpcCode_comp_2, Messages.IcpcCode_comp_3,
		Messages.IcpcCode_comp_4, Messages.IcpcCode_comp_5, Messages.IcpcCode_comp_6,
		Messages.IcpcCode_comp_7
	};
	
	public static String getStatusText(int status){
		if (status == 1) {
			return Active;
		} else {
			return Inactive;
		}
	}
}
