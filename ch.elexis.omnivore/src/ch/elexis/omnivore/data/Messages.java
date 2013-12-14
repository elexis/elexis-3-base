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
	public static String DocHandleMoveErrorCaption;
	public static String DocHandleMoveError;
	public static String DocHandleMoveErrorDestIsDir;
	public static String DocHandleMoveErrorDestIsFile;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
