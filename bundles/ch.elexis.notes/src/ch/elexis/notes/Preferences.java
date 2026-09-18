/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.notes;

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
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

/**
 * Settings for the notes-Plugin
 *
 * @author gerry
 *
 */
public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CFGTREE = NotesSettings.BASEDIR;

	private BooleanFieldEditor bStoreGlobal;

	private URIFieldEditorComposite baseDirEditor;

	public Preferences() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(NotesSettings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(NotesSettings.CFG_PATHS_GLOBAL, global);
					updatePathStore(global);
				}
			}
		};
		addField(bStoreGlobal);

		baseDirEditor = new URIFieldEditorComposite(NotesSettings.BASEDIR, Messages.Preferences_basedir,
				getFieldEditorParent(), SWT.NONE);
		baseDirEditor.setEmptyStringAllowed(true);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStore(NotesSettings.isStoreGlobal());
		return control;
	}

	private void updatePathStore(boolean global) {
		if (baseDirEditor == null || baseDirEditor.isDisposed()) {
			return;
		}
		baseDirEditor.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (getFieldEditorParent().getLayout() instanceof GridLayout
				&& baseDirEditor.getLayoutData() instanceof GridData) {
			((GridData) baseDirEditor.getLayoutData()).horizontalSpan = ((GridLayout) getFieldEditorParent()
					.getLayout()).numColumns;
		}
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

}
