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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.ICD10;

public class ICDDetailDisplay implements IDetailDisplay {
	FormToolkit tk = UiDesk.getToolkit();
	ScrolledForm form;
	Text titel;
	
	public Class getElementClass(){
		return ICD10.class;
	}
	
	public void display(Object obj){
		ICD10 ic = (ICD10) obj;
		form.setText(ic.getCode());
		titel.setText(ic.getText());
	}
	
	public String getTitle(){
		return "ICD-10"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createScrolledForm(parent);
		Composite ret = form.getBody();
		ret.setLayout(new GridLayout());
		Group g1 = new Group(ret, SWT.BORDER);
		g1.setLayout(new FillLayout());
		g1.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		g1.setText("Text"); //$NON-NLS-1$
		titel = tk.createText(g1, ""); //$NON-NLS-1$
		tk.adapt(g1);
		tk.paintBordersFor(ret);
		return ret;
	}
	
}
