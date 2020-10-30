/*******************************************************************************
 * Copyright (c) 2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.data;

import java.io.FileInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.data.importer.PandemieReferenceDataImporter;

public class PandemieImporter extends ImporterPage {
	
	@Override
	public Composite createPage(Composite parent){
		return new FileBasedImporter(parent, this);
	}
	
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		
		PandemieReferenceDataImporter importer = new PandemieReferenceDataImporter();
		return importer.performImport(monitor, new FileInputStream(results[0]), null);
	}
	
	@Override
	public String getDescription(){
		return "Pandemie-Tarif";
	}
	
	@Override
	public String getTitle(){
		return "Pandemie";
	}
}
