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
package ch.elexis.impfplan.controller;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.elexis.impfplan.model.Vaccination;
import ch.elexis.impfplan.model.VaccinationType;
import ch.rgw.tools.TimeTool;

public class VaccinationSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof VaccinationType) {
			VaccinationType v1 = (VaccinationType) e1;
			VaccinationType v2 = (VaccinationType) e2;
			int c = v1.get(VaccinationType.NAME).compareTo(v2.get(VaccinationType.NAME));
			if (c == 0) {
				c = v1.get(VaccinationType.PRODUCT).compareTo(v2.get(VaccinationType.PRODUCT));
			}
			return c;
		} else if (e1 instanceof Vaccination) {
			int c = compare(viewer, ((Vaccination) e1).getVaccinationType(), ((Vaccination) e2).getVaccinationType());
			if (c == 0) {
				Vaccination v1 = (Vaccination) e1;
				Vaccination v2 = (Vaccination) e2;
				TimeTool tt1 = new TimeTool(v1.get(Vaccination.DATE));
				TimeTool tt2 = new TimeTool(v2.get(Vaccination.DATE));
				c = tt1.compareTo(tt2);
			}
			return c;
		} else {
			return 0;
		}
	}

}
