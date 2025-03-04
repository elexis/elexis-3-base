/*******************************************************************************
 * Copyright (c) 2006-2011, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    Niklaus Giger - added fourth element & utility procedures
 *
 *******************************************************************************/

package ch.elexis.extdoc.preferences;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class PreferenceConstants {
	// retour kompatibel
	public static final String BASIS_PFAD1 = "ch.elexis.externe_dokumente/BasisPfad"; //$NON-NLS-1$ l
	public static final String BASIS_PFAD2 = "ch.elexis.externe_dokumente/BasisPfad2"; //$NON-NLS-1$
	public static final String BASIS_PFAD3 = "ch.elexis.externe_dokumente/BasisPfad3"; //$NON-NLS-1$
	public static final String BASIS_PFAD4 = "ch.elexis.externe_dokumente/BasisPfad4"; //$NON-NLS-1$
	public static final String NAME_PFAD1 = "ch.elexis.externe_dokumente/NamePfad"; //$NON-NLS-1$
	public static final String NAME_PFAD2 = "ch.elexis.externe_dokumente/NamePfad2"; //$NON-NLS-1$
	public static final String NAME_PFAD3 = "ch.elexis.externe_dokumente/NamePfad3"; //$NON-NLS-1$
	public static final String NAME_PFAD4 = "ch.elexis.externe_dokumente/NamePfad4"; //$NON-NLS-1$
	public static final String SELECTED_PATHS = "ch.elexis.externe_dokumente/SelectedPaths"; //$NON-NLS-1$
	public static final String CONCERNS = "ch.elexis.externe_dokumente/Concerns"; //$NON-NLS-1$
	public static final String EMAIL_PROGRAM = "ch.elexis.externe_dokumente/Email_app"; //$NON-NLS-1$

	public static class PathElement {
		public String prefName;
		public String name;
		public String prefBaseDir;
		public String baseDir;

		PathElement(String prefsName, String prefsBaseDirName) {
			prefName = prefsName;
			prefBaseDir = prefsBaseDirName;
			name = CoreHub.localCfg.get(prefName, StringUtils.EMPTY);
			baseDir = CoreHub.localCfg.get(prefsBaseDirName, StringUtils.EMPTY);
		}
	}

	public static PathElement[] getPrefenceElements() {
		PathElement prefElems[] = { new PathElement(PreferenceConstants.NAME_PFAD1, PreferenceConstants.BASIS_PFAD1),
				new PathElement(PreferenceConstants.NAME_PFAD2, PreferenceConstants.BASIS_PFAD2),
				new PathElement(PreferenceConstants.NAME_PFAD3, PreferenceConstants.BASIS_PFAD3),
				new PathElement(PreferenceConstants.NAME_PFAD4, PreferenceConstants.BASIS_PFAD4), };
		return prefElems;
	}

	public static String[] getActiveBasePaths() {
		String[] paths = new String[4];
		paths[0] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD1, StringUtils.EMPTY);
		paths[1] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD2, StringUtils.EMPTY);
		paths[2] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD3, StringUtils.EMPTY);
		paths[3] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD4, StringUtils.EMPTY);
		for (int j = 0; j < paths.length; j++)
			if (!pathIsSelected(j))
				paths[j] = null;
		return paths;
	}

	private static int selected = -1;

	private static void ensureValueLoaded() {
		if (selected == -1)
			selected = Integer.parseInt(ConfigServiceHolder.getUser(PreferenceConstants.SELECTED_PATHS, "0")); //$NON-NLS-1$
	}

	/***
	 * Tells whether the user has the path activated or not
	 *
	 * @param whichOne
	 * @return true or false
	 */
	public static boolean pathIsSelected(int whichOne) {
		ensureValueLoaded();
		return (selected & (1 << whichOne)) != 0;
	}

	/***
	 * Writes selection to user configuration
	 */

	public static void saveSelected() {
		ensureValueLoaded();
		ConfigServiceHolder.setUser(PreferenceConstants.SELECTED_PATHS, Integer.toString(selected));

	}

	/**
	 * Sets (but does not save) the active paths
	 *
	 * @param whichOne
	 * @param yes
	 */
	public static void pathSetSelected(int whichOne, boolean yes) {
		ensureValueLoaded();
		int mask = 1 << whichOne;
		if (yes)
			selected |= mask;
		else
			selected &= ~mask;
	}
}