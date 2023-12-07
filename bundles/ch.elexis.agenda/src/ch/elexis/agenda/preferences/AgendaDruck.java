/*******************************************************************************
 * Copyright (c) 2005-2010, D. Lutz and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    D. Lutz - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.elexis.data.Query;

public class AgendaDruck extends PreferencePage implements IWorkbenchPreferencePage {

	Combo cTerminTemplate;
	Text tTerminPrinter;
	Button bTerminPrinterButton;
	Text tTerminTray;

	Composite cPrinterArea;

	Button bDirectPrint;

	PrinterSelector psel;

	public AgendaDruck() {
		setDescription(Messages.AgendaDruck_settingsForPrint);
	}

	@Override
	protected Control createContents(Composite parent) {
		psel = new PrinterSelector();
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(3, false));
		new Label(ret, SWT.NONE).setText(Messages.AgendaDruck_templateForCards);
		cTerminTemplate = new Combo(ret, SWT.READ_ONLY);
		cTerminTemplate.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		bDirectPrint = new Button(ret, SWT.CHECK);
		bDirectPrint.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		bDirectPrint.setText(Messages.AgendaDruck_printDirectly);
		bDirectPrint.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				refreshDirectPrint();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		cPrinterArea = new Composite(ret, SWT.NONE);
		cPrinterArea.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		cPrinterArea.setLayout(new GridLayout(3, false));

		new Label(cPrinterArea, SWT.NONE).setText(Messages.AgendaDruck_printerForCards);
		tTerminPrinter = new Text(cPrinterArea, SWT.BORDER | SWT.READ_ONLY);
		tTerminPrinter.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tTerminPrinter.setData("TerminPrinter"); //$NON-NLS-1$
		bTerminPrinterButton = new Button(cPrinterArea, SWT.PUSH);
		bTerminPrinterButton.setText(" ->"); //$NON-NLS-1$
		bTerminPrinterButton.setData(tTerminPrinter);
		bTerminPrinterButton.addSelectionListener(psel);

		new Label(cPrinterArea, SWT.NONE).setText(Messages.AgendaDruck_TrayForCards);
		tTerminTray = new Text(cPrinterArea, SWT.BORDER);
		tTerminTray.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		setInitialValues();

		return ret;
	}

	private void refreshDirectPrint() {
		boolean directPrint = bDirectPrint.getSelection();

		if (directPrint) {
			cPrinterArea.setVisible(true);
		} else {
			cPrinterArea.setVisible(false);
		}
	}

	/* fill combo box with available templates */
	private void setTemplates() {
		cTerminTemplate.removeAll();
		cTerminTemplate.add(StringUtils.EMPTY);

		String currentTemplate = CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_TEMPLATE,
				PreferenceConstants.AG_PRINT_APPOINTMENTCARD_TEMPLATE_DEFAULT);

		Brief[] templates = getSystemTemplates();
		for (int i = 0; i < templates.length; i++) {
			Brief brief = templates[i];
			String name = brief.getBetreff();
			cTerminTemplate.add(name);
		}

		cTerminTemplate.setText(currentTemplate);
	}

	private void setInitialValues() {
		setTemplates();

		tTerminPrinter.setText(
				CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_NAME, StringUtils.EMPTY));
		tTerminTray.setText(
				CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_TRAY, StringUtils.EMPTY));

		boolean directPrint = CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT,
				PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT_DEFAULT);
		bDirectPrint.setSelection(directPrint);
		refreshDirectPrint();
	}

	@Override
	public boolean performOk() {
		CoreHub.localCfg.set(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_TEMPLATE, cTerminTemplate.getText());
		CoreHub.localCfg.set(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_NAME, tTerminPrinter.getText());
		CoreHub.localCfg.set(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_TRAY, tTerminTray.getText());
		CoreHub.localCfg.set(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_DIRECTPRINT, bDirectPrint.getSelection());

		CoreHub.localCfg.flush();

		return super.performOk();
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

	private Brief[] getSystemTemplates() {
		Query<Brief> qbe = new Query<Brief>(Brief.class);
		qbe.add(Brief.FLD_TYPE, Query.EQUALS, Brief.TEMPLATE);
		qbe.add(Brief.FLD_KONSULTATION_ID, Query.EQUALS, "SYS");
		qbe.startGroup();
		qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS, ContextServiceHolder.getActiveMandatorOrThrow().getId());
		qbe.or();
		qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS, StringConstants.EMPTY);
		qbe.endGroup();
		qbe.and();
		qbe.add("geloescht", Query.NOT_EQUAL, StringConstants.ONE);

		qbe.orderBy(false, Brief.FLD_DATE);
		List<Brief> l = qbe.execute();
		if (l != null) {
			return l.toArray(new Brief[0]);
		} else {
			return new Brief[0];
		}
	}

	class PrinterSelector extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			PrintDialog pd = new PrintDialog(getShell());
			PrinterData pdata = pd.open();
			if (pdata != null) {
				Text tx = (Text) ((Button) e.getSource()).getData();
				tx.setText(pdata.name);
				tx.setData(pdata);
			}
		}

	};

}
