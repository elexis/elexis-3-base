/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.unibe.iam.scg.archie.ArchieActivator;

/**
 * <p>
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * </p>
 *
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 * </p>
 */

public class ArchiePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructs an ArchiePreferencePage
	 */
	public ArchiePreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		this.setPreferenceStore(ArchieActivator.getInstance().getPreferenceStore());
		this.setDefaults();

		this.setDescription(
				"Archie settings page. Use the following input fields to manipulate Archie's default behaviour and adjust to fit your preferences.");
	}

	/**
	 * Sets the default preferences for Archie.
	 */
	private void setDefaults() {
		IPreferenceStore preferences = this.getPreferenceStore();
		preferences.setDefault(PreferenceConstants.P_COHORT_SIZE, 5);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		Composite parent = this.getFieldEditorParent();

		IntegerFieldEditor cohortSizeEditor = new IntegerFieldEditor(PreferenceConstants.P_COHORT_SIZE,
				"Cohort size in Dashboard charts: ", parent);

		cohortSizeEditor.setValidRange(1, 99);
		cohortSizeEditor.setErrorMessage("You must set a cohort size value greater then 0 but smaller then 99.");

		this.addField(cohortSizeEditor);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		((SettingsPreferenceStore) this.getPreferenceStore()).flush();
		return super.performOk();
	}

}