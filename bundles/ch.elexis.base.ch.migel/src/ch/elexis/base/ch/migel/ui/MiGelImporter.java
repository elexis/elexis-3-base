/*******************************************************************************
 * Copyright (c) 2006-2017, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    T. Huster - copied from ch.elexis.base.ch.artikel
 *    
 *******************************************************************************/
package ch.elexis.base.ch.migel.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import au.com.bytecode.opencsv.CSVReader;
import ch.elexis.artikel_ch.data.MiGelArtikel;
import ch.elexis.base.ch.migel.Messages;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.StringTool;

public class MiGelImporter extends ImporterPage {
	private static final String SRC_ENCODING = "iso-8859-1"; //$NON-NLS-1$
	boolean bDelete = false;
	Button bClear;
	String mode;
	
	private enum ImportFields {
			POSNUMER(0), NAME(1), UNIT(2), PRICE(3), CATEGORY(4), SUBCATEGORY(5), AMOUNT(6);
		
		private int index;
		
		ImportFields(int index){
			this.index = index;
		}
		
		private boolean exists(String[] line){
			return line.length > index;
		}
		
		public String getStringValue(String[] line){
			if (exists(line)) {
				return line[index];
			} else {
				return "";
			}
		}
		
		public Money getMoneyValue(String[] line){
			if (exists(line)) {
				try {
					return new Money(getStringValue(line));
				} catch (ParseException e) {
					// ignore
				}
			}
			return new Money();
		}
		
	}
	
	public MiGelImporter(){}
	
	@Override
	public String getTitle(){
		return MiGelArtikel.MIGEL_NAME; //$NON-NLS-1$
	}
	
	@Override
	public String getDescription(){
		return Messages.MiGelImporter_PleaseSelectFile;
	}
	
	@Override
	public IStatus doImport(final IProgressMonitor monitor) throws Exception{
		mode = Messages.MiGelImporter_ModeUpdateAdd;
		if (bDelete == true) {
			PersistentObject.getConnection().exec("DELETE FROM ARTIKEL WHERE TYP='MiGeL'"); //$NON-NLS-1$
			mode = Messages.MiGelImporter_ModeCreateNew;
		}
		try {
			File file = new File(results[0]);
			long l = file.length();
			monitor.beginTask("MiGeL Import " + mode, (int) l / 100); //$NON-NLS-1$
			if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
				return importCSV(file, monitor);
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		return Status.CANCEL_STATUS;
	}
	
	@Override
	public void collect(){
		bDelete = bClear.getSelection();
	}
	
	@Override
	public Composite createPage(final Composite parent){
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		bClear = new Button(parent, SWT.CHECK | SWT.WRAP);
		bClear.setText(Messages.MiGelImporter_ClearAllData);
		bClear.setSelection(true);
		bClear.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		return ret;
		
	}
	
	private IStatus importCSV(final File file, final IProgressMonitor monitor)
		throws FileNotFoundException, IOException{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), SRC_ENCODING);
		CSVReader reader = new CSVReader(isr);
		String[] line;
		monitor.subTask(Messages.MiGelImporter_ReadMigel);
		while ((line = reader.readNext()) != null) {
			if (isFieldsLine(line) && line.length >= 3) {
				StringBuilder sb = new StringBuilder();
				String category = ImportFields.SUBCATEGORY.getStringValue(line);
				if (category.isEmpty()) {
					category = ImportFields.CATEGORY.getStringValue(line);
				}
				// category only 1 line and max 80 char
				if(!category.isEmpty()) {
					sb.append(StringTool.getFirstLine(category, 80, "[\\n\\r]")).append(" - ");
				}
				sb.append(ImportFields.NAME.getStringValue(line));
				
				MiGelArtikel artikel = new MiGelArtikel(ImportFields.POSNUMER.getStringValue(line),
					sb.toString(), ImportFields.UNIT.getStringValue(line),
					ImportFields.PRICE.getMoneyValue(line));
				
				String amount = ImportFields.AMOUNT.getStringValue(line);
				if (!amount.isEmpty()) {
					try {
						double amountDbl = Double.parseDouble(amount);
						artikel.setPackungsGroesse((int) amountDbl);
					} catch (NumberFormatException e) {
						// ignore
					}
				}
				monitor.worked(1);
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private boolean isFieldsLine(String[] line){
		// line[0] contains the code, which always contains digits, so if not its is probably the description
		return containsDigits(line[0]);
	}
	
	private boolean containsDigits(String string){
		for (char character : string.toCharArray()) {
			if (Character.isDigit(character)) {
				return true;
			}
		}
		return false;
	}
}
