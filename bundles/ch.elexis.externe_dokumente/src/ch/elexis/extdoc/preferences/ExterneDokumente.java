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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.ICategory;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.extdoc.Messages;
import ch.elexis.extdoc.omnivore.OmnivoreImporter;

/**
 * Einstellungen zur Verknüpfung externen Dokumenten
 *
 * @author Daniel Lutz
 */
public class ExterneDokumente extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor bStoreGlobal;

	private final List<URIFieldEditorComposite> pathEditors = new ArrayList<>();

	public ExterneDokumente() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
		setDescription(Messages.ExterneDokumente_externe_dokumente);
	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor sfe;

		bStoreGlobal = new BooleanFieldEditor(ExtDocSettings.CFG_PATHS_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(ExtDocSettings.CFG_PATHS_GLOBAL, global);
					updatePathStores(global);
				}
			}
		};
		addField(bStoreGlobal);

		PreferenceConstants.PathElement[] prefElems = PreferenceConstants.getPrefenceElements();
		for (int j = 0; j < prefElems.length; j++) {
			sfe = new StringFieldEditor(prefElems[j].prefName,
					String.format(Messages.ExterneDokumente_shorthand_for_path, j), getFieldEditorParent());
			sfe.setTextLimit(8);
			addField(sfe);
			pathEditors.add(createPathEditor(prefElems[j].prefBaseDir, Messages.ExterneDokumente_path_name_preference));
		}
		sfe = new StringFieldEditor(PreferenceConstants.CONCERNS, Messages.ExterneDokumente_Concerns,
				getFieldEditorParent());
		sfe.setTextLimit(60);
		addField(sfe);
		URIFieldEditorComposite mailEditor = createPathEditor(PreferenceConstants.EMAIL_PROGRAM,
				Messages.ExterneDokumente_email_app);
		mailEditor.getLabelControl().setToolTipText(
				"Programm das zum Verschicken von E-Mails verwendet werden soll, falls leer wird dir URL mailto: verwendet, welche keine Anhänge unterstützt");
		pathEditors.add(mailEditor);

		OmnivoreImporter importer = new OmnivoreImporter();
		Button omnivoreBtn = new Button(getFieldEditorParent(), SWT.PUSH);
		omnivoreBtn.setText("Dateien in Omnivore importieren");
		omnivoreBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Optional<ICategory> importCategory = importer.getCategory();
				importCategory.ifPresent(category -> {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
					try {
						dialog.run(true, true, new IRunnableWithProgress() {

							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
								importer.importAll(category, monitor);
							}
						});
					} catch (InvocationTargetException | InterruptedException ex) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
								"Beim Import ist ein Fehler aufgetreten.");
						LoggerFactory.getLogger(getClass()).error("Exception on external file import", ex);
					}
				});
			}
		});
		omnivoreBtn.setEnabled(importer.isAvailable());
	}

	private URIFieldEditorComposite createPathEditor(String preferenceName, String labelText) {
		URIFieldEditorComposite editor = new URIFieldEditorComposite(preferenceName, labelText,
				getFieldEditorParent(), SWT.NONE);
		editor.setEmptyStringAllowed(true);
		return editor;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updatePathStores(ExtDocSettings.isStoreGlobal());
		return control;
	}

	private void updatePathStores(boolean global) {
		ConfigServicePreferenceStore store = new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL);
		for (URIFieldEditorComposite editor : pathEditors) {
			if (!editor.isDisposed()) {
				editor.setPreferenceStore(store);
			}
		}
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (!(getFieldEditorParent().getLayout() instanceof GridLayout)) {
			return;
		}
		int numColumns = ((GridLayout) getFieldEditorParent().getLayout()).numColumns;
		for (URIFieldEditorComposite editor : pathEditors) {
			if (!editor.isDisposed() && editor.getLayoutData() instanceof GridData) {
				((GridData) editor.getLayoutData()).horizontalSpan = numColumns;
			}
		}
	}

	public void init(IWorkbench workbench) {
	}
}
