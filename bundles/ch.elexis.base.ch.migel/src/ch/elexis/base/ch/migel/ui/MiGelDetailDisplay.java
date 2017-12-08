/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *    
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.rgw.tools.Money;

public class MiGelDetailDisplay implements IDetailDisplay {
	
	FormToolkit tk = UiDesk.getToolkit();
	ScrolledForm form;
	LabeledInputField ifName, ifPreis;
	Text tName, tLong;
	MiGelArtikel act;
	
	public Composite createDisplay(Composite parent, IViewSite site){
		parent.setLayout(new GridLayout());
		form = tk.createScrolledForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite ret = form.getBody();
		ret.setLayout(new GridLayout());
		
		ifName = new LabeledInputField(ret, "Name");
		ifName.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tName = (Text) ifName.getControl();
		tName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e){
				if (act != null) {
					act.setInternalName(tName.getText());
				}
				super.focusLost(e);
			}
			
		});
		ifPreis = new LabeledInputField(ret, Messages.MiGelDetailDisplay_PriceUnit);
		tLong = SWTHelper.createText(tk, ret, 4, SWT.READ_ONLY);

		return ret;
	}
	
	public Class getElementClass(){
		return MiGelArtikel.class;
	}
	
	public void display(Object obj){
		if (obj instanceof MiGelArtikel) {
			act = (MiGelArtikel) obj;
			form.setText(act.getLabel());
			ifName.setText(act.getInternalName());
			ifPreis.setText(new Money(act.getVKPreis()).getAmountAsString() + " " //$NON-NLS-1$
				+ act.getExt("unit")); //$NON-NLS-1$
			tLong.setText(act.getExt("FullText")); //$NON-NLS-1$
		}
	}
	
	public String getTitle(){
		return MiGelArtikel.MIGEL_NAME; //$NON-NLS-1$
	}
	
}
