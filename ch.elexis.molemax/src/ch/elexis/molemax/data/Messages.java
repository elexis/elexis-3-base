/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.data;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.molemax.data.messages"; //$NON-NLS-1$
	public static String MolemaxACL_changeImages;
	public static String MolemaxACL_seeImages;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
