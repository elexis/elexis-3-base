/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.laborimport.hl7.universal;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.ComboFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CFG_DIRECTORY = HL7ImportDirectory.CFG_DIRECTORY;
	public static final String CFG_DIRECTORY_AUTOIMPORT = "hl7/autoimport";

	private IDocumentManager docManager;

	private ComboFieldEditor comboField;

	private BooleanFieldEditor bStoreGlobal;
	private URIFieldEditorComposite directoryEditor;

	public Preferences() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));

		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os instanceof IDocumentManager) {
			this.docManager = (IDocumentManager) os;
		}
	}

	@Override
	protected void createFieldEditors() {
		HL7ImportDirectory.migrateLegacySetting();

		bStoreGlobal = new BooleanFieldEditor(HL7ImportDirectory.CFG_DIRECTORY_GLOBAL,
				Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(HL7ImportDirectory.CFG_DIRECTORY_GLOBAL, global);
					updateDirectoryStore(global);
				}
			}
		};
		addField(bStoreGlobal);

		directoryEditor = new URIFieldEditorComposite(HL7ImportDirectory.CFG_DIRECTORY, Messages.Prefs_ImportDirectory,
				getFieldEditorParent(), SWT.NONE);
		directoryEditor.setEmptyStringAllowed(true);

		addField(new BooleanFieldEditor(HL7Parser.CFG_IMPORT_ENCDATA, Messages.Prefs_ImportAttachedFiles,
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(CFG_DIRECTORY_AUTOIMPORT, "Verzeichnis überwachen und automatisch importieren",
				getFieldEditorParent()));

		if (docManager != null) {
			String[] categories = docManager.getCategories();
			if (categories != null && categories.length > 0) {
				comboField = new ComboFieldEditor(HL7Parser.CFG_IMPORT_ENCDATA_CATEGORY,
						"Bitte wählen Sie eine Omnivore-Kategorie für den Import", categories, getFieldEditorParent());
				addField(comboField);
				changeVisiblityOfComboCategory(ConfigServiceHolder.getLocal(HL7Parser.CFG_IMPORT_ENCDATA, false));
			}
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updateDirectoryStore(HL7ImportDirectory.isStoreGlobal());
		return control;
	}

	private void updateDirectoryStore(boolean global) {
		if (directoryEditor == null || directoryEditor.isDisposed()) {
			return;
		}
		directoryEditor.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (directoryEditor != null && !directoryEditor.isDisposed()
				&& getFieldEditorParent().getLayout() instanceof GridLayout
				&& directoryEditor.getLayoutData() instanceof GridData) {
			((GridData) directoryEditor.getLayoutData())
					.horizontalSpan = ((GridLayout) getFieldEditorParent().getLayout()).numColumns;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);

		if (event.getSource() instanceof FieldEditor) {
			FieldEditor bf = (FieldEditor) event.getSource();
			if (HL7Parser.CFG_IMPORT_ENCDATA.equals(bf.getPreferenceName())) {
				changeVisiblityOfComboCategory(event.getNewValue() == Boolean.TRUE);
			}
		}
	}

	private void changeVisiblityOfComboCategory(boolean visible) {
		if (comboField != null) {
			comboField.getLabelControl(getFieldEditorParent()).setVisible(visible);
			comboField.getCombo().setVisible(visible);
		}
	}

	@Override
	public void init(IWorkbench workbench) {

	}

}
