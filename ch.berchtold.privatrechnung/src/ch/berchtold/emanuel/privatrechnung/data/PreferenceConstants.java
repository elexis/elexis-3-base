/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.berchtold.emanuel.privatrechnung.data;

public class PreferenceConstants {
	public static final String PLUGIN_ID = "ch.berchtold.emanuel.privatrechnung";
	
	// for different incarantations of private billing systems change the following two lines
	public static final String BillingSystemName = "Berchtold";
	public static final String cfgBase = "privatrechnung_berchtold";
	
	public static final String cfgBank = cfgBase + "/bank";
	public static final String cfgTemplateESR = cfgBase + "/templateESR";
	public static final String cfgTemplateBill = cfgBase + "/templateBill";
	public static final String cfgTemplateBillHeight = cfgBase + "/templateBillHeight";
	public static final String cfgTemplateBill2 = cfgBase + "/templateBill2";
	public static final String cfgTemplateBill2Height = cfgBase + "/templateBillHeight2";
	public static final String esrIdentity = cfgBase + "/esrIdentity";
	public static final String esrUser = cfgBase + "/esrUser";
}
