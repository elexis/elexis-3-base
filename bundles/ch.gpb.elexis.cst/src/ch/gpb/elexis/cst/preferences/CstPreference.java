/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
/**
 * @author daniel ludin ludin@swissonline.ch
 * 27.06.2015
 *
 */
package ch.gpb.elexis.cst.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.gpb.elexis.cst.Messages;

public class CstPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static String CST_IDENTIFIER_OMNIVORE = "ch.gpb.elexis.ident.omnivore";
	public static String CST_IDENTIFIER_BRIEFE = "ch.gpb.elexis.ident.briefe";
	public static String CST_IDENTIFIER_LATESTPATH = "ch.gpb.elexis.ident.latestpath";
	public static String CST_IDENTIFIER_FILEPREFIX = "ch.gpb.elexis.ident.fileprefix";
	public static String CST_IDENTIFIER_FILEFORMAT = "ch.gpb.elexis.ident.fileformat";

	public CstPreference() {
		super(Messages.Cst_Preference_Einstellungen, GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.USER));

		initIdentifiers();
	}

	public static void initIdentifiers() {
		if (ConfigServiceHolder.getUser(CST_IDENTIFIER_OMNIVORE, "notset").equals("notset")) {
			ConfigServiceHolder.setUser(CST_IDENTIFIER_OMNIVORE, getDefaultIdentifierOmnivore());
		}

		if (ConfigServiceHolder.getUser(CST_IDENTIFIER_BRIEFE, "notset").equals("notset")) {
			ConfigServiceHolder.setUser(CST_IDENTIFIER_BRIEFE, getDefaultIdentifierBriefe());
		}

		if (ConfigServiceHolder.getUser(CST_IDENTIFIER_FILEPREFIX, "notset").equals("notset")) {
			ConfigServiceHolder.setUser(CST_IDENTIFIER_FILEPREFIX, getDefaultFilePrefix());
		}
		if (ConfigServiceHolder.getUser(CST_IDENTIFIER_FILEFORMAT, "notset").equals("notset")) {
			ConfigServiceHolder.setUser(CST_IDENTIFIER_FILEFORMAT, getDefaultFileFormat());
		}

	}

	@Override
	protected void createFieldEditors() {

		// Composite parent = getFieldEditorParent();

		Label lblDoc = new Label(getFieldEditorParent(), SWT.NONE);
		lblDoc.setText("Anzeigekriterien f�r CST-bezogene Dokumente:");
		GridData gdDoc = new GridData();
		gdDoc.horizontalSpan = 2;
		lblDoc.setLayoutData(gdDoc);

		addField(new StringFieldEditor(CST_IDENTIFIER_OMNIVORE, "Omnivore Document Kategorie", getFieldEditorParent()));
		addField(
				new StringFieldEditor(CST_IDENTIFIER_BRIEFE, "Briefe (Suchkriterium Betreff)", getFieldEditorParent()));

		Label lblFile = new Label(getFieldEditorParent(), SWT.NONE);
		lblFile.setText("File Export Einstellungen:");
		GridData gdFile = new GridData();
		gdFile.horizontalSpan = 2;
		gdFile.verticalIndent = 30;
		lblFile.setLayoutData(gdFile);

		addField(new StringFieldEditor(CST_IDENTIFIER_FILEPREFIX, "Prefix f�r File-Namen", getFieldEditorParent()));
		addField(new StringFieldEditor(CST_IDENTIFIER_FILEFORMAT, "Datumsformat f�r File-Namen (kein : verwenden!)",
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performOk() {
		boolean ret = super.performOk();

		ContextServiceHolder.get().getActiveUser().ifPresent(u -> ContextServiceHolder.get().setActiveUser(u));

		return ret;
	}

	public static String getDefaultIdentifierOmnivore() {
		return "CST";
	}

	public static String getDefaultIdentifierBriefe() {
		return "CST";
	}

	public static String getDefaultFilePrefix() {
		return "cst";
	}

	public static String getDefaultFileFormat() {
		return "yyyyMMdd_HH-mm-ss";
	}

}
