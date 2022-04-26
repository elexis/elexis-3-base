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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.Wizard;

import at.medevit.elexis.ehc.ui.extension.IWizardDescriptor;

public class WizardDescriptor implements IWizardDescriptor {

	private String label;
	private String categoryId;

	private IConfigurationElement configuration;

	private Wizard wizard;

	public WizardDescriptor(IConfigurationElement el) {
		label = el.getAttribute("name");
		categoryId = el.getAttribute("category");

		configuration = el;
	}

	@Override
	public Wizard createWizard() throws CoreException {
		if (wizard == null) {
			wizard = (Wizard) configuration.createExecutableExtension("class");
		}
		return wizard;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getCategoryId() {
		return categoryId;
	}
}
