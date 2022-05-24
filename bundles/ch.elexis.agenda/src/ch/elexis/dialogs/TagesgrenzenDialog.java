/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/
package ch.elexis.dialogs;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class TagesgrenzenDialog extends TitleAreaDialog {
	String day;
	Text text;
	String beiwem;
	List<Termin> lRes;

	public TagesgrenzenDialog(Shell parent, String tag, String bereich) {
		super(parent);
		day = tag;
		beiwem = bereich;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		text = SWTHelper.createText(ret, 6, SWT.BORDER);
		Query<Termin> qbe = new Query<Termin>(Termin.class);
		qbe.add("Tag", "=", day);
		qbe.add("Typ", "=", Termin.typReserviert());
		qbe.add("BeiWem", "=", beiwem);
		qbe.add("deleted", "=", "0");
		lRes = qbe.execute();
		Termin[] lt = lRes.toArray(new Termin[0]);
		Arrays.sort(lt);
		StringBuilder sb = new StringBuilder();
		for (Termin t : lt) {
			sb.append(t.getTimeSpan().from.toString(TimeTool.TIME_SMALL)).append("-")
					.append(t.getTimeSpan().until.toString(TimeTool.TIME_SMALL)).append("\n");
		}
		text.setText(sb.toString());
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Tagesgrenzen");
		setMessage(
				"Bitte geben Sie nicht planbare Zeiträume in der Form hh:mm-hh:mm jeweils in einer eigenen Zeile ein");
		getShell().setText("Agenda");

	}

	@Override
	protected void okPressed() {
		for (Termin t : lRes) {
			t.delete();
		}
		String[] sl = text.getText().split("\\s*[\\n*\\r*,]\\n?\\r?\\s*");
		for (String s : sl) {
			String[] lim = s.split("-");
			int start = TimeTool.minutesStringToInt(lim[0]);
			int end = TimeTool.minutesStringToInt(lim[1]);
			new Termin(beiwem, day, start, end, Termin.typReserviert(), Termin.statusLeer());
		}
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
