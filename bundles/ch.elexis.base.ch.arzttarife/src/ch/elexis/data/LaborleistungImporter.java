/*******************************************************************************
 * Copyright (c) 2005-2006, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;

public class LaborleistungImporter extends ImporterPage {
	private static final String codeline =
		"[A-Z]?  +([0-9]{4,4}\\.[0-9][0-9])+[\\* ]+([0-9]+) +(.*)"; //$NON-NLS-1$
	
	public LaborleistungImporter(){}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		LaborLeistung.createTable();
		File file = new File(results[0]);
		long l = file.length();
		InputStreamReader ir = new InputStreamReader(new FileInputStream(file), "iso-8859-1"); //$NON-NLS-1$
		BufferedReader br = new BufferedReader(ir);
		String in;
		monitor.beginTask(Messages.LaborleistungImporter_AnalyseImport, (int) (l / 100)); //$NON-NLS-1$
		LineFeeder lf = new LineFeeder(br);
		Pattern llcode = Pattern.compile(codeline);
		while ((in = lf.nextLine()) != null) {
			Matcher match = llcode.matcher(in);
			if (match.matches()) {
				String nr = match.group(1);
				String tp = match.group(2);
				String tx = match.group(3);
				/* LaborLeistung ll= */new LaborLeistung(nr, tx, tp);
			}
			monitor.worked(1);
			if (monitor.isCanceled()) {
				monitor.done();
				return Status.CANCEL_STATUS;
			}
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
	@Override
	public String getTitle(){
		return Messages.LaborleistungImporter_AnalyzeTariff; //$NON-NLS-1$
	}
	
	@Override
	public String getDescription(){
		return Messages.LaborleistungImporter_pleseEnterFilename; //$NON-NLS-1$
	}
	
	@Override
	public Composite createPage(Composite parent){
		Composite ret = new ImporterPage.FileBasedImporter(parent, this);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return ret;
	}
	
	class LineFeeder {
		static final String codeline = "[A-Z]? +[0-9]{4,4}\\.[0-9][0-9].*"; //$NON-NLS-1$
		String prev;
		BufferedReader br;
		
		LineFeeder(BufferedReader b) throws Exception{
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
	
}
