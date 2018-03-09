/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.agenda.ui.week;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ColumnHeader extends Composite {
	AgendaWeek view;
	static final String IMG_PERSONS_NAME = Activator.PLUGIN_ID + "/personen"; //$NON-NLS-1$
	static final String IMG_PERSONS_PATH = "icons/personen.png"; //$NON-NLS-1$
	ImageHyperlink ihRes;
	
	public ColumnHeader(Composite parent, AgendaWeek aw){
		super(parent, SWT.NONE);
		view = aw;
		if (UiDesk.getImage(IMG_PERSONS_NAME) == null) {
			UiDesk.getImageRegistry().put(IMG_PERSONS_NAME,
				Activator.getImageDescriptor(IMG_PERSONS_PATH));
		}
		ihRes = new ImageHyperlink(this, SWT.NONE);
		ihRes.setImage(UiDesk.getImage(IMG_PERSONS_NAME));
		ihRes.setToolTipText(Messages.ColumnHeader_selectDaysToDisplay);
		ihRes.addHyperlinkListener(new HyperlinkAdapter() {
			
			@Override
			public void linkActivated(HyperlinkEvent e){
				new SelectDaysDlg().open();
			}
			
		});
	}
	
	void recalc(double widthPerColumn, int left_offset, int padding, int textSize){
		GridData gd = (GridData) getLayoutData();
		gd.heightHint = textSize + 2;
		for (Control c : getChildren()) {
			if (c instanceof Label) {
				c.dispose();
			}
		}
		Point bSize = ihRes.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ihRes.setBounds(0, 0, bSize.x, bSize.y);
		String[] labels = view.getDisplayedDays();
		int count = labels.length;
		for (int i = 0; i < count; i++) {
			int lx = left_offset + (int) Math.round(i * (widthPerColumn + padding));
			Label l = new Label(this, SWT.NONE);
			TimeTool tt = new TimeTool(labels[i]);
			StringBuilder sb = new StringBuilder(tt.toString(TimeTool.WEEKDAY));
			sb.append(", ").append(tt.toString(TimeTool.DATE_GER)); //$NON-NLS-1$
			String coltext = sb.toString();
			Point extend = SWTHelper.getStringBounds(this, coltext);
			if (extend.x > widthPerColumn) {
				coltext = coltext.substring(0, coltext.length() - 4);
				extend = SWTHelper.getStringBounds(this, coltext);
				if (extend.x > widthPerColumn) {
					coltext = coltext.substring(0, 2);
				}
				
			}
			l.setText(coltext);
			
			int outer = (int) Math.round(widthPerColumn);
			int inner = l.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			int off = (outer - inner) / 2;
			lx += off;
			l.setBounds(lx, 0, inner, textSize + 2);
		}
	}
	
	class SelectDaysDlg extends TitleAreaDialog {
		SelectDaysDlg(){
			super(ColumnHeader.this.getShell());
		}
		
		@Override
		protected Control createDialogArea(Composite parent){
			Composite ret = (Composite) super.createDialogArea(parent);
			ret.setLayout(new GridLayout());
			
			String resources =
				CoreHub.localCfg.get(PreferenceConstants.AG_DAYSTOSHOW,
					StringTool.join(TimeTool.Wochentage, ",")); //$NON-NLS-1$
			String[] daysSelected = resources.split(",");
			
			for (TimeTool.DAYS day : TimeTool.DAYS.values()) {
				Button b = new Button(ret, SWT.CHECK);
				b.setText(day.fullName);
				b.setSelection(false);
				b.setData(day.numericDayValue);
				for (String string : daysSelected) {
					if (string.toLowerCase().equalsIgnoreCase(day.fullName.toLowerCase()))
						b.setSelection(true);
				}
				
			}
			return ret;
		}
		
		@Override
		public void create(){
			super.create();
			getShell().setText(Messages.ColumnHeader_configureDisplay);
			setTitle(Messages.ColumnHeader_displayWeekdays);
			setMessage(Messages.ColumnHeader_pleaseSelectWeekdays);
		}
		
		@Override
		protected void okPressed(){
			Composite dlg = (Composite) getDialogArea();
			String[] res = TimeTool.Wochentage;
			ArrayList<String> sel = new ArrayList<String>(res.length);
			for (Control c : dlg.getChildren()) {
				if (c instanceof Button) {
					if (((Button) c).getSelection()) {
						int dayValue = (Integer) ((Button) c).getData();
						sel.add(TimeTool.DAYS.valueOf(dayValue).fullName);
					}
				}
			}
			view.clear();
			CoreHub.localCfg.set(PreferenceConstants.AG_DAYSTOSHOW, StringTool.join(sel, ",")); //$NON-NLS-1$
			view.refresh();
			
			super.okPressed();
		}
		
	}
}
