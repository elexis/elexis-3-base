/*******************************************************************************
 * Copyright (c) 2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.arzttarife.importer;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import jakarta.inject.Inject;

public class AmbulatoryAllowanceImporter extends ImporterPage {

	@Inject
	private IReferenceDataImporterService importerService;

	private FileBasedImporter pauschalenFile;
	private FileBasedImporter tarifeFile;

	public AmbulatoryAllowanceImporter() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public Composite createPage(Composite parent) {
		Composite filesComposite = new Composite(parent, SWT.NONE);
		filesComposite.setLayout(new GridLayout());
		filesComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		pauschalenFile = new ImporterPage.FileBasedImporter(filesComposite, this);
		pauschalenFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		pauschalenFile.setText(
				"Ambulantepauschalen Datei (Z.B.: 250808_Anhang_A1_Katalog_der_Ambulanten_Pauschalen_CSV_v1.1c.csv)");

		tarifeFile = new ImporterPage.FileBasedImporter(filesComposite, this);
		tarifeFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		tarifeFile.setText("Ambulantetarife Datei (Z.B.: 250808_LKAAT_1.0c.CSV)");
		return filesComposite;
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		IStatus ret = null;
		if (StringUtils.isNoneBlank(pauschalenFile.getResult())) {
			IReferenceDataImporter importer = importerService.getImporter("ambulatoryallowance")
					.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
			ret = importer.performImport(monitor, new FileInputStream(pauschalenFile.getResult()), null);
			if (!ret.isOK()) {
				return ret;
			}
		}
		if (StringUtils.isNoneBlank(tarifeFile.getResult())) {
			IReferenceDataImporter importer = importerService.getImporter("ambulatorytarif")
					.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
			ret = importer.performImport(monitor, new FileInputStream(tarifeFile.getResult()), null);
			if (!ret.isOK()) {
				return ret;
			}
		}
		return ret;
	}

	@Override
	public String getDescription() {
		return "Bitte geben Sie den Namen der heruntergeladenen (entzippten) Ambulantepauschalen und Ambulantetarife-Datei an.";
	}

	@Override
	public String getTitle() {
		return "Ambulantepauschalen";
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IAmbulatoryAllowance.class.getName());
	}
}
