/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.preference;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.OsPathEditorGroup;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static String EHC_OUTPUTDIR = "at.medevit.elexis.ehc.ui.output.dir"; //$NON-NLS-1$
	public static String EHC_INPUTDIR = "at.medevit.elexis.ehc.ui.input.dir"; //$NON-NLS-1$

	private BooleanFieldEditor bStoreGlobal;

	private OsPathEditorGroup pathGroup;

	public PreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.USER));
		setDescription("e-Health Connector Einstellungen");

		initDirectories();
	}

	public static void initDirectories() {
		if (ConfigServiceHolder.getUser(EHC_OUTPUTDIR, "notset").equals("notset")) { //$NON-NLS-1$ //$NON-NLS-2$
			File outputDir = new File(getDefaultOutputDir());
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			ConfigServiceHolder.setUser(EHC_OUTPUTDIR, getDefaultOutputDir());
		}

		if (ConfigServiceHolder.getUser(EHC_INPUTDIR, "notset").equals("notset")) { //$NON-NLS-1$ //$NON-NLS-2$
			File inputDir = new File(getDefaultInputDir());
			if (!inputDir.exists()) {
				inputDir.mkdirs();
			}
			ConfigServiceHolder.setUser(EHC_INPUTDIR, getDefaultInputDir());
		}
	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(EhcSettings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(EhcSettings.CFG_PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		pathGroup = new OsPathEditorGroup(getFieldEditorParent(), SWT.NONE);
		pathGroup.addPathEditor(EHC_OUTPUTDIR, "Standard Ausgabeverzeichnis");
		pathGroup.addPathEditor(EHC_INPUTDIR, "Standard Eingangsverzeichnis");
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStores(EhcSettings.isStoreGlobal());
		return control;
	}

	private void updatePathStores(boolean global) {
		pathGroup.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.USER));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		pathGroup.adjustHorizontalSpan();
	}

	public static String getDefaultOutputDir() {
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "output"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getDefaultInputDir() {
		return CoreHub.getWritableUserDir() + File.separator + "eHC" + File.separator + "input"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
