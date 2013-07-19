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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.base.ch.labortarif_2009.data.Labor2009Tarif;
import ch.rgw.tools.TimeTool;

public class Labor2009CodeTextValidFilter extends ViewerFilter {
	
	private String searchString;
	private TimeTool validDate;
	
	public void setSearchText(String s){
		if (s == null || s.length() == 0) {
			searchString = s;
		} else {
			s = s.replaceAll("\\.", "\\\\.");
			searchString = ".*" + s.toLowerCase() + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Show only positions valid at date. Set date to null to show all.
	 * 
	 * @param date
	 */
	public void setValidDate(TimeTool date){
		validDate = date;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element){
		Labor2009Tarif tarif = (Labor2009Tarif) element;
		
		if (validDate != null) {
			if (!tarif.isValidOn(validDate))
				return false;
		}
		
		if (searchString == null || searchString.length() == 0) {
			return true;
		}
		
		String code = tarif.getCode().toLowerCase();
		if (code != null && code.matches(searchString)) {
			return true;
		}
		
		String text = tarif.get(Labor2009Tarif.FLD_NAME).toLowerCase();
		if (text != null && text.matches(searchString)) {
			return true;
		}
		
		return false;
	}
}
