/**
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 * All the rest is done generically. See plug-in elexis-importer.
 *
 */

package ch.elexis.laborimport.RischBern;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.OsPathEditorGroup;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditor;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String JAR_PATH = RischBernSettings.JAR_PATH;
	public static final String INI_PATH = RischBernSettings.INI_PATH;
	public static final String DL_DIR = RischBernSettings.DL_DIR;

	private BooleanFieldEditor bStoreGlobal;

	private OsPathEditorGroup pathGroup;

	private URIFieldEditorComposite jarEditor;
	private URIFieldEditorComposite iniEditor;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(RischBernSettings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(RischBernSettings.CFG_PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		pathGroup = new OsPathEditorGroup(getFieldEditorParent(), SWT.NONE);
		jarEditor = pathGroup.addPathEditor(RischBernSettings.JAR_PATH, Messages.PreferencePage_JMedTrasferJar);
		iniEditor = pathGroup.addPathEditor(RischBernSettings.INI_PATH, Messages.PreferencePage_JMedTrasferJni);
		pathGroup.addPathEditor(RischBernSettings.DL_DIR, Messages.PreferencePage_DownloadDir);

		setOptionalHint(jarEditor);
		setOptionalHint(iniEditor);
	}

	private void setOptionalHint(URIFieldEditorComposite editor) {
		((URIFieldEditor) editor.getFieldEditor()).getTextControl(editor).setMessage("Optional"); //$NON-NLS-1$
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStores(RischBernSettings.isStoreGlobal());
		return control;
	}

	private void updatePathStores(boolean global) {
		pathGroup.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		pathGroup.adjustHorizontalSpan();
	}

	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub
	}
}
