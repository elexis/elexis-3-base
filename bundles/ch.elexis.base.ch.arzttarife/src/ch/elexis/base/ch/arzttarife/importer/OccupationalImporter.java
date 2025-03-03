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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import ch.elexis.base.ch.arzttarife.occupational.IOccupationalLeistung;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.services.IReferenceDataImporterService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

public class OccupationalImporter extends ImporterPage {

	private TimeTool validFrom = new TimeTool();

	private TimeTool endOfEpoch = new TimeTool(TimeTool.END_OF_UNIX_EPOCH);

	@Inject
	private IReferenceDataImporterService importerService;

	public OccupationalImporter() {
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

		return fis;
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		try (FileInputStream tarifInputStream = new FileInputStream(results[0])) {
			IReferenceDataImporter importer = importerService.getImporter("occupational")
					.orElseThrow(() -> new IllegalStateException("No IReferenceDataImporter available."));
			return importer.performImport(monitor, tarifInputStream, getVersionFromValid(validFrom));
		}
	}

	private int getVersionFromValid(TimeTool validFrom) {
		int year = validFrom.get(TimeTool.YEAR);
		int month = validFrom.get(TimeTool.MONTH) + 1;
		int day = validFrom.get(TimeTool.DAY_OF_MONTH);

		return day + (month * 100) + ((year - 2000) * 10000);
	}

	@Override
	public String getDescription() {
		return "Arbeitsmedizinische Vorsorgeuntersuchungen";
	}

	@Override
	public String getTitle() {
		return "Arbeitsmed. Vorsorge";
	}

	@Override
	public List<String> getObjectClass() {
		return Collections.singletonList(IOccupationalLeistung.class.getName());
	}

}
