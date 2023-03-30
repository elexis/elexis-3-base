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

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;

public class TarmedAllowanceImporter extends ImporterPage {

	@Inject
	private IReferenceDataImporterService importerService;

	public TarmedAllowanceImporter() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public Composite createPage(Composite parent) {
		return new FileBasedImporter(parent, this);
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		IReferenceDataImporter importer = importerService.getImporter("tarmedallowance")
				.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
		return importer.performImport(monitor, new FileInputStream(results[0]), null);
	}

	@Override
	public String getDescription() {
		return "Tarmedpauschalen";
	}

	@Override
	public String getTitle() {
		return "Tarmedpauschalen";
	}
}
