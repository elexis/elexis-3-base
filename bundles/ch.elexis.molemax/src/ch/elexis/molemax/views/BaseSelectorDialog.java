/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;

public class BaseSelectorDialog extends TitleAreaDialog {
	List list;
	Patient pat;
	java.util.List<Tracker> dates;
	String ret = null;

	BaseSelectorDialog(final org.eclipse.swt.widgets.Shell shell, final Patient patient) {
		super(shell);
		pat = patient;
		Query<Tracker> qbe = new Query<Tracker>(Tracker.class);
		qbe.add("PatientID", "=", pat.getId());
		qbe.add("ParentID", "=", "NIL");
		dates = qbe.execute();
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		list = new List(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		for (Tracker t : dates) {
			String d = t.getDate();
			if (list.indexOf(d) == -1) {
				list.add(t.getDate());
			}
		}
		return list;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.BaseSelectorDialog_selectSequence);
		setMessage(Messages.BaseSelectorDialog_pleaseSelect);
		getShell().setText("Molemax");
	}

	@Override
	protected void okPressed() {
		int ix = list.getSelectionIndex();
		if (ix != -1) {
			ret = list.getItem(ix);
		}
		super.okPressed();
	}

}
