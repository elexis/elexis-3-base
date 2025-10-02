/*******************************************************************************
 * Copyright (c) 2017, J. Sigle, Niklaus Giger and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    J. Sigle - Initial implementation in a private branch of Elexis 2.1
 *    N. Giger - Reworked for Elexis 3.4 including unit tests
 *
 *******************************************************************************/

package ch.elexis.omnivore.model.util;

import java.util.List;

import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.omnivore.model.DocumentDocHandle;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.model.service.OmnivoreModelServiceHolder;

public class Utils {

	public static final String DEMO_DOCUMENTS = "ch.elexis.documents"; //$NON-NLS-1$

	public static List<IDocumentHandle> getMembers(IDocumentHandle dh, IPatient pat) {
		IQuery<IDocumentHandle> query = OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		query.and("category", COMPARATOR.EQUALS, dh.getTitle()); //$NON-NLS-1$
		query.and("kontakt", COMPARATOR.EQUALS, pat); //$NON-NLS-1$
		return query.execute();
	}

	public static IVirtualFilesystemHandle getStorageFile(IDocumentHandle docHandle, boolean force) {
		if (docHandle instanceof DocumentDocHandle) {
			DocumentDocHandle impl = (DocumentDocHandle) docHandle;
			return impl.getStorageFile(force);
		}
		return null;
	}
}
