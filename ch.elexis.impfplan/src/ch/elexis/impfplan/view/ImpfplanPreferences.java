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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.impfplan.controller.ImpfplanController;
import ch.elexis.impfplan.model.VaccinationType;

public class ImpfplanPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	private IAction removeAction, editAction;
	private TableViewer tv;
	
	public ImpfplanPreferences(){
		makeActions();
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		tv = new TableViewer(ret);
		tv.getControl().setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tv.setContentProvider(new ContentProviderAdapter() {
			@Override
			public Object[] getElements(Object arg0){
				return ImpfplanController.allVaccs().toArray();
			}
		});
		tv.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element){
				if (element instanceof VaccinationType) {
					return ((VaccinationType) element).getLabel();
				}
				return "?"; //$NON-NLS-1$
			}
			
		});
		tv.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event){
				edit();
				
			}
		});
		Composite cButtons = new Composite(ret, SWT.NONE);
		cButtons.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		cButtons.setLayout(new RowLayout(SWT.HORIZONTAL));
		Button bAdd = new Button(cButtons, SWT.PUSH);
		bAdd.setText(Messages.ImpfplanPreferences_addCaption);
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				EditVaccinationDialog dlg =
					new EditVaccinationDialog(getShell(), new VaccinationType(
						Messages.ImpfplanPreferences_nameDummy,
						Messages.ImpfplanPreferences_vaccDummy));
				if (dlg.open() == Dialog.OK) {
					tv.refresh();
				}
			}
			
		});
		MenuManager menu = new MenuManager();
		menu.add(removeAction);
		tv.getControl().setMenu(menu.createContextMenu(tv.getControl()));
		tv.setInput(this);
		return ret;
	}
	
	@Override
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	private void edit(){
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if (!sel.isEmpty()) {
			VaccinationType t = (VaccinationType) sel.getFirstElement();
			if (new EditVaccinationDialog(getShell(), t).open() == Dialog.OK) {
				tv.refresh(true);
			}
			
		}
		
	}
	
	private void makeActions(){
		removeAction = new Action(Messages.ImpfplanPreferences_removeVaccination) {
			{
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
				setToolTipText(Messages.ImpfplanPreferences_removeVaccWarning);
			}
			
			@Override
			public void run(){
				IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
				if (!sel.isEmpty()) {
					VaccinationType t = (VaccinationType) sel.getFirstElement();
					if (t.delete()) {
						tv.remove(sel.getFirstElement());
					}
				}
				
			}
			
		};
	}
	
}
