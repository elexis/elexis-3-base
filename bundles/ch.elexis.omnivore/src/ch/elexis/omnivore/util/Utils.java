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
package ch.elexis.omnivore.util;

import java.util.List;

import ch.elexis.core.cdi.PortableServiceLoader;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.omnivore.model.IDocumentHandle;

public class Utils {

	public static final String DEMO_DOCUMENTS = "ch.elexis.documents"; //$NON-NLS-1$

	public static List<IDocumentHandle> getMembers(IDocumentHandle dh, IPatient pat) {
		IQuery<IDocumentHandle> query = Utils.getOmnivoreModelService().getQuery(IDocumentHandle.class);
		query.and("category", COMPARATOR.EQUALS, dh.getTitle()); //$NON-NLS-1$
		query.and("kontakt", COMPARATOR.EQUALS, pat); //$NON-NLS-1$
		return query.execute();
	}

	public static IModelService getOmnivoreModelService() {
		return PortableServiceLoader.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.omnivore.data.model)").get();
	}

}
