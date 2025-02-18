/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui;

import static at.medevit.elexis.gdt.constants.GDTPreferenceConstants.CFG_GDT_CHARSET;
import static at.medevit.elexis.gdt.constants.GDTPreferenceConstants.CFG_GDT_FILETRANSFER_DELETE_NON_GDT_FILES;
import static at.medevit.elexis.gdt.constants.GDTPreferenceConstants.CFG_GDT_FILETRANSFER_SHORTNAME;
import static at.medevit.elexis.gdt.constants.GDTPreferenceConstants.CFG_GDT_ID;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.gdt.constants.GDTConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class GDTPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static String[][] comboCharsetSelektor = new String[][] {
			{ "7Bit", GDTConstants.ZEICHENSATZ_7BIT_CHARSET_STRING }, //$NON-NLS-1$
			{ "IBM (Standard) CP 437", GDTConstants.ZEICHENSATZ_IBM_CP_437_CHARSET_STRING }, //$NON-NLS-1$
			{ "ISO8859-1 (ANSI) CP 1252", GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252_CHARSET_STRING } }; //$NON-NLS-1$

	/**
	 * Create the preference page.
	 */
	public GDTPreferencePage() {
		super(FLAT);
		setTitle("Gerätedatenträger");
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		{
			Composite composite = getFieldEditorParent();
			StringFieldEditor stringFieldEditorGDTID = new StringFieldEditor(CFG_GDT_ID, "GDT Identität", composite);
			stringFieldEditorGDTID.getLabelControl(composite).setText("GDT Identität (max 8 Zeichen)");
			stringFieldEditorGDTID.getLabelControl(composite).setToolTipText(
					"Die GDT-ID dient zur eindeutigen Identifikation der an der Kommunikation beteiligten Komonenten.");
			stringFieldEditorGDTID.getTextControl(composite).setTextLimit(8);
			addField(stringFieldEditorGDTID);
		}
		{
			Composite composite = getFieldEditorParent();
			StringFieldEditor exchangeKuerzel = new StringFieldEditor(CFG_GDT_FILETRANSFER_SHORTNAME,
					"Kommunikations-Kürzel (4 Zeichen)", composite);
			exchangeKuerzel.getTextControl(composite).setTextLimit(4);
			addField(exchangeKuerzel);
		}
		{
			Composite composite = getFieldEditorParent();
			ComboFieldEditor comboFieldEditor = new ComboFieldEditor(CFG_GDT_CHARSET, "Standard Zeichensatz",
					comboCharsetSelektor, composite);
			comboFieldEditor.getLabelControl(composite)
					.setToolTipText("Zeichensatz welcher zum Datenaustausch verwendet werden soll");
			addField(comboFieldEditor);
		}
		{
			BooleanFieldEditor deleteNonGDTFiles = new BooleanFieldEditor(CFG_GDT_FILETRANSFER_DELETE_NON_GDT_FILES,
					"Nicht GDT Dateien aus Daten-Austausch-Verzeichnissen entfernen", getFieldEditorParent());
			addField(deleteNonGDTFiles);
		}
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

}
