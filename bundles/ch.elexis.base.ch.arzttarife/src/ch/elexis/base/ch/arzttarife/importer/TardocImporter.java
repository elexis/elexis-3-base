/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz	 - Import from different DBMS
 *    N. Giger   - direct import from MDB file using Gerrys AccessWrapper
 *
 *******************************************************************************/

// 8.12.07 G.Weirich avoid duplicate imports
package ch.elexis.base.ch.arzttarife.importer;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import jakarta.inject.Inject;

public class TardocImporter extends ImporterPage {

	@Inject
	private IReferenceDataImporterService importerService;

	public TardocImporter() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public String getTitle() {
		return "TARDOC code"; //$NON-NLS-1$
	}

	@Override
	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.util.ImporterPage#doImport(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	public IStatus doImport(final IProgressMonitor monitor) throws Exception {
		IReferenceDataImporter trdImporter = getImporter();
		return trdImporter.performImport(monitor, new FileInputStream(results[0]), null);
	}

	private IReferenceDataImporter getImporter() {
		// default importer
		return importerService.getImporter("tardoc")
				.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available"));
	}

	@Override
	public String getDescription() {
		return "Bitte geben Sie den Namen der heruntergeladene (entzippten) Tardoc-Datei an (z.B. 250410_TARDOC_1.4b_ohne_001_4.mdb.";
	}

	@Override
	public Composite createPage(final Composite parent) {
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		return fis;
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(ITarmedLeistung.class.getName());
	}
}
