/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.elexis.stickynotes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "ch.elexis.stickynotes.messages";
    public static String Preferences_BackgroundColor;
    public static String Preferences_ForegroundColor;
    public static String StickyNotesView_NoPatientSelected;
    public static String StickyNotesView_StickyNotesName;
    public static String StickyNotesView_StickyNotesNameDash;

    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
