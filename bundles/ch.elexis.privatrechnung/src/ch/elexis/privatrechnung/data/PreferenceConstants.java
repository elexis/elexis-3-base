/*******************************************************************************
 * Copyright (c) 2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.privatrechnung.data;

public class PreferenceConstants {

	// for different incarantations of private billing systems change the following
	// two lines
	public static final String BillingSystemName = "Privatrechnung";
	public static final String cfgBase = "privatrechnung_basic";

	public static final String cfgBank = cfgBase + "/bank";
	public static final String cfgTemplateESR = cfgBase + "/templateESR";
	public static final String cfgTemplateBill = cfgBase + "/templateBill";
	public static final String esrIdentity = cfgBase + "/esrIdentity";
	public static final String esrUser = cfgBase + "/esrUser";

	public static final String DEFAULT_TEMPLATE_BILL = "privatrechnung_S2";
	public static final String DEFAULT_TEMPLATE_ESR = "privatrechnung_ESR";
}
