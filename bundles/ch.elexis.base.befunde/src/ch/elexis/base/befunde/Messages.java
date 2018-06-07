/*******************************************************************************
 * Copyright (c) 2005-2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *    $Id: Messwert.java 1185 2006-10-29 15:29:30Z rgw_ch $
 *******************************************************************************/

package ch.elexis.base.befunde;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.elexis.base.befunde.messages"; //$NON-NLS-1$

  public static  String BefundePrefs_enterRenameMessage;
  public static  String BefundePrefs_enterRenameCaption;
  public static String ACLContributor_addMesswertACLName;
  public static String ACLContributor_messwertACLName;
  public static String ACLContributor_messwertRubrikACLName;
  public static String BefundePrefs_add;
  public static String BefundePrefs_deleteText;
  public static String BefundePrefs_renameFinding;
  public static String BefundePrefs_enterNameCaption;
  public static String BefundePrefs_enterNameMessage;
  public static String BefundePrefs_dotEndingNameNotAllowed;
  public static String DataAccessor_0;
  public static String DataAccessor_data;
  public static String DataAccessor_dataInBefundePlugin;
  public static String DataAccessor_dateExpected;
  public static String DataAccessor_invalidFieldIndex;
  public static String DataAccessor_invalidFieldName;
  public static String DataAccessor_invalidParameter;
  public static String DataAccessor_notFound;
  public static String DataAccessor_first;
  public static String DataAccessor_last;
  public static String DataAccessor_date;
  public static String DataAccessor_all;
  public static String EditFindingDialog_captionBefundEditDlg;
  public static String EditFindingDialog_enterTextForBefund;
  public static String EditFindingDialog_noPatientSelected;
  public static String FindingsView_addNewMeasure;
  public static String FindingsView_deleteActionCaption;
  public static String FindingsView_deleteActionToolTip;
  public static String FindingsView_deleteConfirmCaption;
  public static String FindingsView_deleteConfirmMessage;
  public static String FindingsView_editActionCaption;
  public static String FindingsView_editActionToolTip;
  public static String FindingsView_noPatientSelected;
  public static String FindingsView_printActionCaptiob;
  public static String FindingsView_printActionMessage;
  public static String Messwert_couldNotCreateTable;
  public static String Messwert_valuesError;
  public static String MesswerteView_date;
  public static String MesswerteView_enterNewEntry;
  public static String MesswerteView_enterNewValue;
  public static String MesswerteView_new;
  public static String MesswerteView_noPatSelected;
  public static String PrefsPage_enterCalculationForThis;
  public static String PrefsPage_multilineCaption;
  public static String PrefsPage_warningConfirmMessage;
  public static String PrefsPage_warningNotUndoableCaption;
  public static String PrefsPage_warningConfirmRename;
  public static String PrintFindingsDialog_messwerteCaption;
  public static String PrintFindingsDialog_printMesswerteMessage;
  public static String PrintFindingsDialog_printMesswerteTitle;
  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

