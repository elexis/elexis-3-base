/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG; Portions Copyright (c) 2013 Joerg Sigle   *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/
 
/*
 * Last changes made by $Author: andreas $, $Date: 2006-08-07 13:09:58 +0200 (Mo, 07 Aug 2006) $
 */
package ag.ion.noa4e.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * Native language binding.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 9195 $
 */
public class Messages extends NLS {
  
  private static final String BUNDLE_NAME = "ag.ion.noa4e.internal.ui.preferences.messages"; //$NON-NLS-1$

  private Messages() {
  }

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  public static String LocalOfficeApplicationPreferencesPage_link_define_text;
  public static String LocalOfficeApplicationPreferencesPage_label_application_properties_text;
  public static String LocalOfficeApplicationPreferencesPage_column_name_text;
  public static String LocalOfficeApplicationPreferencesPage_column_value_text;
  
  public static String LocalOfficeApplicationPreferencesPage_description_configure_application;
  public static String LocalOfficeApplicationPreferencesPage_label_application_home;


  public static String LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_title;
  public static String LocalOfficeApplicationPreferencesPage_dialog_restart_workbench_message;
  public static String LocalOfficeApplicationPreferencesPage_prevent_termination_lable;
 
  public static String LocalOfficeApplicationPreferencesPage_label_timeout_bootstrap_connect;   //20130310js timeout made configurable for the loop found in bootstrap*.jar that would originally stop a connection attempt after 500 tries
  public static String LocalOfficeApplicationPreferencesPage_label_timeout_threaded_watchdog;	//20130310js timeout made configurable for the threaded watchdog timer added in 1.4.x by js

  public static String LocalOfficeApplicationPrefrencesPage_construction_of_temporary_filename;   //20130420js: noatext_jsl 1.4.9 -> 1.4.10: Adopt configurability of meaningful temporary filename from omnivore_js 1.4.4:
  public static String LocalOfficeApplicationPrefrencesPage_cotf_constant1;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_pid;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_fn;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_gn;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_dob;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_fill_lead_char;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_num_digits;
  public static String LocalOfficeApplicationPrefrencesPage_cotf_add_trail_char;
}
