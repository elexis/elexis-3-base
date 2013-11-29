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

import java.util.HashMap;

import org.eclipse.jface.viewers.Viewer;

import ch.elexis.base.ch.labortarif_2009.data.Labor2009Tarif;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.data.Query;

public class Labor2009ContentProvider implements ICommonViewerContentProvider {
	
	public Object[] getElements(Object inputElement){
		Query<Labor2009Tarif> qbe = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		return qbe.execute().toArray();
	}
	
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		// TODO Auto-generated method stub
		
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
