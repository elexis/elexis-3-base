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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class MiGelImporter extends ImporterPage {
	private static final String SRC_ENCODING = "iso-8859-1"; //$NON-NLS-1$
	boolean bDelete = false;
	Button bClear;
	String mode;
	
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
		final String line =
			"([0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]\\.[0-9]) +L? +(.+)  +(.+)  +(.+)  +.+"; //$NON-NLS-1$
		try {
			File file = new File(results[0]);
			long l = file.length();
			monitor.beginTask("MiGeL Import " + mode, (int) l / 100); //$NON-NLS-1$
			if (file.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
				return importCSV(file, monitor);
			} else {
				// long l=file.length();
				InputStreamReader is =
					new InputStreamReader(new FileInputStream(file), "iso-8859-1"); //$NON-NLS-1$
				BufferedReader br = new BufferedReader(is);
				
				String in;
				monitor.subTask("MiGel - Import"); //$NON-NLS-1$
				Pattern pat = Pattern.compile(line);
				// Query qbe=new Query(MiGelArtikel.class);
				LineFeeder lf = new LineFeeder(br);
				while ((in = lf.nextLine()) != null) {
					Matcher match = pat.matcher(in);
					if (match.matches()) {
						String code = match.group(1);
						String text = match.group(2);
						String unit = match.group(3);
						Money price = new Money(match.group(4));
						/* MiGelArtikel migel= */new MiGelArtikel(code, text, unit, price);
					}
				}
				return Status.OK_STATUS;
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
	
	class LineFeeder {
		static final String codeline =
			"[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9]\\.[0-9].+"; //$NON-NLS-1$
		String prev;
		BufferedReader br;
		
		LineFeeder(final BufferedReader b) throws Exception{
			br = b;
			prev = br.readLine();
		}
		
		char peek(){
			return prev.charAt(0);
		}
		
		String nextl() throws Exception{
			String r;
			while ((r = br.readLine()) != null) {
				if (r.matches(codeline)) {
					break;
				}
			}
			return r;
		}
		
		String nextLine() throws Exception{
			if (prev == null) {
				return null;
			}
			if (!prev.matches(codeline)) {
				prev = nextl();
			}
			String ret = prev;
			prev = br.readLine();
			if (prev == null) {
				br.close();
				return ret;
			}
			while (!prev.matches(codeline) && !prev.startsWith(" ")) { //$NON-NLS-1$
				if (ret.matches(".*- +[CHIM]?$")) { //$NON-NLS-1$
					ret = ret.replaceFirst("- +[CHIM]?$", prev.trim()); //$NON-NLS-1$
				} else if (ret.matches(".* +[CHIM]$")) { //$NON-NLS-1$
					ret = ret.replaceFirst("[CHIM]$", prev.trim()); //$NON-NLS-1$
				} else {
					ret += " " + prev.trim(); //$NON-NLS-1$
				}
				prev = br.readLine();
				if (prev == null) {
					br.close();
					return ret;
				}
			}
			return ret;
		}
		
		boolean atEOF(){
			return prev == null;
		}
		
		public void close() throws Exception{
			br.close();
		}
	}
	
	private IStatus importCSV(final File file, final IProgressMonitor monitor)
		throws FileNotFoundException, IOException{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), SRC_ENCODING);
		CSVReader reader = new CSVReader(isr);
		String[] line;
		monitor.subTask(Messages.MiGelImporter_ReadMigel);
		while ((line = reader.readNext()) != null) {
			if (line.length < 3) {
				continue;
			}
			// line=StringTool.convertEncoding(line, SRC_ENCODING);
			Money betrag;
			try {
				betrag = new Money(Double.parseDouble(line[3]));
			} catch (Exception ex) {
				betrag = new Money();
			}
			new MiGelArtikel(line[0], line[1], line[2], betrag);
			monitor.worked(1);
		}
		monitor.done();
		return Status.OK_STATUS;
	}
}
