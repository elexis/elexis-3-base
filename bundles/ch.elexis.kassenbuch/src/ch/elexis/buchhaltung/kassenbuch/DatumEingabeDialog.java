/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.buchhaltung.kassenbuch;

import java.util.Date;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePicker;

public class DatumEingabeDialog extends TitleAreaDialog {
	DatePicker dpVon, dpBis;
	TimeTool ttVon, ttBis;
	
	public DatumEingabeDialog(Shell parentShell, TimeTool von, TimeTool bis){
		super(parentShell);
		ttVon = von == null ? null : new TimeTool(von);
		ttBis = bis == null ? null : new TimeTool(bis);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(2, true));
		new Label(ret, SWT.NONE).setText("Von:");
		new Label(ret, SWT.NONE).setText("Bis:");
		dpVon = new DatePicker(ret, SWT.NONE);
		dpBis = new DatePicker(ret, SWT.NONE);
		if (ttVon != null) {
			dpVon.setDate(ttVon.getTime());
		}
		if (ttBis != null) {
			dpBis.setDate(ttBis.getTime());
		}
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		setMessage(
			"Bitte geben Sie den gewünschten Zeitraum ein oder drücken Sie 'Abbrechen' um alle Buchungen anzuzeigen.");
		setTitle("Anzeigezeitraum für Kassenbuch");
		getShell().setText("Elexis Kassenbuch");
	}
	
	@Override
	protected void okPressed(){
		ttVon = getTimeFromField(dpVon); // new TimeTool(dpVon.getDate().getTime());
		
		ttBis = getTimeFromField(dpBis); // new TimeTool(dpBis.getDate().getTime());
		super.okPressed();
	}
	
	private TimeTool getTimeFromField(DatePicker dp){
		if (dp != null) {
			Date dat = dp.getDate();
			if (dat != null) {
				long millis = dat.getTime();
				return new TimeTool(millis);
			}
		}
		return new TimeTool();
	}
	
}
