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
 * Copyright 2003-2006 by IOn AG                                            *
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
package ag.ion.noa4e.ui.wizards.application;

import org.eclipse.osgi.util.NLS;

/**
 * Native language binding.
 * 
 * @author Andreas Bröker
 * @version $Revision: 9195 $
 */
public class Messages extends NLS {
  
  private static final String BUNDLE_NAME = "ag.ion.noa4e.ui.wizards.application.messages"; //$NON-NLS-1$

  private Messages() {
  }

  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  public static String LocalApplicationWizard_title;
  
  public static String LocalApplicationWizardDefinePage_title;
  public static String LocalApplicationWizardDefinePage_description;
  public static String LocalApplicationWizardDefinePage_section_application_test;
  public static String LocalApplicationWizardDefinePage_section_application_description;
  public static String LocalApplicationWizardDefinePage_label_home_text;
  public static String LocalApplicationWizardDefinePage_link_browse_text;
  public static String LocalApplicationWizardDefinePage_directory_dialog_text;
  public static String LocalApplicationWizardDefinePage_directory_dialog_message;
  public static String LocalApplicationWizardDefinePage_section_available_applications_text;
  public static String LocalApplicationWizardDefinePage_section_available_applications_description;
  public static String LocalApplicationWizardDefinePage_column_product_text;
  public static String LocalApplicationWizardDefinePage_column_home_text;
  public static String LocalApplicationWizardDefinePage_message_warning_path_invalid;
  public static String LocalApplicationWizardDefinePage_message_warning_beta_release;
  public static String LocalApplicationWizardDefinePage_message_warning_version_old;
  public static String LocalApplicationWizardDefinePage_message_error_path_not_available;

}