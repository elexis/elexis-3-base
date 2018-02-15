/*******************************************************************************
 * Copyright (c) 2005-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.views;

import java.util.Hashtable;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.TimeTool;

public class TarmedDetailDisplay implements IDetailDisplay {
	private FormToolkit tk;
	private ScrolledForm form;
	private String[] fields = {
		Messages.TarmedDetailDisplay_DigniQuant, Messages.TarmedDetailDisplay_DigniQual,
		Messages.TarmedDetailDisplay_Sparte, Messages.TarmedDetailDisplay_RiskClass,
		Messages.TarmedDetailDisplay_TPDoc, Messages.TarmedDetailDisplay_TPTec,
		Messages.TarmedDetailDisplay_TPAss, Messages.TarmedDetailDisplay_NumbereAss,
		Messages.TarmedDetailDisplay_TimeAct, Messages.TarmedDetailDisplay_TimeBeforeAfter,
		Messages.TarmedDetailDisplay_TimeWrite, Messages.TarmedDetailDisplay_TimeChange,
		Messages.TarmedDetailDisplay_TimeRoom, Messages.TarmedDetailDisplay_Relation,
		Messages.TarmedDetailDisplay_NameInternal
	};
	private String[] retrieve = {
		"DigniQuanti", "DigniQuali", "Sparte", "Anaesthesie", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"TP_AL", "TP_TL", "TP_ASSI", "ANZ_ASSI", "LSTGIMES_MIN", "VBNB_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"BEFUND_MIN", "WECHSEL_MIN", "RAUM_MIN", "Bezug", "Nickname" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
	};
	
	private Text[] inputs = new Text[fields.length];
	private FormText medinter, techinter, exclusion, inclusion, limits;
	private FormText validity;
	private TarmedLeistung actCode;
	
	public TarmedDetailDisplay(){
		
	}
	
	public Composite createDisplay(Composite parent, IViewSite notUsed){
		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		// twl.numColumns=4;
		// twl.makeColumnsEqualWidth=true;
		form.getBody().setLayout(twl /* new GridLayout(6,true) */);
		LabeledInputField.Tableau cFields = new LabeledInputField.Tableau(form.getBody());
		for (int i = 0; i < fields.length; i++) {
			inputs[i] = (Text) cFields.addComponent(fields[i]).getControl();
		}
		final int last = fields.length - 1;
		inputs[last].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e){
				if (actCode != null) {
					actCode.set("Nickname", inputs[last].getText()); //$NON-NLS-1$
				}
			}
			
		});
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		cFields.setLayoutData(twd);
		
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_MedInter);
		medinter = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_TecInter);
		techinter = tk.createFormText(form.getBody(), false);
		// Composite cLimits=tk.createComposite(form.getBody());
		// cLimits.setLayout(new ColumnLayout());
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DontCombine);
		exclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DoCombine);
		inclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Limits);
		limits = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Validity);
		validity = tk.createFormText(form.getBody(), false);
		return form.getBody();
	}
	
	public Class getElementClass(){
		return TarmedLeistung.class;
	}
	
	public void display(Object obj){
		if (obj instanceof TarmedLeistung) {
			actCode = (TarmedLeistung) obj;
			form.setText(actCode.getLabel());
			Hashtable<String, String> ext = actCode.loadExtension();
			inputs[0].setText(actCode.getDigniQuantiAsText());
			inputs[1].setText(actCode.getDigniQualiAsText());
			inputs[2].setText(actCode.getSparteAsText());
			inputs[3]
				.setText(TarmedLeistung.getTextForRisikoKlasse((String) ext.get("ANAESTHESIE"))); //$NON-NLS-1$
			for (int i = 4; i < fields.length - 1; i++) {
				String val = (String) ext.get(retrieve[i]);
				if (val == null) {
					val = ""; //$NON-NLS-1$
				}
				inputs[i].setText(val);
			}
			inputs[fields.length - 1].setText(actCode.get("Nickname")); //$NON-NLS-1$
			medinter.setText(actCode.getMedInterpretation(), false, false);
			techinter.setText(actCode.getTechInterpretation(), false, false);
			String excl = ext.get("exclusion"); //$NON-NLS-1$
			String incl = ext.get("inclusion"); //$NON-NLS-1$
			String limit = ext.get("limits"); //$NON-NLS-1$
			exclusion.setText((excl == null) ? "" : excl, false, false); //$NON-NLS-1$
			inclusion.setText((incl == null) ? "" : incl, false, false); //$NON-NLS-1$
			if (limit != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("<form>"); //$NON-NLS-1$
				String[] ll = limit.split("#"); //$NON-NLS-1$
				for (String line : ll) {
					String[] f = line.split(","); //$NON-NLS-1$
					if (f.length == 6) {
						sb.append("<li>"); //$NON-NLS-1$
						if (f[0].equals("<=")) { //$NON-NLS-1$
							sb.append(Messages.TarmedDetailDisplay_max).append(" "); //$NON-NLS-2$
						} else {
							sb.append(f[0]).append(" "); //$NON-NLS-1$
						}
						sb.append(f[1]).append(Messages.TarmedDetailDisplay_times);
						if (f[3].equals("P")) { //$NON-NLS-1$
							sb.append(Messages.TarmedDetailDisplay_per);
						} else {
							sb.append(Messages.TarmedDetailDisplay_after);
						}
						sb.append(f[2]).append(" "); //$NON-NLS-1$
						sb.append(TarmedLeistung.getTextForZR_Einheit(f[4]));
						sb.append("</li>"); //$NON-NLS-1$
					}
				}
				sb.append("</form>"); //$NON-NLS-1$
				limits.setText(sb.toString(), true, false);
			} else {
				limits.setText("", false, false); //$NON-NLS-1$
			}
			
			// validity
			String text;
			TimeTool tGueltigVon = actCode.getGueltigVon();
			TimeTool tGueltigBis = actCode.getGueltigBis();
			if (tGueltigVon != null && tGueltigBis != null) {
				String from = tGueltigVon.toString(TimeTool.DATE_GER);
				String to;
				if (tGueltigBis.isSameDay(TarmedLeistung.INFINITE)) {
					to = "";
				} else {
					to = tGueltigBis.toString(TimeTool.DATE_GER);
				}
				text = from + "-" + to; //$NON-NLS-1$
			} else {
				text = ""; //$NON-NLS-1$
			}
			validity.setText(text, false, false);
			
			form.reflow(true);
		}
		
	}
	
	public String getTitle(){
		return "Tarmed"; //$NON-NLS-1$
	}
	
}
