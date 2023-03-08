/*******************************************************************************
 * Copyright (c) 2010-2011, Medelexis AG
 * All rights reserved.
 *******************************************************************************/

package org.iatrix.bestellung.rose;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.hin.auth.core.IHinAuthService;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.KontaktFieldEditor;

public class RosePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private Composite additionalClientNumbers;

	public RosePreferences() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		setDescription("Einstellung zur Bestellung bei der Apotheke zur Rose");
	}

	protected void createFieldEditors() {
		addField(new StringFieldEditor(Constants.CFG_ROSE_CLIENT_NUMBER, "Kundennummer", getFieldEditorParent()));

		Optional<IHinAuthService> hinAuthService = HinAuthServiceHolder.get();
		if (!hinAuthService.isPresent()) {
			addField(
					new StringFieldEditor(Constants.CFG_ASAS_PROXY_HOST, "HIN-Client Adresse", getFieldEditorParent()));
			addField(new StringFieldEditor(Constants.CFG_ASAS_PROXY_PORT, "HIN-Client Port", getFieldEditorParent()));
		}
		addField(new KontaktFieldEditor(new ConfigServicePreferenceStore(Scope.GLOBAL), Constants.CFG_ROSE_SUPPLIER,
				"Lieferant", getFieldEditorParent()));
	}

	@Override
	protected Control createContents(Composite parent) {
		Control ret = super.createContents(parent);

		additionalClientNumbers = new Composite((Composite) ret, SWT.BORDER);
		additionalClientNumbers.setLayout(new GridLayout());
		additionalClientNumbers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Button btn = new Button(additionalClientNumbers, SWT.PUSH);
		btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btn.setText("Zusätzliche Kundennummer");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RoseClientNumberComposite comp = new RoseClientNumberComposite(additionalClientNumbers, SWT.BORDER);
				comp.setClientNumber(new AdditionalClientNumber(""));
				comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				((Composite) ret).layout(true, true);
			}
		});

		List<AdditionalClientNumber> additionalClients = AdditionalClientNumber.getConfigured();
		for (AdditionalClientNumber additionalClientNumber : additionalClients) {
			RoseClientNumberComposite comp = new RoseClientNumberComposite(additionalClientNumbers, SWT.BORDER);
			comp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			comp.setClientNumber(additionalClientNumber);
		}
		return ret;
	}

	public void init(final IWorkbench workbench) {
		// do nothing
	}

	@Override
	public boolean performOk() {
		if (super.performOk()) {
			saveAdditionalClientNumbers();
			CoreHub.globalCfg.flush();
			return true;
		}
		return false;
	}

	private void saveAdditionalClientNumbers() {
		List<AdditionalClientNumber> toSave = new ArrayList<>();
		for (Control control : additionalClientNumbers.getChildren()) {
			if (control instanceof RoseClientNumberComposite && !control.isDisposed()) {
				RoseClientNumberComposite composite = (RoseClientNumberComposite) control;
				composite.getClientNumber().ifPresent(ac -> toSave.add(ac));
			}
		}
		String prefString = AdditionalClientNumber.toString(toSave);
		getPreferenceStore().putValue(Constants.CFG_ROSE_ADDITIONAL_CLIENT_NUMBERS, prefString);
	}
}
