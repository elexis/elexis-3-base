/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.base.ch.medikamente.bag.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

import ch.elexis.base.ch.medikamente.bag.data.BAGMedi;
import ch.elexis.core.ui.views.IDetailDisplay;

public class BAGMediDetailDisplay implements IDetailDisplay {
	BAGMediDetailBlatt blatt;
	
	public Composite createDisplay(final Composite parent, final IViewSite site){
		blatt = new BAGMediDetailBlatt(parent);
		return blatt;
	}
	
	public void display(final Object obj){
		blatt.display((BAGMedi) obj);
		
	}
	
	@SuppressWarnings("unchecked")
	public Class getElementClass(){
		return BAGMedi.class;
	}
	
	public String getTitle(){
		return BAGMedi.CODESYSTEMNAME;
	}
	
}
