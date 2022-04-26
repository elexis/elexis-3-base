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

import org.eclipse.jface.wizard.Wizard;

public class ExportSelectionWizard extends Wizard {

	@Override
	public void addPages() {
		addPage(new ExportWizardSelectionPage());
		setForcePreviousAndNextButtons(true);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
