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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class ExportPatientWizard extends Wizard {
	
	private ExportPatientWizardPage1 mainPage;
	
	public ExportPatientWizard(){
		// TODO Auto-generated constructor stub
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection){
		setWindowTitle("Patientenstammdaten export.");
		setNeedsProgressMonitor(true);
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
