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
package at.medevit.elexis.impfplan.ui.handlers;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import at.medevit.elexis.impfplan.model.VaccinationPlanModel;
import at.medevit.elexis.impfplan.model.vaccplans.AbstractVaccinationPlan;
import at.medevit.elexis.impfplan.ui.VaccinationPlanHeaderDefinition;
import at.medevit.elexis.impfplan.ui.VaccinationView;

public class FilterHandlerDynamicContribution extends ContributionItem {

	private List<AbstractVaccinationPlan> vaccPlans = VaccinationPlanModel.getVaccinationPlans();

	public FilterHandlerDynamicContribution() {
		// TODO Auto-generated constructor stub
	}

	public FilterHandlerDynamicContribution(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void fill(Menu menu, int index) {
		for (final AbstractVaccinationPlan avp : vaccPlans) {
			MenuItem temp = new MenuItem(menu, SWT.RADIO, index);
			temp.setText(avp.name);
			temp.setSelection(avp.id.equalsIgnoreCase(VaccinationView.getVaccinationHeaderDefinition().id));
			temp.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					VaccinationPlanHeaderDefinition vphd = new VaccinationPlanHeaderDefinition(avp.id, avp.name,
							avp.getOrderedBaseDiseases(), avp.getOrderedExtendedDiseases());
					VaccinationView.setVaccinationHeaderDefinition(vphd);
				}
			});
		}

		MenuItem menuItemHWAV = new MenuItem(menu, SWT.RADIO, index);
		menuItemHWAV.setText("verabreichten Impfungen");
		menuItemHWAV.setSelection(VaccinationView.getVaccinationHeaderDefinition().id
				.equals(VaccinationView.HEADER_ID_SHOW_ADMINISTERED));
		menuItemHWAV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				@SuppressWarnings("unchecked")
				VaccinationPlanHeaderDefinition vphd = new VaccinationPlanHeaderDefinition(
						VaccinationView.HEADER_ID_SHOW_ADMINISTERED, VaccinationView.HEADER_ID_SHOW_ADMINISTERED,
						Collections.EMPTY_LIST, Collections.EMPTY_LIST);
				VaccinationView.setVaccinationHeaderDefinition(vphd);
			}
		});

		MenuItem menuInfo = new MenuItem(menu, SWT.CHECK, index);
		menuInfo.setEnabled(false);
		menuInfo.setText("Darstellung nach");

		super.fill(menu, index);
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

}
