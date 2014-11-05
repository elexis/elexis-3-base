/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.importer;

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.interfaces.AbstractReferenceDataImporter;

public class ArtikelstammPharmaReferenceDataImporter extends AbstractReferenceDataImporter {
	
	@Override
	public Class<?> getReferenceDataTypeResponsibleFor(){
		return ArtikelstammItem.class;
	}

	@Override
	public int getCurrentVersion(){
		return ArtikelstammItem.getImportSetCumulatedVersion(ArtikelstammConstants.TYPE.P);
	}

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer version){
		return ArtikelstammImporter.performImport(monitor, input, version);	
	}
}
