/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
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
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;

public class ArtikelstammNonPharmaReferenceDataImporter extends AbstractReferenceDataImporter {
	
	@Override
	public @NonNull Class<?> getReferenceDataTypeResponsibleFor(){
		return ArtikelstammItem.class;
	}
	
	@Override
	public int getCurrentVersion(){
		return ArtikelstammItem.getImportSetCumulatedVersion(ArtikelstammConstants.TYPE.N);
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, @NonNull InputStream input, @Nullable Integer version){
		return ArtikelstammImporter.performImport(monitor, input, version);	
	}
	
}
