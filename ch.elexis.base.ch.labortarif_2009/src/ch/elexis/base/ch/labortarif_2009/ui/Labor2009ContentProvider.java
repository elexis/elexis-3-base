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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.labortarif2009.data.Labor2009Tarif;

public class Labor2009ContentProvider implements ICommonViewerContentProvider {
	
	private List<Labor2009Tarif> elements;

	public Object[] getElements(Object inputElement){
		if (elements == null) {
			return Collections.emptyList().toArray();
		}
		return elements.toArray();
	}
	
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		if (newInput instanceof List<?>) {
			elements = (List<Labor2009Tarif>) newInput;
		}
	}
	
	public void changed(HashMap<String, String> values){
		// TODO Auto-generated method stub
		
	}
	
	public void reorder(String field){
		// TODO Auto-generated method stub
		
	}
	
	public void selected(){
		// TODO Auto-generated method stub
		
	}
	
	public void init(){
		// TODO Auto-generated method stub
		
	}
	
	public void startListening(){
		// TODO Auto-generated method stub
		
	}
	
	public void stopListening(){
		// TODO Auto-generated method stub
		
	}
}
