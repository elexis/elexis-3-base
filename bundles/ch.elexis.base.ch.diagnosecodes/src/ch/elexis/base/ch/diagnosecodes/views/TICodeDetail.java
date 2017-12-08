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

package ch.elexis.base.ch.diagnosecodes.views;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.elexis.base.ch.ticode.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.TICode;

public class TICodeDetail implements IDetailDisplay {
	
	FormToolkit tk = UiDesk.getToolkit();
	Form form;
	Text tID, tFull;
	
	public Class getElementClass(){
		return TICode.class;
	}
	
	public void display(Object obj){
		if (obj instanceof TICode) {
			TICode tc = (TICode) obj;
			tID.setText(tc.getCode());
			tFull.setText(tc.getText());
		}
	}
	
	public String getTitle(){
		return "TI Code"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createForm(parent);
		Composite body = form.getBody();
		body.setLayout(new GridLayout(2, false));
		tk.createLabel(body, "Code"); //$NON-NLS-1$
		tID = tk.createText(body, ""); //$NON-NLS-1$
		tID.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tk.createLabel(body, Messages.TICodeDetail_fulltext);
		tFull = tk.createText(body, ""); //$NON-NLS-1$
		tFull.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return body;
	}
	
}
