/*******************************************************************************
 * Copyright (c) 2006-2011, Daniel Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Lutz - initial implementation
 *    Niklaus Giger - new layout with subdirectories
 *    
 *******************************************************************************/
package ch.elexis.extdoc.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.omnivore.OmnivoreImporter;


/**
 * Einstellungen zur Verknüpfung externen Dokumenten
 * 
 * @author Daniel Lutz
 */
public class ExterneDokumente extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public ExterneDokumente(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		setDescription(Messages.ExterneDokumente_externe_dokumente);
	}
	
	@Override
	protected void createFieldEditors(){
		DirectoryFieldEditor dfe;
		StringFieldEditor sfe;
		FileFieldEditor ffe;
	
		PreferenceConstants.PathElement[] prefElems = PreferenceConstants.getPrefenceElements();
		for (int j = 0; j < prefElems.length; j++) {
			sfe =
				new StringFieldEditor(prefElems[j].prefName, String.format(
					Messages.ExterneDokumente_shorthand_for_path, j), getFieldEditorParent());
			sfe.setTextLimit(8);
			addField(sfe);
			dfe =
				new DirectoryFieldEditor(prefElems[j].prefBaseDir,
					Messages.ExterneDokumente_path_name_preference, getFieldEditorParent());
			addField(dfe);
		}
		sfe =
			new StringFieldEditor(PreferenceConstants.CONCERNS, Messages.ExterneDokumente_Concerns,
				getFieldEditorParent());
		sfe.setTextLimit(60);
		addField(sfe);
		Composite composite = getFieldEditorParent();
		ffe = new FileFieldEditor(PreferenceConstants.EMAIL_PROGRAM, 
			Messages.ExterneDokumente_email_app,  getFieldEditorParent());
		ffe.getLabelControl(composite).setToolTipText("Programm das zum Verschicken von E-Mails verwendet werden soll, falls leer wird dir URL mailto: verwendet, welche keine Anhänge unterstützt");
		addField(ffe);

		OmnivoreImporter importer = new OmnivoreImporter();
		Button omnivoreBtn = new Button(getFieldEditorParent(), SWT.PUSH);
		omnivoreBtn.setText("Dateien in Omnivore importieren");
		omnivoreBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				Optional<ICategory> importCategory = importer.getCategory();
				importCategory.ifPresent(category -> {
					ProgressMonitorDialog dialog =
						new ProgressMonitorDialog(Display.getDefault().getActiveShell());
					try {
						dialog.run(true, true, new IRunnableWithProgress() {
							
							@Override
							public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException{
								importer.importAll(category, monitor);
							}
						});
					} catch (InvocationTargetException | InterruptedException ex) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
							"Beim Import ist ein Fehler aufgetreten.");
						LoggerFactory.getLogger(getClass())
							.error("Exception on external file import", ex);
					}
				});
			}
		});
		omnivoreBtn.setEnabled(importer.isAvailable());
	}
	
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));		
	}
}
