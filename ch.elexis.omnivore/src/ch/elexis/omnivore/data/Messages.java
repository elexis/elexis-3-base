/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.omnivore.data.messages"; //$NON-NLS-1$
	public static String DocHandle_documentErrorText1 = null;
	public static String DocHandle_configErrorCaption;
	public static String DocHandle_readErrorHeading;
	public static String DocHandle_configErrorText;
	public static String DocHandle_versionConflictCaption;
	public static String DocHandle_versionConflictText;
	public static String DocHandle_cantReadCaption;
	public static String DocHandle_cantReadMessage;
	public static String DocHandle_dataNotWritten;
	public static String DocHandle_docErrorCaption;
	public static String DocHandle_docErrorMessage;
	public static String DocHandle_execError;
	public static String DocHandle_importErrorCaption;
	public static String DocHandle_importErrorMessage;
	public static String DocHandle_importErrorMessage2;
	public static String DocHandle_noPatientSelected;
	public static String DocHandle_pleaseSelectPatient;
	public static String DocHandle_readErrorCaption;
	public static String DocHandle_readErrorMessage;
	public static String DocHandle_MoveErrorCaption;
	public static String DocHandle_MoveError;
	public static String DocHandle_MoveErrorDestIsDir;
	public static String DocHandle_MoveErrorDestIsFile;
	public static String DocHandle_writeErrorHeading;
	public static String DocumentManagement_contentsMatchNotSupported;
	public static String DocHandle_documentErrorCaption;
	public static String DocHandle_documentErrorText2;
	public static String DocHandle_readErrorText;
	public static String DocHandle_writeErrorCaption;
	public static String xChangeContributor_thisIsAnOmnivoreDoc;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
