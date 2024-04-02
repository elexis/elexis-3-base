/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.icpc;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;

public class IcpcImporter extends ImporterPage {

	@Inject
	private IReferenceDataImporterService importerService;

	public IcpcImporter() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public Composite createPage(Composite parent) {
		FileBasedImporter fbi = new FileBasedImporter(parent, this);
		fbi.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return fbi;

	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		IReferenceDataImporter importer = importerService.getImporter("icpc") //$NON-NLS-1$
				.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available.")); //$NON-NLS-1$
		return importer.performImport(monitor, new FileInputStream(results[0]), null);
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IDiagnosisTree.class.getName());
	}

	@Override
	public String getDescription() {
		return "International Classification of Primary Care";
	}

	@Override
	public String getTitle() {
		return "ICPC-2";
	}
}
