/*******************************************************************************
 * Copyright (c) 2009-2010, G. Weirich and Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.labortarif2009.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

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

import ch.elexis.base.ch.labortarif_2009.Messages;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Importer extends ImporterPage {
	TimeTool validFrom = new TimeTool();
	
	Fachspec[] specs;
	int row;
	
	public Importer(){
		// set default to start of year
		validFrom.clear();
		validFrom.set(TimeTool.getInstance().get(Calendar.YEAR), 0, 1);
	}
	
	@Override
	public Composite createPage(Composite parent){
		FileBasedImporter fis = new ImporterPage.FileBasedImporter(parent, this);
		fis.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		Composite validDateComposite = new Composite(fis, SWT.NONE);
		validDateComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		validDateComposite.setLayout(new FormLayout());
		
		Label lbl = new Label(validDateComposite, SWT.NONE);
		lbl.setText("Tarif ist gültig ab:");
		final DateTime validDate =
			new DateTime(validDateComposite, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		
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
			public void widgetSelected(SelectionEvent e){
				setValidFromDate();
			}
			
			public void widgetDefaultSelected(SelectionEvent e){
				setValidFromDate();
			}
			
			private void setValidFromDate(){
				validFrom.set(validDate.getYear(), validDate.getMonth(), validDate.getDay());
				// System.out.println("VALID FROM: " + validFrom.toString(TimeTool.DATE_COMPACT));
			}
		});
		validDate.setDate(validFrom.get(TimeTool.YEAR), validFrom.get(TimeTool.MONTH),
			validFrom.get(TimeTool.DAY_OF_MONTH));
		
		return fis;
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		EALReferenceDataImporter refImporter = new EALReferenceDataImporter();
		
		FileInputStream tarifInputStream = new FileInputStream(results[0]);
		
		try {
			return refImporter.performImport(monitor, tarifInputStream,
				refImporter.getVersionFromValid(validFrom));
		} finally {
			if (tarifInputStream != null) {
				tarifInputStream.close();
			}
		}
	}
	
	@Override
	public String getDescription(){
		return Messages.Importer_selectFile;
	}
	
	@Override
	public String getTitle(){
		return "EAL 2009"; //$NON-NLS-1$
	}
	
	public static Fachspec[] loadFachspecs(int langdef){
		String specs =
			PlatformHelper.getBasePath(Constants.pluginID) + File.separator
				+ "rsc" + File.separator + "arztpraxen.xls"; //$NON-NLS-1$ //$NON-NLS-2$
		ExcelWrapper x = new ExcelWrapper();
		x.setFieldTypes(new Class[] {
			Integer.class, String.class, Integer.class, Integer.class
		});
		if (x.load(specs, langdef)) {
			int first = x.getFirstRow();
			int last = x.getLastRow();
			Fachspec[] fspecs = new Fachspec[last - first + 1];
			for (int i = first; i <= last; i++) {
				fspecs[i] = new Fachspec(x.getRow(i).toArray(new String[0]));
			}
			return fspecs;
		}
		return null;
	}

	public static class Fachspec {
		public int code, from, until;
		public String name;
		
		Fachspec(String[] line){
			this(Integer.parseInt(StringTool.getSafe(line, 0)), StringTool.getSafe(line, 1),
				Integer.parseInt(StringTool.getSafe(line, 2)), Integer.parseInt(StringTool.getSafe(
					line, 3)));
		}
		
		Fachspec(int code, String name, int from, int until){
			this.code = code;
			this.from = from;
			this.until = until;
			this.name = name;
		}
		
		/**
		 * Find the spec a given row belongs to
		 * 
		 * @param specs
		 *            a list of all specs
		 * @param row
		 *            the row to match
		 * @return the spec number or -1 if no spec
		 */
		public static int getFachspec(Fachspec[] specs, int row){
			for (Fachspec spec : specs) {
				if (spec.from <= row && spec.until >= row) {
					return spec.code;
				}
			}
			return -1;
		}
	}
}
