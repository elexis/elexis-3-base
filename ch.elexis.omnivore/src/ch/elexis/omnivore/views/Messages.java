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

package ch.elexis.omnivore.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.omnivore.views.messages"; //$NON-NLS-1$
	public static String FileImportDialog_importFileCaption;
	public static String FileImportDialog_importFileText;
	public static String FileImportDialog_keywordsLabel;
	public static String FileImportDialog_titleLabel;
	public static String OmnivoreView_dateColumn;
	public static String OmnivoreView_deleteActionCaption;
	public static String OmnivoreView_deleteActionToolTip;
	public static String OmnivoreView_editActionCaption;
	public static String OmnivoreView_editActionTooltip;
	public static String OmnivoreView_importActionCaption;
	public static String OmnivoreView_importActionToolTip;
	public static String OmnivoreView_keywordsColumn;
	public static String OmnivoreView_reallyDeleteCaption;
	public static String OmnivoreView_reallyDeleteContents;
	public static String OmnivoreView_titleColumn;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
