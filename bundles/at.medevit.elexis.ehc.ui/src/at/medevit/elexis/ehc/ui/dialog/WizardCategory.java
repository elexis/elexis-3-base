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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import at.medevit.elexis.ehc.ui.extension.IWizardCategory;
import at.medevit.elexis.ehc.ui.extension.IWizardDescriptor;

public class WizardCategory implements IWizardCategory {

	private List<IWizardDescriptor> wizards;

	private String id;
	private String label;

	public WizardCategory(IConfigurationElement el) {
		id = el.getAttribute("id"); //$NON-NLS-1$
		label = el.getAttribute("name");
	}

	public WizardCategory(String id, String label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public List<IWizardDescriptor> getWizards() {
		return wizards;
	}

	@Override
	public void addWizard(IWizardDescriptor wizard) {
		if (wizards == null) {
			wizards = new ArrayList<IWizardDescriptor>();
		}
		wizards.add(wizard);
	}
}
