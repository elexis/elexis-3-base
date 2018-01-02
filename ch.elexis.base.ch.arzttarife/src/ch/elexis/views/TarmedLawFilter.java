/*******************************************************************************
 * Copyright (c) 2012 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     T. Huster - reworked for Tarmed version 1.08
 ******************************************************************************/
package ch.elexis.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.data.TarmedLeistung;

public class TarmedLawFilter extends ViewerFilter {
	
	private boolean doFilter = true;
	private String law;
	
	public boolean getDoFilter(){
		return doFilter;
	}
	
	public void setDoFilter(boolean value){
		doFilter = value;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		TarmedLeistung leistung = (TarmedLeistung) element;
		
		if (doFilter && law != null) {
			return law.equalsIgnoreCase(leistung.get(TarmedLeistung.FLD_LAW));
		}
		return true;
	}
	
	public void setLaw(String law){
		this.law = law;
	}
}
