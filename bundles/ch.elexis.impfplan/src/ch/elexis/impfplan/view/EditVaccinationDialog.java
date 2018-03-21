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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.impfplan.model.DiseaseDefinitionModel;
import ch.elexis.impfplan.model.DiseaseDefinitionModel.DiseaseDefinition;
import ch.elexis.impfplan.model.VaccinationType;

@SuppressWarnings("unchecked")
public class EditVaccinationDialog extends TitleAreaDialog {
	CheckboxTreeViewer treeViewer;
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
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		panel.setAutosave(true);
		panel.setObject(vt);
		
		Composite treeComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 5;
		treeComposite.setLayout(gridLayout);
		treeComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		treeViewer =
			new CheckboxTreeViewer(treeComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeViewerColumn col = new TreeViewerColumn(treeViewer, SWT.NONE);
		col.getColumn().setWidth(225);
		col.getColumn().setText("Impfung gegen Krankheit(en)");
		
		treeViewer.setContentProvider(new DiseaseTreeContentProvider());
		treeViewer.setLabelProvider(new DiseaseTreeLabelProvider());
		treeViewer.setInput(DiseaseDefinitionModel.getDiseaseDefinitions());
		treeViewer.setCheckedElements(loadSelected().toArray());
		
		return panel;
	}
	
	private List<DiseaseDefinition> loadSelected(){
		List<DiseaseDefinition> diseaseDefinitions = DiseaseDefinitionModel.getDiseaseDefinitions();
		List<DiseaseDefinition> selected = new ArrayList<DiseaseDefinition>();
		
		for (String code : vt.getVaccAgainstList()) {
			for (DiseaseDefinition dd : diseaseDefinitions) {
				if (dd.getATCCode().equals(code)) {
					selected.add(dd);
				}
			}
		}
		return selected;
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
		Object[] checkedElements = treeViewer.getCheckedElements();
		StringBuilder sb = new StringBuilder();
		
		for (Object element : checkedElements) {
			DiseaseDefinition disease = (DiseaseDefinition) element;
			sb.append(disease.getATCCode());
			sb.append(",");
		}
		vt.setVaccAgainst(sb.toString());
		
		super.okPressed();
	}
	
	private class DiseaseTreeContentProvider implements ITreeContentProvider {
		
		@Override
		public void dispose(){}
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){}
		
		@Override
		public Object[] getElements(Object inputElement){
			return DiseaseDefinitionModel.getDiseaseDefinitions().toArray();
		}
		
		@Override
		public Object[] getChildren(Object parentElement){
			return null;
		}
		
		@Override
		public Object getParent(Object element){
			return (DiseaseDefinition) element;
		}
		
		@Override
		public boolean hasChildren(Object element){
			return false;
		}
	}
	
	private class DiseaseTreeLabelProvider implements ILabelProvider {
		
		@Override
		public void addListener(ILabelProviderListener listener){}
		
		@Override
		public void dispose(){}
		
		@Override
		public boolean isLabelProperty(Object element, String property){
			return false;
		}
		
		@Override
		public void removeListener(ILabelProviderListener listener){}
		
		@Override
		public Image getImage(Object element){
			return null;
		}
		
		@Override
		public String getText(Object element){
			return ((DiseaseDefinition) element).getDiseaseLabel();
		}
		
	}
}
