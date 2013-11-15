/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.base.ch.labortarif_2009.ui;

import java.util.LinkedList;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.base.ch.labortarif_2009.data.Importer;
import ch.elexis.base.ch.labortarif_2009.data.Labor2009Tarif;
import ch.elexis.base.ch.labortarif_2009.data.Importer.Fachspec;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.inputs.MultiplikatorEditor;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.io.Settings;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePickerCombo;

public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String SPECNUM = "specnum"; //$NON-NLS-1$
	public static final String FACHDEF = "abrechnung/labor2009/fachdef"; //$NON-NLS-1$
	public static final String OPTIMIZE = "abrechnung/labor2009/optify"; //$NON-NLS-1$
	public static final String OPTIMIZE_ADDITION_DEADLINE =
		"abrechnung/labor2009/optify/addition/deadline"; //$NON-NLS-1$
	
	public static final String OPTIMIZE_ADDITION_INITDEADLINE = "30.06.2013"; //$NON-NLS-1$
	
	int langdef = 0;
	Settings cfg = CoreHub.mandantCfg;
	LinkedList<Button> buttons = new LinkedList<Button>();
	
	public Preferences(){
		String lang = JdbcLink.wrap(CoreHub.localCfg.get( // d,f,i
			ch.elexis.core.constants.Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		if (lang.startsWith("F")) { //$NON-NLS-1$
			langdef = 1;
		} else if (lang.startsWith("I")) { //$NON-NLS-1$
			langdef = 2;
		}
		
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		new Label(ret, SWT.NONE).setText(Messages.Preferences_pleaseEnterMultiplier);
		MultiplikatorEditor me = new MultiplikatorEditor(ret, Labor2009Tarif.MULTIPLICATOR_NAME);
		me.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Fachspec[] specs = Importer.loadFachspecs(langdef);
		Group group = new Group(ret, SWT.BORDER);
		group.setText(Messages.Preferences_specialities);
		group.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		group.setLayout(new GridLayout());
		String[] olddef = cfg.getStringArray(FACHDEF);
		for (Fachspec spec : specs) {
			Button b = new Button(group, SWT.CHECK);
			b.setText(spec.name);
			b.setData(SPECNUM, spec.code);
			b.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			if (olddef != null && StringTool.getIndex(olddef, Integer.toString(spec.code)) != -1) {
				b.setSelection(true);
			}
			buttons.add(b);
		}
		
		Group groupOptify = new Group(ret, SWT.NONE);
		groupOptify.setText(Messages.Preferences_automaticAdditionsGroup);
		groupOptify.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		groupOptify.setLayout(new GridLayout(2, false));
		final Button bOptify = new Button(groupOptify, SWT.CHECK);
		bOptify.setSelection(CoreHub.localCfg.get(OPTIMIZE, true));
		bOptify.setText(Messages.Preferences_automaticallyCalculatioAdditions);
		bOptify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				CoreHub.localCfg.set(OPTIMIZE, bOptify.getSelection());
			}
		});
		bOptify.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		
		Label lbl = new Label(groupOptify, SWT.NONE);
		lbl.setText(Messages.Preferences_automaticAdditionsToLabel);
		final DatePickerCombo dpc = new DatePickerCombo(groupOptify, SWT.BORDER);
		dpc.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		// set selected date on focus lost due to selection event will not fire if text changed
		dpc.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e){
				if (dpc.getDate() != null) {
					TimeTool time = new TimeTool(dpc.getDate().getTime());
					time.set(TimeTool.HOUR_OF_DAY, 23);
					time.set(TimeTool.MINUTE, 59);
					time.set(TimeTool.SECOND, 59);
					CoreHub.globalCfg.set(OPTIMIZE_ADDITION_DEADLINE, time);
					// System.out.println(time.toString(TimeTool.DATE_GER));
				}
			}
			
			public void focusGained(FocusEvent e){}
		});
		TimeTool deadline = CoreHub.globalCfg.getDate(OPTIMIZE_ADDITION_DEADLINE);
		if (deadline == null)
			deadline = new TimeTool(OPTIMIZE_ADDITION_INITDEADLINE);
		dpc.setDate(deadline.getTime());
		
		return ret;
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void performApply(){
		LinkedList<String> bb = new LinkedList<String>();
		for (Button b : buttons) {
			if (b.getSelection()) {
				bb.add(((Integer) b.getData(SPECNUM)).toString());
			}
		}
		CoreHub.mandantCfg.set(FACHDEF, StringTool.join(bb, StringConstants.COMMA));
		super.performApply();
	}
	
}
