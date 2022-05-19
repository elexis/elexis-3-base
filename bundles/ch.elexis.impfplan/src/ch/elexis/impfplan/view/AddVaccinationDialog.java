/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.impfplan.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.DateInput;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.impfplan.controller.ImpfplanController;
import ch.elexis.impfplan.model.VaccinationType;
import ch.rgw.tools.TimeTool;

public class AddVaccinationDialog extends TitleAreaDialog {
	TableViewer tv;
	DateInput di;
	Button bCa;
	public VaccinationType result;
	public TimeTool date;
	public boolean bUnexact;

	public AddVaccinationDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayout(new GridLayout());
		tv = new TableViewer(ret);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite lower = new Composite(ret, SWT.NONE);
		lower.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		lower.setLayout(new FillLayout());
		di = new DateInput(lower);
		// di.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		di.setDate(new TimeTool());
		bCa = new Button(lower, SWT.CHECK);
		bCa.setText(Messages.AddVaccinationDialog_dateOnlyAbout);
		tv.setContentProvider(new ContentProviderAdapter() {
			@Override
			public Object[] getElements(Object arg0) {
				return ImpfplanController.allVaccs().toArray();
			}
		});
		tv.setLabelProvider(new VaccinationLabelProvider());
		// dlg.setTitle("Impfung eintragen");
		tv.setInput(this);
		return ret;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(Messages.AddVaccinationDialog_enterVaccinationTitle);
		setMessage(Messages.AddVaccinationDialog_enterVaccinationText);
	}

	@Override
	protected void okPressed() {
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if (sel.isEmpty()) {
			MessageDialog.openError(getShell(), Messages.AddVaccinationDialog_enterVaccinationTitle,
					Messages.AddVaccinationDialog_enterVaccinationTextError + ".");
			return;
		}
		result = (VaccinationType) sel.getFirstElement();
		date = new TimeTool(di.getDate());
		bUnexact = bCa.getSelection();
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
