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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;

public class TarmedDetailDialog extends Dialog {
	private Verrechnet verrechnet;
	private Combo cSide;
	private Button bPflicht;
	private ComboViewer cBezug;
	
	public TarmedDetailDialog(Shell shell, Verrechnet tl){
		super(shell);
		verrechnet = tl;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		// Composite ret=td.createDisplay(parent, null);
		// td.display(tl);
		TarmedLeistung tl = (TarmedLeistung) verrechnet.getVerrechenbar();
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayout(new GridLayout(8, false));
		
		Label lTitle = new Label(ret, SWT.WRAP);
		lTitle.setText(tl.getText());
		lTitle.setLayoutData(SWTHelper.getFillGridData(8, true, 1, true));
		double tpAL = tl.getAL() / 100.0;
		double tpTL = tl.getTL() / 100.0;
		String arzl = verrechnet.getDetail("AL");
		String tecl = verrechnet.getDetail("TL");
		double primaryScale = verrechnet.getPrimaryScaleFactor();
		double secondaryScale = verrechnet.getSecondaryScaleFactor();
		if (arzl != null) {
			tpAL = Double.parseDouble(arzl) / 100.0;
		}
		if (tecl != null) {
			tpTL = Double.parseDouble(tecl) / 100.0;
		}
		double tpw = verrechnet.getTPW();
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
		String sPflicht = verrechnet.getDetail(TarmedLeistung.PFLICHTLEISTUNG);
		if ((sPflicht == null) || (Boolean.parseBoolean(sPflicht))) {
			bPflicht.setSelection(true);
		}
		String side = verrechnet.getDetail(TarmedLeistung.SIDE);
		if (side == null) {
			cSide.select(0);
		} else if (side.equalsIgnoreCase("l")) {
			cSide.select(1);
		} else {
			cSide.select(2);
		}
		if (tl.getServiceTyp().equals("Z") || tl.getServiceTyp().equals("R")
			|| tl.getServiceTyp().equals("B")) {
			new Label(ret, SWT.NONE);
			new Label(ret, SWT.NONE);
			new Label(ret, SWT.NONE).setText("Bezug");
			
			cBezug = new ComboViewer(ret, SWT.BORDER);
			cBezug.setContentProvider(ArrayContentProvider.getInstance());
			cBezug.setLabelProvider(new LabelProvider());
			List<BezugComboItem> input = new ArrayList<>();
			input.add(BezugComboItem.noBezug());
			for (Verrechnet kVerr : verrechnet.getKons().getLeistungen()) {
				if (!kVerr.getCode().equals(tl.getCode())) {
					input.add(BezugComboItem.of(kVerr.getCode()));
				}
			}
			cBezug.setInput(input);
			String bezug = verrechnet.getDetail("Bezug");
			if (bezug != null) {
				cBezug.setSelection(new StructuredSelection(BezugComboItem.of(bezug)), true);
			} else {
				cBezug.setSelection(new StructuredSelection(BezugComboItem.noBezug()), true);
			}
			cBezug.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event){
					StructuredSelection selection = (StructuredSelection) cBezug.getSelection();
					if (selection != null && !selection.isEmpty()) {
						BezugComboItem selected = (BezugComboItem) selection.getFirstElement();
						if (selected.isNoBezug) {
							verrechnet.setDetail("Bezug", "");
						} else {
							verrechnet.setDetail("Bezug", selected.getCode());
						}
					}
				}
			});
		}
		ret.pack();
		return ret;
	}
	
	private static class BezugComboItem {
		private String code;
		private boolean isNoBezug;
		
		public static BezugComboItem of(String code){
			BezugComboItem ret = new BezugComboItem();
			ret.setCode(code);
			return ret;
		}
		
		public static BezugComboItem noBezug(){
			BezugComboItem ret = new BezugComboItem();
			ret.setCode("kein Bezug");
			ret.isNoBezug = true;
			return ret;
		}
		
		public boolean isNoBezug(){
			return isNoBezug;
		}
		
		private void setCode(String code){
			this.code = code;
		}
		
		public String getCode(){
			return this.code;
		}
		
		@Override
		public String toString(){
			return getCode();
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result + (isNoBezug ? 1231 : 1237);
			return result;
		}
		
		@Override
		public boolean equals(Object obj){
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BezugComboItem other = (BezugComboItem) obj;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			if (isNoBezug != other.isNoBezug)
				return false;
			return true;
		}
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText("Tarmed-Details: " + verrechnet.getCode());
	}
	
	@Override
	protected void okPressed(){
		int idx = cSide.getSelectionIndex();
		if (idx < 1) {
			verrechnet.setDetail(TarmedLeistung.SIDE, null);
		} else if (idx == 1) {
			verrechnet.setDetail(TarmedLeistung.SIDE, "l");
		} else {
			verrechnet.setDetail(TarmedLeistung.SIDE, "r");
		}
		verrechnet.setDetail(TarmedLeistung.PFLICHTLEISTUNG, Boolean.toString(bPflicht.getSelection()));
		super.okPressed();
	}
	
}
