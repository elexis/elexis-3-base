// Copyright 2010 (c) Niklaus Giger <niklaus.giger@member.fsf.org>
/**
 * (c) 2007-2010 by G. Weirich
 * All rights reserved
 *
 * This plug-in provides only a importer for one laboratory.
 * All the rest is done generically. See plug-in elexis-importer.
 *
 */

package ch.elexis.laborimport.LG1;

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

	public static final String JAR_PATH = LG1Settings.JAR_PATH;
	public static final String INI_PATH = LG1Settings.INI_PATH;
	public static final String DL_DIR = LG1Settings.DL_DIR;

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
		bStoreGlobal = new BooleanFieldEditor(LG1Settings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(LG1Settings.CFG_PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		pathGroup = new OsPathEditorGroup(getFieldEditorParent(), SWT.NONE);
		jarEditor = pathGroup.addPathEditor(LG1Settings.JAR_PATH, Messages.PreferencePage_JMedTrasferJar);
		iniEditor = pathGroup.addPathEditor(LG1Settings.INI_PATH, Messages.PreferencePage_JMedTrasferJni);
		pathGroup.addPathEditor(LG1Settings.DL_DIR, Messages.PreferencePage_DownloadDir);

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
		updatePathStores(LG1Settings.isStoreGlobal());
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
