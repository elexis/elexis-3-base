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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.elexis.impfplan.model.Vaccination;
import ch.elexis.impfplan.model.VaccinationType;

public class VaccinationLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex){
		if (element instanceof Vaccination) {
			Vaccination vac = (Vaccination) element;
			VaccinationType vt = vac.getVaccinationType();
			if (columnIndex == 0) {
				return vt.getLabel();
			} else {
				return vac.getDateAsString();
			}
		} else if (element instanceof VaccinationType) {
			if (columnIndex == 0) {
				return ((VaccinationType) element).getLabel();
			}
		}
		return ""; //$NON-NLS-1$
	}
	
}
