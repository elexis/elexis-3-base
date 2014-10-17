/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.impfplan.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.impfplan.model.VaccinationType;

@SuppressWarnings("unchecked")
public class EditVaccinationDialog extends TitleAreaDialog {
	VaccinationType vt;
	
	FieldDescriptor<VaccinationType>[] fields = new FieldDescriptor[] {
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_nameOfVaccination,
			VaccinationType.NAME, FieldDescriptor.Typ.STRING, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_vaccinationSubstance,
			VaccinationType.PRODUCT, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_ageFromTo,
			VaccinationType.RECOMMENDED_AGE, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_distance1_2,
			VaccinationType.DELAY1TO2, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_distance2_3,
			VaccinationType.DELAY2TO3, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_distance3_4,
			VaccinationType.DELAY3TO4, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_distanceRappel,
			VaccinationType.DELAY_REP, null),
		new FieldDescriptor<VaccinationType>(Messages.EditVaccinationDialog_remarks,
			VaccinationType.REMARKS, null),
	
	};
	
	public EditVaccinationDialog(Shell shell, VaccinationType vacc){
		super(shell);
		vt = vacc;
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		DisplayPanel panel = new DisplayPanel(parent, fields, 2, 2, new IAction[0]);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		panel.setAutosave(true);
		panel.setObject(vt);
		return panel;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(Messages.EditVaccinationDialog_enterVaccination);
		getShell().setText(Messages.EditVaccinationDialog_defineVaccination);
		getShell().setSize(800, 600);
		SWTHelper.center(getShell());
	}
	
	@Override
	protected void okPressed(){
		super.okPressed();
	}
	
}
