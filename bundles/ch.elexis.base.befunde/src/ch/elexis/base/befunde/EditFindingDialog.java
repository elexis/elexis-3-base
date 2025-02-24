/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.befunde;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.tiff.common.ui.datepicker.DatePickerCombo;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.scripting.Interpreter;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Script;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class EditFindingDialog extends TitleAreaDialog {
	Messwert mw;
	String name;
	DatePickerCombo dp;
	Map names;
	String[] flds;
	boolean[] multiline;
	String[] values;
	Text[] inputs;

	EditFindingDialog(final Shell parent, final Messwert m, final String n) {
		super(parent);
		mw = m;
		name = n;
		names = Messwert.getSetup().getMap(Messwert.FLD_BEFUNDE);
		String key = n + Messwert._FIELDS;

		if (names.containsKey(key)) {
			flds = ((String) names.get(key)).split(Messwert.SETUP_SEPARATOR);
			multiline = new boolean[flds.length];
			values = new String[flds.length];
			inputs = new Text[flds.length];
			for (int i = 0; i < flds.length; i++) {
				String[] line = flds[i].split(Messwert.SETUP_CHECKSEPARATOR);
				flds[i] = line[0];
				if (line.length < 2) {
					multiline[i] = false;
				} else {
					multiline[i] = line[1].equals("m") ? true : false; //$NON-NLS-1$
				}
			}
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			ret.setLayout(new GridLayout());
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			dp = new DatePickerCombo(ret, SWT.NONE);
			if (mw == null) {
				dp.setDate(new Date());
			}
			if (mw != null) {
				dp.setDate(new TimeTool(mw.get(Messwert.FLD_DATE)).getTime());
				Map vals = mw.getMap(Messwert.FLD_BEFUNDE);
				for (int i = 0; i < flds.length; i++) {
					values[i] = (String) vals.get(flds[i]);
				}
			}
			if (flds != null) {
				for (int i = 0; i < flds.length; i++) {
					final String[] heading = flds[i].split("=", 2); //$NON-NLS-1$
					if (heading.length == 1) {
						new Label(ret, SWT.NONE).setText(flds[i]);
					} else {
						Label hl = SWTHelper.createHyperlink(ret, heading[0], new ScriptListener(heading[1], i));
						hl.setForeground(UiDesk.getColor(UiDesk.COL_BLUE));
					}
					inputs[i] = SWTHelper.createText(ret, multiline[i] ? 4 : 1, SWT.NONE);
					inputs[i].setText(values[i] == null ? StringUtils.EMPTY : values[i]);
					if (heading.length > 1) {
						inputs[i].setEditable(false);
					}
				}
			} else {
				MessageDialog.openWarning(getParentShell(), "Warnung",
						"Es sind keine Metriken für die Messung " + name + " vorhanden.");
				close();
			}
		}
		return ret;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(Messages.EditFindingDialog_captionBefundEditDlg); // $NON-NLS-1$
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat == null) {
			setTitle(Messages.EditFindingDialog_noPatientSelected); // $NON-NLS-1$
		} else {
			setTitle(pat.getLabel());
		}
		setMessage(MessageFormat.format(Messages.EditFindingDialog_enterTextForBefund, name)); // $NON-NLS-1$
		setTitleImage(Images.IMG_LOGO.getImage());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {
		Map hash;
		if (mw == null) {
			hash = new Hashtable();
			mw = new Messwert(ElexisEventDispatcher.getSelectedPatient(), name, dp.getText(), hash);
		} else {
			hash = mw.getMap(Messwert.FLD_BEFUNDE);
		}
		for (int i = 0; i < flds.length; i++) {
			String val = inputs[i].getText();
			if (StringTool.isNothing(val)) {
				hash.remove(flds[i]);
			} else {
				hash.put(flds[i], val);
			}
		}
		mw.setDate(dp.getDate());
		mw.setMap(Messwert.FLD_BEFUNDE, hash);
		super.okPressed();
	}

	class ScriptListener extends HyperlinkAdapter {
		int v;
		String script;

		ScriptListener(final String scr, final int i) {
			script = scr;
			v = i;
		}

		@Override
		public void linkActivated(final HyperlinkEvent e) {
			for (int vals = 0; vals < inputs.length; vals++) {
				String sval = inputs[vals].getText();
				if (!StringTool.isNothing(sval)) {
					double dval = 0.0;
					try {
						dval = Double.parseDouble(sval);
					} catch (NumberFormatException nfe) {
						// don't mind
					}
					script = script.replaceAll("F" + Integer.toString(vals + 1), Double //$NON-NLS-1$
							.toString(dval));
				}
			}

			try {
				Object result = null;
				if (script.startsWith(Script.SCRIPT_MARKER)) {
					String scriptname = script.substring(Script.SCRIPT_MARKER.length());
					result = Script.executeScript(scriptname);
				} else {
					Interpreter scripter = Script.getInterpreterFor(script);
					result = scripter.run(script, false);
				}
				values[v] = result.toString();
				// values[v]=Double.toString((Double)scripter.eval(script));
			} catch (ElexisException e1) {
				ExHandler.handle(e1);
				values[v] = "?eval?"; //$NON-NLS-1$
			}
			inputs[v].setText(values[v]);
		}
	}
}
