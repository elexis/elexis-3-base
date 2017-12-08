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

package ch.berchtold.emanuel.privatrechnung.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.berchtold.emanuel.privatrechnung.data.Leistung;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;

/**
 * An IDetailDisplay must be able to create and manage a form that can display detailed information
 * on a code of this codesystem.
 * 
 * @author Gerry
 * 
 */
public class Leistungsdetail implements IDetailDisplay {
	Form form;
	LabeledInputField.AutoForm tblPls;
	InputData[] data = new InputData[] {
		new InputData("Kuerzel"), //$NON-NLS-1$
		new InputData("Kosten", "Kosten", InputData.Typ.CURRENCY, null), //$NON-NLS-1$
		new InputData("Preis", "Preis", InputData.Typ.CURRENCY, null), //$NON-NLS-1$
	};
	
	/**
	 * Select the given Objetc to display
	 */
	public void display(Object obj){
		if (obj instanceof Leistung) { // should always be true...
			Leistung ls = (Leistung) obj;
			form.setText(ls.getLabel());
			tblPls.reload(ls);
		}
		
	}
	
	public Class getElementClass(){
		return Leistung.class;
	}
	
	public String getTitle(){
		return "Privatrechnung B";
	}
	
	/**
	 * Create the display composite. As usual, we'll keep things simple and re-use existing classes
	 * to simplify our work.
	 */
	public Composite createDisplay(Composite parent, IViewSite site){
		form = UiDesk.getToolkit().createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		
		tblPls = new LabeledInputField.AutoForm(form.getBody(), data);
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblPls.setLayoutData(twd);
		return form.getBody();
	}
	
}
