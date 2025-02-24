/*******************************************************************************
 * Copyright (c) 2011, Niklaus Giger <niklaus.giger@member.fsf.org>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Niklaus Giger <niklaus.giger@member.fsf.org> - initial implementation
 *
 *******************************************************************************/

package ch.elexis.extdoc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.Log;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.dialogs.MoveIntoSubDirsDialog;
import ch.elexis.extdoc.preferences.PreferenceConstants;

/***
 *
 * @author Niklaus Giger <niklaus.giger@member.fsf.org>
 *
 */
public class MatchPatientToPath {

	private Patient pat;
	private static Log logger = Log.get(MoveIntoSubDirsDialog.class.getName());

	/***
	 * Constructor: Create a new object for this patient
	 *
	 * @param patient
	 */
	public MatchPatientToPath(Patient patient) {
		pat = patient;
	}

	static private void logAndConsole(String logEntry, int level) {
		logger.log(logEntry, level);
		System.out.println(logEntry);
	}

	public static String[] getFirstAndFamilyNameFromPathOldConvention(String fullPathname) {
		String basename = new File(fullPathname).getName();
		if (basename.length() <= 6)
			return new String[] { StringUtils.EMPTY, basename.replaceFirst(" *$", StringUtils.EMPTY) };
		String lastname = cleanName(basename.substring(0, 6).replaceFirst(" *$", StringUtils.EMPTY));
		basename = basename.substring(6);
		String separatedBySpace = ".*[. \\s].*";
		if (basename.matches(separatedBySpace)) {
			return new String[] { basename.replaceFirst("[. \\s].*", StringUtils.EMPTY), lastname };
		} else {
			return new String[] { basename, lastname };
		}
	}

	static public boolean MoveIntoSubDir(String path) {
		Patient pat = MatchPatientToPath.filenameBelongsToSomePatient(path);
		if (pat == null) {
			logAndConsole("No unique patient found for " + path, ch.rgw.tools.Log.WARNINGS); //$NON-NLS-1$
			return false;
		} else {
			MatchPatientToPath m = new MatchPatientToPath(pat);
			String dest = m.ShouldBeMovedToThisSubDir(path, pat.getGeburtsdatum());
			File destDir = new File(dest).getParentFile();
			if (!destDir.exists() && !destDir.mkdir()) {
				logAndConsole(String.format("Could not create subdir %1s created for patient %2s", //$NON-NLS-1$
						destDir.getAbsolutePath(), pat.toString()), ch.rgw.tools.Log.WARNINGS);
				return false;
			}

			logAndConsole(String.format("MoveIntoSubDir: %1s renameTo %2s", path, dest), //$NON-NLS-1$
					ch.rgw.tools.Log.INFOS);
			boolean success = new File(path).renameTo(new File(dest));
			return true;
		}
	}

	private static String basenameMustBeginWith(String lastname, String firstname) {
		firstname = firstToken(firstname);

		lastname = cleanName(lastname);
		firstname = cleanName(firstname);

		String shortLastname;

		if (lastname.length() >= 6) {
			// Nachname ist lang genug
			shortLastname = lastname.substring(0, 6);
		} else {
			// Nachname ist zu kurz, mit Leerzeichen auffuellen
			StringBuilder sb = new StringBuilder();
			sb.append(lastname);
			while (sb.length() < 6) {
				sb.append(StringUtils.SPACE);
			}
			shortLastname = sb.toString();
		}
		return shortLastname + firstname;
	}

