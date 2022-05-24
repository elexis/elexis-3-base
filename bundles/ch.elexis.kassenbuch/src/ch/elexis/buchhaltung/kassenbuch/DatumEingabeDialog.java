/*******************************************************************************
 * Copyright (c) 2007-2019, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    N. Giger - Using Nebula CDateTime as DatePicker
 *
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class DatumEingabeDialog extends TitleAreaDialog {
	CDateTime dpVon, dpBis;
	TimeTool ttVon, ttBis;

	public DatumEingabeDialog(Shell parentShell, TimeTool von, TimeTool bis) {
		super(parentShell);
		ttVon = von == null ? null : new TimeTool(von);
		ttBis = bis == null ? null : new TimeTool(bis);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(2, true));
		new Label(ret, SWT.NONE).setText("Von:");
		new Label(ret, SWT.NONE).setText("Bis:");
		dpVon = new CDateTime(ret, CDT.DATE_MEDIUM | CDT.DROP_DOWN | SWT.BORDER | CDT.COMPACT);
		dpBis = new CDateTime(ret, CDT.DATE_MEDIUM | CDT.DROP_DOWN | SWT.BORDER | CDT.COMPACT);
		if (ttVon != null) {
			dpVon.setSelection(ttVon.getTime());
		}
		if (ttBis != null) {
			dpBis.setSelection(ttBis.getTime());
		}
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setMessage(
				"Bitte geben Sie den gewünschten Zeitraum ein oder drücken Sie 'Abbrechen' um alle Buchungen anzuzeigen.");
		setTitle("Anzeigezeitraum für Kassenbuch");
		getShell().setText("Elexis Kassenbuch");
	}

	@Override
	protected void okPressed() {
		ttVon = new TimeTool(dpVon.getSelection());
		ttBis = new TimeTool(dpBis.getSelection());
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
