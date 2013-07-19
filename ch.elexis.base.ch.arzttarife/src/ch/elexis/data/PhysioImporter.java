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
package ch.elexis.data;

import java.io.FileReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import au.com.bytecode.opencsv.CSVReader;
import ch.elexis.core.data.Query;
import ch.elexis.core.ui.util.ImporterPage;

public class PhysioImporter extends ImporterPage {
	
	@Override
	public Composite createPage(Composite parent){
		return new FileBasedImporter(parent, this);
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		CSVReader reader = new CSVReader(new FileReader(results[0]), ';');
		monitor.beginTask("Importiere Physio", 100);
		String[] line = reader.readNext();
		while ((line = reader.readNext()) != null) {
			if (line.length < 3) {
				continue;
			}
			monitor.subTask(line[1]);
			String id =
				new Query<PhysioLeistung>(PhysioLeistung.class).findSingle("Ziffer", "=", line[0]);
			if (id != null) {
				PhysioLeistung pl = PhysioLeistung.load(id);
				pl.set(new String[] {
					"Titel", "TP"
				}, line[1], line[2]);
			} else {
				/* PhysioLeistung pl = */new PhysioLeistung(line[0], line[1], line[2], null, null);
			}
			
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
	@Override
	public String getDescription(){
		return "Physiotherapie-Tarif";
	}
	
	@Override
	public String getTitle(){
		return "Physio";
	}
	
}
