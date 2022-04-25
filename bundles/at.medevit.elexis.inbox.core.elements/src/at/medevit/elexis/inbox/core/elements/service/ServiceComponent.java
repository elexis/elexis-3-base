/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.core.elements.service;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.elexis.inbox.model.IInboxElementService;
import ch.elexis.core.model.ILabOrder;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;

@Component
public class ServiceComponent {

	private static IInboxElementService service;

	private static IModelService coreModelService;

	@Reference
	public void setSerivce(IInboxElementService service) {
		ServiceComponent.service = service;
	}

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelSerivce(IModelService coreModelService) {
		ServiceComponent.coreModelService = coreModelService;
	}

	public static IInboxElementService get() {
		return service;
	}

	public static List<ILabOrder> getLabOrders(ILabResult labResult) {
		IQuery<ILabOrder> query = coreModelService.getQuery(ILabOrder.class);
		query.and(ModelPackage.Literals.ILAB_ORDER__PATIENT, COMPARATOR.EQUALS, labResult.getPatient());
		query.and(ModelPackage.Literals.ILAB_ORDER__RESULT, COMPARATOR.EQUALS, labResult);
		return query.execute();
	}

	public static <T> T load(String id, Class<T> clazz) {
		return coreModelService.load(id, clazz).orElse(null);
	}
}
