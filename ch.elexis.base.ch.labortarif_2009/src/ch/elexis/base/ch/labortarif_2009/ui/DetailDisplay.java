/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.base.ch.labortarif_2009.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.base.ch.labortarif_2009.data.Labor2009Tarif;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.PersistentObject;

public class DetailDisplay implements IDetailDisplay {
	Form form;
	DisplayPanel panel;
	FieldDescriptor<?>[] fields = {
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_chapter,
			Labor2009Tarif.FLD_CHAPTER, Typ.STRING, null),
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_code, Labor2009Tarif.FLD_CODE,
			Typ.STRING, null),
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_fachbereich,
			Labor2009Tarif.FLD_FACHBEREICH, Typ.STRING, null),
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_name, Labor2009Tarif.FLD_NAME,
			Typ.STRING, null),
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_limitation,
			Labor2009Tarif.FLD_LIMITATIO, Typ.STRING, null),
		new FieldDescriptor<Labor2009Tarif>(Messages.DetailDisplay_taxpoints,
			Labor2009Tarif.FLD_TP, Typ.STRING, null)
	};
	
	public void display(Object obj){
		if (obj instanceof Labor2009Tarif) {
			form.setText(((PersistentObject) obj).getLabel());
			panel.setObject((PersistentObject) obj);
		}
	}
	
	public Class<? extends PersistentObject> getElementClass(){
		return Labor2009Tarif.class;
	}
	
	public String getTitle(){
		return "EAL 2009"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = UiDesk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		panel = new DisplayPanel(form.getBody(), fields, 1, 1);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return panel;
	}
	
}
