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

import java.io.InputStream;

import org.eclipse.jface.wizard.Wizard;

import at.medevit.elexis.ehc.ui.extension.ImportWizardsExtension;

public class ImportSelectionWizard extends Wizard {

	@Override
	public void addPages() {
		addPage(new ImportWizardSelectionPage());
		setForcePreviousAndNextButtons(true);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

	public void setDocument(InputStream document) {
		ImportWizardsExtension.setImportDocument(document);
	}
}