	/***
	 * Look for first patient with a matching first and family name
	 *
	 * @param vorname
	 * @param nachname
	 * @return
	 */
	private static List<Patient> getPatient(String vorname, String nachname) {
		Query<Patient> query = new Query<Patient>(Patient.class);
		query.add("Name", "LIKE", nachname + "%", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		query.add("Vorname", "LIKE", vorname + "%", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		List<Patient> patienten = query.execute();
		return patienten;
	}

	/***
	 * Global: Checks whether the pathname, first and lastname match our conventions
	 * Checks basename and parent directory
	 *
	 * @param fullPathname
	 * @param familyName
	 * @param firstName
	 * @return
	 */
	public static boolean filenameIsValid(String fullPathname, String familyName, String firstName) {
		String mustBeginWith = basenameMustBeginWith(familyName, firstName);
		File path = new File(fullPathname);
		File parent = new File(path.getParent());
		return path.getName().startsWith(mustBeginWith) || parent.getName().startsWith(mustBeginWith);
	}

	/***
	 * Global: Checks whether the pathname matches an existing patient
	 *
	 * @param fullPathname
	 * @return patient or null if not none found
	 */
	public static Patient filenameBelongsToSomePatient(String fullPathname) {
		String[] names = getFirstAndFamilyNameFromPathOldConvention(fullPathname);
		List<Patient> patienten = getPatient(names[0], names[1]);
		if (patienten.size() == 1)
			return patienten.get(0);
		else {
			logAndConsole(
					String.format("No unique patient found for %1s => %2s %3s (found %d) ", fullPathname, names[0], //$NON-NLS-1$
							names[1], patienten.size()),
					Log.WARNINGS);
			return null;
		}
	}

	/***
	 * Return a list of files which belong to this patient
	 *
	 * @param actPatient
	 * @param activePaths or null to search on all
	 * @return a list of files or an error String
	 */
	public static Object getFilesForPatient(Patient actPatient, String[] activePaths) {
		Object result;
		if (activePaths == null)
			activePaths = PreferenceConstants.getActiveBasePaths();
		FilenameFilter filter = new FileFilters(actPatient.getName(), actPatient.getVorname());
		List<File> list = ListFiles.getList(activePaths, actPatient.getName(), actPatient.getVorname(),
				actPatient.getGeburtsdatum(), filter);
		/*
		 * List<File> oldFiles = getAllOldConventionFilesWithFilter(filter);
		 * Iterator<File> iterator = oldFiles.iterator(); while (iterator.hasNext()) {
		 * list.add(iterator.next()); }
		 */
		if (list.size() > 0) {
			result = list;
		} else {
			result = Messages.ExterneDokumente_no_files_found;
		}
		return result;
	}

	/***
	 *
	 * @param name
	 * @return name with dashes, underscores removed
	 */
	public static String cleanName(String name) {
		if (name.length() == 0)
			return name;
		name = name.replaceAll("-", StringUtils.EMPTY).replaceAll("_", StringUtils.EMPTY);
		if (name.split("[. \\s]", 0).length > 1) {
			String clean = name.split("[. \\s]", 0)[0].toLowerCase(); //$NON-NLS-1$
			clean = clean.substring(0, 1).toUpperCase() + clean.substring(1);
			return clean;
		} else
			return name;
	}

	/***
	 *
	 * @param fullPathName
	 * @return maximal 6 chars of familyName. Spaces removed
	 */
	static public String firstToken(String fullPathName) {
		String firstToken = fullPathName.replaceFirst("[_\\p{Space}].*", StringUtils.EMPTY); //$NON-NLS-1$
		return firstToken;
	}

	public static String getSubDirPath(Patient pat) {
		String s = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD1, StringUtils.EMPTY) + File.separatorChar
				+ basenameMustBeginWith(pat.getName(), pat.getVorname());
		return s;
	}

	public static String geburtsDatumToCanonical(String geburtsDatum) {
		if (geburtsDatum == null || geburtsDatum.length() == 0)
			return FileFiltersConvention.BirthdayNotKnown;
		String sortableDate = geburtsDatum.substring(6);
		if (sortableDate.length() != 4)
			sortableDate = "XX";
		sortableDate += "-" + geburtsDatum.substring(3, 5);
		sortableDate += "-" + geburtsDatum.substring(0, 2);
		return sortableDate;
	}

	/***
	 * Returns a pathname where the file should be stored according to the new
	 * convention
	 *
	 * @param fullPathname
	 * @return null if already stored in the correct place, else new pathname
	 */
	public String ShouldBeMovedToThisSubDir(String oldPathname, String geburtsDatum) {
		String basename = new File(oldPathname).getName();
		String dirname = new File(oldPathname).getParent();
		// Convert 31.02.79 -> 1979-02-31
		String s = dirname + File.separatorChar + (new File(getSubDirPath(pat)).getName()) + StringUtils.SPACE
				+ geburtsDatumToCanonical(geburtsDatum) + File.separatorChar + basename;
		return dirname + File.separatorChar + (new File(getSubDirPath(pat)).getName()) + StringUtils.SPACE
				+ geburtsDatumToCanonical(geburtsDatum) + File.separatorChar + basename;
	}

	/***
	 * Get all files corresponding to the old convention
	 *
	 * @return a list of string
	 */
	public static java.util.List<File> getAllOldConventionFiles() {
		List<File> allFiles = new ArrayList<File>();
		String[] paths = PreferenceConstants.getActiveBasePaths();
		for (int j = 0; j < paths.length; j++) {
			if (paths[j] != null && paths[j].length() > 0) {
				File dir = new File(paths[j]);
				FileFilter fileFilter = new FileFilter() {
					public boolean accept(File file) {
						return file.isFile();
					}
				};
				File[] files = dir.listFiles(fileFilter);
				if (files != null)
					for (int k = 0; k < files.length; k++) {
						allFiles.add(files[k]);
					}
			}
		}
		return allFiles;
	}
}
