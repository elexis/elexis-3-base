/*******************************************************************************
 * Copyright (c) 2009, SGAM Informatics and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc.fire.ui;

import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.DateTimeFieldEditor;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Sticker;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	static final String EXPL_BD_SYST = "BD syst.";
	public static final String CFG_BD_SYST = "icpc/fire/field_bd_syst"; //$NON-NLS-1$
	static final String EXPL_BD_DIAST = "BD diast.";
	public static final String CFG_BD_DIAST = "icpc/fire/field_bd_diast"; //$NON-NLS-1$
	static final String EXPL_PULS = "Puls";
	public static final String CFG_PULS = "icpc/fire/field_puls"; //$NON-NLS-1$
	static final String EXPL_HEIGHT = "Grösse";
	public static final String CFG_HEIGHT = "icpc/fire/field_groesse"; //$NON-NLS-1$
	static final String EXPL_BU = "Bauchumfang";
	public static final String CFG_BU = "icpc/fire/field_bu"; //$NON-NLS-1$
	static final String EXPL_WEIGHT = "Gewicht";
	public static final String CFG_WEIGHT = "icpc/fire/field_gewicht"; //$NON-NLS-1$
	public static final String CFG_BASE_IS_HAM_STICKER = "icpc/fire/isHamSticker/"; //$NON-NLS-1$
	public static final String CFGPARAM = "ICPC_FIRE_LAST_UPLOAD"; //$NON-NLS-1$

	public Preferences() {
		super("Fire", GRID); //$NON-NLS-1$

	}

	@Override
	protected void createFieldEditors() {
		Label expl = new Label(getFieldEditorParent(), SWT.WRAP);
		expl.setText(
				"Geben Sie bitte für jedes der folgenden Felder\nan, wie der entsprechende Befund definiert ist.\nVerwenden Sie dafür die Notation Name:Feld");
		expl.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		addField(new StringFieldEditor(CFG_BD_SYST, EXPL_BD_SYST, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_BD_DIAST, EXPL_BD_DIAST, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_PULS, EXPL_PULS, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_HEIGHT, EXPL_HEIGHT, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_WEIGHT, EXPL_WEIGHT, getFieldEditorParent()));
		addField(new StringFieldEditor(CFG_BU, EXPL_BU, getFieldEditorParent()));

		createSeparator(true);
		Label lblStickerConfig = new Label(getFieldEditorParent(), SWT.WRAP);
		lblStickerConfig.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		lblStickerConfig.setText(
				"Selektieren Sie die Sticker die einen Patient als Mitglied in einem\nintegrierten Versorgungsmodell markieren.");

		List<Sticker> patientStickers = Sticker.getStickersForClass(Patient.class);
		for (Sticker sticker : patientStickers) {
			BooleanFieldEditor bfe = new BooleanFieldEditor(CFG_BASE_IS_HAM_STICKER + sticker.getId(),
					sticker.getLabel(), getFieldEditorParent());
			addField(bfe);
		}

		createSeparator(false);
		addField(new DateTimeFieldEditor(CFGPARAM, "Datum des letzten Exports: ", getFieldEditorParent(), true));
	}

	private void createSeparator(boolean addEmptyLineBefore) {
		if (addEmptyLineBefore) {
			new Label(getFieldEditorParent(), SWT.None);
		}
		Label separator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		// default date must be specified otherwise DateTimeFieldEditor shows the
		// current date for null
		getPreferenceStore().setDefault(CFGPARAM, "20180101"); //$NON-NLS-1$
	}

	@Override
	public void performApply() {
		super.performApply();
	}

}
