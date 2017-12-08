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
package at.medevit.elexis.ehc.ui.example.wizard;

import org.eclipse.jface.wizard.Wizard;

public class ExportPatientWizard extends Wizard {
	
	private ExportPatientWizardPage1 mainPage;
	
	public ExportPatientWizard(){
		setWindowTitle("Patientenstammdaten export.");
	}
	
	@Override
	public void addPages(){
		super.addPages();
		mainPage = new ExportPatientWizardPage1("Export Patientenstammdaten.");
		addPage(mainPage);
	}
	
	@Override
	public boolean performFinish(){
		return mainPage.finish();
	}
	
}
