/*******************************************************************************
 * Copyright (c) 2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.PersistentObject;

public class PandemieDetailDisplay implements IDetailDisplay {
	Form form;
	FormToolkit tk = UiDesk.getToolkit();
	LabeledInputField.AutoForm tblLab;
	
	InputData[] data = new InputData[] {
		new InputData("Taxpunkte", PandemieLeistung.FLD_TAXPOINTS, InputData.Typ.STRING, null), //$NON-NLS-1$
		new InputData("Preis in Rappen", PandemieLeistung.FLD_CENTS, InputData.Typ.STRING, null), //$NON-NLS-1$
		};
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = tk.createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		
		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblLab.setLayoutData(twd);
		return form.getBody();
	}
	
	public void display(Object obj){
		PandemieLeistung ll = (PandemieLeistung) obj;
		form.setText(ll.getLabel());
		tblLab.reload(ll);
	}
	
	public Class<? extends PersistentObject> getElementClass(){
		return PandemieLeistung.class;
	}
	
	public String getTitle(){
		return "Pandemie";
	}
	
}
