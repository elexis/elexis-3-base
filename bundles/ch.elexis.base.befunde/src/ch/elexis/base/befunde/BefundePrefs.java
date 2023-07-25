/*******************************************************************************
 * Copyright (c) 2005-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.befunde;

import org.apache.commons.lang3.StringUtils;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;

/**
 * Hier kann der Anwender Einstellungen (Preferences) für das Befunde-Plugin
 * vornehmen. Diese Einstellungsseite wird bei den gemeinsamen Einstellungen
 * (Datei-Einstellungen) eingebaut. Jedes Plugin kann keine bis beliebig viele
 * Einstellungsseiten unter keiner bis beliebig vielen Unterkategorien
 * erstellen.
 *
 * @author Gerry
 *
 *         Here can the user define some Preferences for the "Befunde-Plugin"
 *         (Findings) This adjustment page will be added to the general
 *         adjustments (Datei-Einstellung i.e. "Data-Adjustements"). Each plugin
 *         is able to have, or not, as many as wanted adjustment pages with none
 *         or as many as desired subcategories
 *
 */
public class BefundePrefs extends PreferencePage implements IWorkbenchPreferencePage {

	Map<Object, Object> fields;
	// Combo cbNames;
	// Text vals;
	// String sel;
	Messwert setup;
	String names;
	CTabFolder ctabs;
	int lastIDX;

	public BefundePrefs() {
		super("Befunde"); //$NON-NLS-1$
	}

	/**
	 * Diese Methode erledigt den eigentlichen Aufbau der Seite. Here we create the
	 * contents of the preference page
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Control createContents(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		ctabs = new CTabFolder(ret, SWT.NONE);
		ctabs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ctabs.setLayout(new FillLayout());
		setup = Messwert.getSetup();
		fields = setup.getMap("Befunde"); //$NON-NLS-1$
		names = (String) fields.get("names"); //$NON-NLS-1$
		if (!StringTool.isNothing(names)) {
			for (String f : names.split(Messwert.SETUP_SEPARATOR)) {
				CTabItem ci = new CTabItem(ctabs, SWT.NONE);
				ci.setText(f);
				PrefsPage fp = new PrefsPage(ctabs, fields, f);
				ci.setControl(fp);
			}
		}
		ctabs.setSelection(0);
		lastIDX = 0;
		ctabs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (lastIDX != -1) {
					flush(lastIDX);
					lastIDX = ctabs.getSelectionIndex();
				}
			}
		});

		Composite cButtons = new Composite(ret, SWT.NONE);
		cButtons.setLayout(new FillLayout());
		Button bAdd = new Button(cButtons, SWT.PUSH);
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				InputDialog id = new InputDialog(getShell(), Messages.BefundePrefs_enterNameCaption, // $NON-NLS-1$
						Messages.BefundePrefs_enterNameMessage, StringUtils.EMPTY, // $NON-NLS-1$
						new FindingNameInputValidator());
				if (id.open() == Dialog.OK) {
					String name = id.getValue();
					if (StringTool.isNothing(names)) {
						names = name;
					} else {
						names += Messwert.SETUP_SEPARATOR + name;
					}
					fields.put("names", names); //$NON-NLS-1$
					CTabItem ci = new CTabItem(ctabs, SWT.NONE);
					ci.setText(name);
					PrefsPage fp = new PrefsPage(ctabs, fields, name);
					ci.setControl(fp);
					ctabs.setSelection(ci);
				}
			}

		});
		bAdd.setText(Messages.BefundePrefs_add); // $NON-NLS-1$

		Button bRemove = new Button(cButtons, SWT.PUSH);
		bRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CTabItem ci = ctabs.getSelection();
				if (ci != null) {
					PrefsPage pp = (PrefsPage) ci.getControl();
					if (pp.remove()) {
						names = names.replaceFirst(ci.getText(), StringUtils.EMPTY);
						names = names.replaceAll(Messwert.SETUP_SEPARATOR + Messwert.SETUP_SEPARATOR,
								Messwert.SETUP_SEPARATOR);
						names.replaceFirst(Messwert.SETUP_SEPARATOR + "$", StringUtils.EMPTY); //$NON-NLS-1$
						names = names.replaceFirst("^" + Messwert.SETUP_SEPARATOR, StringUtils.EMPTY); //$NON-NLS-1$
						fields.put("names", names); //$NON-NLS-1$
						lastIDX = -1;
						ci.dispose();
						ctabs.setSelection(0);
					}
				}
			}
		});
		bRemove.setText(Messages.BefundePrefs_deleteText); // $NON-NLS-1$
		if (!AccessControlServiceHolder.get().evaluate(EvACE.of(Messwert.class, Right.DELETE))) {
			bRemove.setEnabled(false);
		}

		Button bRename = new Button(cButtons, SWT.PUSH);
		bRename.setText(Messages.BefundePrefs_renameFinding);
		bRename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem tab = ctabs.getSelection();
				if (tab != null) {
					// ask user for desired new name
					InputDialog id = new InputDialog(getShell(), Messages.BefundePrefs_enterRenameCaption,
							Messages.BefundePrefs_enterRenameMessage, StringUtils.EMPTY,
							new FindingNameInputValidator());
					if (id.open() == Dialog.OK) {
						String oldName = tab.getText();
						String newName = id.getValue();

						PrefsPage pp = (PrefsPage) tab.getControl();
						// renames all relations in DB
						if (pp.rename(newName)) {
							names = names.replaceFirst(oldName, newName); // $NON-NLS-1$
						}
						fields.put("names", names); //$NON-NLS-1$
						tab.setText(newName);
					}
				}
			}
		});

		return ret;
	}

	/**
	 * Hier könnte man Dinge erledigen, die noch vor createContents gemacht werden
	 * müssen.
	 *
	 * Here we are able to solve things that needs to be made before createContents
	 */
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	private void flush(final int idx) {
		CTabItem it = ctabs.getItem(idx);
		PrefsPage pp = (PrefsPage) it.getControl();
		pp.flush();
	}

	@Override
	protected void performApply() {
		performOk();
		int idx = ctabs.getSelectionIndex();
		CTabItem it = ctabs.getItem(idx);
		PrefsPage pp = (PrefsPage) it.getControl();
		pp.load();
	}

	/**
	 * Dies wird ausgeführt, wenn der Anwender auf den "Apply"- bzw, "Übernehmen" -
	 * Knopf klickt.
	 *
	 * This will be executed when the user clicks on the "Apply" or OKButton
	 */
	@Override
	public boolean performOk() {
		int idx = ctabs.getSelectionIndex();
		if (idx != -1) {
			flush(idx);
		}
		setup.setMap("Befunde", fields); //$NON-NLS-1$
		return super.performOk();
	}

	class FindingNameInputValidator implements IInputValidator {

		@Override
		public String isValid(String newText) {
			newText = newText.trim();
			if (newText.endsWith(".")) { //$NON-NLS-1$
				return Messages.BefundePrefs_dotEndingNameNotAllowed;
			}
			return null;
		}

	}
}
