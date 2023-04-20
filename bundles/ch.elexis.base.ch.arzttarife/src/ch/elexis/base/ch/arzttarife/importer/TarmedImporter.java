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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import ch.elexis.core.importer.div.importers.AccessWrapper;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

/**
 * Import des Tarmed-Tarifsystems aus der Datenbank der ZMT. We use Gerry
 * AccessWrapper to
 *
 * * copy all tables from the MDB file into the actual DB. The tablenames are
 * all prefixed with TARMED_IMPORT_. Then we close the connection to the MDB
 * file.
 *
 * * now import everything using plain SQL-Statements.
 *
 * * finally drop all intermediate tables again
 *
 * (Download der Datenbank z.B.: <a href=
 * "http://www.zmt.ch/de/tarmed/tarmed_tarifstruktur/tarmed_database.htm"
 * >hier</a> oder <a href=
 * "http://www.tarmedsuisse.ch/site_tarmed/pages/edito/public/e_02_03.htm"
 * >hier</a>.)
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

	String selectedLaw = StringUtils.EMPTY;
	String[] availableLaws = new String[] { StringUtils.EMPTY, "KVG", "UVG", "MVG", "IVG" };

	@Inject
	private IReferenceDataImporterService importerService;

	public TarmedImporter() {
		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public String getTitle() {
		return "TarMed code"; //$NON-NLS-1$
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
		// special importers since Tarmed 1.09
		if ("KVG".equals(selectedLaw)) {
			return importerService.getImporter("tarmed_kvg_34")
					.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available"));
		}
		// default importer
		return importerService.getImporter("tarmed_34")
				.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available"));
	}

	@Override
	public String getDescription() {
		return Messages.TarmedImporter_enterSource;
	}

	@Override
	public Composite createPage(final Composite parent) {
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite updateIDsComposite = new Composite(fis, SWT.NONE);
		updateIDsComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		updateIDsComposite.setLayout(new FormLayout());

		Label lbl = new Label(updateIDsComposite, SWT.NONE);
		lbl.setText("Gesetz des Datensatz (relevant ab Tarmed 1.09)");
		final ComboViewer lawCombo = new ComboViewer(updateIDsComposite, SWT.BORDER);

		lawCombo.setContentProvider(ArrayContentProvider.getInstance());
		lawCombo.setInput(availableLaws);
		lawCombo.setSelection(new StructuredSelection(selectedLaw));
		lawCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				if (selection != null && !selection.isEmpty()) {
					selectedLaw = (String) selection.getFirstElement();
				} else {
					selectedLaw = StringUtils.EMPTY;
				}
			}
		});

		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 0);
		lbl.setLayoutData(fd);

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(lbl, 5);
		lawCombo.getCombo().setLayoutData(fd);

		lbl = new Label(updateIDsComposite, SWT.NONE);
		lbl.setText(Messages.TarmedImporter_updateOldIDEntries);
		final Button updateIDsBtn = new Button(updateIDsComposite, SWT.CHECK);

		fd = new FormData();
		fd.top = new FormAttachment(lawCombo.getControl(), 5);
		fd.left = new FormAttachment(0, 0);
		lbl.setLayoutData(fd);

		fd = new FormData();
		fd.top = new FormAttachment(lawCombo.getControl(), 5);
		fd.left = new FormAttachment(lbl, 5);
		updateIDsBtn.setLayoutData(fd);

		updateIDsBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateIDs = updateIDsBtn.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateIDs = updateIDsBtn.getSelection();
			}
		});
		return fis;
	}
}
