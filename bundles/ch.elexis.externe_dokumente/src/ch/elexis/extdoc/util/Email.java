/*******************************************************************************
 * Copyright (c) 2013 Niklaus Giger and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger - Initial implementation
 *
 *******************************************************************************/
package ch.elexis.extdoc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Patient;
import ch.elexis.extdoc.preferences.PreferenceConstants;

@SuppressWarnings("deprecation")
public class Email {

	private static Logger logger = null;

	/**
	 * Private helper to encoding for mailto URL
	 *
	 * @param p String to encodes
	 * @return URL-encoded string
	 */
	private static String enc(String p) {
		if (p == null)
			p = StringUtils.EMPTY;
		try {
			return URLEncoder.encode(p, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Save text to system clipboard
	 */

	public static void saveTextToClipboard(String body) {
		Clipboard clipboard = new Clipboard(UiDesk.getDisplay());
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { body }, new Transfer[] { textTransfer });
		clipboard.dispose();

	}

	/**
	 * Helper function to get all consultations of a given patients.
	 *
	 * Copied from private function ch.elexis.util.AllDataAccessor to avoid a new
	 * version of the elexis core in 2.1.7
	 *
	 * TODO: Should be moved somewhere to the elexis-core
	 */
	public static String getEmailPreface(Patient patient) {
		StringBuilder sb = new StringBuilder();
		String s;
		sb.append(patient.getName() + StringUtils.SPACE);
		sb.append(patient.getVorname() + StringUtils.SPACE);
		sb.append(patient.getPostAnschrift(false) + StringUtils.SPACE);
		sb.append(patient.get(Patient.FLD_PHONE1) + StringUtils.SPACE);
		sb.append(patient.getNatel() + StringUtils.SPACE);
		sb.append(patient.getMailAddress() + StringUtils.SPACE);
		return sb.toString();
	}

	/**
	 * Helper function to get all consultations of a given patients.
	 *
	 * Copied from private function ch.elexis.util.AllDataAccessor to avoid a new
	 * version of the elexis core in 2.1.7
	 *
	 * TODO: Should be moved somewhere to the elexis-core
	 */
	public static String getAllKonsultations(Patient patient) {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	/**
	 * Returns true if we are running under Windows
	 */
	public static boolean onWindows() {
		return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
	}

	public static void runExternalProgram(String app, String params) {
		String cmd = app + StringUtils.SPACE + params;
		logger.info(cmd);
		try {
			File temp = File.createTempFile("batch", ".cmd");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write("\"" + app + "\" " + params);
			bw.close();
			temp.setExecutable(true);
			logger.info("will call " + temp.getAbsolutePath()); //$NON-NLS-1$
			if (onWindows()) {
				Runtime.getRuntime().exec("cmd /c " + temp.getAbsolutePath());
			} else {
				Runtime.getRuntime().exec(temp.getAbsolutePath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens the configured application to send an email
	 */
	public static void openMailApplication(String to, String subject, String body, List<File> attachments) {
		logger = LoggerFactory.getLogger("ch.elexis.extdoc");

		if (subject == null)
			subject = CoreHub.localCfg.get(PreferenceConstants.CONCERNS, "Überweisung");
		// quote for programs with white spaces in file
		String app = CoreHub.localCfg.get(PreferenceConstants.EMAIL_PROGRAM, "mailto:");
		String params = StringUtils.EMPTY;
		try {
			if (app.toLowerCase().indexOf("outlook") >= 0) {
				params += " --composer --subject '" + subject + "'";
				// Did not find a way to compose a body for outlook
				if (attachments != null) {
					for (File f : attachments) {
						params += " /a \"" + f.getAbsolutePath() + "\"";
					}
				}
				if (to != null && to.length() > 0)
					params += StringUtils.SPACE + to;
				runExternalProgram(app, params);
			} else if (app.toLowerCase().indexOf("kmail") >= 0) {
				params += " --composer --subject '" + subject + "'";
				File temp = File.createTempFile("message", ".tmp");
				BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
				bw.write(body);
				bw.close();
				params += " --msg " + temp.getAbsolutePath();
				if (attachments != null) {
					for (File f : attachments) {
						params += " --attach 'file://" + f.getAbsolutePath() + "'";
					}
				}
				if (to != null && to.length() > 0)
					params += StringUtils.SPACE + to;
				runExternalProgram(app, params);
			} else if (app.toLowerCase().indexOf("thunderbird") >= 0 || app.toLowerCase().indexOf("icedove") >= 0) {
				params += " -compose \"";
				if (to != null && to.length() > 0)
					params = " to='" + to + "',";
				params += "subject=\"" + subject + '"';
				params += ",body='Bitte Ctrl-V benutzen, um Konsultationen einzufügen'";
				if (attachments != null && attachments.size() > 0) {
					params += ",attachment='";
					for (File f : attachments) {
						// Quoted for spaces in filename
						params += '"' + f.getAbsolutePath() + "\",";
					}
					// remove trailing ','
					params = params.substring(0, params.length() - 1);
					params += "'";
				}
				params += "\"";
				saveTextToClipboard(body);
				runExternalProgram(app, params);
			} else {
				// Default. We are using the mailto URL see: http://www.ietf.org/rfc/rfc6068.txt
				if (to != null && to.length() > 0)
					app += "?to=" + enc(to);
				else
					app += "?";
				app += "subject=" + enc(subject);
				app += "&body=" + enc(body);
				saveTextToClipboard(body);
				runExternalProgram(app, StringUtils.EMPTY);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
