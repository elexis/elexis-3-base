/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.base.ch.arzttarife.importer;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class PhysioImporter extends ImporterPage {

	private TimeTool validFrom = new TimeTool();

	String selectedLaw = StringUtils.EMPTY;
	String[] availableLaws = new String[] { StringUtils.EMPTY, "KVG", "UVG", "MVG", "IVG" };

	@Inject
	private IReferenceDataImporterService importerService;

	public PhysioImporter() {
		// set default to start of year
		validFrom.clear();
		validFrom.set(TimeTool.getInstance().get(Calendar.YEAR), 0, 1);

		CoreUiUtil.injectServicesWithContext(this);
	}

	@Override
	public Composite createPage(Composite parent) {
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite validDateComposite = new Composite(fis, SWT.NONE);
		validDateComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		validDateComposite.setLayout(new FormLayout());

		Label lbl = new Label(validDateComposite, SWT.NONE);
		lbl.setText("Tarif ist gültig ab:");
		final DateTime validDate = new DateTime(validDateComposite, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);

		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(20, -5);
		lbl.setLayoutData(fd);

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(20, 5);
		validDate.setLayoutData(fd);

		validDate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValidFromDate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setValidFromDate();
			}

			private void setValidFromDate() {
				validFrom.set(validDate.getYear(), validDate.getMonth(), validDate.getDay());
				// System.out.println("VALID FROM: " +
				// validFrom.toString(TimeTool.DATE_COMPACT));
			}
		});
		validDate.setDate(validFrom.get(TimeTool.YEAR), validFrom.get(TimeTool.MONTH),
				validFrom.get(TimeTool.DAY_OF_MONTH));

		lbl = new Label(validDateComposite, SWT.NONE);
		lbl.setText("Gesetz des Datensatz (relevant ab 1 Juli 2025)");
		final ComboViewer lawCombo = new ComboViewer(validDateComposite, SWT.BORDER);

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

		fd = new FormData();
		fd.top = new FormAttachment(validDate, 5);
		fd.left = new FormAttachment(0, 0);
		lbl.setLayoutData(fd);

		fd = new FormData();
		fd.top = new FormAttachment(validDate, 5);
		fd.left = new FormAttachment(lbl, 5);
		lawCombo.getControl().setLayoutData(fd);

		return fis;
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		try (FileInputStream tarifInputStream = new FileInputStream(results[0])) {
			IReferenceDataImporter importer = getImporter();
			return importer.performImport(monitor, tarifInputStream, getVersionFromValid(validFrom));
		}
	}

	private IReferenceDataImporter getImporter() {
		// special importers since 1.7.2025
		if ("KVG".equals(selectedLaw)) {
			return importerService.getImporter("physio_kvg")
					.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available"));
		}
		// default importer
		return importerService.getImporter("physio")
				.orElseThrow(() -> new IllegalStateException("No ReferenceDataImporter available"));
	}

	private int getVersionFromValid(TimeTool validFrom) {
		int year = validFrom.get(TimeTool.YEAR);
		int month = validFrom.get(TimeTool.MONTH) + 1;
		int day = validFrom.get(TimeTool.DAY_OF_MONTH);

		return day + (month * 100) + ((year - 2000) * 10000);
	}

	@Override
	public String getDescription() {
		return "Physiotherapie-Tarif";
	}

	@Override
	public String getTitle() {
		return "Physio";
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IPhysioLeistung.class.getName());
	}

}
