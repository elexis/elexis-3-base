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
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.example.service.ServiceComponent;
import at.medevit.elexis.ehc.ui.extension.IImportWizard;

public class ImportPatientWizard extends Wizard implements IImportWizard {

	private static Logger logger = LoggerFactory.getLogger(ImportPatientWizard.class);

	private ImportPatientWizardPage1 mainPage;

	private POCDMT000040ClinicalDocument ehcDocument;

	public ImportPatientWizard() {
		setWindowTitle("Patientenstammdaten import.");
	}

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new ImportPatientWizardPage1("Import Patientenstammdaten.", ehcDocument);
		addPage(mainPage);
	}

	@Override
	public boolean performFinish() {
		return mainPage.finish();
	}

	@Override
	public void setDocument(InputStream document) {
		try {
			document.reset();
			POCDMT000040ClinicalDocument clinicalDocument = ServiceComponent.getService().loadDocument(document);
			if (mainPage != null) {
				mainPage.setDocument(ehcDocument);
			}
		} catch (IOException e) {
			logger.error("Could not open document", e); //$NON-NLS-1$
			MessageDialog.openError(getShell(), "Fehler", "Konnte das Dokument nicht öffnen.");
		}
	}
}
