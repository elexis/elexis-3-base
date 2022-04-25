/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.defaultfilecp.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.gdt.defaultfilecp.FileCommPartner;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class GDTPreferencePageFileTransfer extends PreferencePage implements IWorkbenchPreferencePage {

	private Composite editorParent;

	List<FileCommPartnerComposite> fileCommPartnerComposites = new ArrayList<FileCommPartnerComposite>();

	/**
	 * Create the preference page.
	 */
	public GDTPreferencePageFileTransfer() {
		setTitle("Datei-Kommunikation");
	}

	@Override
	protected Control createContents(Composite parent) {
		fileCommPartnerComposites.clear();
		editorParent = new Composite(parent, SWT.NONE);
		editorParent.setLayout(new GridLayout(3, false));

		Button btnCfg = new Button(editorParent, SWT.CHECK);
		btnCfg.setText("Dateisystem Einstellungen global speichern");
		btnCfg.setSelection(FileCommPartner.isFileTransferGlobalConfigured());
		btnCfg.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				FileCommPartner.setFileTransferConfiguration(button.getSelection());

				for (Control c : editorParent.getChildren()) {
					if (c instanceof FileCommPartnerComposite) {
						c.dispose();
					}
				}
				createContent();
				editorParent.layout(true, true);
			}
		});
		createContent();
		return editorParent;
	}

	private void createContent() {
		ScrolledComposite scrolledComposite = findScrolledComposite();
		for (String id : FileCommPartner.getAllFileCommPartnersArray()) {
			createNewFileCommPartnerComposite(id, null, scrolledComposite);
		}
	}

	public void createNewFileCommPartnerComposite(String id, String name, ScrolledComposite scrolledComposite) {
		FileCommPartner fileCommPartner = new FileCommPartner(id);
		if (name != null) {
			fileCommPartner.getSettings().setValue(fileCommPartner.getFileTransferName(), name);
		}
		fileCommPartnerComposites
				.add(new FileCommPartnerComposite(this, scrolledComposite, editorParent, fileCommPartner));
	}

	private ScrolledComposite findScrolledComposite() {
		Composite parent = editorParent;
		for (int i = 0; i < 10; i++) {
			parent = parent.getParent();
			if (parent instanceof ScrolledComposite) {
				return (ScrolledComposite) parent;
			}

		}
		return null;
	}

	@Override
	public boolean performOk() {
		for (FileCommPartnerComposite fileCommPartnerComposite : fileCommPartnerComposites) {
			fileCommPartnerComposite.save();
		}
		return super.performOk();
	}

	/**
	 * Initialize the preferference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(
				FileCommPartner.isFileTransferGlobalConfigured() ? Scope.GLOBAL : Scope.LOCAL));
	}
}
