/*******************************************************************************
 * Copyright (c) 2013-2016 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.importer;

import java.io.FileInputStream;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;

public class ArtikelstammImporterPage extends ImporterPage {
	private static Logger log = LoggerFactory.getLogger(ArtikelstammImporter.class);
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		log.info("ArtikelstammImporterPage. doImport " + results[0]);
		ArtikelstammReferenceDataImporter ardi = new ArtikelstammReferenceDataImporter();
		return ardi.performImport(monitor, new FileInputStream(results[0]), null);
	}
	
	@Override
	public String getTitle(){
		return "Artikelstamm CH Import";
	}
	
	@Override
	public String getDescription(){
		return "Importiere Artikelstamm";
	}
	
	@Override
	public Composite createPage(Composite parent){
		Composite versionInfo = new Composite(parent, SWT.None);
		versionInfo.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		versionInfo.setLayout(new GridLayout(2, false));
		Label lblVersion = new Label(versionInfo, SWT.None);
		lblVersion.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		lblVersion.setText("Aktuelle Version:");
		Label lblVERSION = new Label(versionInfo, SWT.None);
		lblVERSION.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		int version = ArtikelstammItem.getCurrentVersion();
		StringBuilder sb = new StringBuilder();
		sb.append(" v" + version);
		Date importSetCreationDate = ArtikelstammItem.getImportSetCreationDate();
		if (importSetCreationDate != null) {
			sb.append(" / " + ArtikelstammHelper.monthAndYearWritten
				.format(ArtikelstammItem.getImportSetCreationDate()));
		}
		lblVERSION.setText(sb.toString());
		
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout(2, false));
		
		return ret;
	}
}
