/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.laborimport.LG1;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.laborimport.lg1.messages";
	public static String PreferencePage_DownloadDir;
	public static String PreferencePage_JMedTrasferJni;
	public static String PreferencePage_JMedTrasferJar;
	public static String LabOrderAction_errorMessageNoFallSelected;
	public static String LabOrderAction_errorMessageNoPatientSelected;
	public static String LabOrderAction_errorTitleCannotCreateHL7;
	public static String LabOrderAction_errorTitleCannotShowURL;
	public static String LabOrderAction_errorTitleNoFallSelected;
	public static String LabOrderAction_errorTitleNoPatientSelected;
	public static String LabOrderAction_infoMessageLabOrderFinshed;
	public static String LabOrderAction_infoTitleLabOrderFinshed;
	public static String LabOrderAction_nameAction;
	public static String LabOrderAction_receivingApplication;
	public static String LabOrderAction_receivingFacility;
	public static String Lg1PreferencePage_labelUploadDir;

	static { // load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
