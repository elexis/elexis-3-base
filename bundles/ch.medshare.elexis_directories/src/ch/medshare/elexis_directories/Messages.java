/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package ch.medshare.elexis_directories;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "ch.medshare.elexis_directories.messages";
    public static String WeisseSeitenSearchForm_btn_Suchen;
    public static String WeisseSeitenSearchForm_label_Ort;
    public static String WeisseSeitenSearchForm_label_werWasWo;
    public static String WeisseSeitenSearchView_header_Adresse;
    public static String WeisseSeitenSearchView_header_Name;
    public static String WeisseSeitenSearchView_header_Ort;
    public static String WeisseSeitenSearchView_header_Plz;
    public static String WeisseSeitenSearchView_header_Tel;
    public static String WeisseSeitenSearchView_header_Zusatz;
    public static String WeisseSeitenSearchView_popup_newKontakt;
    public static String WeisseSeitenSearchView_popup_newPatient;
    public static String WeisseSeitenSearchView_tooltip_newKontakt;
    public static String WeisseSeitenSearchView_tooltip_newPatient;

    static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
