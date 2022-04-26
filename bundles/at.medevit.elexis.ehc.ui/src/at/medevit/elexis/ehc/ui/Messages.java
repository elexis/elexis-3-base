/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "at.medevit.elexis.ehc.ui.messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String Btn_Display;
	public static String Dlg_ResolveError;
	public static String Dlg_ResolveErrorMsg;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}