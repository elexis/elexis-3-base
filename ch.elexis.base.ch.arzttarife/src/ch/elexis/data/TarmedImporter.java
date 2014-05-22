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
package ch.elexis.data;

import java.io.FileInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.ui.importer.div.importers.AccessWrapper;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.importer.TarmedReferenceDataImporter;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Import des Tarmed-Tarifsystems aus der Datenbank der ZMT. We use Gerry AccessWrapper to
 * 
 * * copy all tables from the MDB file into the actual DB. The tablenames are all prefixed with
 * TARMED_IMPORT_. Then we close the connection to the MDB file.
 * 
 * * now import everything using plain SQL-Statements.
 * 
 * * finally drop all intermediate tables again
 * 
 * (Download der Datenbank z.B.: <a
 * href="http://www.zmt.ch/de/tarmed/tarmed_tarifstruktur/tarmed_database.htm" >hier</a> oder <a
 * href= "http://www.tarmedsuisse.ch/site_tarmed/pages/edito/public/e_02_03.htm" >hier</a>.)
 * 
 * @author gerry
 * 
 */
public class TarmedImporter extends ImporterPage {
	
	AccessWrapper aw;
	JdbcLink pj;
	Stm source, dest;
	// then real import
	boolean updateIDs = false;
	
	public TarmedImporter(){}
	
	@Override
	public String getTitle(){
		return "TarMed code"; //$NON-NLS-1$
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.elexis.util.ImporterPage#doImport(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		TarmedReferenceDataImporter trdImporter = new TarmedReferenceDataImporter();
		return trdImporter.performImport(monitor, new FileInputStream(results[0]), null);
	}
	
	@Override
	public String getDescription(){
		return Messages.TarmedImporter_enterSource;
	}
	
	@Override
	public Composite createPage(final Composite parent){
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite updateIDsComposite = new Composite(fis, SWT.NONE);
		updateIDsComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		updateIDsComposite.setLayout(new FormLayout());
		
		Label lbl = new Label(updateIDsComposite, SWT.NONE);
		lbl.setText(Messages.TarmedImporter_updateOldIDEntries);
		final Button updateIDsBtn = new Button(updateIDsComposite, SWT.CHECK);
		
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		lbl.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(lbl, 5);
		updateIDsBtn.setLayoutData(fd);
		
		updateIDsBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e){
				updateIDs = updateIDsBtn.getSelection();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){
				updateIDs = updateIDsBtn.getSelection();
			}
		});
		
		return fis;
	}
}
