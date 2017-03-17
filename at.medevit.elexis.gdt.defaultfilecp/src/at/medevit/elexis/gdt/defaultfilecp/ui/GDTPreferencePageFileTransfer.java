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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.gdt.defaultfilecp.FileCommPartner;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class GDTPreferencePageFileTransfer extends PreferencePage
		implements IWorkbenchPreferencePage {
	
	private IPreferenceStore prefStore;
	private Composite editorParent;
	
	List<FileCommPartnerComposite> fileCommPartnerComposites =
		new ArrayList<FileCommPartnerComposite>();
	
	/**
	 * Create the preference page.
	 */
	public GDTPreferencePageFileTransfer(){
		setTitle("Datei-Kommunikation");
	}
	
	@Override
	protected Control createContents(Composite parent){
		fileCommPartnerComposites.clear();
		editorParent = new Composite(parent, SWT.NONE);
		editorParent.setLayout(new GridLayout(3, false));
		
		for (String name : FileCommPartner.getAllFileCommPartnersArray()) {
			createNewFileCommPartnerComposite(name);
		}
		return editorParent;
	}
	
	public void createNewFileCommPartnerComposite(String name){
		fileCommPartnerComposites
			.add(new FileCommPartnerComposite(this, editorParent, new FileCommPartner(name)));
	}
	
	@Override
	public boolean performOk(){
		for (FileCommPartnerComposite fileCommPartnerComposite : fileCommPartnerComposites) {
			fileCommPartnerComposite.save();
		}
		CoreHub.localCfg.flush();
		return super.performOk();
	}
	
	/**
	 * Initialize the preferference page.
	 */
	public void init(IWorkbench workbench){
		prefStore = new SettingsPreferenceStore(CoreHub.localCfg);
		setPreferenceStore(prefStore);
	}
}
