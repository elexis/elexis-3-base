/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 * Adapted to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 */

package ch.elexis.laborimport.bioanalytica;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditor;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String JAR_PATH = BioanalyticaSettings.JAR_PATH;
	public static final String INI_PATH = BioanalyticaSettings.INI_PATH;
	public static final String DL_DIR = BioanalyticaSettings.DL_DIR;

	private BooleanFieldEditor bStoreGlobal;

	private URIFieldEditorComposite jarEditor;
	private URIFieldEditorComposite iniEditor;
	private URIFieldEditorComposite dirEditor;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(BioanalyticaSettings.CFG_PATHS_GLOBAL,
				Messages.PreferencePage_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(BioanalyticaSettings.CFG_PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		jarEditor = createPathEditor(BioanalyticaSettings.JAR_PATH, Messages.PreferencePage_JMedTrasferJar);
		iniEditor = createPathEditor(BioanalyticaSettings.INI_PATH, Messages.PreferencePage_JMedTrasferJni);
		dirEditor = createPathEditor(BioanalyticaSettings.DL_DIR, Messages.PreferencePage_DownloadDir);

		setOptionalHint(jarEditor);
		setOptionalHint(iniEditor);
	}

	private URIFieldEditorComposite createPathEditor(String preferenceName, String labelText) {
		URIFieldEditorComposite editor = new URIFieldEditorComposite(preferenceName, labelText, getFieldEditorParent(),
				SWT.NONE);
		editor.setEmptyStringAllowed(true);
		return editor;
	}

	private void setOptionalHint(URIFieldEditorComposite editor) {
		((URIFieldEditor) editor.getFieldEditor()).getTextControl(editor).setMessage("Optional"); //$NON-NLS-1$
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStores(BioanalyticaSettings.isStoreGlobal());
		return control;
	}

	private void updatePathStores(boolean global) {
		ConfigServicePreferenceStore store = new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL);
		for (URIFieldEditorComposite editor : getPathEditors()) {
			editor.setPreferenceStore(store);
		}
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (!(getFieldEditorParent().getLayout() instanceof GridLayout)) {
			return;
		}
		int numColumns = ((GridLayout) getFieldEditorParent().getLayout()).numColumns;
		for (URIFieldEditorComposite editor : getPathEditors()) {
			if (editor.getLayoutData() instanceof GridData) {
				((GridData) editor.getLayoutData()).horizontalSpan = numColumns;
			}
		}
	}

	private List<URIFieldEditorComposite> getPathEditors() {
		List<URIFieldEditorComposite> editors = new ArrayList<>(3);
		for (URIFieldEditorComposite editor : new URIFieldEditorComposite[] { jarEditor, iniEditor, dirEditor }) {
			if (editor != null && !editor.isDisposed()) {
				editors.add(editor);
			}
		}
		return editors;
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
