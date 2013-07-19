/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.Verrechnet;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.TarmedLeistung;
import ch.rgw.tools.Money;


public class TarmedDetailDialog extends Dialog {
	Verrechnet v;
	TarmedDetailDisplay td;
	Combo cSide;
	Button bPflicht;
	
	public TarmedDetailDialog(Shell shell, Verrechnet tl){
		super(shell);
		v = tl;
		td = new TarmedDetailDisplay();
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		// Composite ret=td.createDisplay(parent, null);
		// td.display(tl);
		TarmedLeistung tl = (TarmedLeistung) v.getVerrechenbar();
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayout(new GridLayout(8, false));
		
		Label lTitle = new Label(ret, SWT.WRAP);
		lTitle.setText(tl.getText());
		lTitle.setLayoutData(SWTHelper.getFillGridData(8, true, 1, true));
		double tpAL = tl.getAL() / 100.0;
		double tpTL = tl.getTL() / 100.0;
		String arzl = v.getDetail("AL");
		String tecl = v.getDetail("TL");
		double primaryScale = v.getPrimaryScaleFactor();
		double secondaryScale = v.getSecondaryScaleFactor();
		if (arzl != null) {
			tpAL = Double.parseDouble(arzl) / 100.0;
		}
		if (tecl != null) {
			tpTL = Double.parseDouble(tecl) / 100.0;
		}
		double tpw = v.getTPW();
		Money mAL = new Money(tpAL * tpw * primaryScale * secondaryScale);
		Money mTL = new Money(tpTL * tpw * primaryScale * secondaryScale);
		double tpAll = Math.round((tpAL + tpTL) * 100.0) / 100.0;
		Money mAll = new Money(tpAll * tpw * primaryScale * secondaryScale);
		
		new Label(ret, SWT.NONE).setText("TP AL");
		new Label(ret, SWT.NONE).setText(Double.toString(tpAL));
		new Label(ret, SWT.NONE).setText(" x ");
		new Label(ret, SWT.NONE).setText("TP-Wert");
		new Label(ret, SWT.NONE).setText(Double.toString(tpw));
		new Label(ret, SWT.NONE).setText(" = ");
		new Label(ret, SWT.NONE).setText("CHF AL");
		new Label(ret, SWT.NONE).setText(mAL.getAmountAsString());
		
		new Label(ret, SWT.NONE).setText("TP TL");
		new Label(ret, SWT.NONE).setText(Double.toString(tpTL));
		new Label(ret, SWT.NONE).setText(" x ");
		new Label(ret, SWT.NONE).setText("TP-Wert");
		new Label(ret, SWT.NONE).setText(Double.toString(tpw));
		new Label(ret, SWT.NONE).setText(" = ");
		new Label(ret, SWT.NONE).setText("CHF TL");
		new Label(ret, SWT.NONE).setText(mTL.getAmountAsString());
		
		Label sep = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(SWTHelper.getFillGridData(8, true, 1, false));
		
		new Label(ret, SWT.NONE).setText("TP ");
		new Label(ret, SWT.NONE).setText(Double.toString(tpAll));
		new Label(ret, SWT.NONE).setText(" x ");
		new Label(ret, SWT.NONE).setText("TP-Wert");
		new Label(ret, SWT.NONE).setText(Double.toString(tpw));
		new Label(ret, SWT.NONE).setText(" = ");
		new Label(ret, SWT.NONE).setText("CHF ");
		new Label(ret, SWT.NONE).setText(mAll.getAmountAsString());
		
		Label sep2 = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep2.setLayoutData(SWTHelper.getFillGridData(8, true, 1, false));
		
		String mins = Integer.toString(tl.getMinutes());
		new Label(ret, SWT.NONE).setText("Zeit:");
		new Label(ret, SWT.NONE).setText(mins + " min.");
		
		new Label(ret, SWT.NONE).setText("Seite");
		cSide = new Combo(ret, SWT.SINGLE);
		cSide.setItems(new String[] {
			"egal", "links", "rechts"
		});
		
		new Label(ret, SWT.NONE).setText("Pflichtleist.");
		bPflicht = new Button(ret, SWT.CHECK);
		String sPflicht = v.getDetail(TarmedLeistung.PFLICHTLEISTUNG);
		if ((sPflicht == null) || (Boolean.parseBoolean(sPflicht))) {
			bPflicht.setSelection(true);
		}
		String side = v.getDetail(TarmedLeistung.SIDE);
		if (side == null) {
			cSide.select(0);
		} else if (side.equalsIgnoreCase("l")) {
			cSide.select(1);
		} else {
			cSide.select(2);
		}
		ret.pack();
		return ret;
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Tarmed-Details: " + v.getCode());
	}
	
	@Override
	protected void okPressed(){
		int idx = cSide.getSelectionIndex();
		if (idx < 1) {
			v.setDetail(TarmedLeistung.SIDE, null);
		} else if (idx == 1) {
			v.setDetail(TarmedLeistung.SIDE, "l");
		} else {
			v.setDetail(TarmedLeistung.SIDE, "r");
		}
		v.setDetail(TarmedLeistung.PFLICHTLEISTUNG, Boolean.toString(bPflicht.getSelection()));
		super.okPressed();
	}
	
}
