/*******************************************************************************
 * Copyright (c) 2013 J. Sigle; Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - added a preference page to omnivore to control  a new automatic archiving functionality  
 *    G. Weirich and others - preference pages for other plugins, used as models for this one
 *    
 *******************************************************************************/

package ch.elexis.omnivore.views;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

//FIXME: Layout needs a thorough redesign. See: http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html -- 20130411js: done to some extent.
//FIXME: We want a layout that will use all the available space, auto re-size input fields etc., have nested elements, and still NOT result in "dialog has invalid data" error messages.
//FIXME: Maybe we must add PREFERENCE_BRANCH to some editor element add etc. commands, to ensure the parameters are store.

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private static Logger log = LoggerFactory.getLogger("ch.elexis.omnivore.PreferencePage"); //$NON-NLS-1$
	
	public static final String PREFBASE = "ch.elexis.omnivore/"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFBASE + "/store_in_fs_global"; //$NON-NLS-1$
	public static final String STOREFS = PREFBASE + "store_in_fs"; //$NON-NLS-1$
	public static final String BASEPATH = PREFBASE + "basepath"; //$NON-NLS-1$
	public static final String CATEGORIES = PREFBASE + "categories"; //$NON-NLS-1$
	public static final String DATE_MODIFIABLE = PREFBASE + "date_modifiable"; //$NON-NLS-1$
	public static final String PREFERENCE_SRC_PATTERN = PREFBASE + "src_pattern"; //$NON-NLS-1$
	public static final String PREFERENCE_DEST_DIR = PREFBASE + "dest_dir"; //$NON-NLS-1$
	
	// The following setting is used in ch.elexis.omnivore.data/DocHandle.java.
	// Linux and MacOS may be able to handle longer filenames, but we observed that Windows 7 64-bit
	// will not import files with names longer than 80 chars.
	// So I make this setting configurable. Including a safe default and limits that a user cannot
	// exceed.
	public static final Integer OmnivoreMax_Filename_Length_Min = 12;
	public static final Integer OmnivoreMax_Filename_Length_Default = 120;
	public static final Integer OmnivoreMax_Filename_Length_Max = 255;
	public static final String PREF_MAX_FILENAME_LENGTH = PREFBASE + "/max_filename_length";
	
	// For automatic archiving of incoming files:
	// Here is a comfortable way to specify how many rules shall be available:
	// The individual Strings in the following arrays may be left empty - they will be automatically
	// filled.
	// But the smaller number of entries for Src and Dest determines
	// how many rule editing field pairs are provided on the actual preferences page, and processed
	// later on.
	// The actual content of all field labels, and all preference store keys,
	// is computed from content of the messages.properties file.
	// I've tested the construction of the preferences dialog with fields for some 26 rules, worked
	// like a charm :-)
	public static final String[] PREF_SRC_PATTERN = {
		"", "", "", "", ""
	};
	public static final String[] PREF_DEST_DIR = {
		"", "", "", "", ""
	};
	public static final int nPREF_SRC_PATTERN = PREF_SRC_PATTERN.length;
	public static final int nPREF_DEST_DIR = PREF_DEST_DIR.length;
	
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
	static final String cotf_unwanted_chars = "\\/:*?()+,;\"'´`";
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
		Messages.Preferences_cotf_dt, Messages.Preferences_cotf_dk,
		Messages.Preferences_cotf_dguid, Messages.Preferences_cotf_random,
		Messages.Preferences_cotf_constant2
	};
	public static final String[] PREFERENCE_cotf_parameters_messages = {
		Messages.Preferences_cotf_fill_lead_char, Messages.Preferences_cotf_num_digits,
		Messages.Preferences_cotf_add_trail_char
	};
	
	public static final String USR_COLUMN_WIDTH_SETTINGS = PREFBASE + "/columnwidths";
	public static final String SAVE_COLUM_WIDTH = PREFBASE + "/savecolwidths";
	
	public static final String USR_SORT_DIRECTION_SETTINGS = PREFBASE + "/sortdirection";
	public static final String SAVE_SORT_DIRECTION = PREFBASE + "/savesortdirection";
	
	private static SettingsPreferenceStore fsSettingsStore;
	
	private BooleanFieldEditor bStoreFSGlobal;
	private BooleanFieldEditor bStoreFS;
	private DirectoryFieldEditor dfStorePath;
	
	private Button btnSaveColumnWidths;
	private Button btnSaveSortDirection;
	
	public Preferences(){
		super(GRID);
		
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		setDescription(Messages.Preferences_omnivore);
	}
	
	@Override
	protected void createFieldEditors(){
		// I'd like to place ALL groups in this preference dialog one under another,
		// so that each group completely occupies the available horizontal space.
		// But the default behaviour is to put the groups next to each other :-(
		
		// For instructions, see:
		// http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html
		
		// I can't use any other layout but GridLayout.
		// Otherwise some adjustGridLayout() somewhere else will invariably throw:
		// "The currently displayed page contains invalid values." at runtime. 201304110439js
		// Besides, RowLayout() wouldn't make sense here.
		
		// ---
		
		// Nachdem ich aussenrum einmal eine globale Gruppe installiere,
		// bekomme ich wenigstens die nachfolgend tieferen Gruppen untereinander in einer Spalte,
		// und nicht mehr nebeneinander.
		// Offenbar hat die Zuweisung eines Layouts zu getFieldEditorParent() keinen Effekt gehabt.
		// Warum auch immer...
		
		Group gAllOmnivorePrefs = new Group(getFieldEditorParent(), SWT.NONE);
		
		// getFieldEditorParent().setLayoutData(SWTHelper.getFillGridData(1,false,0,false));
		
		GridLayout gOmnivorePrefsGridLayout = new GridLayout();
		gOmnivorePrefsGridLayout.numColumns = 1; // this is sadly and apparently ignored.
		gOmnivorePrefsGridLayout.makeColumnsEqualWidth = true;
		
		gAllOmnivorePrefs.setLayout(gOmnivorePrefsGridLayout);
		// getFieldEditorParent().setLayout(gOmnivorePrefsGridLayout);
		
		GridData gOmnivorePrefsGridLayoutData = new GridData();
		gOmnivorePrefsGridLayoutData.grabExcessHorizontalSpace = true;
		gOmnivorePrefsGridLayoutData.horizontalAlignment = GridData.FILL;
		
		gAllOmnivorePrefs.setLayoutData(gOmnivorePrefsGridLayoutData);
		Group gGeneralOptions = new Group(gAllOmnivorePrefs, SWT.NONE);
		GridData gGeneralOptionsGridLayoutData = new GridData();
		gGeneralOptionsGridLayoutData.grabExcessHorizontalSpace = true;
		gGeneralOptionsGridLayoutData.horizontalAlignment = GridData.FILL;
		gGeneralOptions.setLayoutData(gGeneralOptionsGridLayoutData);
		
		addField(new BooleanFieldEditor(DATE_MODIFIABLE, Messages.Preferences_dateModifiable,
			gGeneralOptions));
		
		Group gPathForDocs = new Group(gGeneralOptions, SWT.NONE);
		gPathForDocs.setLayout(new FillLayout());
		
		bStoreFSGlobal = new BooleanFieldEditor(STOREFSGLOBAL,
			"Dateisystem Einstellungen global speichern", gPathForDocs) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue){
				super.fireValueChanged(property, oldValue, newValue);
				if ((Boolean) newValue) {
					fsSettingsStore = new SettingsPreferenceStore(CoreHub.globalCfg);
					updateFSSettingsStore();
				} else {
					fsSettingsStore = new SettingsPreferenceStore(CoreHub.localCfg);
					updateFSSettingsStore();
				}
			}
		};
		addField(bStoreFSGlobal);
		
		bStoreFS = new BooleanFieldEditor(STOREFS, Messages.Preferences_storeInFS, gPathForDocs);
		addField(bStoreFS);
		storeInFilesystem();
		
		dfStorePath =
			new DirectoryFieldEditor(BASEPATH, Messages.Preferences_pathForDocs, gPathForDocs);
		getBasepath();
		dfStorePath.setEmptyStringAllowed(true);
		addField(dfStorePath);
		
		Group gPathForMaxChars = new Group(gGeneralOptions, SWT.NONE);
		gPathForMaxChars.setLayout(new FillLayout());
		IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
		preferenceStore.setDefault(PREF_MAX_FILENAME_LENGTH, OmnivoreMax_Filename_Length_Default);
		IntegerFieldEditor maxCharsEditor =
			new IntegerFieldEditor(PREF_MAX_FILENAME_LENGTH,
				Messages.Preferences_MAX_FILENAME_LENGTH, gPathForMaxChars);
		maxCharsEditor.setValidRange(OmnivoreMax_Filename_Length_Min,
			OmnivoreMax_Filename_Length_Max);
		addField(maxCharsEditor);
		
		// ---
		
		// For automatic archiving of incoming files:
		// add field groups for display or editing of rule sets.
		// First, we define a new group (that will visually appear as an outlined box) and give it a
		// header like setText("Regel i");
		// Then, within this group, we add one StringFieldEditor for the search pattern to be
		// matched, and a DirectoryFieldEditor for the auto archiving target to be used.
		
		Integer nAutoArchiveRules = getOmnivorenRulesForAutoArchiving();
		
		Group gAutoArchiveRules = new Group(gAllOmnivorePrefs, SWT.NONE);
		// Group gAutoArchiveRules = new Group(getFieldEditorParent(), SWT.NONE);
		
		// gAutoArchiveRules.setLayoutData(SWTHelper.getFillGridData(1,true,nAutoArchiveRules,false));
		
		GridLayout gAutoArchiveRulesGridLayout = new GridLayout();
		gAutoArchiveRulesGridLayout.numColumns = 1; // bestimmt die Anzahl der Spalten, in denen die
		// Regeln innerhab des AutoArchiveRules containers angeordnet werden.
		gAutoArchiveRules.setLayout(gAutoArchiveRulesGridLayout);
		
		GridData gAutoArchiveRulesGridLayoutData = new GridData();
		gAutoArchiveRulesGridLayoutData.grabExcessHorizontalSpace = true;
		gAutoArchiveRulesGridLayoutData.horizontalAlignment = GridData.FILL;
		gAutoArchiveRules.setLayoutData(gAutoArchiveRulesGridLayoutData);
		
		gAutoArchiveRules.setText(Messages.Preferences_automatic_archiving_of_processed_files);
		
		for (int i = 0; i < nAutoArchiveRules; i++) {
			
			// Just to check whether the loop is actually used, even if nothing appears in the
			// preference dialog:
			log.debug(PREF_SRC_PATTERN[i] + " : " + Messages.Preferences_SRC_PATTERN);
			log.debug(PREF_DEST_DIR[i] + " : " + Messages.Preferences_DEST_DIR);
			
			Group gAutoArchiveRule = new Group(gAutoArchiveRules, SWT.NONE);
			
			GridLayout gAutoArchiveRuleGridLayout = new GridLayout();
			gAutoArchiveRuleGridLayout.numColumns = 1; // bestimmt die Anzahl der Spalten für jede
			// Regel: links label, rechts eingabefeld (ggf. mit Knopf), but: 1, 2, 3: no change.
			gAutoArchiveRule.setLayout(gAutoArchiveRuleGridLayout);
			
			GridData gAutoArchiveRuleGridLayoutData = new GridData();
			gAutoArchiveRuleGridLayoutData.grabExcessHorizontalSpace = true; // damit die Gruppe der
			// Rules so breit ist, wie oben Label und Max_Filename_Length Eingabefeld zusammen.
			gAutoArchiveRuleGridLayoutData.horizontalAlignment = GridData.FILL;
			gAutoArchiveRule.setLayoutData(gAutoArchiveRuleGridLayoutData);
			
			// Cave: The labels show 1-based rule numbers, although the actual array indizes are 0
			// based.
			gAutoArchiveRule.setText(Messages.Preferences_Rule + " " + (i + 1)); // The brackets are
			// needed, or the string representations of i and 1 will both be added...
			
			addField(new StringFieldEditor(PREF_SRC_PATTERN[i], Messages.Preferences_SRC_PATTERN,
				gAutoArchiveRule));
			addField(new DirectoryFieldEditor(PREF_DEST_DIR[i], Messages.Preferences_DEST_DIR,
				gAutoArchiveRule));
		}
		// ---
		
		// Make the temporary filename configurable
		// which is generated to extract the document from the database for viewing.
		// Thereby, simplify tasks like adding a document to an e-mail.
		// For most elements noted below, we can set the maximum number of digits
		// to be used (taken from the source from left); which character to add thereafter;
		// and whether to fill leading digits by a given character.
		// This makes a large number of options, so I construct the required preference store keys
		// from arrays.
		
		// Originally, I would have preferred a simple tabular matrix:
		// columns: element name, fill_lead_char, num_digits, add_trail_char
		// lines: each of the configurable elements of the prospective temporary filename.
		// But such a simple thing is apparently not so simple to make using the PreferencePage
		// class.
		// So instead, I add a new group for each configurable element, including each of the 3
		// parameters.
		
		Integer nCotfRules = PREFERENCE_cotf_elements.length;
		
		Group gCotfRules = new Group(gAllOmnivorePrefs, SWT.NONE);
		// Group gCotfRules = new Group(getFieldEditorParent(), SWT.NONE);
		
		// gCotfRules.setLayoutData(SWTHelper.getFillGridData(6,false,nCotfRules,false)); //This
		// would probably make groups-within-group completely disappear.
		
		GridLayout gCotfRulesGridLayout = new GridLayout();
		gCotfRulesGridLayout.numColumns = nCotfRules; // at least this one is finally honoured...
		gCotfRules.setLayout(gCotfRulesGridLayout);
		
		GridData gCotfRulesGridLayoutData = new GridData();
		gCotfRulesGridLayoutData.grabExcessHorizontalSpace = true;
		gCotfRulesGridLayoutData.horizontalAlignment = GridData.FILL;
		gCotfRules.setLayoutData(gCotfRulesGridLayoutData);
		
		gCotfRules.setText(Messages.Preferences_construction_of_temporary_filename);
		
		for (int i = 0; i < nCotfRules; i++) {
			
			Group gCotfRule = new Group(gCotfRules, SWT.NONE);
			
			gCotfRule.setLayout(new FillLayout());
			GridLayout gCotfRuleGridLayout = new GridLayout();
			gCotfRuleGridLayout.numColumns = 6;
			gCotfRule.setLayout(gCotfRuleGridLayout);
			
			GridData gCotfRuleGridLayoutData = new GridData();
			gCotfRuleGridLayoutData.grabExcessHorizontalSpace = true;
			gCotfRuleGridLayoutData.horizontalAlignment = GridData.FILL;
			gCotfRule.setLayoutData(gCotfRuleGridLayoutData);
			
			gCotfRule.setText(PREFERENCE_cotf_elements_messages[i]);
			
			if (PREFERENCE_cotf_elements[i].contains("constant")) {
				addField(new StringFieldEditor("", "", 10, gCotfRule));
				addField(new StringFieldEditor(PREFBASE + PREFERENCE_COTF
					+ PREFERENCE_cotf_elements[i] + "_" + PREFERENCE_cotf_parameters[1],
					PREFERENCE_cotf_elements_messages[i], 10, gCotfRule));
				addField(new StringFieldEditor("", "", 10, gCotfRule));
			} else {
				addField(new StringFieldEditor(PREFBASE + PREFERENCE_COTF
					+ PREFERENCE_cotf_elements[i] + "_" + PREFERENCE_cotf_parameters[0],
					PREFERENCE_cotf_parameters_messages[0], 10, gCotfRule));
				addField(new StringFieldEditor(PREFBASE + PREFERENCE_COTF
					+ PREFERENCE_cotf_elements[i] + "_" + PREFERENCE_cotf_parameters[1],
					PREFERENCE_cotf_parameters_messages[1], 10, gCotfRule));
				addField(new StringFieldEditor(PREFBASE + PREFERENCE_COTF
					+ PREFERENCE_cotf_elements[i] + "_" + PREFERENCE_cotf_parameters[2],
					PREFERENCE_cotf_parameters_messages[2], 10, gCotfRule));
			}
		}
	}
	
	private void updateFSSettingsStore(){
		// update fs settings store accoring to global cfg
		bStoreFS.setPreferenceStore(fsSettingsStore);
		bStoreFS.load();
		dfStorePath.setPreferenceStore(fsSettingsStore);
		dfStorePath.load();
	}
	
	@Override
	protected Control createContents(Composite parent){
		Control c = super.createContents(parent);
		// save global setting to global cfg
		bStoreFSGlobal.setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		bStoreFSGlobal.load();
		// must be called after createFieldEditors / super.createContents
		updateFSSettingsStore();
		
		addSeparator();
		
		btnSaveColumnWidths = new Button(getFieldEditorParent(), SWT.CHECK);
		btnSaveColumnWidths.setText("Spaltenbreite speichern (für Ihren Benutzer)");
		btnSaveColumnWidths.setSelection(CoreHub.userCfg.get(Preferences.SAVE_COLUM_WIDTH, false));
		
		btnSaveSortDirection = new Button(getFieldEditorParent(), SWT.CHECK);
		btnSaveSortDirection.setText("Sortierung speichern (für Ihren Benutzer)");
		btnSaveSortDirection.setSelection(CoreHub.userCfg.get(Preferences.SAVE_SORT_DIRECTION,
			false));
		
		return c;
	}
	
	private void addSeparator(){
		Label separator = new Label(getFieldEditorParent(), SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData();
		separatorGridData.horizontalSpan = 3;
		separatorGridData.grabExcessHorizontalSpace = true;
		separatorGridData.horizontalAlignment = GridData.FILL;
		separatorGridData.verticalIndent = 0;
		separator.setLayoutData(separatorGridData);
	}
	
	@Override
	public void init(IWorkbench workbench){
		// For automatic archiving of incoming files:
		// construct the keys to the elexis preference store from a fixed header plus rule number:
		for (Integer i = 0; i < getOmnivorenRulesForAutoArchiving(); i++) {
			PREF_SRC_PATTERN[i] = PREFBASE + PREFERENCE_SRC_PATTERN + i.toString().trim(); //$NON-NLS-1$	//If this source pattern is found in the filename...
			PREF_DEST_DIR[i] = PREFBASE + PREFERENCE_DEST_DIR + i.toString().trim(); //$NON-NLS-1$					//the incoming file will be archived here after having been read
		}
		
	}
	
	@Override
	protected void performApply(){
		CoreHub.userCfg.set(Preferences.SAVE_COLUM_WIDTH, btnSaveColumnWidths.getSelection());
		CoreHub.userCfg.set(Preferences.SAVE_SORT_DIRECTION, btnSaveSortDirection.getSelection());
		CoreHub.userCfg.flush();
		CoreHub.globalCfg.flush();
		CoreHub.localCfg.flush();
		super.performApply();
	}
	
	public static boolean getDateModifiable(){
		return CoreHub.localCfg.get(DATE_MODIFIABLE, false);
	}
	
	// ----------------------------------------------------------------------------
	/**
	 * Accepts some data to turn into a temporary filename element, and returns a formatted
	 * temporary filename element, observing current settings from the preference store, also
	 * observing default settings and min/max settings for that parameter
	 * 
	 * @param Can
	 *            be called with an already available preferenceStore. If none is passed, one will
	 *            be temporarily instantiated on the fly. Also accepts
	 *            <code>String element_key</code> to identify the requested filename element, and
	 *            the
	 *            <code>String element_data</data> to be processed into a string constituting that filename element.
	 * 
	 * @return The requested filename element as a string.
	 * 
	 * @author Joerg Sigle
	 */
	
	public static String getOmnivoreTemp_Filename_Element(String element_key, String element_data){
		IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
		return getOmnivoreTemp_Filename_Element(preferenceStore, element_key, element_data);
	}
	
	public static String getOmnivoreTemp_Filename_Element(IPreferenceStore preferenceStore,
		String element_key, String element_data){
		
		log.debug("getOmnivoreTemp_Filename_Element: element_key=<" + element_key + ">");
		
		StringBuffer element_data_processed = new StringBuffer();
		Integer nCotfRules = PREFERENCE_cotf_elements.length;
		for (int i = 0; i < nCotfRules; i++) {
			
			log.debug("getOmnivoreTemp_Filename_Element: PREFERENCE_cotf_elements[" + i + "]=<"
				+ PREFERENCE_cotf_elements[i] + ">");
			
			if (PREFERENCE_cotf_elements[i].equals(element_key)) {
				
				log.debug("getOmnivoreTemp_Filename_Element: Match!");
				
				if (element_key.contains("constant")) {
					String constant =
						preferenceStore.getString(
							PREFBASE + PREFERENCE_COTF + PREFERENCE_cotf_elements[i] + "_"
								+ PREFERENCE_cotf_parameters[1]).trim();
					
					log.debug("getOmnivoreTemp_Filename_Element: returning constant=<" + constant
						+ ">");
					
					return constant;
				} else {
					// Shall we return ANY digits at all for this element, and later on: shall we
					// cut down or extend the processed string to some defined number of digits?
					String snum_digits =
						preferenceStore.getString(
							PREFBASE + PREFERENCE_COTF + PREFERENCE_cotf_elements[i] + "_"
								+ PREFERENCE_cotf_parameters[1]).trim();
					log.debug("getOmnivoreTemp_Filename_Element: snum_digits=<" + snum_digits + ">");
					
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
					
					if (num_digits > nPreferences_cotf_element_digits_max) {
						num_digits = nPreferences_cotf_element_digits_max;
					}
					log.debug("getOmnivoreTemp_Filename_Element: num_digits=<" + num_digits + ">");
					
					// Start with the passed element_data string
					String element_data_incoming = element_data.trim();
					log.debug("getOmnivoreTemp_Filename_Element: element_data_incoming=<"
						+ element_data_incoming + ">");
					
					// Remove all characters that shall not appear in the generated filename
					// Ich verwende kein replaceAll, weil dessen Implementation diverse
					// erforderliche Escapes offenbar nicht erlaubt.
					// Especially, \. is not available to specify a plain dot. (Na ja: \0x2e ginge
					// dann doch - oder sollte gehen.
					// Findet aber nichts. Im interaktiven Suchen/Ersetzen in Eclipse ist \0x2e
					// illegal; \x2e geht eher.
					// In Java Code geht ggf. \056 (octal) . Siehe unten beim automatischen
					// Entfernen von Dateinamen-Resten besonders aus dem docTitle.))
					StringBuffer element_data_clean = new StringBuffer();
					if (element_data_incoming != null) {
						for (int n = 0; n < element_data_incoming.length(); n++) {
							String c = element_data_incoming.substring(n, n + 1);
							if ((c.codePointAt(0) >= 32) && (!cotf_unwanted_chars.contains(c))) {
								element_data_clean.append(c);
							}
						}
					}
					String element_data_processed5 = (element_data_clean.toString().trim());
					
					log.debug("getOmnivoreTemp_Filename_Element: element_data_processed5=<"
						+ element_data_processed5 + ">");
					
					// filter out some special unwanted strings from the title that may have entered
					// while importing and partially renaming files
					String element_data_processed4 =
						element_data_processed5.replaceAll("_noa[0-9]+\056[a-zA-Z0-9]{0,3}", ""); // remove
					// filename remainders like _noa635253160443574060.doc
					String element_data_processed3 =
						element_data_processed4.replaceAll("noa[0-9]+\056[a-zA-Z0-9]{0,3}", ""); // remove
					// filename remainders like noa635253160443574060.doc
					String element_data_processed2 =
						element_data_processed3.replaceAll("_omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}",
							""); // remove filename remainders like
					// _omni_635253160443574060_vore.pdf
					String element_data_processed1 =
						element_data_processed2.replaceAll("omni_[0-9]+_vore\056[a-zA-Z0-9]{0,3}",
							""); // remove filename remainders like omni_635253160443574060_vore.pdf
					
					log.debug("getOmnivoreTemp_Filename_Element: element_data_processed1=<"
						+ element_data_processed1 + ">");
					
					// Limit the length of the result if it exceeds the specified or predefined max
					// number of digits
					if (element_data_processed1.length() > num_digits) {
						element_data_processed1 = element_data_processed1.substring(0, num_digits);
					}
					
					log.debug("getOmnivoreTemp_Filename_Element: num_digits=<" + num_digits + ">");
					
					// If a leading fill character is given, and the length of the result is below
					// the specified max_number of digits, then fill it up.
					// Note: We could also check whether the num_digits has been given. Instead, I
					// use the default max num of digits if not.
					String lead_fill_char =
						preferenceStore.getString(
							PREFBASE + PREFERENCE_COTF + PREFERENCE_cotf_elements[i] + "_"
								+ PREFERENCE_cotf_parameters[0]).trim();
					
					log.debug("getOmnivoreTemp_Filename_Element: lead_fill_char=<" + lead_fill_char
						+ ">");
					
					if ((lead_fill_char != null) && (lead_fill_char.length() > 0)
						&& (element_data_processed1.length() < num_digits)) {
						lead_fill_char = lead_fill_char.substring(0, 1);
						
						log.debug("getOmnivoreTemp_Filename_Element: lead_fill_char=<"
							+ lead_fill_char + ">");
						log.debug("getOmnivoreTemp_Filename_Element: num_digits=<" + num_digits
							+ ">");
						log.debug("getOmnivoreTemp_Filename_Element: element_data_processed1.length()=<"
							+ element_data_processed1.length() + ">");
						log.debug("getOmnivoreTemp_Filename_Element: element_data_processed1=<"
							+ element_data_processed1 + ">");
						
						for (int n = element_data_processed1.length(); n <= num_digits; n++) {
							element_data_processed.append(lead_fill_char);
							log.debug("getOmnivoreTemp_Filename_Element: n, element_data_processed="
								+ n + ", <" + element_data_processed + ">");
						}
					}
					element_data_processed.append(element_data_processed1);
					
					log.debug("getOmnivoreTemp_Filename_Element: element_data_processed=<"
						+ element_data_processed + ">");
					
					// If an add trailing character is given, add one (typically, this would be a
					// space or an underscore)
					String add_trail_char =
						preferenceStore.getString(
							PREFBASE + PREFERENCE_COTF + PREFERENCE_cotf_elements[i] + "_"
								+ PREFERENCE_cotf_parameters[2]).trim();
					
					log.debug("getOmnivoreTemp_Filename_Element: add_trail_char=<" + add_trail_char
						+ ">");
					
					if ((add_trail_char != null) && (add_trail_char.length() > 0)) {
						add_trail_char = add_trail_char.substring(0, 1);
						log.debug("getOmnivoreTemp_Filename_Element: add_trail_char=<"
							+ add_trail_char + ">");
						element_data_processed.append(add_trail_char);
						log.debug("getOmnivoreTemp_Filename_Element: element_data_processed=<"
							+ element_data_processed + ">");
					}
				}
				
				return element_data_processed.toString(); // This also breaks the for loop
			} // if ... equals(element_key)
		} // for int i...
		return ""; // default return value, if nothing is defined.
	}
	
	public static boolean storeInFilesystem(){
		initGlobalConfig();
		return fsSettingsStore.getBoolean(STOREFS);
	}
	
	public static String getBasepath(){
		initGlobalConfig();
		return fsSettingsStore.getString(BASEPATH);
	}
	
	/**
	 * reload the fs settings store
	 */
	private static void initGlobalConfig(){
		if (fsSettingsStore == null) {
			boolean isGlobal = CoreHub.globalCfg.get(STOREFSGLOBAL, false);
			if (isGlobal) {
				fsSettingsStore = new SettingsPreferenceStore(CoreHub.globalCfg);
			} else {
				fsSettingsStore = new SettingsPreferenceStore(CoreHub.localCfg);
			}
		}
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
		;
		
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
	
}
