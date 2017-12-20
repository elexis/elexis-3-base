package ch.elexis.omnivore.data;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.DATE_MODIFIABLE;
import static ch.elexis.omnivore.PreferenceConstants.OmnivoreMax_Filename_Length_Default;
import static ch.elexis.omnivore.PreferenceConstants.PREFBASE;
import static ch.elexis.omnivore.PreferenceConstants.PREFERENCE_DEST_DIR;
import static ch.elexis.omnivore.PreferenceConstants.PREFERENCE_SRC_PATTERN;
import static ch.elexis.omnivore.PreferenceConstants.PREF_DEST_DIR;
import static ch.elexis.omnivore.PreferenceConstants.PREF_MAX_FILENAME_LENGTH;
import static ch.elexis.omnivore.PreferenceConstants.PREF_SRC_PATTERN;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;
import static ch.elexis.omnivore.PreferenceConstants.STOREFSGLOBAL;
import static ch.elexis.omnivore.PreferenceConstants.nPREF_DEST_DIR;
import static ch.elexis.omnivore.PreferenceConstants.nPREF_SRC_PATTERN;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.rgw.io.Settings;

public class Preferences {
	
	private static SettingsPreferenceStore fsSettingsStore;
	
	/**
	 * Workaround for bug https://redmine.medelexis.ch/issues/9501. Migrates old key values to new
	 * key values.
	 * 
	 * @param oldKey
	 * @param newKey
	 * @param isGlobal
	 */
	private static void transformConfigKeysFromV31To33(String oldKey, String newKey,
		boolean isGlobal){
		Settings settings = isGlobal ? CoreHub.globalCfg : CoreHub.localCfg;
		if (settings.get(oldKey, null) != null && settings.get(newKey, null) == null) {
			settings.set(newKey, settings.get(oldKey, null));
		}
	}
	
	/**
	 * reload the fs settings store
	 */
	private static void initGlobalConfig(){
		if (fsSettingsStore == null) {
			
			//  workaround for bug https://redmine.medelexis.ch/issues/9501 -> migrate old key to new key
			transformConfigKeysFromV31To33("plugins/omnivore-direct/store_in_fs_global",
				STOREFSGLOBAL, true);
			// bug from omnivore
			transformConfigKeysFromV31To33("ch.elexis.omnivore//store_in_fs_global", STOREFSGLOBAL,
				true);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/store_in_fs", STOREFS, true);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/store_in_fs", STOREFS, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/basepath", BASEPATH, true);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/basepath", BASEPATH, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/categories", STOREFS, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/date_modifiable", STOREFS,
				false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/columnwidths", STOREFS, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/savecolwidths", STOREFS, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/sortdirection", STOREFS, false);
			transformConfigKeysFromV31To33("plugins/omnivore-direct/savesortdirection", STOREFS,
				false);
			
			boolean isGlobal = CoreHub.globalCfg.get(STOREFSGLOBAL, false);
			if (isGlobal) {
				fsSettingsStore = new SettingsPreferenceStore(CoreHub.globalCfg);
			} else {
				fsSettingsStore = new SettingsPreferenceStore(CoreHub.localCfg);
			}
		}
	}
	
	public static boolean storeInFilesystem(){
		initGlobalConfig();
		return fsSettingsStore.getBoolean(STOREFS);
	}
	
	public static String getBasepath(){
		initGlobalConfig();
		return fsSettingsStore.getString(BASEPATH);
	}
	
	public static boolean getDateModifiable(){
		return CoreHub.localCfg.get(DATE_MODIFIABLE, false);
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns the number of rules to process for automatic archiving
	 * 
	 * @author Joerg Sigle
	 */
	
	public static Integer getOmnivorenRulesForAutoArchiving(){
		// For automatic archiving of incoming files:
		// The smaller number of entries available for Src and Dest determines
		// how many rule editing field pairs are provided on the actual preferences page, and
		// processed later on.
		// Now: Determine the number of slots for rule defining src and target strings,
		// and compute the actual number of rules to be the larger of these two.
		// Normally, they should be identical, if the dummy arrays used for initialization above
		// have had the same size.
		Integer nRules = nPREF_SRC_PATTERN;
		if (nPREF_DEST_DIR > nPREF_SRC_PATTERN) {
			nRules = nPREF_DEST_DIR;
		}
		
		return nRules;
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns configured content of rules for automatic archiving
	 * 
	 * @param Rule
	 *            number whose match pattern shall be retrieved. Cave: Visible only internally to
	 *            the program, this index is 0 based, whereas the preference page for the user shows
	 *            1-based "Rule n" headings.
	 * 
	 * @return Either null if the index is out of bounds, or if the respective String is technically
	 *         undefined (which should never be the case); or the respective String (which may also
	 *         be "", i.e. an empty string), if the user has cleared or left clear the respective
	 *         input field.
	 * 
	 * @author Joerg Sigle
	 */
	
	public static String getOmnivoreRuleForAutoArchivingSrcPattern(Integer i){
		if ((i < 0) || (i >= getOmnivorenRulesForAutoArchiving())) {
			return null;
		}
		
		// The preferences keys should already have been constructed by init - but if not, let's do
		// it here for the one that we need now:
		if (PREF_SRC_PATTERN[i].equals("")) {
			PREF_SRC_PATTERN[i] = PREFBASE + PREFERENCE_SRC_PATTERN + i.toString().trim(); //$NON-NLS-1$
		}
		return CoreHub.localCfg.get(PREF_SRC_PATTERN[i], "").trim();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns configured content of rules for automatic archiving
	 * 
	 * @param Rule
	 *            number whose destination directory shall be retrieved. Cave: Visible only
	 *            internally to the program, this index is 0 based, whereas the preference page for
	 *            the user shows 1-based "Rule n" headings.
	 * 
	 * @return Either null if the index is out of bounds, or if the respective String is technically
	 *         undefined (which should never be the case); or the respective String (which may also
	 *         be "", i.e. an empty string), if the user has cleared or left clear the respective
	 *         input field.
	 * 
	 * @author Joerg Sigle
	 */
	
	public static String getOmnivoreRuleForAutoArchivingDestDir(Integer i){
		if ((i < 0) || (i >= getOmnivorenRulesForAutoArchiving())) {
			return null;
		}
		
		// The preferences keys should already have been constructed by init - but if not, let's do
		// it here for the one that we need now:
		if (PREF_DEST_DIR[i].equals("")) {
			PREF_DEST_DIR[i] = PREFBASE + PREFERENCE_DEST_DIR + i.toString().trim(); //$NON-NLS-1$
		}
		return CoreHub.localCfg.get(PREF_DEST_DIR[i], "").trim();
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Returns a currently value from the preference store, observing default settings and min/max
	 * settings for that parameter
	 * 
	 * Can be called with an already available preferenceStore. If none is passed, one will be
	 * temporarily instantiated on the fly.
	 * 
	 * @return The requested integer parameter
	 * 
	 * @author Joerg Sigle
	 */
	
	public static Integer getOmnivoreMax_Filename_Length(){
		IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
		int ret = preferenceStore.getInt(PREF_MAX_FILENAME_LENGTH);
		if (ret == 0) {
			ret = OmnivoreMax_Filename_Length_Default;
		}
		return ret;
	}

	public static void setFsSettingStore(SettingsPreferenceStore settingsPreferenceStore){
		Preferences.fsSettingsStore = settingsPreferenceStore;
	}
	public static SettingsPreferenceStore getFsSettingsStore(){
		return fsSettingsStore;
	}
}
