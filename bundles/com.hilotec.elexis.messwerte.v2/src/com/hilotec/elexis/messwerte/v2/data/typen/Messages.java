/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data.typen;

import org.eclipse.osgi.util.NLS;

public class Messages {
	public static String MesswertTypBool_No = ch.elexis.core.l10n.Messages.Corr_No;
	public static String MesswertTypBool_Yes = ch.elexis.core.l10n.Messages.Core_Yes;
	public static String MesswertTypNum_CastFailure = ch.elexis.core.l10n.Messages.MesswertTypNum_CastFailure;
	public static String MesswertTypScale_CastFailure = ch.elexis.core.l10n.Messages.MesswertTypScale_CastFailure;
	static {
		// initialize resource bundle
		NLS.initializeMessages("com.hilotec.elexis.messwerte.v2.data.typen.messages", //$NON-NLS-1$
				Messages.class);
	}

}
