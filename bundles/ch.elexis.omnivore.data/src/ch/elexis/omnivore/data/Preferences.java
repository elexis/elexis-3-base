package ch.elexis.omnivore.data;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.DATE_MODIFIABLE;
import static ch.elexis.omnivore.PreferenceConstants.OmnivoreMax_Filename_Length_Default;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.activator.CoreHubHelper;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences {
	
	private static SettingsPreferenceStore fsSettingsStore;
	public static Logger log = LoggerFactory.getLogger(Preferences.class);
	
	/**
	 * reload the fs settings store
	 */
	private static void initGlobalConfig(){
		if (fsSettingsStore == null) {
			
			//  workaround for bug https://redmine.medelexis.ch/issues/9501 -> migrate old key to new key
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/store_in_fs_global",
				STOREFSGLOBAL, true);
			// bug from omnivore
			CoreHubHelper.transformConfigKey("ch.elexis.omnivore//store_in_fs_global",
				STOREFSGLOBAL, true);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/store_in_fs", STOREFS, true);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/store_in_fs", STOREFS, false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/basepath", BASEPATH, true);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/basepath", BASEPATH, false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/categories", STOREFS, false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/date_modifiable", STOREFS,
				false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/columnwidths", STOREFS,
				false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/savecolwidths", STOREFS,
				false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/sortdirection", STOREFS,
				false);
			CoreHubHelper.transformConfigKey("plugins/omnivore-direct/savesortdirection", STOREFS,
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
			PREF_SRC_PATTERN[i] = PREFERENCE_SRC_PATTERN + i.toString().trim(); //$NON-NLS-1$
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
			PREF_DEST_DIR[i] = PREFERENCE_DEST_DIR + i.toString().trim(); //$NON-NLS-1$
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
	
	// Make the temporary filename configurable
	// which is generated to extract the document from the database for viewing.
	// Thereby, simplify tasks like adding a document to an e-mail.
	// For most elements noted below, we can set the maximum number of digits
	// to be used (taken from the source from left); which character to add thereafter;
	// and whether to fill leading digits by a given character.
	// This makes a large number of options, so I construct the required preference store keys from
	// arrays.
	// Note: The DocHandle.getTitle() javadoc says that a document title in omnivore may contain 80
	// chars.
	// To enable users to copy that in full, I allow for a max of 80 chars to be specified as
	// num_digits for *any* element.
	// Using all elements to that extent will return filename that's vastly too long, but that will
	// probably be handled elsewhere.
	public static final Integer nPreferences_cotf_element_digits_max = 80;
	public static final String PREFERENCE_COTF = "cotf_";
	public static final String[] PREFERENCE_cotf_elements = {
		"constant1", "PID", "fn", "gn", "dob", "dt", "dk", "dguid", "random", "constant2"
	};
	public static final String[] PREFERENCE_cotf_parameters = {
		"fill_leading_char", "num_digits", "add_trailing_char"
	};
	// The following unwanted characters, and all below codePoint=32 will be cleaned in advance.
	// Please see the getOmnivoreTemp_Filename_Element for details.
	public static final String cotf_unwanted_chars = "[\\:/:*?()+,\';\"\r\t\n´`<>]";
	// Dank Eclipse's mglw. etwas übermässiger "Optimierung" werden externalisierte Strings nun als
	// Felder von Messges angesprochen -
	// und nicht mehr wie zuvor über einen als String übergebenen key. Insofern muss ich wohl zu den
	// obigen Arrays korrespondierende Arrays
	// vorab erstellen, welche die jeweils zugehörigen Strings aus omnivore.Messages dann in eine
	// definierte Reihenfolge bringen,
	// in der ich sie unten auch wieder gerne erhalten würde. Einfach per Programm at runtime die
	// keys generieren scheint nicht so leicht zu gehen.
	public static final String[] PREFERENCE_cotf_elements_messages = {
		Messages.Preferences_cotf_constant1, Messages.Preferences_cotf_pid,
		Messages.Preferences_cotf_fn, Messages.Preferences_cotf_gn, Messages.Preferences_cotf_dob,
		Messages.Preferences_cotf_dt, Messages.Preferences_cotf_dk, Messages.Preferences_cotf_dguid,
		Messages.Preferences_cotf_random, Messages.Preferences_cotf_constant2
	};
	public static final String[] PREFERENCE_cotf_parameters_messages = {
		Messages.Preferences_cotf_fill_lead_char, Messages.Preferences_cotf_num_digits,
		Messages.Preferences_cotf_add_trail_char
	};
}
