/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.ui.UiDesk;
import ch.rgw.tools.TimeTool;


public class VaccinationComposite extends Composite {
	
	private VaccinationCompositePaintListener vcpl;
	
	public VaccinationComposite(Composite parent){
		super(parent, SWT.NONE);
		vcpl = new VaccinationCompositePaintListener();
		addPaintListener(vcpl);
		setBackground(UiDesk.getColor(UiDesk.COL_WHITE));
	}
	
	@Override
	public void dispose(){
		removePaintListener(vcpl);
		vcpl = null;
		super.dispose();
	}

	public void updateUi(VaccinationPlanHeaderDefinition _ihd, List<Vaccination> vaccinations, TimeTool birthDate){
		vcpl.setVaccinationPlanHeader(_ihd);
		vcpl.setVaccinationEntries(vaccinations);
		vcpl.setPatientBirthdate(birthDate);
		redraw();
	}
	
	public VaccinationCompositePaintListener getVaccinationCompositePaintListener() {
		return vcpl;
	}

}
