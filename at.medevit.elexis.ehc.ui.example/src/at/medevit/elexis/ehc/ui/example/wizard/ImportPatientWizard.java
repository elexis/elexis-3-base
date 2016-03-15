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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.ehealth_connector.cda.ch.AbstractCdaCh;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.example.service.ServiceComponent;
import at.medevit.elexis.ehc.ui.extension.IImportWizard;

public class ImportPatientWizard extends Wizard implements IImportWizard {
	
	private static Logger logger = LoggerFactory.getLogger(ImportPatientWizard.class);
	
	private ImportPatientWizardPage1 mainPage;
	
	private AbstractCdaCh<?> ehcDocument;
	
	public ImportPatientWizard(){
		setWindowTitle("Patientenstammdaten import.");
	}
	
	@Override
	public void addPages(){
		super.addPages();
		mainPage = new ImportPatientWizardPage1("Import Patientenstammdaten.", ehcDocument);
		addPage(mainPage);
	}
	
	@Override
	public boolean performFinish(){
		return mainPage.finish();
	}
	
	@Override
	public void setDocument(InputStream document){
		try {
			document.reset();
			ClinicalDocument clinicalDocument = ServiceComponent.getService().getDocument(document);
			ehcDocument = ServiceComponent.getService().getCdaChDocument(clinicalDocument);
			if (mainPage != null) {
				mainPage.setDocument(ehcDocument);
			}
		} catch (IOException e) {
			logger.error("Could not open document", e);
			MessageDialog.openError(getShell(), "Fehler", "Konnte das Dokument nicht öffnen.");
		}
	}
}
