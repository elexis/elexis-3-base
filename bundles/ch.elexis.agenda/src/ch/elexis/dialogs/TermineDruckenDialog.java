/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.dialogs;

import static ch.elexis.agenda.text.AgendaTextTemplateRequirement.TT_APPOINTMENT_CARD;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.text.ITextPlugin.ICallback;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.rgw.tools.TimeTool;

public class TermineDruckenDialog extends TitleAreaDialog implements ICallback {
	Termin[] liste;

	private TextContainer text = null;

	public TermineDruckenDialog(Shell shell, Termin[] liste) {
		super(shell);
		this.liste = liste;
		Arrays.sort(this.liste, new Comparator<Termin>() {
			private TimeTool lDay = new TimeTool();
			private TimeTool rDay = new TimeTool();

			@Override
			public int compare(Termin l, Termin r) {
				int dayRes = 0;
				if (l.getDay() != null && l.getDay().length() > 3 && r.getDay() != null && r.getDay().length() > 3) {
					lDay.set(l.getDay());
					rDay.set(r.getDay());
					dayRes = lDay.compareTo(rDay);
				}
				if (dayRes == 0) {
					return Integer.compare(l.getStartMinute(), r.getStartMinute());
				} else {
					return dayRes;
				}
			}
		});
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new FillLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		text = new TextContainer(getShell());
		text.getPlugin().createContainer(ret, this);
		text.getPlugin().showMenu(true);
		text.getPlugin().showToolbar(true);
		text.createFromTemplateName(null, TT_APPOINTMENT_CARD, Brief.UNKNOWN, CoreHub.getLoggedInContact(), "Agenda");
		/*
		 * String[][] termine=new String[liste.length+1][3]; termine[0]=new
		 * String[]{"Datum", "Zeit","Bei"}; for(int i=0;i<liste.length;i++){ TimeTool
		 * day=new TimeTool(liste[i].getDay());
		 * termine[i+1][0]=day.toString(TimeTool.DATE_GER);
		 * termine[i+1][1]=Plannables.getStartTimeAsString(liste[i]);
		 * termine[i+1][2]=liste[i].getBereich(); }
		 * text.getPlugin().setFont("Helvetica", SWT.NORMAL, 9);
		 * text.getPlugin().insertTable("[Termine]", ITextPlugin.FIRST_ROW_IS_HEADER,
		 * termine, new int[]{20,20,60});
		 */
		StringBuilder sb = new StringBuilder();
		for (Termin t : liste) {
			TimeTool day = new TimeTool(t.getDay());
			sb.append(day.toString(TimeTool.WEEKDAY)).append(", ").append(day.toString(TimeTool.DATE_GER)).append(" - ")
					.append(Plannables.getStartTimeAsString(t)).append(StringUtils.LF);
		}
		text.replace("\\[Termine\\]", sb.toString());
		if (text.getPlugin().isDirectOutput()) {
			text.getPlugin().print(null, null, true);
			okPressed();
		}
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setMessage("Terminliste ausdrucken");
		setTitle("Terminliste");
		getShell().setText("Agenda");
		getShell().setSize(800, 700);

	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public void save() {
	}

	public boolean saveAs() {
		return false;
	}

	public boolean doPrint() {
		if (text == null) {
			// text container is not initialized
			return false;
		}

		String printer = CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_NAME,
				StringUtils.EMPTY);
		String tray = CoreHub.localCfg.get(PreferenceConstants.AG_PRINT_APPOINTMENTCARD_PRINTER_TRAY, null);

		return text.getPlugin().print(printer, tray, false);
	}
}
