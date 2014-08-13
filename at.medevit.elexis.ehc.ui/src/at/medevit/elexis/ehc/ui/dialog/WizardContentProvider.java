/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.dialog;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import at.medevit.elexis.ehc.ui.extension.IWizardCategory;

public class WizardContentProvider implements ITreeContentProvider {
	
	List<IWizardCategory> categories;
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof List) {
			categories = (List<IWizardCategory>) newInput;
		}
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return categories.toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement){
		if (parentElement instanceof IWizardCategory) {
			return ((IWizardCategory) parentElement).getWizards().toArray();
		}
		return new Object[0];
	}
	
	@Override
	public Object getParent(Object element){
		return null;
	}
	
	@Override
	public boolean hasChildren(Object element){
		if (element instanceof IWizardCategory) {
			return true;
		}
		return false;
	}
	
}
