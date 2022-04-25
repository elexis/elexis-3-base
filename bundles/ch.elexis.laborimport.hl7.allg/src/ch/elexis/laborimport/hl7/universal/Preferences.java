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
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.ComboFieldEditor;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String CFG_DIRECTORY = "hl7/downloaddir";
	public static final String CFG_DIRECTORY_AUTOIMPORT = "hl7/autoimport";

	private IDocumentManager docManager;

	private ComboFieldEditor comboField;

	public Preferences() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));

		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os instanceof IDocumentManager) {
			this.docManager = (IDocumentManager) os;
		}
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(CFG_DIRECTORY, Messages.Prefs_ImportDirectory, getFieldEditorParent()));
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
				changeVisiblityOfComboCategory(CoreHub.localCfg.get(HL7Parser.CFG_IMPORT_ENCDATA, false));
			}
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

	public void init(IWorkbench workbench) {

	}

	@Override
	protected void performApply() {
		super.performApply();
		CoreHub.localCfg.flush();
	}

}
