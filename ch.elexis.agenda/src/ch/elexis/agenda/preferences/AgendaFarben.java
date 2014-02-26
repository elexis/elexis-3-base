/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class AgendaFarben extends PreferencePage implements IWorkbenchPreferencePage {
	private SettingsPreferenceStore prefs;
	private int typCols, typRows, statusCols;
	
	// private ColorCellEditor[] editors;
	// private String[] columnProperties;
	
	public AgendaFarben(){
		prefs = new SettingsPreferenceStore(CoreHub.userCfg);
		setPreferenceStore(prefs);
		setDescription(Messages.AgendaFarben_colorSettings);
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite par = new Composite(parent, SWT.NONE);
		par.setLayout(new GridLayout(1, false));
		Group top = new Group(par, SWT.BORDER);
		Group bottom = new Group(par, SWT.BORDER);
		String[] typ = Termin.TerminTypes;
		top.setText(Messages.AgendaFarben_appTypes);
		bottom.setText(Messages.AgendaFarben_appstateTypes);
		int num = typ.length;
		typRows = ((int) Math.sqrt(num));
		typCols = (num - (typRows * typRows));
		if (typCols < 4) {
			typCols = 4;
		}
		top.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		top.setLayout(new GridLayout(typCols, true));
		for (int i = 0; i < num; i++) {
			Label lab = new Label(top, SWT.NONE);
			lab.setText(typ[i]);
			String coldesc =
					CoreHub.userCfg.get(PreferenceConstants.AG_TYPCOLOR_PREFIX + typ[i], "FFFFFF"); //$NON-NLS-1$
			Color background = UiDesk.getColorFromRGB(coldesc);
			lab.setBackground(background);
			GridData gd = new GridData(GridData.FILL_BOTH);
			lab.setLayoutData(gd);
			lab.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseDoubleClick(MouseEvent e){
					ColorDialog cd = new ColorDialog(getShell());
					Label l = (Label) e.getSource();
					RGB selected = cd.open();
					String symbolic = UiDesk.createColor(selected);
					l.setBackground(UiDesk.getColorFromRGB(symbolic));
					CoreHub.userCfg.set(PreferenceConstants.AG_TYPCOLOR_PREFIX + l.getText(), symbolic);
				}
				
			});
		}
		
		String[] status = Termin.TerminStatus;
		
		num = status.length;
		int statusRows = ((int) Math.sqrt(num));
		statusCols = num - (statusRows * statusRows);
		if (statusCols < 4) {
			statusCols = 4;
		}
		bottom.setLayout(new GridLayout(statusCols, true));
		bottom.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		for (int i = 0; i < num; i++) {
			Label lab = new Label(bottom, SWT.NONE);
			lab.setText(status[i]);
			GridData gd = new GridData(GridData.FILL_BOTH);
			lab.setLayoutData(gd);
			lab.setBackground(UiDesk.getColorFromRGB(CoreHub.userCfg.get(
				PreferenceConstants.AG_STATCOLOR_PREFIX + status[i], "FFFFFF"))); //$NON-NLS-1$
			lab.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseDoubleClick(MouseEvent e){
					ColorDialog cd = new ColorDialog(getShell());
					Label l = (Label) e.getSource();
					RGB selected = cd.open();
					String symbolic = UiDesk.createColor(selected);
					l.setBackground(UiDesk.getColorFromRGB(symbolic));
					CoreHub.userCfg.set(PreferenceConstants.AG_STATCOLOR_PREFIX + l.getText(), symbolic);
				}
				
			});
		}
		
		return par;
	}
	
}
