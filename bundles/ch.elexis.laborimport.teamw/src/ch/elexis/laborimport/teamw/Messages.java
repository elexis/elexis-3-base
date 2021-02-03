/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 * All the rest is done generically. See plug-in elexis-importer.
 * 
 */
package ch.elexis.laborimport.teamw;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.elexis.laborimport.teamw.messages";

  public static String Importer_automatisch;
  public static String Importer_batch_label;
  public static String Importer_batchFehler_error;
  public static String Importer_cancelled;
  public static String Importer_error_archivieren;
  public static String Importer_error_import;
  public static String Importer_error_moveToArchive;
  public static String Importer_ftp_label;
  public static String Importer_import_header;
  public static String Importer_import_message;
  public static String Importer_lab;
  public static String Importer_label_importDirect;
  public static String Importer_label_importFile;
  public static String Importer_leereBatchdatei_error;
  public static String Importer_ok;
  public static String Importer_question_allreadyImported;
  public static String Importer_question_allreadyImported_continue;
  public static String Importer_semaphore_error;
  public static String Importer_title_description;
  public static String ImporterPage_allFiles;
  public static String ImporterPage_browse;
  public static String ImporterPage_file;
  public static String PreferencePage_batchdatei_label;
  public static String PreferencePage_batchscript_label;
  public static String PreferencePage_direktimport_label;
  public static String PreferencePage_ftpserver_label;
  public static String PreferencePage_label_download;
  public static String PreferencePage_label_host;
  public static String PreferencePage_label_password;
  public static String PreferencePage_label_user;
  public static String PreferencePage_title_description;
	public static String PreferencePage_labelDocumentCategory;
  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

