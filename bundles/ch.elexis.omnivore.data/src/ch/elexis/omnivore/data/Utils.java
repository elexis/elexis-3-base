/*******************************************************************************
 * Copyright (c) 2017, J. Sigle, Niklaus Giger and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - Initial implementation in a private branch of Elexis 2.1
 *    N. Giger - Reworked for Elexis 3.4 including unit tests
 *    
 *******************************************************************************/

package ch.elexis.omnivore.data;

import static ch.elexis.omnivore.PreferenceConstants.PREFBASE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.MessageFormat;
import org.eclipse.jface.preference.IPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;

public class Utils {
	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	static public File archiveFile(File file, DocHandle dh){
		File newFile = null;
		String SrcPattern = null;
		String DestDir = null;
		
		try {
			for (Integer i = 0; i < Preferences.getOmnivorenRulesForAutoArchiving(); i++) {
				SrcPattern = Preferences.getOmnivoreRuleForAutoArchivingSrcPattern(i);
				DestDir = Preferences.getOmnivoreRuleForAutoArchivingDestDir(i);
				if ((SrcPattern != null) && (DestDir != null)
					&& ((SrcPattern != "" || DestDir != ""))) {
					if (file.getAbsolutePath().contains(SrcPattern)) {
						log.debug("SrcPattern {} found in file.getAbsolutePath() pos {}",
							SrcPattern, i);
						if (DestDir == "") {
							log.debug(
								"DestDir is empty. No more rules will be evaluated for this file. Returning.");
						}
						newFile = new File(DestDir);
						if (newFile.isDirectory()) {
							newFile = new File(DestDir + File.separatorChar + file.getName());
						}
						
						if (newFile.isDirectory()) {
							log.debug("new File {} is a directory ; archiveFile not attempted",
								newFile.getAbsolutePath());
							SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
								MessageFormat.format(Messages.DocHandle_MoveErrorDestIsDir, DestDir,
									file.getName()));
							return null;
						} else {
							if (newFile.isFile()) {
								log.debug("new File {} already exits ; archiveFile not attempted",
									newFile.getAbsolutePath());
								SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,
									MessageFormat.format(Messages.DocHandle_MoveErrorDestIsFile,
										DestDir, file.getName()));
								return null;
							} else {
								log.debug("Will move file {} {} to: {} {}", file.getAbsolutePath(),
									file.exists(), newFile.getAbsolutePath(), newFile.exists());
								if (Files.move(file.toPath(), newFile.toPath(),
									REPLACE_EXISTING) != null) {
									log.debug("Archived incoming file {} to: {}",
										file.getAbsolutePath(), newFile.getAbsolutePath());
									return newFile;
								} else {
									log.debug("Failed archiveFile incoming file {} to: {}",
										file.getAbsolutePath(), newFile.getAbsolutePath());
									// SWTHelper.showError(Messages.DocHandle_MoveErrorCaption,Messages.DocHandle_MoveError);
									return null;
								}
							}
						}
					}
				}
			}
		} catch (Throwable throwable) {
			ExHandler.handle(throwable);
			if (file != null && newFile != null) {
				log.debug("Exception while moving file {} {} to: {} {}", file.getAbsolutePath(),
					file.exists(), newFile.getAbsolutePath(), newFile.exists());
			} else {
				log.debug("Exception while moving file [{}] {} src {} dest {}",
					(file != null) ? file.getAbsolutePath() : "null",
					(file != null) ? file.exists() : "invalid", SrcPattern, DestDir);
			}
			SWTHelper.showError(Messages.DocHandle_MoveErrorCaption, Messages.DocHandle_MoveError);
			return null;
		}
		return newFile;
	}
	
	static private String getFileElement(String element_key, String element_data){
		IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
		return Utils.processFileElement(preferenceStore, element_key, element_data);
	}
	
	static private String processFileElement(IPreferenceStore preferenceStore, String element_key,
		String element_data){
		
		log.debug("processFileElement: element_key=<{}> data <{}>", element_key, element_data);
		StringBuffer element_data_processed = new StringBuffer();
		Integer nCotfRules = Preferences.PREFERENCE_cotf_elements.length;
		for (int i = 0; i < nCotfRules; i++) {
			if (Preferences.PREFERENCE_cotf_elements[i].equals(element_key)) {
				if (element_key.contains("constant")) {
					String search = PREFBASE + Preferences.PREFERENCE_COTF
						+ Preferences.PREFERENCE_cotf_elements[i] + "_"
						+ Preferences.PREFERENCE_cotf_parameters[1];
					String constant = preferenceStore.getString(search).trim();
					log.debug("processFileElement: {} returning constant=<{}>", search, constant);
					if (constant.length() > 0)
						log.debug("processFileElement: {} returning constant=<{}>", search,
							constant);
					return constant;
				} else {
					// Shall we return ANY digits at all for this element, and later on: shall we
					// cut down or extend the processed string to some defined number of digits?
					String snumId = PREFBASE + Preferences.PREFERENCE_COTF
						+ Preferences.PREFERENCE_cotf_elements[i] + "_"
						+ Preferences.PREFERENCE_cotf_parameters[1];
					String snum_digits = preferenceStore.getString(snumId).trim();
					// If the num_digits for this element is empty, then return an empty result -
					// the element is disabled.
					if (snum_digits.isEmpty()) {
						return "";
					}
					
					Integer num_digits = -1;
					if (snum_digits != null) {
						try {
							num_digits = Integer.parseInt(snum_digits);
						} catch (Throwable throwable) {
							// do not consume
						}
					}
					
					// if num_digits for this element is <= 0, then return an empty result - the
					// element is disabled.
					if (num_digits <= 0) {
						return "";
					}
					
					if (num_digits > Preferences.nPreferences_cotf_element_digits_max) {
						num_digits = Preferences.nPreferences_cotf_element_digits_max;
					}
					
					// Remove all characters that shall not appear in the generated filename
					String element_data_processed5 = (element_data
						.replaceAll(java.util.regex.Matcher
							.quoteReplacement(Preferences.cotf_unwanted_chars), "")
						.toString().trim());
					
					// filter out some special unwanted strings from the title that may have eeclipse-javadoc:%E2%98%82=ch.elexis.core.data/%5C/usr%5C/lib%5C/jvm%5C/java-8-oracle%5C/jre%5C/lib%5C/rt.jar%3Cjava.util.regex(Matcher.class%E2%98%83Matcher~quoteReplacement~Ljava.lang.String;%E2%98%82java.lang.Stringntered
					// while importing and partially renaming files
					String element_data_processed4 =
						element_data_processed5.replaceAll("_noa[0-9]+\056[a-zA-Z0-9]{0,3}", ""); // remove
					// filename remainders like _noa635253160443574060.doc
					String element_data_processed3 =
						element_data_processed4.replaceAll("noa[0-9]+\056[a-zA-Z0-9]{0,3}", ""); // remove
					// filename remainders like noa635253160443574060.doc
					String element_data_processed2 = element_data_processed3
						.replaceAll("_omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}", ""); // remove filename remainders like
					// _omni_635253160443574060_vore.pdf
					String element_data_processed1 = element_data_processed2
						.replaceAll("omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}", ""); // remove filename remainders like omni_635253160443574060_vore.pdf
					
					// Limit the length of the result if it exceeds the specified or predefined max
					// number of digits
					if (element_data_processed1.length() > num_digits) {
						element_data_processed1 = element_data_processed1.substring(0, num_digits);
					}
					
					// If a leading fill character is given, and the length of the result is below
					// the specified max_number of digits, then fill it up.
					// Note: We could also check whether the num_digits has been given. Instead, I
					// use the default max num of digits if not.
					String leadId = PREFBASE + Preferences.PREFERENCE_COTF
						+ Preferences.PREFERENCE_cotf_elements[i] + "_"
						+ Preferences.PREFERENCE_cotf_parameters[0];
					String lead_fill_char = preferenceStore.getString(leadId).trim();
					
					if ((lead_fill_char != null) && (lead_fill_char.length() > 0)
						&& (element_data_processed1.length() < num_digits)) {
						lead_fill_char = lead_fill_char.substring(0, 1);
						for (int n = element_data_processed1.length(); n <= num_digits; n++) {
							element_data_processed.append(lead_fill_char);
						}
					}
					element_data_processed.append(element_data_processed1);
					
					// If an add trailing character is given, add one (typically, this would be a
					// space or an underscore)
					String trailId = PREFBASE + Preferences.PREFERENCE_COTF
						+ Preferences.PREFERENCE_cotf_elements[i] + "_"
						+ Preferences.PREFERENCE_cotf_parameters[2];
					String add_trail_char = preferenceStore.getString(trailId).trim();
					
					if ((add_trail_char != null) && (add_trail_char.length() > 0)) {
						add_trail_char = add_trail_char.substring(0, 1);
						element_data_processed.append(add_trail_char);
					}
					log.debug("{} {} {} {} <{}> {} <{>}", i, snumId, snum_digits, leadId,
						lead_fill_char, trailId, add_trail_char);
				}
				log.debug("processFileElement: element_data_processed=<{}>",
					element_data_processed);
				
				return element_data_processed.toString(); // This also breaks the for loop
			} // if ... equals(element_key)
		} // for int i...
		return ""; // default return value, if nothing is defined.
	}
	
	/**
	 * Generate a nice file name for the docHandle in the temp directory Used when double-clicking
	 * or dragging the file <br>
	 * <br>
	 * Can be configured via the setting document exchange..Omnivore <br>
	 * Returns a formatted temporary filename element, observing current settings from the
	 * preference store, also observing default settings and min/max settings for that parameter
	 * 
	 * 
	 * @param dh
	 *            The docHandle (containing title, patient, etc for which we want to generate the
	 *            name
	 * @return The requested filename element as a string.
	 * 
	 * @author Joerg Sigle, reworked for Elexis 3.5 by Niklaus Giger
	 */
	
	public static String createNiceFileName(DocHandle dh){
		StringBuffer tmp = new StringBuffer();
		tmp.append(getFileElement("constant1", ""));
		tmp.append(getFileElement("PID", dh.getPatient().getKuerzel())); //getPatient() liefert in etwa: ch.elexis.com@1234567; getPatient().getId() eine DB-ID; getPatient().getKuerzel() die Patientennummer.
		tmp.append(getFileElement("fn", dh.getPatient().getName()));
		tmp.append(getFileElement("gn", dh.getPatient().getVorname()));
		tmp.append(getFileElement("dob", dh.getPatient().getGeburtsdatum()));
		
		tmp.append(getFileElement("dt", dh.getTitle())); //not more than 80 characters, laut javadoc
		tmp.append(getFileElement("dk", dh.getKeywords()));
		//Da könnten auch noch Felder wie die Document Create Time etc. rein - siehe auch unten, die Methoden getPatient() etc.
		
		tmp.append(getFileElement("dguid", dh.getGUID()));
		
		//N.B.: We may NOT REALLY assume for sure that another filename, derived from a createTempFile() result, where the random portion would be moved forward in the name, may also be guaranteed unique!
		//So *if* we should use createTempFile() to obtain such a filename, we should put constant2 away from configured_temp_filename and put it in the portion provided with "ext", if a unique_temp_id was requested.
		//And, we should probably not cut down the size of that portion, so it would be best to do nothing for that but offer a checkbox.
		
		//Es muss aber auch gar nicht mal unique sein - wenn die Datei schon existiert UND von einem anderen Prozess, z.B. Word, mit r/w geöffnet ist, erscheint ein sauberer Dialog mit einer Fehlermeldung. Wenn sie nicht benutzt wird, kann sie überschrieben werden.
		
		//Der Fall, dass hier auf einem Rechner / von einem User bei dem aus Daten erzeugten Filenamen zwei unterschiedliche Inhalte mit gleichem Namen im gleichen Tempdir gleichzeitig nur r/o geöffnet werden und einander in die Quere kommen, dürfte unwahrscheinlich sein.
		//Wie wohl... vielleicht doch nicht. Wenn da jemand beim selben Patienten den Titel 2x einstellt nach: "Bericht Dr. Müller", und das dann den Filenamen liefert, ist wirklich alles gleich.
		//So we should ... possibly really add some random portion; or use any other property of the file in that filename (recommendation: e.g. like in AnyQuest Server :-)  )
		
		//Ganz notfalls naoch ein Feld mit der Uhrzeit machen... oder die Temp-ID je nach eingestellten num_digits aus den clockticks speisen. Und das File mit try createn, notfalls wiederholen mit anderem clocktick - dann ist das so gut wie ein createTempFile().
		//For now, I compute my own random portion - by creating a random BigInteger with a sufficient number of bits to represent  PreferencePage.nOmnivore_jsPREF_cotf_element_digits_max decimal digits.
		//And I accept the low chance of getting an existing random part, i.e. I don't check the file is already there.
		
		SecureRandom random = new SecureRandom();
		int needed_bits = (int) Math.round(
			Math.ceil(Math.log(Preferences.nPreferences_cotf_element_digits_max) / Math.log(2)));
		tmp.append(getFileElement("random", new BigInteger(needed_bits, random).toString()));
		
		tmp.append(getFileElement("constant2", ""));
		return tmp.toString();
	}
}
