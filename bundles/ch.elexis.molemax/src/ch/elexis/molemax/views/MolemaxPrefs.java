/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.views;

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
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.MolemaxSettings;

public class MolemaxPrefs extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String BASEDIR = MolemaxSettings.BASEDIR;

	private BooleanFieldEditor bStoreGlobal;

	private URIFieldEditorComposite baseDirEditor;

	public MolemaxPrefs() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(MolemaxSettings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(MolemaxSettings.CFG_PATHS_GLOBAL, global);
					updatePathStore(global);
				}
			}
		};
		addField(bStoreGlobal);

		baseDirEditor = new URIFieldEditorComposite(MolemaxSettings.BASEDIR, Messages.MolemaxPrefs_basedir,
				getFieldEditorParent(), SWT.NONE);
		baseDirEditor.setEmptyStringAllowed(true);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStore(MolemaxSettings.isStoreGlobal());
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
