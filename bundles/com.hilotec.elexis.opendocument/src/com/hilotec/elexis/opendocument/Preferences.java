/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation
 *
 *******************************************************************************/

package com.hilotec.elexis.opendocument;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String BASE = "com.hilotec.elexis.ooo."; //$NON-NLS-1$
	public static final String P_EDITOR = BASE + "editorpath"; //$NON-NLS-1$
	public static final String P_EDITARGS = BASE + "editargs"; //$NON-NLS-1$
	public static final String P_PRINTARGS = BASE + "printargs"; //$NON-NLS-1$
	public static final String P_PDFCONVERTER = BASE + "pdfpath"; //$NON-NLS-1$
	public static final String P_PDFARGS = BASE + "pdfargs"; //$NON-NLS-1$
	public static final String P_WRAPPERSCRIPT = BASE + "wrapperscript"; //$NON-NLS-1$
	public static final String P_EDITOR_DEFAULT = "lowriter"; //$NON-NLS-1$
	public static final String P_EDITARGS_DEFAULT = ""; //$NON-NLS-1$
	public static final String P_PRINTARGS_DEFAULT = "--headless -p"; //$NON-NLS-1$
	public static final String P_PDFCONVERTER_DEFAULT = "lowriter"; //$NON-NLS-1$
	public static final String P_PDFARGS_DEFAULT = "--headless --convert-to pdf"; //$NON-NLS-1$
	public static final boolean P_WRAPPERSCRIPT_DEFAULT = true;

	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.localCfg);

	private static StringFieldEditor editorField = null;
	private static MultilineFieldEditor editorArgsField = null;
	private static MultilineFieldEditor printArgsField = null;
	private static StringFieldEditor pdfConverterField = null;
	private static MultilineFieldEditor pdfArgsField = null;
	private static BooleanFieldEditor wrapperActivatedField = null;

	public Preferences() {
		super(GRID);
		setPreferenceStore(prefs);
		setDescription("Einstellungen für das Hilotec-OpenDocument-Plugin");
	}

	private static String findExecutableOnPath(String name) {
		// Check full path given
		if (!name.isEmpty()) {
			File file = new File(name);
	        if (file.isFile() && file.canExecute()) {
	            return file.getAbsolutePath();
	        }
		}
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator)) { //$NON-NLS-1$
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute()) {
	            return file.getAbsolutePath();
	        }
	    }
	    return "";
	}
	@Override
	protected void createFieldEditors() {
		prefs.setDefault(P_EDITOR, P_EDITOR_DEFAULT);
		prefs.setDefault(P_EDITARGS, P_EDITARGS_DEFAULT);
		prefs.setDefault(P_PRINTARGS, P_PRINTARGS_DEFAULT);
		prefs.setDefault(P_PDFCONVERTER, P_PDFCONVERTER_DEFAULT);
		prefs.setDefault(P_PDFARGS, P_PDFARGS_DEFAULT);
		prefs.setDefault(P_WRAPPERSCRIPT, P_WRAPPERSCRIPT_DEFAULT);
		editorField = new StringFieldEditor(P_EDITOR, "OpenDocument Editor", getFieldEditorParent());
		addField(editorField);
		editorField.setStringValue(findExecutableOnPath(prefs.getString(P_EDITOR)));
		editorField.getTextControl(getFieldEditorParent()).setToolTipText(
				"Voller Pfad oder Name der Programms zum Bearbeiten von ODF-Dateien");
		editorArgsField = new MultilineFieldEditor(P_EDITARGS, "Argumente zum Editieren", 5, SWT.V_SCROLL, true,
				getFieldEditorParent());
		addField(editorArgsField);

		printArgsField = new MultilineFieldEditor(P_PRINTARGS, "Argumente zum Drucken", 5, SWT.V_SCROLL, true,
				getFieldEditorParent());
		addField(printArgsField);

		pdfConverterField = new StringFieldEditor(P_PDFCONVERTER, "PDF-Konverter", getFieldEditorParent());
		addField(pdfConverterField);
		pdfConverterField.getTextControl(getFieldEditorParent()).setToolTipText(
				"Voller Pfad oder Name der Programms zum Konvertieren von ODF-Dateien in PDF");
		pdfConverterField.setStringValue(findExecutableOnPath(prefs.getString(P_PDFCONVERTER)));
		pdfArgsField = new MultilineFieldEditor(P_PDFARGS, "Argumente fuer PDF-Konvertierung", 5, SWT.V_SCROLL, false,
				getFieldEditorParent());
		addField(pdfArgsField);
		wrapperActivatedField = new BooleanFieldEditor(P_WRAPPERSCRIPT, "Wrapper Skript aktivieren",
				getFieldEditorParent());
		addField(wrapperActivatedField);
	}

	@Override
	public void init(IWorkbench arg0) {

	}
	@Override
	public boolean performOk(){
		String editorPath  = findExecutableOnPath(editorField.getStringValue());
		String pdfConverterPath = findExecutableOnPath(pdfConverterField.getStringValue());
		if (editorPath.isEmpty() || editorPath.isEmpty()) {
			return false;
		} else {
			editorField.setStringValue(editorPath);
			pdfConverterField.setStringValue(pdfConverterPath);
			return super.performOk();
		}
	}
	@Override
	protected void performDefaults() {
		editorField.setStringValue(findExecutableOnPath(P_EDITOR_DEFAULT));
		editorArgsField.setStringValue(P_EDITARGS_DEFAULT);
		printArgsField.setStringValue(P_PRINTARGS_DEFAULT);
		pdfConverterField.setStringValue(findExecutableOnPath(P_PDFCONVERTER_DEFAULT));
		pdfArgsField.setStringValue(P_PDFARGS_DEFAULT);
		prefs.setValue(P_WRAPPERSCRIPT, P_WRAPPERSCRIPT_DEFAULT);
		wrapperActivatedField.loadDefault();

	}
}
